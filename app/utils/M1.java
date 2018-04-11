package utils;

import java.util.Random;

public class M1 {

	//16个加密数
	private static final int [] ENCODE_KEY = {0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
		0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7};
	
	//加密
	public static final byte[] encode(byte[] aSrc) {
		int[] sKey = ENCODE_KEY;

		int[] sMask = new int[8];
		System.arraycopy(sKey, 0, sMask, 0, 8);

		int[] sInc = new int[8];
		System.arraycopy(sKey, 8, sInc, 0, 8);

		byte[] sRnd = new byte[8];
		int sRand = new Random(System.currentTimeMillis()).nextInt();
		sRnd[0] = (byte) (sRand >> 24 & 0xFF);
		sRnd[1] = (byte) (sRand >> 16 & 0xFF);
		sRnd[2] = (byte) (sRand >> 8 & 0xFF);
		sRnd[3] = (byte) (sRand & 0xFF);

		sRnd[4] = (byte) ((sRnd[0] + 87) % 256);
		sRnd[5] = (byte) ((sRnd[1] + 29) % 256);
		sRnd[6] = (byte) ((sRnd[2] + 171) % 256);
		sRnd[7] = (byte) ((sRnd[3] + 148) % 256);

		int sLength = aSrc.length;
		byte[] sDest = new byte[sLength + 18];
		
		//jhw
		sDest[0] = 106;
		sDest[1] = 104;
		sDest[2] = 119;
		sDest[3] = 0;
		
		//版本信息
		sDest[4] = 0;
		sDest[5] = 0;
		sDest[6] = 1;
		sDest[7] = 1;
		
		//冗余位
		sDest[8] = 0;
		sDest[9] = 0;
		sDest[10] = 0;
		sDest[11] = 0;
		
		//数据
		sDest[12] = sRnd[0];
		sDest[13] = sRnd[1];
		sDest[14] = sRnd[2];
		sDest[15] = sRnd[3];

		int sMaskCheck = 0;

		for (int j = 0; j < sLength; j++) {
			if (j % 8 == 0) {
				sMask[0] = ((sMask[0] + sInc[0] + sRnd[0]) % 256);
				sMask[1] = ((sMask[1] + sInc[1] + sRnd[1]) % 256);
				sMask[2] = ((sMask[2] + sInc[2] + sRnd[2]) % 256);
				sMask[3] = ((sMask[3] + sInc[3] + sRnd[3]) % 256);
				sMask[4] = ((sMask[4] + sInc[4] + sRnd[4]) % 256);
				sMask[5] = ((sMask[5] + sInc[5] + sRnd[5]) % 256);
				sMask[6] = ((sMask[6] + sInc[6] + sRnd[6]) % 256);
				sMask[7] = ((sMask[7] + sInc[7] + sRnd[7]) % 256);
			}

			int sTempA = aSrc[j] & 0xFF;
			int sTempB = sTempA ^ sMask[(j % 8)];
			sDest[(16 + j)] = (byte) (sTempB & 0xFF);
			sMaskCheck ^= sTempA;
//			
//			System.out.println("encode,\tsmark: "+ sMask[(j % 8)] + "\tsTempb" + sTempB + ",\taSrc:"+ aSrc[j] + ",\t dest:" + sDest[j+16] + ",\tj:" +j);
		}

		sDest[(16 + sLength)] = (byte) (0xFF & (sMaskCheck ^ sMask[0]));
		sDest[(16 + sLength + 1)] = (byte) (0xFF & (sMaskCheck ^ sMask[1]));

		return sDest;
	}

	//解密
	public static final int decode(byte[] aSrc, StringBuffer dest) {
		if (aSrc == null) {
			return -1;
		}
		int sLength = aSrc.length;
		
		byte[] bSrc = new byte[sLength];
		
		System.arraycopy(aSrc, 0, bSrc, 0, sLength);

		if ((sLength < 18) || (bSrc[0] != 106) || (bSrc[1] != 104) || (bSrc[2] != 119)) {
			return -2;
		}

		int[] sKey = ENCODE_KEY;

		int[] sMask = new int[8];
		System.arraycopy(sKey, 0, sMask, 0, 8);

		int[] sInc = new int[8];
		System.arraycopy(sKey, 8, sInc, 0, 8);

		byte[] rnd = new byte[8];
		rnd[0] = bSrc[12];
		rnd[1] = bSrc[13];
		rnd[2] = bSrc[14];
		rnd[3] = bSrc[15];

		rnd[4] = (byte) ((rnd[0] + 87) % 256);
		rnd[5] = (byte) ((rnd[1] + 29) % 256);
		rnd[6] = (byte) ((rnd[2] + 171) % 256);
		rnd[7] = (byte) ((rnd[3] + 148) % 256);

		int sMaskCheck = 0;

		for (int j = 16; j < sLength - 2; j++) {
			if (j % 8 == 0) {
				sMask[0] = ((sMask[0] + sInc[0] + rnd[0]) % 256);
				sMask[1] = ((sMask[1] + sInc[1] + rnd[1]) % 256);
				sMask[2] = ((sMask[2] + sInc[2] + rnd[2]) % 256);
				sMask[3] = ((sMask[3] + sInc[3] + rnd[3]) % 256);
				sMask[4] = ((sMask[4] + sInc[4] + rnd[4]) % 256);
				sMask[5] = ((sMask[5] + sInc[5] + rnd[5]) % 256);
				sMask[6] = ((sMask[6] + sInc[6] + rnd[6]) % 256);
				sMask[7] = ((sMask[7] + sInc[7] + rnd[7]) % 256);
			}
			
			int sTempA = bSrc[j];
			int sTempB = sTempA ^ sMask[(j % 8)];
			bSrc[j] = (byte) (sTempB & 0xFF);
			sMaskCheck ^= sTempB;
//			
//			System.out.println("decode,\tsmask:" + sMask[(j % 8)] +",\ttmpB:" + sTempB + ", aSrc:"+ aSrc[j] + ", dest:" +bSrc[j] + ",j:" +j);
		}

		if ((bSrc[(sLength - 2)] != (byte) (0xFF & (sMaskCheck ^ sMask[0])))
				|| (bSrc[(sLength - 1)] != (byte) (0xFF & (sMaskCheck ^ sMask[1])))) {
			return -3;
		}
		
		dest.setLength(0);
		dest.append(new String(bSrc, 16, bSrc.length - 18));
		return 0;
	}
	
	public static boolean isEncoded(byte[] aSrc)
	{
		if(aSrc == null) {
			return false;
		}
		
		if(aSrc.length < 2) {
			return false;
		}
		
		if((byte) aSrc[0] == 106 && (byte) aSrc[1] == 104 && (byte) aSrc[2] == 119) {
			return true;
		}

		return false;
	}
	
}
