package plugs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import eventbus.EventBusCenter;
import eventbus.event.ProductUnShellEvent;
import jws.Logger;

public class Init implements jws.Init{

	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(10); 
	
	@Override
	public void init() {
		Logger.info("Plugs init...");
		//service.scheduleAtFixedRate(new CheckTogetherValid(), 0, 10, TimeUnit.MINUTES); 
		EventBusCenter.register(new ProductUnShellEvent());
 	}

}
