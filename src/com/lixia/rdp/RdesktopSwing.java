package com.lixia.rdp;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.lixia.rdp.keymapping.KeyCode_FileBased;
import com.lixia.rdp.rdp5.Rdp5JPanel;
import com.lixia.rdp.rdp5.VChannel;
import com.lixia.rdp.rdp5.VChannels;
import com.lixia.rdp.rdp5.cliprdr.ClipChannel;
import com.lixia.rdp.rdp5.keys.KeysChannel;
import com.lixia.rdp.tools.SendEventJPanel;
import com.redpois0n.Cracker;
import com.redpois0n.Main;

public class RdesktopSwing {
	public static final int exDiscReasonNoInfo = 0;
	public static final int exDiscReasonAPIInitiatedDisconnect = 1;
	public static final int exDiscReasonAPIInitiatedLogoff = 2;
	public static final int exDiscReasonServerIdleTimeout = 3;
	public static final int exDiscReasonServerLogonTimeout = 4;
	public static final int exDiscReasonReplacedByOtherConnection = 5;
	public static final int exDiscReasonOutOfMemory = 6;
	public static final int exDiscReasonServerDeniedConnection = 7;
	public static final int exDiscReasonServerDeniedConnectionFips = 8;
	public static final int exDiscReasonLicenseInternal = 256;
	public static final int exDiscReasonLicenseNoLicenseServer = 257;
	public static final int exDiscReasonLicenseNoLicense = 258;
	public static final int exDiscReasonLicenseErrClientMsg = 259;
	public static final int exDiscReasonLicenseHwidDoesntMatchLicense = 260;
	public static final int exDiscReasonLicenseErrClientLicense = 261;
	public static final int exDiscReasonLicenseCantFinishProtocol = 262;
	public static final int exDiscReasonLicenseClientEndedProtocol = 263;
	public static final int exDiscReasonLicenseErrClientEncryption = 264;
	public static final int exDiscReasonLicenseCantUpgradeLicense = 265;
	public static final int exDiscReasonLicenseNoRemoteConnections = 266;
	static Logger logger = Logger.getLogger("com.lixia.rdp");
	static boolean keep_running;
	static boolean loggedon;
	static boolean readytosend;
	static boolean application = false;
	static boolean showTools;
	static final String keyMapPath = "keymaps/";
	static String mapFile = "en-gb";
	static String keyMapLocation = "";
	static SendEventJPanel toolFrame = null;
	public static KeysChannel keyChannel = null;
	public static RdesktopJPanel g_canvas;
	public static KeyCode_FileBased keyMap = null;
	public static VChannel seamlessChannel = null;
	public static Method seamlessSetcursor = null;
	public static Method seamlessRepaint = null;

	static String textDisconnectReason(int paramInt) {
		String str;
		switch (paramInt) {
		case 0:
			str = "No information available";
			break;
		case 1:
			str = "Server initiated disconnect";
			break;
		case 2:
			str = "Server initiated logoff";
			break;
		case 3:
			str = "Server idle timeout reached";
			break;
		case 4:
			str = "Server logon timeout reached";
			break;
		case 5:
			str = "Another user connected to the session";
			break;
		case 6:
			str = "The server is out of memory";
			break;
		case 7:
			str = "The server denied the connection";
			break;
		case 8:
			str = "The server denied the connection for security reason";
			break;
		case 256:
			str = "Internal licensing error";
			break;
		case 257:
			str = "No license server available";
			break;
		case 258:
			str = "No valid license available";
			break;
		case 259:
			str = "Invalid licensing message";
			break;
		case 260:
			str = "Hardware id doesn't match software license";
			break;
		case 261:
			str = "Client license error";
			break;
		case 262:
			str = "Network error during licensing protocol";
			break;
		case 263:
			str = "Licensing protocol was not completed";
			break;
		case 264:
			str = "Incorrect client license enryption";
			break;
		case 265:
			str = "Can't upgrade license";
			break;
		case 266:
			str = "The server is not licensed to accept remote connections";
			break;
		default:
			if ((paramInt > 4096) && (paramInt < 32767))
				str = "Internal protocol error";
			else
				str = "Unknown reason";
			break;
		}
		return str;
	}

