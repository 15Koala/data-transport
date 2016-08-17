package org.koala.source;
public class NumberSource extends Source{
	
	@Override
	public void initMe() {
		
	}


	public String call() throws Exception {
		int cnt = 0;
		while (cnt > -1) {
			bufQueuq.put(String.valueOf(cnt++));
		}
		return "ok";
	}
}
