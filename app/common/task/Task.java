package common.task;

/**
 * 任务抽象类，每个需要执行的任务必须继承该类
 * @author chenxx
 *
 */
public abstract class Task implements Runnable {

	private long time;
	
	private long start;
	
	private boolean enabled;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}
	
}
