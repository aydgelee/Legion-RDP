package com.lixia.rdp;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.IndexColorModel;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import com.lixia.rdp.crypto.CryptoException;
import com.lixia.rdp.rdp5.VChannels;
import com.redpois0n.Cracker;
import com.redpois0n.Disconnectable;

public class RdpJPanel {
	
	public static int RDP5_DISABLE_NOTHING = 0;
	public static int RDP5_NO_WALLPAPER = 1;
	public static int RDP5_NO_FULLWINDOWDRAG = 2;
	public static int RDP5_NO_MENUANIMATIONS = 4;
	public static int RDP5_NO_THEMING = 8;
	public static int RDP5_NO_CURSOR_SHADOW = 32;
	public static int RDP5_NO_CURSORSETTINGS = 64;
	protected static Logger logger = Logger.getLogger("RdpJPanel");
	public static final int RDP_LOGON_NORMAL = 51;
	public static final int RDP_LOGON_AUTO = 8;
	public static final int RDP_LOGON_BLOB = 256;
	private static final int RDP_PDU_DEMAND_ACTIVE = 1;
	private static final int RDP_PDU_CONFIRM_ACTIVE = 3;
	private static final int RDP_PDU_DEACTIVATE = 6;
	private static final int RDP_PDU_DATA = 7;
	private static final int RDP_DATA_PDU_UPDATE = 2;
	private static final int RDP_DATA_PDU_CONTROL = 20;
	private static final int RDP_DATA_PDU_POINTER = 27;
	private static final int RDP_DATA_PDU_INPUT = 28;
	private static final int RDP_DATA_PDU_SYNCHRONISE = 31;
	private static final int RDP_DATA_PDU_BELL = 34;
	private static final int RDP_DATA_PDU_LOGON = 38;
	private static final int RDP_DATA_PDU_FONT2 = 39;
	private static final int RDP_DATA_PDU_DISCONNECT = 47;
	private static final int RDP_CTL_REQUEST_CONTROL = 1;
	private static final int RDP_CTL_GRANT_CONTROL = 2;
	private static final int RDP_CTL_DETACH = 3;
	private static final int RDP_CTL_COOPERATE = 4;
	private static final int RDP_UPDATE_ORDERS = 0;
	private static final int RDP_UPDATE_BITMAP = 1;
	private static final int RDP_UPDATE_PALETTE = 2;
	private static final int RDP_UPDATE_SYNCHRONIZE = 3;
	private static final int RDP_POINTER_SYSTEM = 1;
	private static final int RDP_POINTER_MOVE = 3;
	private static final int RDP_POINTER_COLOR = 6;
	private static final int RDP_POINTER_CACHED = 7;
	private static final int RDP_NULL_POINTER = 0;
	private static final int RDP_DEFAULT_POINTER = 32512;
	private static final int RDP_INPUT_SYNCHRONIZE = 0;
	private static final int RDP_INPUT_CODEPOINT = 1;
	private static final int RDP_INPUT_VIRTKEY = 2;
	private static final int RDP_INPUT_SCANCODE = 4;
	private static final int RDP_INPUT_MOUSE = 32769;
	private static final int RDP_CAPSET_GENERAL = 1;
	private static final int RDP_CAPLEN_GENERAL = 24;
	private static final int OS_MAJOR_TYPE_UNIX = 4;
	private static final int OS_MINOR_TYPE_XSERVER = 7;
	private static final int RDP_CAPSET_BITMAP = 2;
	private static final int RDP_CAPLEN_BITMAP = 28;
	private static final int RDP_CAPSET_ORDER = 3;
	private static final int RDP_CAPLEN_ORDER = 88;
	private static final int ORDER_CAP_NEGOTIATE = 2;
	private static final int ORDER_CAP_NOSUPPORT = 4;
	private static final int RDP_CAPSET_BMPCACHE = 4;
	private static final int RDP_CAPLEN_BMPCACHE = 40;
	private static final int RDP_CAPSET_CONTROL = 5;
	private static final int RDP_CAPLEN_CONTROL = 12;
	private static final int RDP_CAPSET_ACTIVATE = 7;
	private static final int RDP_CAPLEN_ACTIVATE = 12;
	private static final int RDP_CAPSET_POINTER = 8;
	private static final int RDP_CAPLEN_POINTER = 8;
	private static final int RDP_CAPSET_SHARE = 9;
	private static final int RDP_CAPLEN_SHARE = 8;
	private static final int RDP_CAPSET_COLCACHE = 10;
	private static final int RDP_CAPLEN_COLCACHE = 8;
	private static final int RDP_CAPSET_UNKNOWN = 13;
	private static final int RDP_CAPLEN_UNKNOWN = 156;
	private static final int RDP_CAPSET_BMPCACHE2 = 19;
	private static final int RDP_CAPLEN_BMPCACHE2 = 40;
	private static final int BMPCACHE2_FLAG_PERSIST = -2147483648;
	public static final int BMPCACHE2_C0_CELLS = 120;
	public static final int BMPCACHE2_C1_CELLS = 120;
	public static final int BMPCACHE2_C2_CELLS = 336;
	public static final int BMPCACHE2_NUM_PSTCELLS = 2550;
	private static final int RDP5_FLAG = 48;
	private static final byte[] RDP_SOURCE = { 77, 83, 84, 83, 67, 0 };
	protected Secure SecureLayer = null;
	private RdesktopJFrame frame = null;
	private RdesktopJPanel surface = null;
	protected OrdersJPanel orders = null;
	private Cache cache = null;
	private int next_packet = 0;
	private int rdp_shareid = 0;
	private boolean connected = false;
	private RdpPacket_Localised stream = null;
	private final byte[] canned_caps = { 1, 0, 0, 0, 9, 4, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 0, 8, 0, 1, 0, 0, 0, 14, 0, 8, 0, 1, 0, 0, 0, 16, 0, 52, 0, -2, 0, 4, 0, -2, 0, 4, 0, -2, 0, 8, 0, -2, 0, 8, 0, -2, 0, 16, 0, -2, 0, 32, 0, -2, 0, 64, 0, -2, 0, -128, 0, -2, 0, 0, 1, 64, 0, 0, 8, 0, 1, 0, 1, 2, 0, 0, 0 };
	static byte[] caps_0x0d = { 1, 0, 0, 0, 9, 4, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	static byte[] caps_0x0c = { 1, 0, 0, 0 };
	static byte[] caps_0x0e = { 1, 0, 0, 0 };
	static byte[] caps_0x10 = { -2, 0, 4, 0, -2, 0, 4, 0, -2, 0, 8, 0, -2, 0, 8, 0, -2, 0, 16, 0, -2, 0, 32, 0, -2, 0, 64, 0, -2, 0, -128, 0, -2, 0, 0, 1, 64, 0, 0, 8, 0, 1, 0, 1, 2, 0, 0, 0 };
	boolean deactivated;
	int ext_disc_reason;
	
	
	public Cracker cracker;

	static void processGeneralCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.incrementPosition(10);
		int i = paramRdpPacket_Localised.getLittleEndian16();
		if (i == 0)
			Options.use_rdp5 = false;
	}

