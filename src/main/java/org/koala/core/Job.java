package org.koala.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.koala.source.Source;

import com.admaster.data.metrics.utils.TimeUtil;

import org.koala.sink.Sink;


public class Job {
	
	public String getSourceClassName() {
		return sourceClassName;
	}

	public void setSourceClassName(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}

	public String getSinkClassName() {
		return sinkClassName;
	}

	public void setSinkClassName(String sinkClassName) {
		this.sinkClassName = sinkClassName;
	}

	public int getSinkNum() {
		return sinkNum;
	}

	public void setSinkNum(int sinkNum) {
		this.sinkNum = sinkNum;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	public String getSourcePara() {
		return sourcePara;
	}

	public void setSourcePara(String sourcePara) {
		this.sourcePara = sourcePara;
	}

	public String getSinkPara() {
		return sinkPara;
	}

	public void setSinkPara(String sinkPara) {
		this.sinkPara = sinkPara;
	}
	
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	public JobStatus getJobStatus() {
		return this.jobStatus;
	}

	private String sourceClassName;
	private String sinkClassName;
	private int sinkNum = 1;
	private String jobId;
	private String sourcePara;
	private String sinkPara;
	private JobStatus jobStatus = JobStatus.ACCEPT;
	private Source source;
	private List<Sink> sinks;
	private FutureTask<String> sourceF;
	private List<FutureTask<String>> sinksF;
	
	// 释放所有资源，进入完成的状态
	public void release() {
		sinks = null;
		sourceF = null;
		sinksF = null;
		stoppedTime = TimeUtil.getCurDatetime();
	}
	
	public FutureTask<String> getSourceFuture() {
		return sourceF;
	}
	
	public void setSourceFuture(FutureTask<String> sourceF) {
		this.sourceF = sourceF;
	}
	
	public List<FutureTask<String>> getSinksFuture() {
		return sinksF;
	}
	
	public void setSinksFuture(List<FutureTask<String>> sinksF) {
		 this.sinksF = sinksF;
	}
	
	private String createdTime = TimeUtil.getCurDatetime();
	private String stoppedTime = null;
	
	public Source getSource() {
		return source;
	}
	
	public List<Sink> getSinks() {
		return sinks;
	}
	
	// 试管采样
	public String peekOne() {
		return source.peek();
	}
	
	public long getSourceCounter() {
		return source.getSourceCounter();
	}
	
	public int getSourceBufSize() {
		return source.bufsize();
	}
	
	public String getCreatedTime() {
		return createdTime;
	}
	
	public String getStoppedTime() {
		return stoppedTime;
	}
	
	public void setStoppedTime(String stoppedTime) {
		 this.stoppedTime = stoppedTime;
	}
	
	public void initComponents() throws Exception {
		source = (Source) Class.forName(sourceClassName).newInstance();
		source.init(jobId, sourcePara);
		sinks = new ArrayList<Sink>();
		for(int i=0;i<sinkNum;i++) {
			Sink s = (Sink) Class.forName(sinkClassName).newInstance();
			s.init(jobId, sinkPara, source);
			sinks.add(s);
		}
	}
	
	public FutureTask<String> addSink() throws Exception {
		Sink s = (Sink) Class.forName(sinkClassName).newInstance();
		s.init(jobId, sinkPara, source);
		sinks.add(s);
		return new FutureTask<String>(s);
	}
	
	public FutureTask<String> delSink() {
		int size = sinks.size();
		sinks.remove(size-1);
		return sinksF.remove(size-1);
	}

}
