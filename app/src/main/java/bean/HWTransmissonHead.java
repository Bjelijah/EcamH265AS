package bean;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import struct.StructClass;
import struct.StructField;


/**
 * 
 * @author cbj
 * head is Little Endian
 * 
 * java is Big Endian
 */

@StructClass
public class HWTransmissonHead {//Little Endian
	@StructField(order = 0)
	byte sync;
	@StructField(order = 1)
	byte version;
	@StructField(order = 2)
	byte flag;
	@StructField(order = 3)
	byte reserved;
	@StructField(order = 4)
	short command;
	@StructField(order = 5)
	short seq;
	@StructField(order = 6)
	int payload_len;
	@StructField(order = 7)
	int reserved2;
	public byte getSync() {
		return sync;
	}
	public void setSync(byte sync) {
		this.sync = sync;
	}
	public byte getVersion() {
		return version;
	}
	public void setVersion(byte version) {
		this.version = version;
	}
	public byte getFlag() {
		return flag;
	}
	public void setFlag(byte flag) {
		this.flag = flag;
	}
	public byte getReserved() {
		return reserved;
	}
	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}
	public short getCommand() throws IOException {
		return LittleEndian2BigEndian16(command);
	}
	public void setCommand(short command) throws IOException {
		this.command = BigEndian2LittleEndian16(command);	
	}
	public short getSeq() {
		return seq;
	}
	public void setSeq(short m_seq) throws IOException {
		this.seq =  BigEndian2LittleEndian16(m_seq);	
	}
	public int getPayload_len() throws IOException {
		return LittleEndian2BigEndian32(payload_len);
	}
	public void setPayload_len(int payload_len) throws IOException {
		this.payload_len = BigEndian2LittleEndian32(payload_len);
	}
	public int getReserved2() {
		return reserved2;
	}
	public void setReserved2(int reserved2) {
		this.reserved2 = reserved2;
	}
	public HWTransmissonHead() {
		super();
		sync = (byte) 0xa5;
		reserved = 0;
		reserved2 = 0;
	}

	private final short BigEndian2LittleEndian16(short x) throws IOException {  
//		Log.i("123", "short="+x+ "  order:"+ByteOrder.nativeOrder().toString());
//		test();
//		return (short) ((x & 0xFF) << 8 | 0xFF & (x >> 8));  

		ByteBuffer bb = ByteBuffer.wrap(new byte[2]);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asShortBuffer().put(x);
		ByteArrayInputStream bintput = new ByteArrayInputStream(bb.array());
		DataInputStream dShortPut = new DataInputStream(bintput);
		short y = dShortPut.readShort();
		bintput.close();
		dShortPut.close();
		return y;
		
	}  
	
	private final short LittleEndian2BigEndian16(short x) throws IOException{
		ByteBuffer bb = ByteBuffer.wrap(new byte[2]);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.asShortBuffer().put(x);
		ByteArrayInputStream bintput = new ByteArrayInputStream(bb.array());
		DataInputStream dShortPut = new DataInputStream(bintput);
		short y = dShortPut.readShort();
		bintput.close();
		dShortPut.close();
		return y;
	}
	
	private final int BigEndian2LittleEndian32(int x) throws IOException {  
//		return (x & 0xFF) << 24 | (0xFF & x >> 8) << 16 | (0xFF & x >> 16) << 8  
//				| (0xFF & x >> 24);  
		
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
//		return (i&0xff)<<24 | (i&0xff00)<<8 | (i&0xff0000)>>8 | (i>>24)&0xff;
		
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



	private void test(){
		int x = 0x01020304;

		ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
		bb.asIntBuffer().put(x);
		String ss_before = Arrays.toString(bb.array());

		System.out.println("默认字节序 " +  bb.order().toString() +  ","  +  " 内存数据 " +  ss_before);

		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asIntBuffer().put(x);
		String ss_after = Arrays.toString(bb.array());

		System.out.println("修改字节序 " + bb.order().toString() +  ","  +  " 内存数据 " +  ss_after);
	}

}
