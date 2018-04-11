package utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jws.Logger;

/**
 * 异步任务提交后台线程处理
 * 比如输出操作日志到db等
 * @author fish
 *
 */
public class ThreadPoolUtils {

	private static Executor threadPool = Executors.newFixedThreadPool(15);
			
		//	new ThreadPoolExecutor(1, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),new ThreadPoolExecutor.CallerRunsPolicy());
	
	public static void sumbit(Runnable runnable){
		try{
			threadPool.execute(runnable);
		}catch(Exception e){
			Logger.error(e, "");
		}
	}
}
