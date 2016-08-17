import org.koala.util.HttpClient;

import net.sf.json.JSONObject;

public class TestSubmitJobRTQ {

	public static void main(String[] args) {
		
		String ip = "10.10.10.42";
		String sourceClassName = "org.koala.source.HDFSSource";
		JSONObject body = new JSONObject();
		body.put("op", "submit");
		//body.put("op", "kill");
		body.put("sourceClassName", sourceClassName);
		body.put("sinkClassName", "org.koala.sink.RTQSink");
		body.put("sinkNum", 2);
		
		
		JSONObject SourcePara = new JSONObject();
		SourcePara.put("hdfs_path", "/user/dmp_ids/hive/output/4919");
		body.put("sourcePara", SourcePara.toString());
		
		
		JSONObject SinkPara = new JSONObject();
		SinkPara.put("midlist", "1849");
		//SinkPara.put("datamode", "ZIPMAPPING");
		SinkPara.put("datamode", "ZIPDEV");
		SinkPara.put("ip", "117.121.17.106");
		SinkPara.put("separator", "\001");
		body.put("sinkPara", SinkPara.toString());
		String r = HttpClient.post("http://" + ip + ":8089/DT/job", body.toString());
		System.out.println(r);
		
	}

}
