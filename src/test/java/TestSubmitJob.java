import org.koala.util.HttpClient;

import net.sf.json.JSONObject;

public class TestSubmitJob {

	public static void main(String[] args) {
		//String ip = "10.20.20.82";
		
		String ip = "10.10.10.42";
		//String sourceClassName = "org.koala.source.NumberSource";
		String sourceClassName = "org.koala.source.HDFSSource";
		JSONObject body = new JSONObject();
		body.put("op", "submit");
		//body.put("op", "kill");
		body.put("sourceClassName", sourceClassName);
		body.put("sinkClassName", "org.koala.sink.EmptySink");
		body.put("sinkNum", 1);
		JSONObject para = new JSONObject();
		para.put("hdfs_path", "/user/dmp_ids/hive/output/4898");
		body.put("sourcePara", para.toString());
		body.put("sinkPara", "");
		String r = HttpClient.post("http://" + ip + ":8089/DT/job", body.toString());
		System.out.println(r);
		
	}

}
