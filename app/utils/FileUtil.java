package utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import jws.Logger;

public class FileUtil {
    private static Random random = new Random();

	/**
	 * 将文件对象转换为byte数组
	 * @param file
	 * @return
	 */
	public static byte[] fileToBytes(File file) {
		
		BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        byte[] data = new byte[0];
        
        try {
        	in = new BufferedInputStream(new FileInputStream(file));
        	out = new ByteArrayOutputStream(1024);
	        byte[] temp = new byte[1024];        
	        int size = 0;        
	        while ((size = in.read(temp)) != -1) {        
	            out.write(temp, 0, size);        
	        }
	        data = out.toByteArray();
        } catch(Exception e) {
        	Logger.error(e, "file to bytes failed");
        } finally {
        	try {
				in.close();
				out.close();
			} catch (IOException e) {
				Logger.error(e, "file stream close failed");
			}
        }
        return data;
	}
	
	/**
	 * 获取文件扩展名
	 * @param file
	 * @return
	 */
	public static String getExt(File file) {
		String name = file.getName();
		String[] names = name.split("\\.");
		if(names.length > 1) {
			return names[names.length - 1];
		}
		return "";
	}
	
	public static String getPathByDate(int uid,String suffix){
	    String randomMD5 = EncryptionUtil.md5(
                String.valueOf(random.nextInt(10000000)) + System.currentTimeMillis())
                .substring(7, 23);
        String yy = String.valueOf(new Date().getYear() + 1900).substring(2, 4);
        int m = new Date().getMonth() + 1;
        String mm = "";
        if (m < 10) {
            mm = "0" + m;
        } else {
            mm = String.valueOf(m);
        }
        int d = new Date().getDate();
        String dd = "";
        if (d < 10) {
            dd = "0" + d;
        } else {
            dd = String.valueOf(d);
        }
        String date = yy + "/" + mm + "/" + dd;
        return "/" + uid + "/" + date + "/" + randomMD5 + suffix;
	}
}
