package org.koala.server.controller;

import java.util.Map;
import java.util.Set;

import org.koala.core.JobSubmit;
import org.koala.server.core.Controller;
import org.koala.util.JSONFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class dtStatController extends Controller{

	@Override
	public String process(Map<String, String> paras, String body) {
		JobSubmit jobSubmit = JobSubmit.getJobSubmit();
		Set<String> jobDoingIds = jobSubmit.getDoingJobIds();
		JSONObject r = new JSONObject();
		JSONArray jobA = new JSONArray();
		for(String jobId: jobDoingIds) {
			String jobInfo = jobSubmit.jobInfo(jobId);
			jobA.add(jobInfo);
		}
		
		Set<String> jobDoneIds = jobSubmit.getStoppedJobIds();
		JSONArray jobB = new JSONArray();
		for(String jobId: jobDoneIds) {
			String jobInfo = jobSubmit.jobInfo(jobId);
			jobB.add(jobInfo);
		}
		
		r.put("jobs", jobA);
		r.put("stopped jobs", jobB);
		r.put("stat", "ok");
		return JSONFormat.formatJson(r.toString());
	}

}
