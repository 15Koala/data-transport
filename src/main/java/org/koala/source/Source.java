package org.koala.source;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 所有source的父类。每个source有个队列最为他对外提供数据的buffer
 * @author koala
 *
 */
public abstract class Source implements Callable<String>{
	// 对外供数的缓存
	protected BlockingQueue<String> bufQueuq = new LinkedBlockingQueue<String>(10);
	protected String JobId;
	
	protected String paraJson; // Source 接受的参数
	protected AtomicLong msgNum = new AtomicLong(0);
	
	public long getSourceCounter() {
		return msgNum.get();
	}
	
	public void init(String jobId, String paraJson) {
		this.JobId = jobId;
		this.paraJson = paraJson;
		initMe();
	}
	
	public String getParaJson() {
		return paraJson;
	}

	// 不断的获取数据
	public String get() throws InterruptedException {
		return bufQueuq.take();
	}
	/**
	 * 捞点出来，但不删除
	 * @return
	 */
	public String peek() {
		return bufQueuq.peek();
	}
	
	/**
	 * 获取buffer的大小
	 * @return
	 */
	public int bufsize() {
		return bufQueuq.size();
	}
	
	public String getJobId() {
		return this.JobId;
	}
	
	public abstract void initMe();
}
