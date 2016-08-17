import org.koala.util.HttpClient;

import net.sf.json.JSONObject;

public class TestKillJob {

	public static void main(String[] args) {
		//String ip = "10.20.20.82";
		String ip = "10.10.10.42";

		JSONObject body = new JSONObject();
		body.put("op", "kill");
		body.put("jobId", "1471435113307_1572C371D52FA8CA6CC976AB72D7239B");
		String r = HttpClient.post("http://" + ip + ":8089/DT/job", body.toString());
		System.out.println(r);
	}

}
