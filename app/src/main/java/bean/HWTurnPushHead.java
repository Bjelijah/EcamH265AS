package bean;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import struct.StructClass;
import struct.StructField;

@StructClass
public class HWTurnPushHead {
	@StructField(order = 0)
	byte flag;
	@StructField(order = 1)
	int dialog_id;
	@StructField(order = 2)
	byte frame_type;
	@StructField(order = 3)
	byte [] reserved = new byte[2];
	public byte getFlag() {
		return flag;
	}
	public void setFlag(byte flag) {
		this.flag = flag;
	}
	public int getDialog_id() throws IOException {
		return LittleEndian2BigEndian32(dialog_id);
	}
	public void setDialog_id(int dialog_id) throws IOException {
		this.dialog_id = BigEndian2LittleEndian32(dialog_id);
	}
	public byte getFrame_type() {
		return frame_type;
	}
	public void setFrame_type(byte frame_type) {
		this.frame_type = frame_type;
	}
	public byte[] getReserved() {
		return reserved;
	}
	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}
	
	
	private final int BigEndian2LittleEndian32(int x) throws IOException {  
		ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asIntBuffer().put(x);
		ByteArrayInputStream bintput = new ByteArrayInputStream(bb.array());
		DataInputStream dIntPut = new DataInputStream(bintput);
		int y = dIntPut.readInt();
		bintput.close();
		dIntPut.close();
		return y;
	}  

	private final int LittleEndian2BigEndian32(int x) throws IOException {
		ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.asIntBuffer().put(x);
		ByteArrayInputStream bintput = new ByteArrayInputStream(bb.array());
		DataInputStream dintput = new DataInputStream(bintput);
		int y = dintput.readInt();
		bintput.close();
		dintput.close();
		return y;
	}
	
}
