package org.koala.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.koala.sink.Sink;
import org.koala.source.Source;
import org.koala.util.MD5Util;
import org.koala.util.TimeUtil;

import net.sf.json.JSONObject;

/**
 * 任务的管理
 * @author koala
 *
 */
public class JobSubmit {

	private static ConcurrentHashMap<String, Job> doingJobs = new ConcurrentHashMap<String, Job>();
	//private static ConcurrentHashMap<String, Job> stoppedJobs = new ConcurrentHashMap<String, Job>();

	private static int STOPPEDJOBSCACHENUM = 10;
	private static HashMap<String, Job> stoppedJobs = new LinkedHashMap<String, Job>(STOPPEDJOBSCACHENUM + 1, .75F, false) {
		private static final long serialVersionUID = -1L;
		protected boolean removeEldestEntry(Map.Entry<String, Job> eldest) {
           return size() > STOPPEDJOBSCACHENUM;
        }
     };

	private static ExecutorService executor4Source = Executors.newCachedThreadPool();
	private static ExecutorService executor4Sink = Executors.newCachedThreadPool();

	private static JobSubmit single = new JobSubmit();
	
	private static Logger logger = Logger.getLogger(JobSubmit.class);

	static {
		JobManager jm = new JobManager();
		jm.start();
	}
	
	private JobSubmit() {}
	
	public static JobSubmit getJobSubmit() {
		return single;
	}
	
	/**
	 * jobId == null 说明任务提交失败了，初始化或者启动线程时就失败了
	 * @param job
	 * @return
	 */
	public String submit(Job job) {
		// 二阶段提交
		String jobId = null;
		if(job != null) 
		try {
			// 先分配一个jobId
			jobId = TimeUtil.getCurrentTs() + "_" + MD5Util.textToMD5U32(TimeUtil.getCurrentTs() + job.toString());
			job.setJobId(jobId);
			logger.info("job " + jobId + " accepted.");
			job.initComponents();
			// 初始化完毕进入READY状态
			job.setJobStatus(JobStatus.READY);
			// 启动source, 并注册到doingJobsSourceFuture
			Source source = job.getSource();
			FutureTask<String> futureTaskSource = new FutureTask<String>(source);
			executor4Source.submit(futureTaskSource);
			job.setSourceFuture(futureTaskSource);

			// 启动sink, 并注册到doingJobsSinkFuture
			List<Sink> sinks = job.getSinks();
			List<FutureTask<String>> sinkF = new ArrayList<FutureTask<String>>();
			for (Sink sink : sinks) {
				FutureTask<String> futureTaskSink = new FutureTask<String>(sink);
				executor4Sink.submit(futureTaskSink);
				sinkF.add(futureTaskSink);
			}
			job.setSinksFuture(sinkF);
			doingJobs.put(jobId, job);
			// 启动了线程，进入DOING状态
			job.setJobStatus(JobStatus.DOING);
			logger.info("job " + jobId + " is doing.");
		} catch (Exception e) {
			logger.error("submit job faild and clear job stat. jobId=" + jobId + ", info=" + e.toString());
			if (jobId != null) {
				doingJobs.remove(jobId);
			}
			jobId = null;
		}
		return jobId;
	}
	
	/**
	 * 如果jobId是null那么返回false, 否则一定会成功。保证注册表中无任务信息
	 * @param jobId
	 * @return
	 */
	public String kill(String jobId) {
		String stat = "jobId is null";
		if (jobId != null) {
			Job job = doingJobs.remove(jobId);
			if (job != null) {
				FutureTask<String> sourceF = job.getSourceFuture();
				if (sourceF != null) {
					sourceF.cancel(true);
				}
				List<FutureTask<String>> sinksF = job.getSinksFuture();
				if (sinksF != null) {
					for (FutureTask<String> sinkF : sinksF) {
						if (sinkF != null)
							sinkF.cancel(true);
					}
				}
				job.setJobStatus(JobStatus.KILLED);
				job.release();
				stoppedJobs.put(jobId, job);
				logger.info("job " + jobId + " is killed.");
				stat = "killed success";
			} else {
				logger.info("job " + jobId + " dose not exist. Nothing to kill");
				stat = "nothing to kill";
			}
		}
		return stat;
	}
	
