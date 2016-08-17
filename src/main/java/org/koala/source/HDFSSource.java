package org.koala.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
public class HDFSSource extends Source{

	private String hdfsDir;
	private static Logger logger = Logger.getLogger(HDFSSource.class);

	/**
	 * 获取hdfs句柄
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private InputStream getHdfsFileInputStream(String path) throws Exception {
		String[] get = new String[] { "/bin/bash", "-c",
				"hadoop fs -cat '" + path + "/*'" };
		Process process = Runtime.getRuntime().exec(get);
		InputStream inputStream = process.getInputStream();
		return inputStream;
	}

	private BufferedReader getBufferedReader(String path, boolean isFile) {
		InputStream inputStream = null;
		if (isFile) {
			File file = new File(path);
			InputStreamReader read = null;
			try {
				read = new InputStreamReader(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} // 考虑到编码格式
			return new BufferedReader(read);
		} else {
			try {
				inputStream = getHdfsFileInputStream(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			InputStreamReader read = new InputStreamReader(inputStream);
			return new BufferedReader(read);
		}
	}

	@Override
	public void initMe() {
		JSONObject p = JSONObject.fromObject(paraJson);
		this.hdfsDir = p.getString("hdfs_path");		
	}

	public String call() throws Exception {
		try {
			// 读取hdfs数据，写入队列
			BufferedReader bufferedReader = getBufferedReader(hdfsDir, false);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				bufQueuq.put(lineTxt);
				msgNum.incrementAndGet();
			}
			logger.info("HDFS path: " + this.hdfsDir + " closing handler.");
			bufferedReader.close();
			logger.info("HDFS path: " + this.hdfsDir + " check queue size: " + bufQueuq.size());
			while (bufQueuq.size() > 0) {
				Thread.sleep(1000);
			}
			logger.info("HDFS path: " + this.hdfsDir + " data loading end.");
		} catch (Exception e) {
			logger.error("HDFS path: " + this.hdfsDir + " " + e.toString());
		} finally {
			
		}
		return "ok";
	}
}
