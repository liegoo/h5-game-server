package common.core;

import jws.Jws;
import jws.Logger;
import jws.libs.Codec;
import utils.ByteUtil;
import utils.CheckSum;
import utils.WhCrypto;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageHeader {
	public static String constProtocolName = "WHMP";
	private String protocolName = constProtocolName;
    private int protocolVersion = Integer.parseInt(Jws.configuration.getProperty("whmp.protocolVersion", "1").trim());
    private int compressMethod = Integer.parseInt(Jws.configuration.getProperty("whmp.compressMethod", "1").trim());
    private int encryptedMethod = Integer.parseInt(Jws.configuration.getProperty("whmp.encryptedMethod", "1").trim());
    
    private int dataLength = 0;
    private MessageHeader(){
    }
    
    public MessageHeader(int dataLength){
        this.dataLength = dataLength;
    }
    public static MessageHeader parse(byte[] header) throws Exception{
        if (Logger.isDebugEnabled()){
            Logger.debug("ready to parse header(before): %s", Arrays.toString(header));
        }

    	MessageHeader headerObj = new MessageHeader();
    	byte[] nameBytes = new byte[]{header[0], header[1], header[2], header[3]};
    	
    	if (!(new String(nameBytes)).equals(constProtocolName)){
    		WhCrypto.decryptBlock(header);
    		nameBytes = new byte[]{header[0], header[1], header[2], header[3]};
            if (Logger.isDebugEnabled()){
                Logger.debug("ready to parse header(after): %s", Arrays.toString(header));
            }
    	}

    	if (!checkValid(header)){
    		throw new Exception("check header: invalid");
    	}
    	try {
			headerObj.setProtocolName(new String(nameBytes, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			headerObj.setProtocolName("WHMP");
		}

        headerObj.setProtocolVersion(ByteUtil.subBinary(header, 32, 4));
        headerObj.setCompressMethod(ByteUtil.subBinary(header, 36, 4));
        headerObj.setEncryptedMethod(ByteUtil.subBinary(header, 40, 4));

        int tmp1 = (header[4*3]&0xFF)<<24;
        int tmp2 = (header[4*3+1]&0xFF)<<16;
        int tmp3 = (header[4*3+2]&0xFF)<<8;
        int tmp4 = (header[4*3+3]&0xFF);
        
        headerObj.setDataLength(tmp1 | tmp2 | tmp3 | tmp4);
        
        return headerObj;
    }
    private static boolean checkValid(byte[] header) {
    	byte[] sum = CheckSum.calc(header);
		return (sum[0] == 0x00) && (sum[1] == 0x00);
	}
	public String getProtocolName() {
		return protocolName;
	}
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	public int getEncryptedMethod() {
		return encryptedMethod;
	}
	public void setEncryptedMethod(int encryptedMethod) {
		this.encryptedMethod = encryptedMethod;
	}
	public int getCompressMethod() {
		return compressMethod;
	}
	public void setCompressMethod(int compressMethod) {
		this.compressMethod = compressMethod;
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	@Override
    public String toString(){
    	return String.format("message header{name:%s, ver:%d, enc:%d, compress:%d, len:%d}", 
                    protocolName, protocolVersion, encryptedMethod, compressMethod, dataLength);
    }
    public byte[] toBytes(){
    	byte reserve = ByteUtil.valueRightShift(0, 8);

    	byte[] name = protocolName.getBytes();
    	
        byte protocolVersionByte = (byte)(((byte)protocolVersion) << 4);
        byte compressMethodByte = (byte)((byte)compressMethod);
        byte byte1 = (byte) (protocolVersionByte | compressMethodByte);
        
        byte encryptedMethodByte = (byte)(((byte)encryptedMethod) << 4);
        byte byte2 = (byte) (encryptedMethodByte | reserve);
        

        byte[] checksum = new byte[2];
        checksum[0] = 0x00;
        checksum[1] = 0x00;
        
        byte[] lengthBytes = new byte[4];
        lengthBytes = ByteBuffer.allocate(4).putInt(dataLength).array();
        
        byte[] tmpHeader = new byte[]{
        		name[0], name[1], name[2], name[3], 
        		byte1, byte2, checksum[0], checksum[1], 
        		reserve, reserve, reserve, reserve, 
        		lengthBytes[0], lengthBytes[1], lengthBytes[2], lengthBytes[3]
        };
        checksum = CheckSum.calc(tmpHeader);
        byte[] finalHeader = new byte[]{
        		name[0], name[1], name[2], name[3], 
        		byte1, byte2, checksum[0], checksum[1], 
        		reserve, reserve, reserve, reserve, 
        		lengthBytes[0], lengthBytes[1], lengthBytes[2], lengthBytes[3]
        	};
        
        if (Logger.isDebugEnabled()){
            Logger.debug("header to bytes(before): %s", Arrays.toString(finalHeader));
        }

        if (isEncrypted()){
        	WhCrypto.encryptBlock(finalHeader);
        }
        
        if (Logger.isDebugEnabled()){
            Logger.debug("header to bytes(after): %s", Arrays.toString(finalHeader));
        }
        
        return finalHeader;
    }
    
    public int getProtocolVersion() {
        return protocolVersion;
    }
    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    public static void main(String[] args) throws Exception{
        MessageHeader header = new MessageHeader(2);
        header.setProtocolName("WHMP");
        header.setProtocolVersion(1);
        header.setCompressMethod(12);
        header.setEncryptedMethod(9);
        header.setDataLength(200000001);

        byte[] bs = header.toBytes();
        System.out.println(Codec.byteToHexString(bs));
        
        header = MessageHeader.parse(bs);
        System.out.println(header.getProtocolName());
        System.out.println(header.getProtocolVersion());
        System.out.println(header.getCompressMethod());
        System.out.println(header.getEncryptedMethod());
        System.out.println(header.getDataLength());
        
//        FileInputStream fis = new FileInputStream("/Users/eason/Documents/checksum.dat");
//        byte[] bs2 = new byte[16];
//        fis.read(bs2, 0, 16);
//        
//        MessageHeader header = MessageHeader.parse(bs2);
//        System.out.println(header.getCompressMethod());
//        System.out.println(header.getDataLength());
//        System.out.println(header.getEncryptedMethod());
//        System.out.println(header.getProtocolVersion());
//        System.out.println(header.getProtocolName());
    }
	public boolean isEncrypted() {
		return encryptedMethod > 0;
	}
	public boolean isCompressed() {
		return compressMethod > 0;
	}
}
