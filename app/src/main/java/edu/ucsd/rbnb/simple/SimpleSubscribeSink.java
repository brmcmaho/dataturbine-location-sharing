package edu.ucsd.rbnb.simple;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;

public class SimpleSubscribeSink extends SimpleSink{

	public SimpleSubscribeSink(String sinkName) throws SAPIException {
		super(sinkName);
	}
	
	public SimpleSubscribeSink(String sinkName, String server, int port)
			throws SAPIException {
		super(sinkName, server, port);
	}

	public void subscribeToNewest() throws SAPIException{
		subscribeTo(0, NEWEST);
	}
	public void subscribeToOldest() throws SAPIException{
		subscribeTo(0, OLDEST);
	}
	
	public void subscribeTo(double startTime) throws SAPIException{
		subscribeTo(startTime, OLDEST);
	}
	
	private void subscribeTo(double startTime, String timeReference) 
			throws SAPIException{
		if(monitoredChannels == null) monitorAll();
		sink.Subscribe(monitoredChannels, startTime, 1, timeReference);
	}
	
	public ChannelMap fetch() throws SAPIException{
		return fetch(1000);
	}
	
	public ChannelMap fetch(int timeout) throws SAPIException{
		return fetchData(timeout);
	}
}
