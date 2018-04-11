package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import jws.Logger;


/**
 * http工具类
 * 
 * @author lox@warthog.cn
 * @createDate 2015年5月7日
 * 
 */
public class HttpUtil {
	
	/**
	 * 下载文件
	 * @param httpUrl	URL
	 * @param saveFile	本地存储位置
	 * @return
	 */
	public static boolean download(String httpUrl,String saveFile){
        // 下载网络文件
        int byteread = 0;

        URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e) {
			Logger.error(e, "");
			return false;
		}

		URLConnection conn = null;
		InputStream input = null;
		OutputStream output = null;
        try {
        	conn = url.openConnection();
            input = conn.getInputStream();
            output = new FileOutputStream(saveFile);

            byte[] buffer = new byte[4096];
            while ((byteread = input.read(buffer)) != -1) {
                output.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            Logger.error(e, "");
            return false;
        } catch (IOException e) {
        	Logger.error(e, "");
            return false;
        } finally {
        	IOUtils.closeQuietly(input);
        	IOUtils.closeQuietly(output);
        }
    }
	
}
