package org.koala.server.controller;

import java.util.Map;

import org.koala.core.Job;
import org.koala.core.JobSubmit;
import org.koala.server.core.Controller;

import net.sf.json.JSONObject;

public class dtJobController extends Controller{

	/**
	 * {
	 * 	op : submit kill addSink delSink
	 * 	sourceClassName : ""
	 * 	sinkClassName: ""
	 * 	sinkNum: 1 
	 * 	sourcePara : {xxx}
	 * 	sinkPara : {xxx}
	 * }
	 */
	@Override
	public String process(Map<String, String> paras, String body) {
		JSONObject para = JSONObject.fromObject(body);
		JSONObject result = new JSONObject();
		String op = para.getString("op");
		if("submit".equals(op)) {
			String sourceClassName = para.getString("sourceClassName");
			String sinkClassName = para.getString("sinkClassName");
			int sinkNum = Integer.parseInt(para.getString("sinkNum"));
			String sourcePara = para.getString("sourcePara");
			String sinkPara = para.getString("sinkPara");
			Job job = new Job();
			job.setSourceClassName(sourceClassName);
			job.setSinkClassName(sinkClassName);
			job.setSinkNum(sinkNum);
			job.setSourcePara(sourcePara);
			job.setSinkPara(sinkPara);
			JobSubmit jobSubmit = JobSubmit.getJobSubmit();
			String jobId = jobSubmit.submit(job);
			result.put("jobId", jobId);
			result.put("stat", "ok");
		} else if("kill".equals(op)) {
			String jobId = para.getString("jobId");
			JobSubmit jobSubmit = JobSubmit.getJobSubmit();
			String r = jobSubmit.kill(jobId);
			result.put("stat", r);
		} else if("addSink".equals(op)) {
			result.put("stat", "ok");
		} else if("delSink".equals(op)) {
			result.put("stat", "ok");
		} else {
			result.put("stat", "bad op");
		}
		return result.toString();
	}

}
