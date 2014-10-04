package edu.ucsd.rbnb.simple;

import java.util.Iterator;
import java.util.LinkedList;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.ChannelTree;
import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Sink;

/**
 * 
 * SimpleSink.java 
 * Created: Mar 14, 2011
 * Last Modified:	Apr 27, 2012
 * @author Michael Nekrasov
 * @version 0.5
 * 
 * Description: An interface for writing Sink Applications that connect to a 
 * DataTurbine server.
 *
 */
public abstract class SimpleSink {
	
	public static final String	DEFAULT_RBNB_SERVER	= "localhost";
	public static final int		DEFAULT_RBNB_PORT	= 3333;

	protected Sink sink = new Sink();
	protected String sinkName;
	protected String server;
	protected int port;
	
	protected ChannelMap monitoredChannels;
	protected final String NEWEST = "newest";
	protected final String OLDEST = "oldest";
	protected final String NEXT = 	"next";
	
	
	public SimpleSink(String sinkName) throws SAPIException{
		this(sinkName, DEFAULT_RBNB_SERVER,DEFAULT_RBNB_PORT);
	}
	public SimpleSink(String sinkName, String server, int port) throws SAPIException{
		this.sinkName	= sinkName;
		this.server		= server;
		this.port		= port;
		sink.OpenRBNBConnection(server+":"+port, sinkName);
	}
	
	public String[] getAvailableChannels() throws SAPIException{
		return getChannelsMap().GetChannelList();
	}	
	public String[] getAvailableSources() throws SAPIException{
		@SuppressWarnings("rawtypes")
		Iterator treeIterator = getChannelTree().iterator();
		LinkedList<String> sources = new LinkedList<String>();
		
		while(treeIterator.hasNext()){
			ChannelTree.Node node = (ChannelTree.Node)treeIterator.next();
			if(node.getType() == ChannelTree.SOURCE)
				sources.add(node.getName());
		}
		return sources.toArray(new String[0]);
		
	}

	private ChannelTree getChannelTree() throws SAPIException{
		return ChannelTree.createFromChannelMap(getChannelsMap());
	}
	private ChannelMap getChannelsMap() throws SAPIException{
		sink.RequestRegistration();
		return sink.Fetch(-1);
	}
	
	
	public void monitorAll() throws SAPIException{
		monitoredChannels = getChannelsMap();
	}
	
	public void monitorChannel(String channel) throws SAPIException{
		if(monitoredChannels == null)
			monitoredChannels = new ChannelMap();
		
		monitoredChannels.Add(channel);
	}	
	public void monitorSource(String source) throws SAPIException{
		monitorChannel(source+"/*");
	}
	
	protected ChannelMap fetchData(int timeout) throws SAPIException{
		return sink.Fetch(timeout);
	}
		
	public int getId(String name){
		return monitoredChannels.GetIndex(name);
	}
	
	public void close(){
		sink.CloseRBNBConnection();
	}
}
