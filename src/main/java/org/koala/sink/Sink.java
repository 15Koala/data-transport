package org.koala.sink;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.koala.source.Source;

public abstract class Sink implements Callable<String>{
	
	protected Source source;
	protected String JobId;
	protected String paraJson;
	
	public void init(String jobId, String paraJson, Source source) {
		this.JobId = jobId;
		this.paraJson = paraJson;
		this.source = source;
	}
	
	public String paraJson() {
		return this.paraJson;
	}
	
	public String getJobId() {
		return this.JobId;
	}

	// 消费数据的业务逻辑
	public abstract void initMe();
}
