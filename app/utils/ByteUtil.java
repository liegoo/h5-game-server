package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import jws.Logger;
import constants.GlobalConstants;
/**
 * 字节相关工具类
 * @author chenxx
 *
 */
public class ByteUtil {

	/**
	 * 将字节数组转换成对应的二进制数组
	 * @param b 字节数组
	 * @return 二进制数组
	 */
	public static boolean[] getBinaryFromByte(byte[] b) {
		boolean[] binary = new boolean[b.length * 8];
		int offset = 0;
		for(int i=0; i<b.length; i++) {
			boolean[] tmpBin = getBinaryFromByte(b[i]);
			System.arraycopy(tmpBin, 0, binary, offset, tmpBin.length);
			offset += tmpBin.length;
		}
		return binary;
	}
	
	/**
	 * 将字节转换成对应的二进制数组
	 * @param b 字节
	 * @return 二进制数组
	 */
	public static boolean[] getBinaryFromByte(byte b) {
		boolean[] binary = new boolean[8];
		for (int i=0; i<8; i++){
			binary[i] = pos(b,i);
		}
		return binary;
	}
	
	/**
	 * 获取单个字节某个位上的值，从左边算起，编号分别为0-7
	 * @param b 字节
	 * @param pos 位置
	 * @return
	 */
	public static boolean pos(byte b, int pos) {
		byte tmp = 0x01;
		tmp = (byte) (tmp << (8 - pos - 1));
		return (b & tmp) == tmp;
	}
	
	/**
	 * 截取二进制数组，截取后转换为数字
	 * @param bs 二进制数组
	 * @param begin 截取位置
	 * @param count 截取位数
	 * @return 
	 */
	public static int subBinary(boolean[] bs, int begin, int count) {
		int sum = 0;
		int i = 0;
		while (true) {
			if (bs[begin + i]) {
				sum += 1;
			}
			if (i == count - 1) {
				break;
			} else {
				sum = sum << 1;
				i++;
			}
		}
		return sum;
	}
	
	/**
	 * 截取二进制数组，截取后转换为数字
	 * @param bs 字节数组
	 * @param begin 截取位置
	 * @param count 截取位数
	 * @return 
	 */
	public static int subBinary(byte[] bs, int begin, int count) {
		return subBinary(getBinaryFromByte(bs), begin, count);
	}
	
	/**
	 * 将二进制流转换成字符串
	 * @param is
	 * @return
	 * @author chenxx
	 */
	public static String toString(InputStream is) {
		try {
			int size = is.available();
		    char[] theChars = new char[size];
		    byte[] bytes = new byte[size];

		    is.read(bytes, 0, size);
		    for (int i = 0; i < size;) {
		        theChars[i] = (char)(bytes[i++]&0xff);
		    }
		    
		    return new String(theChars);
		} catch (Exception e) {
			Logger.error(e, "byte array input stream convert to string failed");
		}
		return null;
	}
	/**
	 * 把一个数字value右移cnt位后的字节表达
	 * 如：3右移2位后为：11000000，如：1右移8位后是：00000001
	 * @param value
	 * @param cnt
	 * @return
	 */
	public static byte valueRightShift(int value, int cnt){
	    byte bit4 = (byte) 0x00; 
        for (int i=0; i<cnt; i++){
            int mod = value % 2;
            bit4 = (byte) (bit4 >>> 1);
            if (mod == 1){
                bit4 = (byte) (bit4 | 0x80);
            }else{
                bit4 = (byte) (bit4 & 0x7f);
            }
            value /= 2;
        }
        return bit4;
	}
	public static byte[] subBytes(byte[] original, int offset, int size){
		if (offset >= 0 && size > 0 && (offset+size) <= original.length){
			byte[] result = new byte[size];
			for (int i=0; i<size; i++){
				result[i] = original[i+offset];
			}
			return result;
		}
		return null;
	}
}
