package util;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ThreadUtil {

	private static ExecutorService cachedThreadPool = Executors.newFixedThreadPool(5);
	
	public static void sumbit(Runnable runnable){
		cachedThreadPool.submit(runnable);
	}
}
