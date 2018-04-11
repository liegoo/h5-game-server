package moudles.gae.assist;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jws.Logger;

/**
 * 异步任务提交后台线程处理
 * 比如输出操作日志到db等
 * @author fish
 *
 */
public class LotteryTaskThreadPool {

	private static Executor threadPool = Executors.newFixedThreadPool(1);
	
	public static void sumbit(Runnable runnable){
		try{
			threadPool.execute(runnable);
		}catch(Exception e){
			Logger.error(e, "");
		}
	}
}