	public static void usage() {
		System.err.println("Elusiva Everywhere version " + Version.version);
		System.err.println("Usage: java com.lixia.rdp.Rdesktop [options] server[:port]");
		System.err.println("\t-b \t\t\t\t\t\t\tbandwidth saving (good for 56k modem, but higher latency");
		System.err.println("\t-c DIR\t\t\t\t\t\tworking directory");
		System.err.println("\t-d DOMAIN\t\t\t\t\tlogon domain");
		System.err.println("\t-D is dubug model");
		System.err.println("\t-f[s]\t\t\t\tfull-screen mode [s to enable seamless mode]");
		System.err.println("\t-g WxH\t\t\t\t\t\tdesktop geometry");
		System.err.println("\t-m MAPFILE\t\t\t\t\tkeyboard mapping file for terminal server");
		System.err.println("\t-l LEVEL\t\t\t\t\tlogging level {DEBUG, INFO, WARN, ERROR, FATAL}");
		System.err.println("\t-n HOSTNAME\t\t\t\t\tclient hostname");
		System.err.println("\t-p PASSWORD\t\t\t\t\tpassword");
		System.err.println("\t-s SHELL\t\t\t\t\tshell");
		System.err.println("\t-t NUM\t\t\t\t\t\tRDP port (default 3389)");
		System.err.println("\t-T TITLE\t\t\t\t\tdo not support -T");
		System.err.println("\t-u USERNAME\t\t\t\t\tuser name");
		System.err.println("\t-o BPP\t\t\t\t\t\tbits-per-pixel for display");
		System.err.println("    -e path                     path to load licence from (requests and saves licence from server if not found)");
		System.err.println("\t-r device \t\t\t\t\tenable specified device redirection (this flag can be repeated)");
		System.err.println("    --save_licence              request and save licence from server");
		System.err.println("    --load_licence              load licence from file");
		System.err.println("    --console                   connect to console");
		System.err.println("\t--debug_key \t\t\t\tshow scancodes sent for each keypress etc");
		System.err.println("\t--debug_hex \t\t\t\tshow bytes sent and received");
		System.err.println("\t--no_remap_hash \t\t\tdisable hash remapping");
		System.err.println("\t--quiet_alt \t\t\t\tenable quiet alt fix");
		System.err.println("\t--no_encryption\t\t\t\tdisable encryption from client to server");
		System.err.println("\t--use_rdp4\t\t\t\t\tuse RDP version 4");
		System.err.println("    --enable_menu               enable menu bar");
		System.err.println("    --overHttp                http proxy server address and port(example--192.168.100.100:80)");
		System.err.println("\t--log4j_config=FILE\t\t\tuse FILE for log4j configuration");
		System.err.println("Example: java com.lixia.rdp.RdesktopSwing -g 800x600 -l WARN m52.propero.int");
		exit(0, null, (RdesktopJFrame) null, true);
	}

