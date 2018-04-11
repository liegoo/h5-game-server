package utils;

import jws.Logger;

public class KeyPointLogUtil {

	/**
	 * 记录关键点日志
	 * @param message
	 * @param args
	 */
	public static void log(String message,Object... args){
		Logger.event("keypoint_event", message,args);
	}
}
