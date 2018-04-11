package utils;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;

public class CheckSum {
	 
	public static byte[] calc(byte[] bs){
		int sum = 0;
		for (int i=0; i<bs.length; i+=2){
			sum +=  (((bs[i] & 0xff) << 8) | (bs[i+1] & 0xff << 0));
		}
		
		while (sum > 0xffff){
			sum = (sum & 0xffff) + (sum >>> 16);
		}

		sum = ~sum;
		byte bytes[] = new byte[2];
		bytes[0] = (byte)((sum >>> 8) & 0xff);
		bytes[1] = (byte)(sum & 0xff);
		return bytes;
	}

	public static void main(String[] args) {
		byte[] bs = {0x11, 0x09, 0x07, 0x40};
		// TODO Auto-generated method stub
		byte[] sum = CheckSum.calc(bs);
		System.out.println(sum[0]);
		System.out.println(sum[1]);

		byte[] total = new byte[bs.length + sum.length];
		for (int i=0; i<bs.length; i++){
			total[i] = bs[i];
		}
		for (int i=0; i<sum.length; i++){
			total[bs.length + i] = sum[i];
		}
		byte[] last = CheckSum.calc(total);
		System.out.println(last[0]);
		System.out.println(last[1]);
	}

}
