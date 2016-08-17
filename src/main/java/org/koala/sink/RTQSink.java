package org.koala.sink;

import org.koala.sink.RTQ.DataMode;
import org.koala.sink.RTQ.TagDict;
import org.koala.util.HttpClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RTQSink extends Sink{

	@Override
	public void initMe() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * midlist
	 * datamode
	 * ip
	 * separator
	 */
	public String call() throws Exception {
		JSONArray batch = new JSONArray();		
		JSONArray mapbatch = new JSONArray();
		JSONObject p = JSONObject.fromObject(this.paraJson);
		String [] midArray = p.getString("midlist").split(",");
		String datamode = p.getString("datamode");
		String ip = p.getString("ip");
		String separator = p.getString("separator");
		String data = null;
		while ( ( data = source.get() ) != null) { 
			try {
				String prefix = DataMode.getPrefix(datamode);
				String[] datameta = data.split(separator, -1);
				
				if(DataMode.EMPTY.equals(datamode)) {
					System.out.println(data);
				} 
				
				else if(DataMode.ZIPMAPPING.equals(datamode)) {
					if( datameta.length == 2 ) {
						StringBuilder smap = new StringBuilder();
						String midUid =  midArray[0] + "-" + datameta[1];
						String admckid =  datameta[0];
						smap.append(prefix).append("\t").append(midUid).append("\t").append(admckid);
						mapbatch.add(smap.toString());
					}
					
					if(mapbatch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/zipSetMap?t=adinsertxd", mapbatch.toString());
						mapbatch.clear();
					}
				}
				
				else if(DataMode.PC.equals(datamode)) {

					if( datameta.length == midArray.length + 3 ) {
						String admckid = datameta[0];
						for(int i=0;i<midArray.length;i++) {
							String muid = datameta[i+1];
							if( "\\N".equals(muid) || "".equals(muid) ) continue;
							String midUid = midArray[i] + "-" + muid;
							//System.out.println("##" + midUid);
							StringBuilder smap = new StringBuilder();
							smap.append(prefix).append("\t").append(midUid).append("\t").append(admckid);
							mapbatch.add(smap.toString());
						}
						String gender = TagDict.getTagCode(datameta[midArray.length + 1]);
						String age = TagDict.getTagCode(datameta[midArray.length + 2]);
						StringBuilder s = new StringBuilder();
						s.append(prefix).append("\t").append(admckid).append("\t").append(age).append("\t").append(gender);
						batch.add(s.toString());
					}
					if(batch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/HSet?t=adinsertxd", batch.toString());
						batch.clear();
					}
					if(mapbatch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/Set?t=adinsertxd", mapbatch.toString());
						mapbatch.clear();
					}
					
				} else if(DataMode.DEV.equals(datamode)) {
					if (datameta.length == 3) {
						String deviceId = datameta[0];
						String gender = TagDict.getTagCode(datameta[1]);
						String age = TagDict.getTagCode(datameta[2]);
						StringBuilder s = new StringBuilder();
						s.append(prefix).append("\t").append(deviceId).append("\t").append(age).append("\t").append(gender);
						batch.add(s.toString());
					}
					if(batch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/HSet?t=adinsertxd", batch.toString());
						//System.out.println(batch.toString());
						batch.clear();
					}
				} else if(DataMode.PCBLK.equals(datamode) || DataMode.DEVBLK.equals(datamode) || DataMode.IPBLK.equals(datamode)) {
					String deviceId = datameta[0];
					StringBuilder s = new StringBuilder();
					s.append(prefix).append("\t").append(deviceId).append("\t").append("0");
					batch.add(s.toString());
					if(batch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/Set?t=adinsertxd", batch.toString());
						//System.out.println(batch.toString());
						batch.clear();
					}
				} else if(DataMode.ZIPPC.equals(datamode) || DataMode.SVZIPPC.equals(datamode)) {
					
					if( datameta.length == midArray.length + 3 ) {
						String admckid = datameta[0];
						for(int i=0;i<midArray.length;i++) {
							String muid = datameta[i+1];
							if( "\\N".equals(muid) || "".equals(muid) ) continue;
							String midUid = midArray[i] + "-" + muid;
							//System.out.println("##" + midUid);
							StringBuilder smap = new StringBuilder();
							smap.append(prefix).append("\t").append(midUid).append("\t").append(admckid);
							mapbatch.add(smap.toString());
						}
						String gender = TagDict.getTagCode(datameta[midArray.length + 1]);
						String age = TagDict.getTagCode(datameta[midArray.length + 2]);
						StringBuilder s = new StringBuilder();
						s.append(prefix).append("\t").append(admckid).append("\t").append(age).append("\t").append(gender);
						batch.add(s.toString());
					}
					if(batch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/zipSet?t=adinsertxd", batch.toString());
						batch.clear();
					}
					if(mapbatch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/zipSetMap?t=adinsertxd", mapbatch.toString());
						mapbatch.clear();
					}
					
				} else if(DataMode.ZIPDEV.equals(datamode)) {
					if (datameta.length == 3) {
						String deviceId = datameta[0];
						String gender = TagDict.getTagCode(datameta[1]);
						String age = TagDict.getTagCode(datameta[2]);
						StringBuilder s = new StringBuilder();
						s.append(prefix).append("\t").append(deviceId).append("\t").append(age).append("\t").append(gender);
						batch.add(s.toString());
					}
					if(batch.size() > 100 ) {
						HttpClient.post("http://" + ip + "/insert/zipSet?t=adinsertxd", batch.toString());
						//System.out.println(batch.toString());
						batch.clear();
					}
				} else {
					System.out.println("bad datamode. thread exit.");
					break;
				}
			} catch (Exception e) {
				System.out.println("ERR: " + e.toString());
			}
		}
		return null;
	}
}