	public String addSink(String jobId) {
		String stat = "jobId is null";
		if (jobId != null) {
			Job job = doingJobs.get(jobId);
			try {
				FutureTask<String> sink = job.addSink();
				executor4Sink.submit(sink);
				logger.info("job " + jobId + " add one sink");
			} catch (Exception e) {
				logger.error("job " + jobId + " info: " + e.toString());
			}
		} else {
			logger.info("job " + jobId + " dose not exist. Nothing to kill");
			stat = "nothing to kill";
		}
		return stat;
	}
	
	public String delSink(String jobId) {
		String stat = "jobId is null";
		if (jobId != null) {
			Job job = doingJobs.get(jobId);
			try {
				FutureTask<String> sink = job.delSink();
				sink.cancel(true);
				logger.info("job " + jobId + " delete one sink");
			} catch (Exception e) {
				logger.error("job " + jobId + " info: " + e.toString());
			}
		} else {
			logger.info("job " + jobId + " dose not exist. Nothing to kill");
			stat = "nothing to kill";
		}
		return stat;
	}
	
	public String jobInfo(String jobId) {
		Job job = doingJobs.get(jobId);
		if(job == null) job = stoppedJobs.get(jobId);
		JSONObject j = new JSONObject();
		j.put("jobId", job.getJobId());
		j.put("sourceClassName", job.getSourceClassName());
		j.put("sourcePara", job.getSourcePara());
		j.put("sourceCounter", job.getSourceCounter());
		j.put("sourceBufSize", job.getSourceBufSize());
		j.put("sinkClassName", job.getSinkClassName());
		j.put("sinkPara", job.getSinkPara());
		j.put("sinkInitNum", job.getSinkNum());
		List<FutureTask<String>> x = job.getSinksFuture();
		j.put("sinkCurNum", x == null ? 0 : x.size());
		j.put("status", job.getJobStatus());
		j.put("peekOne", job.peekOne());
		j.put("createdTime", job.getCreatedTime());
		j.put("stoppedTime", job.getStoppedTime());
		return j.toString();
	}
	
	public Set<String> getDoingJobIds() {
		return doingJobs.keySet();
	}
	
	public Set<String> getStoppedJobIds() {
		return stoppedJobs.keySet();
	}
	
	/**
	 * 主要用于检测Source和Sink的运行情况，失败或者完成
	 * @author koala
	 *
	 */
	public static class JobManager extends Thread {
		private static Logger logger = Logger.getLogger(JobManager.class);

		public void run() {
			while(true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (String jobId : doingJobs.keySet()) {
					Job job = doingJobs.get(jobId);
					FutureTask<String> jobF = job.getSourceFuture();
					if (jobF.isDone()) {
						// source完成则任务完成
						try {
							logger.info("jobId: " + jobId + " source worker done.");
							jobF.get();
							job.setJobStatus(JobStatus.DONE);

						} catch (Exception e) {

							logger.error("jobId: " + jobId + ", source worker info: " + e.toString());
							job.setJobStatus(JobStatus.FAIL);

						} finally {
							List<FutureTask<String>> sinksF = job.getSinksFuture();
							for(FutureTask<String> sinkF : sinksF) {
								sinkF.cancel(true);
							}
							doingJobs.remove(jobId);
							job.release();// 释放所有资源，进入完成的状态
							stoppedJobs.put(jobId, job);
						}
					} else {
						
							List<FutureTask<String>> sinksF = job.getSinksFuture();
							for (FutureTask<String> sinkF : sinksF) {
								if(sinkF.isDone()) {
									try {
										sinkF.get();
									} catch (Exception e) {
										job.getSinksFuture().remove(sinkF);
										logger.error("jobId: " + jobId + ", sink worker info: " + e.toString());
									}
								}
							}
						
					}
				} 
			}
		}

	}
}