	public static void init(String[] paramArrayOfString, Cracker cracker) throws OrderException, RdesktopException {
		keep_running = true;
		loggedon = false;
		readytosend = false;
		showTools = false;
		mapFile = "en-gb";
		keyMapLocation = "";
		toolFrame = null;
		BasicConfigurator.configure();
		logger.setLevel(Level.INFO);
		RDPClientChooser localRDPClientChooser = new RDPClientChooser();
		//if ((localRDPClientChooser.RunNativeRDPClient(paramArrayOfString)) && (!Common.underApplet))
			//System.exit(0);
		int i = 51;
		int j = 0;
		StringBuffer localStringBuffer = new StringBuffer();
		LongOpt[] arrayOfLongOpt = new LongOpt[16];
		arrayOfLongOpt[0] = new LongOpt("debug_key", 0, null, 0);
		arrayOfLongOpt[1] = new LongOpt("debug_hex", 0, null, 0);
		arrayOfLongOpt[2] = new LongOpt("no_paste_hack", 0, null, 0);
		arrayOfLongOpt[3] = new LongOpt("log4j_config", 1, localStringBuffer, 0);
		arrayOfLongOpt[4] = new LongOpt("packet_tools", 0, null, 0);
		arrayOfLongOpt[5] = new LongOpt("quiet_alt", 0, localStringBuffer, 0);
		arrayOfLongOpt[6] = new LongOpt("no_remap_hash", 0, null, 0);
		arrayOfLongOpt[7] = new LongOpt("no_encryption", 0, null, 0);
		arrayOfLongOpt[8] = new LongOpt("use_rdp4", 0, null, 0);
		arrayOfLongOpt[9] = new LongOpt("use_ssl", 0, null, 0);
		arrayOfLongOpt[10] = new LongOpt("enable_menu", 0, null, 0);
		arrayOfLongOpt[11] = new LongOpt("console", 0, null, 0);
		arrayOfLongOpt[12] = new LongOpt("load_licence", 0, null, 0);
		arrayOfLongOpt[13] = new LongOpt("save_licence", 0, null, 0);
		arrayOfLongOpt[14] = new LongOpt("persistent_caching", 0, null, 0);
		arrayOfLongOpt[15] = new LongOpt("overHttp", 1, null, 0);
		String str2 = "Elusiva Everywhere";
		Getopt localGetopt = new Getopt("properJavaRDP", paramArrayOfString, "bc:d:f::g:k:l:m:n:p:s:t:u:o:r:", arrayOfLongOpt);
		ClipChannel localClipChannel = new ClipChannel();
		int k;
		Object localObject2;
		while ((k = localGetopt.getopt()) != -1) {
			String str1;
			switch (k) {
			case 0:
				switch (localGetopt.getLongind()) {
				case 0:
					Options.debug_keyboard = true;
					break;
				case 1:
					Options.debug_hexdump = true;
					break;
				case 2:
					break;
				case 3:
					str1 = localGetopt.getOptarg();
					PropertyConfigurator.configure(str1);
					logger.info("Log4j using config file " + str1);
					break;
				case 4:
					showTools = true;
					break;
				case 5:
					Options.altkey_quiet = true;
					break;
				case 6:
					Options.remap_hash = false;
					break;
				case 7:
					Options.packet_encryption = false;
					break;
				case 8:
					Options.use_rdp5 = false;
					Options.set_bpp(8);
					break;
				case 9:
					Options.use_ssl = true;
					break;
				case 10:
					Options.enable_menu = true;
					break;
				case 11:
					Options.console_session = true;
					break;
				case 12:
					Options.load_licence = true;
					break;
				case 13:
					Options.save_licence = true;
					break;
				case 14:
					Options.persistent_bitmap_caching = true;
					break;
				case 15:
					Options.http_mode = true;
					str1 = localGetopt.getOptarg();
					Options.http_server = str1;
					logger.info("remote http proxy server " + str1);
					break;
				default:
					usage();
				}
				break;
			case 111:
				Options.set_bpp(Integer.parseInt(localGetopt.getOptarg()));
				break;
			case 98:
				Options.low_latency = false;
				break;
			case 109:
				mapFile = localGetopt.getOptarg();
				break;
			case 99:
				Options.directory = localGetopt.getOptarg();
				break;
			case 100:
				Options.domain = localGetopt.getOptarg();
				break;
			case 102:
				Object localObject1 = Toolkit.getDefaultToolkit().getScreenSize();
				Frame localFrame = new Frame();
				localObject2 = Toolkit.getDefaultToolkit().getScreenInsets(localFrame.getGraphicsConfiguration());
				Options.width = ((Dimension) localObject1).width & 0xFFFFFFFC;
				if (application)
					Options.height = ((Dimension) localObject1).height - ((Insets) localObject2).bottom;
				else
					Options.height = ((Dimension) localObject1).height;
				Options.fullscreen = true;
				str1 = localGetopt.getOptarg();
				if (str1 != null)
					if (str1.charAt(0) == 's') {
						Options.seamless_active = true;
					} else {
						System.err.println(str2 + ": Invalid fullscreen option '" + str1 + "'");
						usage();
					}
				break;
			case 103:
				str1 = localGetopt.getOptarg();
				int n = str1.indexOf("x", 0);
				if (n == -1) {
					System.err.println(str2 + ": Invalid geometry: " + str1);
					usage();
				}
				Options.width = Integer.parseInt(str1.substring(0, n)) & 0xFFFFFFFC;
				Options.height = Integer.parseInt(str1.substring(n + 1));
				break;
			case 108:
				str1 = localGetopt.getOptarg();
				switch (str1.charAt(0)) {
				case 'D':
				case 'd':
					logger.setLevel(Level.DEBUG);
					break;
				case 'I':
				case 'i':
					logger.setLevel(Level.INFO);
					break;
				case 'W':
				case 'w':
					logger.setLevel(Level.WARN);
					break;
				case 'E':
				case 'e':
					logger.setLevel(Level.ERROR);
					break;
				case 'F':
				case 'f':
					logger.setLevel(Level.FATAL);
					break;
				default:
					System.err.println(str2 + ": Invalid debug level: " + str1.charAt(0));
					usage();
				}
				break;
			case 110:
				Options.hostname = localGetopt.getOptarg();
				break;
			case 112:
				Options.password = localGetopt.getOptarg();
				i |= 8;
				break;
			case 115:
				Options.command = localGetopt.getOptarg();
				application = true;
				break;
			case 117:
				Object localObject4 = localGetopt.getOptarg();
				Options.username = (String) localObject4;
				break;
			case 116:
				str1 = localGetopt.getOptarg();
				try {
					Options.port = Integer.parseInt(str1);
				} catch (NumberFormatException localNumberFormatException) {
					System.err.println(str2 + ": Invalid port number: " + str1);
					usage();
				}
			case 84:
				Options.windowTitle = localGetopt.getOptarg().replace('_', ' ');
				break;
			case 101:
				Options.licence_path = localGetopt.getOptarg();
				break;
			case 68:
				Options.is_debug = true;
				break;
			case 107:
				break;
			case 63:
			default:
				usage();
			}
		}
		if (j != 0)
			;
		Object localObject1 = null;
		if (localGetopt.getOptind() < paramArrayOfString.length) {
			int m = paramArrayOfString[(paramArrayOfString.length - 1)].indexOf(":", 0);
			if (m == -1) {
				localObject1 = paramArrayOfString[(paramArrayOfString.length - 1)];
			} else {
				localObject1 = paramArrayOfString[(paramArrayOfString.length - 1)].substring(0, m);
				Options.port = Integer.parseInt(paramArrayOfString[(paramArrayOfString.length - 1)].substring(m + 1));
			}
		} else {
			System.err.println(str2 + ": A server name is required!");
			usage();
		}
		VChannels localVChannels = new VChannels();
		if (Options.use_rdp5) {
			if (Options.map_clipboard)
				localVChannels.register(localClipChannel);
			if (Options.keys_register) {
				keyChannel = new KeysChannel();
				localVChannels.register(keyChannel);
			}
		}
		if (Options.seamless_active)
			try {
				localObject2 = Class.forName("com.lixia.rdp.seamless.SeamlessChannel");
				Object localObject3 = ((Class) localObject2).getDeclaredMethod("getInstance", null);
				seamlessChannel = (VChannel) ((Method) localObject3).invoke(null, null);
				localVChannels.register(seamlessChannel);
				Object localObject4 = new Class[] { Class.forName("java.awt.Cursor") };
				seamlessSetcursor = ((Class) localObject2).getDeclaredMethod("setSubCursor", (Class[]) localObject4);
				seamlessRepaint = ((Class) localObject2).getDeclaredMethod("repaintAll", null);
			} catch (Exception localException1) {
				localException1.printStackTrace();
			}
		logger.info("Elusiva Everywhere version " + Version.version);
		if (paramArrayOfString.length == 0)
			usage();
		String str3 = System.getProperty("java.specification.version");
		logger.info("Java version is " + str3);
		Object localObject3 = System.getProperty("os.name");
		Object localObject4 = System.getProperty("os.version");
		if ((((String) localObject3).equals("Windows 2000")) || (((String) localObject3).equals("Windows XP")))
			Options.built_in_licence = true;
		logger.info("Operating System is " + (String) localObject3 + " version " + (String) localObject4);
		if (((String) localObject3).startsWith("Linux"))
			Constants.OS = 2;
		else if (((String) localObject3).startsWith("Windows"))
			Constants.OS = 1;
		else if (((String) localObject3).startsWith("Mac"))
			Constants.OS = 3;
		if (Constants.OS == 3)
			Options.caps_sends_up_and_down = false;
		Rdp5JPanel localRdp5JPanel = null;
		Common.rdp = localRdp5JPanel;
		RdesktopJFrame_Localised localRdesktopJFrame_Localised = new RdesktopJFrame_Localised();
		localRdesktopJFrame_Localised.setTitle(localRdesktopJFrame_Localised.getTitle() + (String) localObject1);
		RdesktopJPanel localRdesktopJPanel = (RdesktopJPanel) localRdesktopJFrame_Localised.getContentPane();
		localRdesktopJPanel.addFocusListener(localClipChannel);
		g_canvas = localRdesktopJPanel;
		try {
			InputStream localInputStream = RdesktopSwing.class.getResourceAsStream("/keymaps/" + mapFile);
			if (localInputStream == null) {
				logger.debug("Loading keymap from filename");
				keyMap = new KeyCode_FileBased_Localised("keymaps/" + mapFile);
			} else {
				logger.debug("Loading keymap from InputStream");
				keyMap = new KeyCode_FileBased_Localised(localInputStream);
			}
			if (localInputStream != null)
				localInputStream.close();
			Options.keylayout = keyMap.getMapCode();
		} catch (Exception localException2) {
			Object localObject5 = new String[] { localException2.getClass() + ": " + localException2.getMessage() };
			Main.error((String[]) localObject5);
			localException2.printStackTrace();
			exit(0, null, (RdesktopJFrame) null, true);
		}
		logger.debug("Registering keyboard...");
		if (keyMap != null)
			localRdesktopJPanel.registerKeyboard(keyMap);
		boolean[] arrayOfBoolean = new boolean[1];
		int[] localObject5 = new int[1];
		Object localObject6 = null;
		Object localObject7;
		logger.debug("keep_running = " + keep_running);
		while (keep_running) {
			logger.debug("Initialising RDP layer...");
			localRdp5JPanel = new Rdp5JPanel(localVChannels, cracker);
			Common.rdp = localRdp5JPanel;
			logger.debug("Registering drawing surface...");
			localRdp5JPanel.registerDrawingSurface(localRdesktopJFrame_Localised);
			logger.debug("Registering comms layer...");
			localRdesktopJFrame_Localised.registerCommLayer(localRdp5JPanel);
			loggedon = false;
			readytosend = false;
			logger.info("Connecting to " + (String) localObject1 + ":" + Options.port + " ...");
			if (((String) localObject1).equalsIgnoreCase("localhost"))
				localObject1 = "127.0.0.1";
			if (localRdp5JPanel != null)
				try {
					localRdp5JPanel.connect(Options.username, InetAddress.getByName((String) localObject1), i, Options.domain, Options.password, Options.command, Options.directory);
					if (showTools) {
						toolFrame = new SendEventJPanel(localRdp5JPanel);
						toolFrame.setVisible(true);
					}
					if (keep_running) {
						if (!Options.packet_encryption)
							Options.encryption = false;
						logger.info("Connection successful");
						localRdp5JPanel.mainLoop(arrayOfBoolean, (int[]) localObject5);
						String str4;
						if (arrayOfBoolean[0] != false) // TODO )
						{
							exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
						} else {
							if ((localObject5[0] == 1) || (localObject5[0] == 2))
								exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
							if (localObject5[0] >= 2) {
								str4 = textDisconnectReason(localObject5[0]);
								localObject6 = new String[] { "Connection terminated", str4 };
								Main.error((String[]) localObject6);
								logger.warn("Connection terminated: " + str4);
								exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
							}
						}
						keep_running = false;
						if (!readytosend) {
							str4 = "The terminal server disconnected before licence negotiation completed.";
							localObject7 = new String[] { str4, "Possible cause: terminal server could not issue a licence." };
							logger.warn(str4);
							logger.warn(localObject6);
							Main.error((String[]) localObject7);
						}
					}
					if (showTools)
						toolFrame.dispose();
				} catch (ConnectionException localConnectionException) {
					localObject6 = new String[] { "Server can not be reached!", "Please try again later.", localConnectionException.getMessage() };
					Main.error((String[]) localObject6);
					exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
				} catch (UnknownHostException localUnknownHostException) {
					error(localUnknownHostException, localRdp5JPanel, localRdesktopJFrame_Localised, true);
				} catch (SocketException localSocketException) {
					if (localRdp5JPanel.isConnected()) {
						logger.fatal(localSocketException.getClass().getName() + " " + localSocketException.getMessage());
						error(localSocketException, localRdp5JPanel, localRdesktopJFrame_Localised, true);
						exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
					}
				} catch (RdesktopException localRdesktopException) {
					localObject6 = localRdesktopException.getClass().getName();
					localObject7 = localRdesktopException.getMessage();
					logger.fatal((String) localObject6 + ": " + (String) localObject7);
					localRdesktopException.printStackTrace(System.err);
					String[] arrayOfString;
					if (!readytosend) {
						arrayOfString = new String[] { "The terminal server reset connection before licence negotiation completed.", "Possible cause: terminal server could not connect to licence server.", "Retry?" };
						boolean bool = true;//localRdesktopJFrame_Localised.showYesNoErrorDialog(arrayOfString);
						if (!bool) {
							logger.info("Selected not to retry.");
							exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
						} else {
							if ((localRdp5JPanel != null) && (localRdp5JPanel.isConnected())) {
								logger.info("Disconnecting ...");
								localRdp5JPanel.disconnect();
								logger.info("Disconnected");
							}
							logger.info("Retrying connection...");
							keep_running = true;
							continue;
						}
					} else {
						arrayOfString = new String[] { localRdesktopException.getMessage() };
						Main.error(arrayOfString);
						exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
					}
				} catch (Exception localException3) {
					logger.warn(localException3.getClass().getName() + " " + localException3.getMessage());
					localException3.printStackTrace();
					error(localException3, localRdp5JPanel, localRdesktopJFrame_Localised, true);
				}
		}
		exit(0, localRdp5JPanel, localRdesktopJFrame_Localised, true);
	}

