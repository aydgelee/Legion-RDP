package com.lixia.rdp.rdp5;

import com.lixia.rdp.OrderException;
import com.lixia.rdp.RdesktopException;
import com.lixia.rdp.RdpJPanel;
import com.lixia.rdp.RdpPacket_Localised;
import com.lixia.rdp.crypto.CryptoException;
import com.redpois0n.Cracker;

public class Rdp5JPanel extends RdpJPanel {
	private VChannels channels;

	public Rdp5JPanel(VChannels paramVChannels, Cracker cracker) {
		super(paramVChannels, cracker);
		this.channels = paramVChannels;
	}

	public void rdp5_process(RdpPacket_Localised paramRdpPacket_Localised, boolean paramBoolean) throws RdesktopException, OrderException, CryptoException {
		rdp5_process(paramRdpPacket_Localised, paramBoolean, false);
	}

	public void rdp5_process(RdpPacket_Localised paramRdpPacket_Localised, boolean paramBoolean1, boolean paramBoolean2) throws RdesktopException, OrderException, CryptoException {
		if (paramBoolean1) {
			paramRdpPacket_Localised.incrementPosition(8);
			byte[] arrayOfByte1 = new byte[paramRdpPacket_Localised.size() - paramRdpPacket_Localised.getPosition()];
			paramRdpPacket_Localised.copyToByteArray(arrayOfByte1, 0, paramRdpPacket_Localised.getPosition(), arrayOfByte1.length);
			byte[] arrayOfByte2 = this.SecureLayer.decrypt(arrayOfByte1);
			paramRdpPacket_Localised.copyFromByteArray(arrayOfByte2, 0, paramRdpPacket_Localised.getPosition(), arrayOfByte2.length);
		}
		while (paramRdpPacket_Localised.getPosition() < paramRdpPacket_Localised.getEnd()) {
			int k = paramRdpPacket_Localised.get8();
			if ((k & 0x80) > 0)
				k ^= 128;
			int i = paramRdpPacket_Localised.getLittleEndian16();
			int m = paramRdpPacket_Localised.getPosition() + i;
			switch (k) {
			case 0:
				int j = paramRdpPacket_Localised.getLittleEndian16();
				this.orders.processOrders(paramRdpPacket_Localised, m, j);
				break;
			case 1:
				paramRdpPacket_Localised.incrementPosition(2);
				processBitmapUpdates(paramRdpPacket_Localised);
				break;
			case 2:
				paramRdpPacket_Localised.incrementPosition(2);
				processPalette(paramRdpPacket_Localised);
				break;
			case 3:
				break;
			case 5:
				process_null_system_pointer_pdu(paramRdpPacket_Localised);
				break;
			case 6:
				break;
			case 9:
				process_colour_pointer_pdu(paramRdpPacket_Localised);
				break;
			case 10:
				process_cached_pointer_pdu(paramRdpPacket_Localised);
				break;
			case 4:
			case 7:
			case 8:
			default:
				logger.warn("Unimplemented RDP5 opcode " + k);
			}
			paramRdpPacket_Localised.setPosition(m);
		}
	}

	void rdp5_process_channel(RdpPacket_Localised paramRdpPacket_Localised, int paramInt) {
		VChannel localVChannel = this.channels.find_channel_by_channelno(paramInt);
		if (localVChannel != null)
			try {
				localVChannel.process(paramRdpPacket_Localised);
			} catch (Exception localException) {
			}
	}
}
