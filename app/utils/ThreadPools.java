package utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 线程池
 * 
 * @author junefsh@warthog.cn
 * @createDate 2015年4月29日
 *
 */
public class ThreadPools {
    private static final int DEFAULT_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_MAX_POOL_SIZE = DEFAULT_POOL_SIZE * 5;
    private static final int DEFAULT_QUEUE_SIZE = 20000;
    
    public static ExecutorService getTaskThreadPool(String taskName)
    {
        return getTaskThreadPool(taskName,DEFAULT_POOL_SIZE,DEFAULT_MAX_POOL_SIZE,DEFAULT_QUEUE_SIZE);
    }
    public static ExecutorService getTaskThreadPool(String taskName,int minPool,int maxPool)
    {
        return getTaskThreadPool(taskName,minPool,maxPool,DEFAULT_QUEUE_SIZE);
    }
    
	public static ExecutorService getTaskThreadPool(String taskName,int minPool,int maxPool,int queueSize)
	{
	    return new ThreadPoolExecutor(minPool,
	            maxPool, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize), new DefaultThreadFactory(
                        taskName), new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	/**
	 * 
	 * 线程池工厂
	 * 
	 * @author junefsh@warthog.cn
	 * @createDate 2015年4月29日
	 *
	 */
	static class DefaultThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        DefaultThreadFactory(String preffix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();
            namePrefix = preffix+"-p-" +
                          poolNumber.getAndIncrement() +
                         "-thd-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
