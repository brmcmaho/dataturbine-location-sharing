package edu.ucsd.rbnb.simple;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;

public class SimpleRequestSink extends SimpleSink{
	
	public SimpleRequestSink(String sinkName) throws SAPIException {
		super(sinkName);
	}
	public SimpleRequestSink(String sinkName, String server, int port)
			throws SAPIException {
		super(sinkName, server, port);
	}
	
	public ChannelMap request(double time) throws SAPIException{
		return request(time, 1000);
	}
	public ChannelMap request(double time, int timeout) throws SAPIException{
		sink.Request(monitoredChannels, time, 1, NEXT);
		return fetchData(timeout);
	}
	
}