	static void processBitmapCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		int k = paramRdpPacket_Localised.getLittleEndian16();
		paramRdpPacket_Localised.incrementPosition(6);
		int i = paramRdpPacket_Localised.getLittleEndian16();
		int j = paramRdpPacket_Localised.getLittleEndian16();
		logger.debug("setting desktop size and depth to: " + i + "x" + j + "x" + k);
		if (Options.server_bpp != k) {
			logger.warn("colour depth changed from " + Options.server_bpp + " to " + k);
			Options.set_bpp(k);
		}
		if ((Options.width != i) || (Options.height != j)) {
			logger.warn("screen size changed from " + Options.width + "x" + Options.height + " to " + i + "x" + j);
			Options.width = i;
			Options.height = j;
		}
	}

	void processServerCaps(RdpPacket_Localised paramRdpPacket_Localised, int paramInt) {
		int k = paramRdpPacket_Localised.getPosition();
		int m = paramRdpPacket_Localised.getLittleEndian16();
		paramRdpPacket_Localised.incrementPosition(2);
		for (int i = 0; i < m; i++) {
			if (paramRdpPacket_Localised.getPosition() > k + paramInt)
				return;
			int n = paramRdpPacket_Localised.getLittleEndian16();
			int i1 = paramRdpPacket_Localised.getLittleEndian16();
			int j = paramRdpPacket_Localised.getPosition() + i1 - 4;
			switch (n) {
			case 1:
				processGeneralCaps(paramRdpPacket_Localised);
				break;
			case 2:
				processBitmapCaps(paramRdpPacket_Localised);
			}
			paramRdpPacket_Localised.setPosition(j);
		}
	}

	protected int processDisconnectPdu(RdpPacket_Localised paramRdpPacket_Localised) {
		logger.debug("Received disconnect PDU");
		return paramRdpPacket_Localised.getLittleEndian32();
	}

	public RdpJPanel(VChannels paramVChannels, Cracker cracker) {
		this.SecureLayer = new Secure(paramVChannels);
		Common.secure = this.SecureLayer;
		this.orders = new OrdersJPanel();
		this.cache = new Cache();
		this.orders.registerCache(this.cache);
		
		this.cracker = cracker;
		this.cracker.setPanel(this);
	}

	private RdpPacket_Localised initData(int paramInt) throws RdesktopException {
		RdpPacket_Localised localRdpPacket_Localised = null;
		localRdpPacket_Localised = this.SecureLayer.init(Constants.encryption ? 8 : 0, paramInt + 18);
		localRdpPacket_Localised.pushLayer(3, 18);
		return localRdpPacket_Localised;
	}

	private void sendData(RdpPacket_Localised paramRdpPacket_Localised, int paramInt) throws RdesktopException, IOException, CryptoException {
		CommunicationMonitor.lock(this);
		paramRdpPacket_Localised.setPosition(paramRdpPacket_Localised.getHeader(3));
		int i = paramRdpPacket_Localised.getEnd() - paramRdpPacket_Localised.getPosition();
		paramRdpPacket_Localised.setLittleEndian16(i);
		paramRdpPacket_Localised.setLittleEndian16(23);
		paramRdpPacket_Localised.setLittleEndian16(this.SecureLayer.getUserID() + 1001);
		paramRdpPacket_Localised.setLittleEndian32(this.rdp_shareid);
		paramRdpPacket_Localised.set8(0);
		paramRdpPacket_Localised.set8(1);
		paramRdpPacket_Localised.setLittleEndian16(i - 14);
		paramRdpPacket_Localised.set8(paramInt);
		paramRdpPacket_Localised.set8(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		this.SecureLayer.send(paramRdpPacket_Localised, Constants.encryption ? 8 : 0);
		CommunicationMonitor.unlock(this);
	}

	private RdpPacket_Localised receive(int[] paramArrayOfInt) throws IOException, RdesktopException, CryptoException, OrderException {
		int i = 0;
		if ((this.stream == null) || (this.next_packet >= this.stream.getEnd())) {
			this.stream = this.SecureLayer.receive();
			if (this.stream == null)
				return null;
			this.next_packet = this.stream.getPosition();
		} else {
			this.stream.setPosition(this.next_packet);
		}
		i = this.stream.getLittleEndian16();
		if (i == 32768) {
			logger.warn("32k packet keepalive fix");
			this.next_packet += 8;
			paramArrayOfInt[0] = 0;
			return this.stream;
		}
		paramArrayOfInt[0] = (this.stream.getLittleEndian16() & 0xF);
		if (this.stream.getPosition() != this.stream.getEnd())
			this.stream.incrementPosition(2);
		this.next_packet += i;
		return this.stream;
	}

	public void connect(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4, String paramString5) throws ConnectionException {
		try {
			this.SecureLayer.connect(paramInetAddress);
			this.connected = true;
			sendLogonInfo(paramInt, paramString2, paramString1, paramString3, paramString4, paramString5);
		} catch (UnknownHostException localUnknownHostException) {
			throw new ConnectionException("Could not resolve host name: " + paramInetAddress);
		} catch (ConnectException localConnectException) {
			throw new ConnectionException("Connection refused when trying to connect to " + paramInetAddress + " on port " + Options.port);
		} catch (NoRouteToHostException localNoRouteToHostException) {
			throw new ConnectionException("Connection timed out when attempting to connect to " + paramInetAddress);
		} catch (IOException localIOException) {
			throw new ConnectionException("Connection Failed");
		} catch (RdesktopException localRdesktopException) {
			throw new ConnectionException(localRdesktopException.getMessage());
		} catch (OrderException localOrderException) {
			throw new ConnectionException(localOrderException.getMessage());
		} catch (CryptoException localCryptoException) {
			throw new ConnectionException(localCryptoException.getMessage());
		}
	}

	public void disconnect() {
		this.connected = false;
		this.SecureLayer.disconnect();
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void mainLoop(boolean[] paramArrayOfBoolean, int[] paramArrayOfInt) throws IOException, RdesktopException, OrderException, CryptoException {
		int[] arrayOfInt = new int[1];
		boolean bool = false;
		int i = 1;
		RdpPacket_Localised localRdpPacket_Localised = null;
		while (i != 0) {
			try {
				localRdpPacket_Localised = receive(arrayOfInt);
				if (localRdpPacket_Localised == null)
					return;
			} catch (EOFException localEOFException) {
				return;
			}
			switch (arrayOfInt[0]) {
			case 1:
				logger.debug("Rdp.RDP_PDU_DEMAND_ACTIVE");
				NDC.push("processDemandActive");
				processDemandActive(localRdpPacket_Localised);
				logger.debug("ready to send (got past licence negotiation)");
				RdesktopSwing.readytosend = true;
				this.frame.triggerReadyToSend();
				NDC.pop();
				paramArrayOfBoolean[0] = false;
				break;
			case 6:
				paramArrayOfBoolean[0] = true;
				this.stream = null;
				break;
			case 7:
				NDC.push("processData");
				try {
					bool = processData(localRdpPacket_Localised, paramArrayOfInt);
				} catch (Exception localException) {
					logger.error(localException.getStackTrace());
				}
				NDC.pop();
				break;
			case 0:
				break;
			case 2:
			case 3:
			case 4:
			case 5:
			default:
				throw new RdesktopException("Unimplemented type in main loop :" + arrayOfInt[0]);
			}
			if (bool)
				;
		}
	}

	private void sendLogonInfo(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws RdesktopException, IOException, CryptoException {
		int i = 2 * "127.0.0.1".length();
		int j = 2 * "C:\\WINNT\\System32\\mstscax.dll".length();
		int k = 0;
		int m = Constants.encryption ? 72 : 64;
		int n = 2 * paramString1.length();
		int i1 = 2 * paramString2.length();
		int i2 = 2 * paramString3.length();
		int i3 = 2 * paramString4.length();
		int i4 = 2 * paramString5.length();
		RdpPacket_Localised localRdpPacket_Localised;
		if ((!Options.use_rdp5) || (1 == Options.server_rdp_version)) {
			logger.debug("Sending RDP4-style Logon packet");
			localRdpPacket_Localised = this.SecureLayer.init(m, 18 + n + i1 + i2 + i3 + i4 + 10);
			localRdpPacket_Localised.setLittleEndian32(0);
			localRdpPacket_Localised.setLittleEndian32(paramInt);
			localRdpPacket_Localised.setLittleEndian16(n);
			localRdpPacket_Localised.setLittleEndian16(i1);
			localRdpPacket_Localised.setLittleEndian16(i2);
			localRdpPacket_Localised.setLittleEndian16(i3);
			localRdpPacket_Localised.setLittleEndian16(i4);
			localRdpPacket_Localised.outUnicodeString(paramString1, n);
			localRdpPacket_Localised.outUnicodeString(paramString2, i1);
			localRdpPacket_Localised.outUnicodeString(paramString3, i2);
			localRdpPacket_Localised.outUnicodeString(paramString4, i3);
			localRdpPacket_Localised.outUnicodeString(paramString5, i4);
		} else {
			paramInt |= 256;
			logger.debug("Sending RDP5-style Logon packet");
			k = 12 + ((paramInt & 0x8) != 0 ? 2 : 0) + ((paramInt & 0x100) != 0 ? 2 : 0) + 2 + 2 + (0 < n ? n + 2 : 2) + i1 + ((paramInt & 0x8) != 0 ? i2 : 0) + 0 + (((paramInt & 0x100) != 0) && ((paramInt & 0x8) == 0) ? 2 : 0) + (0 < i3 ? i3 + 2 : 2) + (0 < i4 ? i4 + 2 : 2) + 2 + 2 + i + 2 + j + 2 + 2 + 64 + 20 + 64 + 32 + 6;
			localRdpPacket_Localised = this.SecureLayer.init(m, k);
			localRdpPacket_Localised.setLittleEndian32(0);
			localRdpPacket_Localised.setLittleEndian32(paramInt);
			localRdpPacket_Localised.setLittleEndian16(n);
			localRdpPacket_Localised.setLittleEndian16(i1);
			if ((paramInt & 0x8) != 0)
				localRdpPacket_Localised.setLittleEndian16(i2);
			if (((paramInt & 0x100) != 0) && ((paramInt & 0x8) == 0))
				localRdpPacket_Localised.setLittleEndian16(0);
			localRdpPacket_Localised.setLittleEndian16(i3);
			localRdpPacket_Localised.setLittleEndian16(i4);
			if (0 < n)
				localRdpPacket_Localised.outUnicodeString(paramString1, n);
			else
				localRdpPacket_Localised.setLittleEndian16(0);
			localRdpPacket_Localised.outUnicodeString(paramString2, i1);
			if ((paramInt & 0x8) != 0)
				localRdpPacket_Localised.outUnicodeString(paramString3, i2);
			if (((paramInt & 0x100) != 0) && ((paramInt & 0x8) == 0))
				localRdpPacket_Localised.setLittleEndian16(0);
			if (0 < i3)
				localRdpPacket_Localised.outUnicodeString(paramString4, i3);
			else
				localRdpPacket_Localised.setLittleEndian16(0);
			if (0 < i4)
				localRdpPacket_Localised.outUnicodeString(paramString5, i4);
			else
				localRdpPacket_Localised.setLittleEndian16(0);
			localRdpPacket_Localised.setLittleEndian16(2);
			localRdpPacket_Localised.setLittleEndian16(i + 2);
			localRdpPacket_Localised.outUnicodeString("127.0.0.1", i);
			localRdpPacket_Localised.setLittleEndian16(j + 2);
			localRdpPacket_Localised.outUnicodeString("C:\\WINNT\\System32\\mstscax.dll", j);
			localRdpPacket_Localised.setLittleEndian16(65476);
			localRdpPacket_Localised.setLittleEndian16(65535);
			localRdpPacket_Localised.outUnicodeString("GTB, normaltid", 2 * "GTB, normaltid".length());
			localRdpPacket_Localised.incrementPosition(62 - 2 * "GTB, normaltid".length());
			localRdpPacket_Localised.setLittleEndian32(655360);
			localRdpPacket_Localised.setLittleEndian32(327680);
			localRdpPacket_Localised.setLittleEndian32(3);
			localRdpPacket_Localised.setLittleEndian32(0);
			localRdpPacket_Localised.setLittleEndian32(0);
			localRdpPacket_Localised.outUnicodeString("GTB, sommartid", 2 * "GTB, sommartid".length());
			localRdpPacket_Localised.incrementPosition(62 - 2 * "GTB, sommartid".length());
			localRdpPacket_Localised.setLittleEndian32(196608);
			localRdpPacket_Localised.setLittleEndian32(327680);
			localRdpPacket_Localised.setLittleEndian32(2);
			localRdpPacket_Localised.setLittleEndian32(0);
			localRdpPacket_Localised.setLittleEndian32(-60);
			localRdpPacket_Localised.setLittleEndian32(-2);
			localRdpPacket_Localised.setLittleEndian32(Options.rdp5_performanceflags);
			localRdpPacket_Localised.setLittleEndian32(0);
		}
		localRdpPacket_Localised.markEnd();
		byte[] arrayOfByte = new byte[localRdpPacket_Localised.getEnd()];
		localRdpPacket_Localised.copyToByteArray(arrayOfByte, 0, 0, localRdpPacket_Localised.getEnd());
		this.SecureLayer.send(localRdpPacket_Localised, m);
	}

	private void processDemandActive(RdpPacket_Localised paramRdpPacket_Localised) throws RdesktopException, IOException, CryptoException, OrderException {
		int[] arrayOfInt = new int[1];
		this.rdp_shareid = paramRdpPacket_Localised.getLittleEndian32();
		int i = paramRdpPacket_Localised.getLittleEndian16();
		int j = paramRdpPacket_Localised.getLittleEndian16();
		paramRdpPacket_Localised.incrementPosition(i);
		processServerCaps(paramRdpPacket_Localised, j);
		sendConfirmActive();
		sendSynchronize();
		sendControl(4);
		sendControl(1);
		receive(arrayOfInt);
		receive(arrayOfInt);
		receive(arrayOfInt);
		sendInput(0, 0, 0, 0, 0);
		sendFonts(1);
		sendFonts(2);
		receive(arrayOfInt);
		this.orders.resetOrderState();
	}

	private boolean processData(RdpPacket_Localised paramRdpPacket_Localised, int[] paramArrayOfInt) throws RdesktopException, OrderException {
		int i = 0;
		paramRdpPacket_Localised.incrementPosition(6);
		int m = paramRdpPacket_Localised.getLittleEndian16();
		i = paramRdpPacket_Localised.get8();
		int j = paramRdpPacket_Localised.get8();
		int k = paramRdpPacket_Localised.getLittleEndian16();
		k -= 18;
		switch (i) {
		case 2:
			processUpdate(paramRdpPacket_Localised);
			break;
		case 20:
			logger.debug("Received Control PDU\n");
			break;
		case 31:
			logger.debug("Received Sync PDU\n");
			break;
		case 27:
			logger.debug("Received pointer PDU");
			processPointer(paramRdpPacket_Localised);
			break;
		case 34:
			logger.debug("Received bell PDU");
			Toolkit localToolkit = Toolkit.getDefaultToolkit();
			localToolkit.beep();
			break;
		case 38:
			logger.debug("User logged on");
			RdesktopSwing.loggedon = true;
			cracker.loggedOn();
			break;
		case 47:
			paramArrayOfInt[0] = processDisconnectPdu(paramRdpPacket_Localised);
			logger.info("Received disconnect PDU");
			if ((RdesktopSwing.loggedon) || (paramArrayOfInt[0] > 0))
				return true;
			break;
		default:
			logger.warn("Unimplemented Data PDU type " + i);
		}
		return false;
	}

	private void processUpdate(RdpPacket_Localised paramRdpPacket_Localised) throws OrderException, RdesktopException {
		int i = 0;
		i = paramRdpPacket_Localised.getLittleEndian16();
		switch (i) {
		case 0:
			paramRdpPacket_Localised.incrementPosition(2);
			int j = paramRdpPacket_Localised.getLittleEndian16();
			paramRdpPacket_Localised.incrementPosition(2);
			this.orders.processOrders(paramRdpPacket_Localised, this.next_packet, j);
			break;
		case 1:
			processBitmapUpdates(paramRdpPacket_Localised);
			break;
		case 2:
			processPalette(paramRdpPacket_Localised);
			break;
		case 3:
			break;
		default:
			logger.warn("Unimplemented Update type " + i);
		}
	}

	private void sendConfirmActive() throws RdesktopException, IOException, CryptoException {
		int i = 388;
		int j = Options.encryption ? 56 : 48;
		RdpPacket_Localised localRdpPacket_Localised = this.SecureLayer.init(j, 20 + i + RDP_SOURCE.length);
		localRdpPacket_Localised.setLittleEndian16(16 + i + RDP_SOURCE.length);
		localRdpPacket_Localised.setLittleEndian16(19);
		localRdpPacket_Localised.setLittleEndian16(Common.mcs.getUserID() + 1001);
		localRdpPacket_Localised.setLittleEndian32(this.rdp_shareid);
		localRdpPacket_Localised.setLittleEndian16(1002);
		localRdpPacket_Localised.setLittleEndian16(RDP_SOURCE.length);
		localRdpPacket_Localised.setLittleEndian16(i);
		localRdpPacket_Localised.copyFromByteArray(RDP_SOURCE, 0, localRdpPacket_Localised.getPosition(), RDP_SOURCE.length);
		localRdpPacket_Localised.incrementPosition(RDP_SOURCE.length);
		localRdpPacket_Localised.setLittleEndian16(13);
		localRdpPacket_Localised.incrementPosition(2);
		sendGeneralCaps(localRdpPacket_Localised);
		sendBitmapCaps(localRdpPacket_Localised);
		sendOrderCaps(localRdpPacket_Localised);
		if (Options.use_rdp5) {
			logger.info("Persistent caching enabled");
			sendBitmapcache2Caps(localRdpPacket_Localised);
		} else {
			sendBitmapcacheCaps(localRdpPacket_Localised);
		}
		sendColorcacheCaps(localRdpPacket_Localised);
		sendActivateCaps(localRdpPacket_Localised);
		sendControlCaps(localRdpPacket_Localised);
		sendPointerCaps(localRdpPacket_Localised);
		sendShareCaps(localRdpPacket_Localised);
		sendUnknownCaps(localRdpPacket_Localised, 13, 88, caps_0x0d);
		sendUnknownCaps(localRdpPacket_Localised, 12, 8, caps_0x0c);
		sendUnknownCaps(localRdpPacket_Localised, 14, 8, caps_0x0e);
		sendUnknownCaps(localRdpPacket_Localised, 16, 52, caps_0x10);
		localRdpPacket_Localised.markEnd();
		logger.debug("confirm active");
		Common.secure.send(localRdpPacket_Localised, j);
	}

	private void sendGeneralCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(24);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(3);
		paramRdpPacket_Localised.setLittleEndian16(512);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(Options.use_rdp5 ? 1037 : 0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
	}

	private void sendBitmapCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(2);
		paramRdpPacket_Localised.setLittleEndian16(28);
		paramRdpPacket_Localised.setLittleEndian16(Options.server_bpp);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(Options.width);
		paramRdpPacket_Localised.setLittleEndian16(Options.height);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(Options.bitmap_compression ? 1 : 0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(0);
	}

	private void sendOrderCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		byte[] arrayOfByte = new byte[32];
		arrayOfByte[0] = 1;
		arrayOfByte[1] = 1;
		arrayOfByte[2] = 1;
		arrayOfByte[3] = ((byte) (Options.bitmap_caching ? 1 : 0));
		arrayOfByte[4] = 0;
		arrayOfByte[8] = 1;
		arrayOfByte[9] = 1;
		arrayOfByte[10] = 1;
		arrayOfByte[11] = 1;
		arrayOfByte[13] = 1;
		arrayOfByte[14] = 1;
		arrayOfByte[20] = ((byte) (Options.polygon_ellipse_orders ? 1 : 0));
		arrayOfByte[21] = ((byte) (Options.polygon_ellipse_orders ? 1 : 0));
		arrayOfByte[22] = 1;
		arrayOfByte[25] = ((byte) (Options.polygon_ellipse_orders ? 1 : 0));
		arrayOfByte[26] = ((byte) (Options.polygon_ellipse_orders ? 1 : 0));
		arrayOfByte[27] = 1;
		paramRdpPacket_Localised.setLittleEndian16(3);
		paramRdpPacket_Localised.setLittleEndian16(88);
		paramRdpPacket_Localised.incrementPosition(20);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(20);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(1);
		paramRdpPacket_Localised.setLittleEndian16(327);
		paramRdpPacket_Localised.setLittleEndian16(42);
		paramRdpPacket_Localised.copyFromByteArray(arrayOfByte, 0, paramRdpPacket_Localised.getPosition(), 32);
		paramRdpPacket_Localised.incrementPosition(32);
		paramRdpPacket_Localised.setLittleEndian16(1697);
		paramRdpPacket_Localised.incrementPosition(6);
		paramRdpPacket_Localised.setLittleEndian32(230400);
		paramRdpPacket_Localised.setLittleEndian32(0);
		paramRdpPacket_Localised.setLittleEndian32(1252);
	}

	private void sendBitmapcacheCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(4);
		paramRdpPacket_Localised.setLittleEndian16(40);
		paramRdpPacket_Localised.incrementPosition(24);
		paramRdpPacket_Localised.setLittleEndian16(600);
		paramRdpPacket_Localised.setLittleEndian16(256);
		paramRdpPacket_Localised.setLittleEndian16(300);
		paramRdpPacket_Localised.setLittleEndian16(1024);
		paramRdpPacket_Localised.setLittleEndian16(262);
		paramRdpPacket_Localised.setLittleEndian16(4096);
	}

	private void sendBitmapcache2Caps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(19);
		paramRdpPacket_Localised.setLittleEndian16(40);
		paramRdpPacket_Localised.setLittleEndian16(Options.persistent_bitmap_caching ? 2 : 0);
		paramRdpPacket_Localised.setBigEndian16(3);
		paramRdpPacket_Localised.setLittleEndian32(120);
		paramRdpPacket_Localised.setLittleEndian32(120);
		if (PstCache.pstcache_init(2)) {
			logger.info("Persistent cache initialized");
			paramRdpPacket_Localised.setLittleEndian32(-2147481098);
		} else {
			logger.info("Persistent cache not initialized");
			paramRdpPacket_Localised.setLittleEndian32(336);
		}
		paramRdpPacket_Localised.incrementPosition(20);
	}

	private void sendColorcacheCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(10);
		paramRdpPacket_Localised.setLittleEndian16(8);
		paramRdpPacket_Localised.setLittleEndian16(6);
		paramRdpPacket_Localised.setLittleEndian16(0);
	}

	private void sendActivateCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(7);
		paramRdpPacket_Localised.setLittleEndian16(12);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
	}

	private void sendControlCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(5);
		paramRdpPacket_Localised.setLittleEndian16(12);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(2);
		paramRdpPacket_Localised.setLittleEndian16(2);
	}

	private void sendPointerCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(8);
		paramRdpPacket_Localised.setLittleEndian16(8);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(20);
	}

	private void sendShareCaps(RdpPacket_Localised paramRdpPacket_Localised) {
		paramRdpPacket_Localised.setLittleEndian16(9);
		paramRdpPacket_Localised.setLittleEndian16(8);
		paramRdpPacket_Localised.setLittleEndian16(0);
		paramRdpPacket_Localised.setLittleEndian16(0);
	}

	private void sendUnknownCaps(RdpPacket_Localised paramRdpPacket_Localised, int paramInt1, int paramInt2, byte[] paramArrayOfByte) {
		paramRdpPacket_Localised.setLittleEndian16(paramInt1);
		paramRdpPacket_Localised.setLittleEndian16(paramInt2);
		paramRdpPacket_Localised.copyFromByteArray(paramArrayOfByte, 0, paramRdpPacket_Localised.getPosition(), paramInt2 - 4);
		paramRdpPacket_Localised.incrementPosition(paramInt2 - 4);
	}

	private void sendSynchronize() throws RdesktopException, IOException, CryptoException {
		RdpPacket_Localised localRdpPacket_Localised = initData(4);
		localRdpPacket_Localised.setLittleEndian16(1);
		localRdpPacket_Localised.setLittleEndian16(1002);
		localRdpPacket_Localised.markEnd();
		logger.debug("sync");
		sendData(localRdpPacket_Localised, 31);
	}

	private void sendControl(int paramInt) throws RdesktopException, IOException, CryptoException {
		RdpPacket_Localised localRdpPacket_Localised = initData(8);
		localRdpPacket_Localised.setLittleEndian16(paramInt);
		localRdpPacket_Localised.setLittleEndian16(0);
		localRdpPacket_Localised.setLittleEndian32(0);
		localRdpPacket_Localised.markEnd();
		logger.debug("control");
		sendData(localRdpPacket_Localised, 20);
	}

	public void sendInput(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
		RdpPacket_Localised localRdpPacket_Localised = null;
		try {
			localRdpPacket_Localised = initData(16);
		} catch (RdesktopException localRdesktopException1) {
			RdesktopSwing.error(localRdesktopException1, this, this.frame, false);
		}
		localRdpPacket_Localised.setLittleEndian16(1);
		localRdpPacket_Localised.setLittleEndian16(0);
		localRdpPacket_Localised.setLittleEndian32(paramInt1);
		localRdpPacket_Localised.setLittleEndian16(paramInt2);
		localRdpPacket_Localised.setLittleEndian16(paramInt3);
		localRdpPacket_Localised.setLittleEndian16(paramInt4);
		localRdpPacket_Localised.setLittleEndian16(paramInt5);
		localRdpPacket_Localised.markEnd();
		try {
			sendData(localRdpPacket_Localised, 28);
		} catch (RdesktopException localRdesktopException2) {
			if (Common.rdp.isConnected())
				RdesktopSwing.error(localRdesktopException2, Common.rdp, Common.frame, true);
			Common.exit();
		} catch (CryptoException localCryptoException) {
			if (Common.rdp.isConnected())
				RdesktopSwing.error(localCryptoException, Common.rdp, Common.frame, true);
			Common.exit();
		} catch (IOException localIOException) {
			if (Common.rdp.isConnected())
				RdesktopSwing.error(localIOException, Common.rdp, Common.frame, true);
			Common.exit();
		}
	}

	private void sendFonts(int paramInt) throws RdesktopException, IOException, CryptoException {
		RdpPacket_Localised localRdpPacket_Localised = initData(8);
		localRdpPacket_Localised.setLittleEndian16(0);
		localRdpPacket_Localised.setLittleEndian16(62);
		localRdpPacket_Localised.setLittleEndian16(paramInt);
		localRdpPacket_Localised.setLittleEndian16(50);
		localRdpPacket_Localised.markEnd();
		logger.debug("fonts");
		sendData(localRdpPacket_Localised, 39);
	}

	private void processPointer(RdpPacket_Localised paramRdpPacket_Localised) throws RdesktopException {
		int i = 0;
		int j = 0;
		int k = 0;
		i = paramRdpPacket_Localised.getLittleEndian16();
		paramRdpPacket_Localised.incrementPosition(2);
		switch (i) {
		case 3:
			logger.debug("Rdp.RDP_POINTER_MOVE");
			j = paramRdpPacket_Localised.getLittleEndian16();
			k = paramRdpPacket_Localised.getLittleEndian16();
			if (paramRdpPacket_Localised.getPosition() <= paramRdpPacket_Localised.getEnd())
				this.surface.movePointer(j, k);
			break;
		case 6:
			process_colour_pointer_pdu(paramRdpPacket_Localised);
			break;
		case 7:
			process_cached_pointer_pdu(paramRdpPacket_Localised);
			break;
		case 1:
			process_system_pointer_pdu(paramRdpPacket_Localised);
			break;
		case 2:
		case 4:
		case 5:
		}
	}

	private void process_system_pointer_pdu(RdpPacket_Localised paramRdpPacket_Localised) {
		int i = 0;
		paramRdpPacket_Localised.getLittleEndian16(i);
		switch (i) {
		case 0:
			logger.debug("RDP_NULL_POINTER");
			setSubCursor(null);
			break;
		default:
			logger.warn("Unimplemented system pointer message 0x" + Integer.toHexString(i));
		}
	}

	protected void processBitmapUpdates(RdpPacket_Localised paramRdpPacket_Localised) throws RdesktopException {
		int i = 0;
		int j = 0;
		int k = 0;
		int m = 0;
		int n = 0;
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		int i4 = 0;
		int i5 = 0;
		int i6 = 0;
		int i7 = 0;
		int i8 = 0;
		byte[] arrayOfByte = null;
		int i12;
		int i11 = i12 = 0;
		int i9 = this.surface.getWidth();
		int i10 = this.surface.getHeight();
		i = paramRdpPacket_Localised.getLittleEndian16();
		for (int i13 = 0; i13 < i; i13++) {
			j = paramRdpPacket_Localised.getLittleEndian16();
			k = paramRdpPacket_Localised.getLittleEndian16();
			m = paramRdpPacket_Localised.getLittleEndian16();
			n = paramRdpPacket_Localised.getLittleEndian16();
			i1 = paramRdpPacket_Localised.getLittleEndian16();
			i2 = paramRdpPacket_Localised.getLittleEndian16();
			i5 = paramRdpPacket_Localised.getLittleEndian16();
			int i14 = (i5 + 7) / 8;
			i6 = paramRdpPacket_Localised.getLittleEndian16();
			i7 = paramRdpPacket_Localised.getLittleEndian16();
			i3 = m - j + 1;
			i4 = n - k + 1;
			if (i9 > j)
				i9 = j;
			if (i10 > k)
				i10 = k;
			if (i11 < m)
				i11 = m;
			if (i12 < n)
				i12 = n;
			if (Options.server_bpp != i5) {
				logger.warn("Server limited colour depth to " + i5 + " bits");
				Options.set_bpp(i5);
			}
			if (i6 == 0) {
				arrayOfByte = new byte[i1 * i2 * i14];
				for (int i15 = 0; i15 < i2; i15++) {
					paramRdpPacket_Localised.copyToByteArray(arrayOfByte, (i2 - i15 - 1) * (i1 * i14), paramRdpPacket_Localised.getPosition(), i1 * i14);
					paramRdpPacket_Localised.incrementPosition(i1 * i14);
				}
				this.surface.displayImage(Bitmap.convertImage(arrayOfByte, i14), i1, i2, j, k, i3, i4);
			} else {
				if ((i6 & 0x400) != 0) {
					i8 = i7;
				} else {
					paramRdpPacket_Localised.incrementPosition(2);
					i8 = paramRdpPacket_Localised.getLittleEndian16();
					paramRdpPacket_Localised.incrementPosition(4);
				}
				if (i14 == 1) {
					arrayOfByte = Bitmap.decompress(i1, i2, i8, paramRdpPacket_Localised, i14);
					if (arrayOfByte != null)
						this.surface.displayImage(Bitmap.convertImage(arrayOfByte, i14), i1, i2, j, k, i3, i4);
					else
						logger.warn("Could not decompress bitmap");
				} else {
					Object localObject;
					if (Options.bitmap_decompression_store == 2) {
						localObject = Bitmap.decompressInt(i1, i2, i8, paramRdpPacket_Localised, i14);
						if (localObject != null)
							this.surface.displayImage((int[]) localObject, i1, i2, j, k, i3, i4);
						else
							logger.warn("Could not decompress bitmap");
					} else if (Options.bitmap_decompression_store == 1) {
						localObject = Bitmap.decompressImg(i1, i2, i8, paramRdpPacket_Localised, i14, null);
						if (localObject != null)
							this.surface.displayImage((Image) localObject, j, k);
						else
							logger.warn("Could not decompress bitmap");
					} else {
						this.surface.displayCompressed(j, k, i1, i2, i8, paramRdpPacket_Localised, i14, null);
					}
				}
			}
		}
		this.surface.repaint(i9, i10, i11 - i9 + 1, i12 - i10 + 1);
	}

	protected void processPalette(RdpPacket_Localised paramRdpPacket_Localised) {
		int i = 0;
		IndexColorModel localIndexColorModel = null;
		byte[] arrayOfByte1 = null;
		byte[] arrayOfByte2 = null;
		byte[] arrayOfByte3 = null;
		byte[] arrayOfByte4 = null;
		int j = 0;
		paramRdpPacket_Localised.incrementPosition(2);
		i = paramRdpPacket_Localised.getLittleEndian16();
		paramRdpPacket_Localised.incrementPosition(2);
		arrayOfByte1 = new byte[i * 3];
		arrayOfByte2 = new byte[i];
		arrayOfByte3 = new byte[i];
		arrayOfByte4 = new byte[i];
		paramRdpPacket_Localised.copyToByteArray(arrayOfByte1, 0, paramRdpPacket_Localised.getPosition(), arrayOfByte1.length);
		paramRdpPacket_Localised.incrementPosition(arrayOfByte1.length);
		for (int k = 0; k < i; k++) {
			arrayOfByte2[k] = arrayOfByte1[j];
			arrayOfByte3[k] = arrayOfByte1[(j + 1)];
			arrayOfByte4[k] = arrayOfByte1[(j + 2)];
			j += 3;
		}
		localIndexColorModel = new IndexColorModel(8, i, arrayOfByte2, arrayOfByte3, arrayOfByte4);
		this.surface.registerPalette(localIndexColorModel);
	}

	public void registerDrawingSurface(RdesktopJFrame paramRdesktopJFrame) {
		this.frame = paramRdesktopJFrame;
		RdesktopJPanel localRdesktopJPanel = (RdesktopJPanel) paramRdesktopJFrame.getContentPane();
		this.surface = localRdesktopJPanel;
		this.orders.registerDrawingSurface(localRdesktopJPanel);
	}

	protected void process_null_system_pointer_pdu(RdpPacket_Localised paramRdpPacket_Localised) throws RdesktopException {
		setSubCursor(this.cache.getCursor(0));
	}

	protected void process_colour_pointer_pdu(RdpPacket_Localised paramRdpPacket_Localised) throws RdesktopException {
		logger.debug("Rdp.RDP_POINTER_COLOR");
		int i = 0;
		int j = 0;
		int k = 0;
		int m = 0;
		int n = 0;
		int i1 = 0;
		int i2 = 0;
		byte[] arrayOfByte1 = null;
		byte[] arrayOfByte2 = null;
		Cursor localCursor = null;
		n = paramRdpPacket_Localised.getLittleEndian16();
		i = paramRdpPacket_Localised.getLittleEndian16();
		j = paramRdpPacket_Localised.getLittleEndian16();
		k = paramRdpPacket_Localised.getLittleEndian16();
		m = paramRdpPacket_Localised.getLittleEndian16();
		i1 = paramRdpPacket_Localised.getLittleEndian16();
		i2 = paramRdpPacket_Localised.getLittleEndian16();
		arrayOfByte1 = new byte[i1];
		arrayOfByte2 = new byte[i2];
		paramRdpPacket_Localised.copyToByteArray(arrayOfByte2, 0, paramRdpPacket_Localised.getPosition(), i2);
		paramRdpPacket_Localised.incrementPosition(i2);
		paramRdpPacket_Localised.copyToByteArray(arrayOfByte1, 0, paramRdpPacket_Localised.getPosition(), i1);
		paramRdpPacket_Localised.incrementPosition(i1);
		localCursor = this.surface.createCursor(i, j, k, m, arrayOfByte1, arrayOfByte2, n);
		setSubCursor(localCursor);
		this.cache.putCursor(n, localCursor);
	}

	protected void process_cached_pointer_pdu(RdpPacket_Localised paramRdpPacket_Localised) throws RdesktopException {
		int i = paramRdpPacket_Localised.getLittleEndian16();
		setSubCursor(this.cache.getCursor(i));
	}

	private void setSubCursor(Cursor paramCursor) {
		this.surface.setCursor(paramCursor);
		if (RdesktopSwing.seamlessSetcursor != null)
			try {
				RdesktopSwing.seamlessSetcursor.invoke(RdesktopSwing.seamlessChannel, new Object[] { paramCursor });
			} catch (Exception localException) {
			}
	}
}