	public static void exit(int paramInt, RdpJPanel paramRdpJPanel, RdesktopJFrame paramRdesktopJFrame, boolean paramBoolean) {
		keep_running = false;
		if ((showTools) && (toolFrame != null))
			toolFrame.dispose();
		if ((paramRdpJPanel != null) && (paramRdpJPanel.isConnected())) {
			logger.info("Disconnecting ...");
			paramRdpJPanel.disconnect();
			logger.info("Disconnected");
		}
		if (paramRdesktopJFrame != null) {
			paramRdesktopJFrame.setVisible(false);
			paramRdesktopJFrame.dispose();
		}
		System.gc();
		//if ((paramBoolean) && (!Common.underApplet))
			//System.exit(paramInt);
	}

	public static void customError(String paramString, RdpJPanel paramRdpJPanel, RdesktopJFrame paramRdesktopJFrame, boolean paramBoolean) {
		logger.fatal(paramString);
		String[] arrayOfString = { paramString };
		Main.error(arrayOfString);
		exit(0, paramRdpJPanel, paramRdesktopJFrame, true);
	}

	public static void error(Exception paramException, RdpJPanel paramRdpJPanel, RdesktopJFrame paramRdesktopJFrame, boolean paramBoolean) {
		try {
			String str1 = paramException.getClass().getName();
			String str2 = paramException.getMessage();
			logger.fatal(str1 + ": " + str2);
			String[] arrayOfString = { str1, str2 };
			Main.error(arrayOfString);
		} catch (Exception localException) {
			logger.warn("Exception in Rdesktop.error: " + localException.getClass().getName() + ": " + localException.getMessage());
		}
		exit(0, paramRdpJPanel, paramRdesktopJFrame, paramBoolean);
	}
}