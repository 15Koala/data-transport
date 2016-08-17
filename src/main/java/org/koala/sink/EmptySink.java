package org.koala.sink;

public class EmptySink extends Sink{
	
	@Override
	public void initMe() {
		
	}
	
	public String call() throws Exception {
		String msg = source.get();
		while ( ( msg = source.get() ) != null) {
			System.out.println("jobId: " + this.JobId + ", msg: " + msg);
		}
		return "ok";
	}
}
