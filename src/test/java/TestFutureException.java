import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class TestFutureException {
	private static ExecutorService executor = Executors.newCachedThreadPool();

	public static void main(String [] args) throws InterruptedException {
		FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>(){

			public String call() throws Exception {
				return "OK";
			}
			
		});
		executor.submit(futureTask);
		Thread.sleep(4000);

		try {
			//String r = futureTask.get(1, TimeUnit.MILLISECONDS);
			String r = futureTask.get();
			System.out.println(r);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
		
		
		executor.shutdown();
	}
}
