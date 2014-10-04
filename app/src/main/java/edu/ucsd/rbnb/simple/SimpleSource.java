package edu.ucsd.rbnb.simple;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Source;

/**
 * SimpleSource.java
 * Created: Jan 17, 2010
 * Updated:	April 27, 2012	
 * @author Michael Nekrasov
 * @version 2.1
 * 
 * Description:	A generic way to create a source. Saves a lot of repeated
 * 				book keeping methods. Can be extended or instantiated to
 * 				to provide a way to store data into RBNB
 * <pre>
 * Example1 (primative sources):
 * 		SimpleSource src = new SimpleSource("MySource");
 *		src.addChannel("count");
 *		src.put("count", 42);
 *		src.flush();
 * 
 * Example2 (multiple channels with time stamp):
 * 		double time = System.currentTimeMillis()/1000;
 * 		SimpleSource src = new SimpleSource("MySource2");
 * 		src.addChannel("temp");
 * 		src.addChannel("gps", SimpleSource.MIME_GPS);
 * 		src.put("temp", 32.1, time);
 * 		src.put("gps", new double[]{53.0,-12.0}, time);
 * 		src.flush();
 * 
 * Example3 (with time stamp):
 * 		SimpleSource src = new SimpleSource("MySource3");
 * 		src.setConnectionHandling(false);
 * 		src.setArchiveSize(400);
 * 		src.setCacheSize(10);
 * 		src.addChannel("temp");
 * 
 * 		src.connect();
 * 		src.put("temp", 32.1);
 * 		src.flush();
 * 		src.put("temp", 30.7);
 * 		src.flush();
 * 		src.close();
 * Example4 (with Listener)
 * 		SimpleSource src = new SimpleSource("MySource4");
 * 		src.addChannel("count");
 * 		src.addListener(new SimpleSourceListener() {
 * 			public void onPut(String chName, Object[] data, double timestamp) {
 * 				System.out.println(chName +": "+data[0]+" @ "+timestamp);
 * 			}
 * 			public void onFlush() {
 * 				System.out.println("Sending Data");
 * 			}			
 * 			public void onAddChannel(String channelName, String mimeType) {
 * 				System.out.println("Ready Channel: "+channelName +" as"+
 * 			mimeType);
 * 			}
 * 			public void onConnect() {
 * 				System.out.println("Connecting to Source");
 * 			}
 * 			public void onDisconect() {
 * 				System.out.println("Disconnecting");
 * 			}
 * 		});
 * 		
 * 		src.connect();
 * 		src.put("count", 31);
 * 		src.flush();		
 * 		src.put("count", 40);
 * 		src.flush();
 * 		src.close();
 * 
 *</pre>
 */
public class SimpleSource {


	
	/** 
	 * Default Archive Mode
	 * @see #SimpleSource(String, String, int, int, int, String)
	 * @see #getArchiveMode()
	 */
	public static final String	DEFAULT_ARCHIVEMODE = "append";
	/** 
	 * Default Server Address
	 * @see #SimpleSource(String, String, int, int, int, String)
	 * @see #getServer()
	 * @see #getFullServerURL()
	 */
	public static final String	DEFAULT_RBNB_SERVER	= "localhost";
	/** 
	 * Default Port
	 * @see #SimpleSource(String, String, int, int, int, String)
	 * @see #getPort()
	 */
	public static final int		DEFAULT_RBNB_PORT	= 3333;
	/** 
	 * Default Cache Size (in number of data points)
	 * @see #SimpleSource(String, String, int, int, int, String)
	 * @see #getCacheSize()
	 */
	public static final int		DEFAULT_CACHESIZE	= 1440;
	/** 
	 * Default Archive Size (in number of data points)
	 * @see #SimpleSource(String, String, int, int, int, String)
	 * @see #getArchiveSize()
	 */
	public static final int		DEFAULT_ARCHIVESIZE = Integer.MAX_VALUE;
	
	
	private final String sourceName;
	private final String server;
	private final int port;
	private int archiveSize; 
	private int cacheSize;
	private String archiveMode;
	protected boolean autoConnectionHandling; //Try to auto connect?
	protected final Source source;					//Source to write to
	protected ChannelMap cmap;						//Channel map to store data
	protected HashMap<String, Integer> chRecorder;	//Stores ch names
	protected LinkedList<SimpleSourceListener> listeners;
	

	/**
	 * Create a new Simple Source
	 * @see #SimpleSource(String, String, int, int, int, String)
	 */
	public SimpleSource(String srcName) {
		this(srcName, DEFAULT_CACHESIZE, DEFAULT_ARCHIVESIZE);
	}	
	/**
	 * Create a new Simple Source
	 * @see #SimpleSource(String, String, int, int, int, String)
	 * @since 1.4
	 */
	public SimpleSource( String srcName, String serverPath, int port){
		this(srcName, serverPath, port, DEFAULT_CACHESIZE, DEFAULT_ARCHIVESIZE);
	}
	/**
	 * Create a new Simple Source
	 * @see #SimpleSource(String, String, int, int, int, String)
	 */
	public SimpleSource( String srcName, int cacheSize, int archiveSize){
		this(srcName, DEFAULT_RBNB_SERVER, DEFAULT_RBNB_PORT, 
				cacheSize, archiveSize); 
	}
	/**
	 * Create a new Simple Source
	 * @see #SimpleSource(String, String, int, int, int, String)
	 */
	public SimpleSource( 
			String srcName, 
			String serverPath, int port,
			int cacheSize, int archiveSize
		){
		
		this(srcName, serverPath, port, cacheSize, archiveSize, 
				DEFAULT_ARCHIVEMODE);
	}	
	/**
	 * Create a new Simple Source
	 * @param srcName		Name of this Source
	 * @param cacheSize		Number of frames (data points) to store in memory
	 * @param archiveSize	Number of frames (data points) to store on disk
	 * @param serverPath	URL to RBNB Server (default to localhost)
	 * @param port			Port of RBNB Server (default to 3333)
	 * @param archiveMode	Mode to add data as (default to append)<br/>
	 * 						{@link #setArchiveMode(String)}
	 */
	public SimpleSource( 
			String srcName, 
			String serverPath, int port,
			int cacheSize, int archiveSize, 
			String archiveMode
		){

		this.sourceName = srcName;
		this.server = serverPath;
		this.port = port;
		this.archiveSize = archiveSize;
		this.cacheSize = cacheSize;
		this.archiveMode = archiveMode; 
		this.autoConnectionHandling = true;
		
		chRecorder = new HashMap<String, Integer>();
		cmap = new ChannelMap();
		listeners = new LinkedList<SimpleSourceListener>();
		source = new Source(cacheSize, archiveMode, archiveSize);
		
	}
	
	/**
	 * Gets the name of the Source as reflected on the RBNB server
	 * @return the name of the source
	 */
	public String getName(){
		try{ return source.GetClientName();	}
		catch (IllegalStateException e) { return sourceName;}
	}	
	/**
	 * Gets port to the RBNB server is operating on 
	 * @return the port number
	 */
	public int getPort(){ return port;}
	/**
	 * Gets the path to the RBNB server (excluding port)
	 * @return the address of the server
	 */
	public String getServer(){ return server;}	
	/**
	 * Gets the url to the RBNB server (including port)
	 * @return the full url
	 */
	public String getFullServerURL(){ return server+":"+port;}	
	/**
	 * Gets the number of archive frames (data points) archived by this source
	 * @return the number of frames to archive
	 */
	public int getArchiveSize(){ return archiveSize;}	
	/**
	 * Gets the number of cache frames (data points) cached by this source
	 * @return the number of frames to cache
	 */
	public int getCacheSize(){ return cacheSize;}	
	/**
	 * Gets the mode that source is operating on
	 * @return the mode
	 * @see #SimpleSource(String, String, int, int, int, String, boolean)
	 */
	public String getArchiveMode(){	return source.GetArchiveMode();}
	/**
	 * Gets of all defined channels
	 * @return an array of all the channel names
	 */
	public synchronized String[] getChannels(){
		return chRecorder.keySet().toArray(new String[0]);
	}
	/**
	 * Set size of Source archive (hard disk storage)
	 * @param archiveSize in number of frames (data points)
	 * @throws SAPIException on error
	 */
	public synchronized void setArchiveSize(int archiveSize) 
		throws SAPIException
	{
		this.archiveSize = archiveSize;
		source.SetRingBuffer(cacheSize,archiveMode,archiveSize);
	}
	/**
	 * Set size of source cache (in memory)
	 * @param cacheSize in number of frames (data points)
	 * @throws SAPIException on error
	 */
	public synchronized void setCacheSize(int cacheSize) 
		throws SAPIException
	{
		this.cacheSize = cacheSize;
		source.SetRingBuffer(cacheSize,archiveMode,archiveSize);
	}
	/**
	 * Sets the mode of Storage for the ring buffer
	 * @param archiveMode	Mode to add data as (default to append)<br/>
	 * 						<ul>
	 * 							<li>"none" - no Archive is made. </li>
	 * 							<li>"load" - load an archive, but do not allow 
	 * 								any further writing to it. </li>
	 * 							<li> "create" - create an archive. </li>
	 * 							<li> "append" - load an archive, but allow 
	 * 								writing new data to it. </li>
	 * 						</ul>
	 * @throws SAPIException
	 */
	public synchronized void setArchiveMode(String archiveMode) 
			throws SAPIException
	{
		this.archiveMode = archiveMode;
		source.SetRingBuffer(cacheSize,archiveMode,archiveSize);
	}
	/**
	 * Automatically set the source to connect and disconect on flush
	 * Defaults to automatic
	 * @param auto set false to disable connection handling
	 */
	public synchronized void setConnectionHandling(boolean auto){
		this.autoConnectionHandling = auto;
	}
	/**
	 * Checks if this source is currently connected to the server
	 * @return true if connected
	 */
	public synchronized boolean isConnected(){
		return source.VerifyConnection();
	}
		
	/**
	 * Create a new channel with the default {@link #MIME_BINARY} MIME Type,
	 * for numerical data.
	 * @see #addChannel(String channelName, String mimeType)
	 */
	public synchronized void addChannel(String channelName) throws SAPIException{
		addChannel(channelName, MIME.BINARY);
	}
	/**
	 * Adds a channel to the source ready to accept data. You will now be able 
	 * to use the channel name to add data via the {@link #put(String, int)} 
	 * method. For example {@link #MIME_BINARY}.
	 * 
	 * @param channelName	Name of channel to add 
	 * @param mimeType		Content type of data stored in this channel. 
	 * @throws SAPIException If the channel cannot be created.
	 */
	public synchronized void addChannel
		(String channelName, String mimeType)
		throws SAPIException
	{
		int ch = cmap.Add(channelName);
        cmap.PutMime(ch, mimeType);
        chRecorder.put(channelName, ch);
        
        for(SimpleSourceListener call : listeners)
			call.onAddChannel(channelName, mimeType);
	}
	
	
	//
	// PUT BYTE ARRAY METHODS
	//
	/**
	 * Prepares a binary array for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  byte[] put(String channelName, byte[] data) throws SAPIException{
		put(channelName, data, System.currentTimeMillis()/1000);
		return data;
	}	
	/**
	 * Prepares a binary array for insertion into RBNB Server.
	 * Need to {@link #flush()} before this data is committed.
	 * 
	 * @param channelName		Channel to add data to. Needs to be created via 
	 * 						{@link #addChannel(String)}
	 * @param data			Binary data to add to the server
	 * @param timestamp		Timestamp (UNIX Time in seconds)
	 * @return				The data just added (can be used for chaining)
	 * @throws SAPIException If there is an error adding the data
	 */
	public synchronized byte[] put(String channelName, byte[] data, double timestamp)
	throws SAPIException{
		putTime(1, timestamp);
		cmap.PutDataAsByteArray(getCh(channelName), data);
		callPutListener(channelName, data, timestamp);
		return data;
	}
	
	//
	// PUT SHORT METHODS
	//
	/**
	 * Prepares a single short for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized short put
		(String channelName, short data)
		throws SAPIException
	{
		put(channelName, new short[]{data});
		return data;
	}
	/**
	 * Prepares a single short for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized short put
		(String channelName, short data, double timestamp)
		throws SAPIException
	{
		put(channelName, new short[]{data}, timestamp);
		return data;
	}
	/**
	 * Prepares array of short values for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized short[] put
		(String channelName, short[] data)
		throws SAPIException
	{
		return put(channelName, data, System.currentTimeMillis()/1000);
	}
	/**
	 * Prepares array of short values for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  short[] put
		(String channelName, short[] data, double timestamp)
		throws SAPIException
	{
		putTime(data.length, timestamp);
		cmap.PutDataAsInt16(getCh(channelName), data);
		
		callPutListener(channelName, data, timestamp);
		return data;
	}	
	/**
	 * Prepares String to be inserted as a short into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized short putAsShort
		(String channelName, String data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Short.parseShort(data));
	}	
	/**
	 * Prepares String to be inserted as a short into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized int putAsShort
		(String channelName, String data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Short.parseShort(data), timestamp);
	}
	/**
	 * Prepares any number to be inserted as a Short into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsShort
		(String channelName, double data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (short)data);
	}
	/**
	 * Prepares any number to be inserted as a Short into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsShort
		(String channelName, double data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (short)data, timestamp);
	}
	
	
	//
	// PUT INT METHODS
	//
	/**
	 * Prepares a single integer for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized int put
		(String channelName, int data)
		throws SAPIException
	{	
		put(channelName, new int[]{data});
		return data;
	}
	/**
	 * Prepares a single integer for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized int put
		(String channelName, int data, double timestamp)
		throws SAPIException
	{
		put(channelName, new int[]{data}, timestamp);
		return data;
	}
	/**
	 * Prepares array of integer values for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized int[] put
		(String channelName, int[] data)
		throws SAPIException
	{
		return put(channelName, data, System.currentTimeMillis()/1000);
	}
	/**
	 * Prepares array of integer values for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  int[] put
		(String channelName, int[] data, double timestamp)
		throws SAPIException
	{
		putTime(data.length, timestamp);
		cmap.PutDataAsInt32(getCh(channelName), data);
		
		
		callPutListener(channelName, data, timestamp);
		return data;
	}	
	/**
	 * Prepares String to be inserted as an integer into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized int putAsInt
		(String channelName, String data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Integer.parseInt(data));
	}	
	/**
	 * Prepares String to be inserted as an integer into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized int putAsInt
		(String channelName, String data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Integer.parseInt(data), timestamp);
	}
	/**
	 * Prepares any number to be inserted as an Int into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsInt
		(String channelName, double data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (int)data);
	}
	/**
	 * Prepares any number to be inserted as an Int into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsInt
		(String channelName, double data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (int)data, timestamp);
	}
	
	//
	// PUT LONG METHODS
	//
	/**
	 * Prepares a single long for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized long put
		(String channelName, long data)
		throws SAPIException
	{	
		put(channelName, new long[]{data});
		return data;
	}
	/**
	 * Prepares a single long for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized long put
		(String channelName, long data, double timestamp)
		throws SAPIException
	{
		put(channelName, new long[]{data}, timestamp);
		return data;
	}
	/**
	 * Prepares array of long values for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized long[] put
		(String channelName, long[] data)
		throws SAPIException
	{
		return put(channelName, data, System.currentTimeMillis()/1000);
	}
	/**
	 * Prepares array of long values for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  long[] put
		(String channelName, long[] data, double timestamp)
		throws SAPIException
	{
		putTime(data.length, timestamp);
		cmap.PutDataAsInt64(getCh(channelName), data);
		
		callPutListener(channelName, data, timestamp);
		return data;
	}
	/**
	 * Prepares String to be inserted as an long into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized long putAsLong
		(String channelName, String data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Long.parseLong(data));
	}
	/**
	 * Prepares String to be inserted as an long into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized long putAsLong
		(String channelName, String data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Long.parseLong(data), timestamp);
	}
	/**
	 * Prepares any number to be inserted as a Long into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsLong
		(String channelName, double data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (long)data);
	}
	/**
	 * Prepares any number to be inserted as a Long into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsLong
		(String channelName, double data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (long)data, timestamp);
	}
	
	//
	// PUT FLOAT METHODS
	//
	/**
	 * Prepares a single float value for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized float put
		(String channelName, float data)
		throws SAPIException
	{
		put(channelName, new float[]{data});
		return data;
	}
	/**
	 * Prepares a single float value for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized float put
		(String channelName, float data, double timestamp) 
		throws SAPIException
	{
		put(channelName, new float[]{data}, timestamp);
		return data;
	}
	/**
	 * Prepares an array of float values for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized float[] put
		(String channelName, float[] data)
		throws SAPIException
	{
		put(channelName, data, System.currentTimeMillis()/1000);
		return data;
	}
	/**
	 * Prepares an array of float values for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized float[] put
		(String channelName, float[] data, double timestamp) 
		throws SAPIException
	{
		putTime(data.length, timestamp);
		cmap.PutDataAsFloat32(getCh(channelName), data);
		
		callPutListener(channelName, data, timestamp);
		return data;
	}
	/**
	 * Prepares String to be inserted as a floating point into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized float putAsFloat
		(String channelName, String data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Float.parseFloat(data));
	}	
	/**
	 * Prepares String to be inserted as a floating point into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized float putAsFloat
		(String channelName, String data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Float.parseFloat(data), timestamp);
	}
	/**
	 * Prepares any number to be inserted as a Float into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsFloat
		(String channelName, double data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (float)data);
	}
	/**
	 * Prepares any number to be inserted as a Float into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsFloat
		(String channelName, double data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, (float)data, timestamp);
	}
	
	
	//
	// PUT DOUBLE METHODS
	//
	/**
	 * Prepares a single double for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  double put(String channelName, double data) throws SAPIException{
		put(channelName, new double[]{data});
		return data;
	}
	/**
	 * Prepares a single double for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  double put(String channelName, double data, double timestamp) throws SAPIException{
		put(channelName, new double[]{data}, timestamp);
		return data;
	}
	/**
	 * Prepares an array of doubles for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  double[] put(String channelName, double[] data) throws SAPIException{
		put(channelName, data, System.currentTimeMillis()/1000);
		return data;
	}
	/**
	 * Prepares an array of doubles for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  double[] put(String channelName, double[] data, double timestamp) throws SAPIException{
		putTime(data.length, timestamp);
		cmap.PutDataAsFloat64(getCh(channelName), data);
		
		callPutListener(channelName, data, timestamp);
		return data;
	}	
	/**
	 * Prepares String to be inserted as a double into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsDouble
		(String channelName, String data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Double.parseDouble(data));
	}
	/**
	 * Prepares String to be inserted as a double into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsDouble
		(String channelName, String data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, Double.parseDouble(data), timestamp);
	}
	/**
	 * Prepares any number to be inserted as a double into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsDouble
		(String channelName, double data)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, data);
	}
	/**
	 * Prepares any number to be inserted as a double into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized double putAsDouble
		(String channelName, double data, double timestamp)
		throws NumberFormatException, SAPIException
	{
		return put(channelName, data, timestamp);
	}
	
	//
	// PUT STRING METHODS
	// 
	/**
	 * Prepares a String for insertion into RBNB Server.
	 * Uses current system time for the timestamp
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  String put(String channelName, String data) throws SAPIException{
		put(channelName, data, System.currentTimeMillis()/1000);
		return data;
	}	
	/**
	 * Prepares a String for insertion into RBNB Server.
	 * @see #put(String channelName, byte[] data, double timestamp)
	 */
	public synchronized  String put(String channelName, String data, double timestamp) throws SAPIException{
		putTime(1, timestamp);
		cmap.PutDataAsString(getCh(channelName), data);
		
		callPutListener(channelName, data, timestamp);
		return data;
	}
	

	/**
	 * Commit data to RBNB Server, needs to be called before data is stored
	 * to the server.  
	 * If autoConnectionHandling is enabled each flush will result in a new
	 * connection, flush, and disconnect
	 * <code>confirm</code> defaults to FALSE
	 * @see #flush(boolean confirm)
	 */
	public synchronized  void flush() throws SAPIException{
		flush(false);
	}
	/**
	 * Commit data to RBNB Server, needs to be called before data is stored
	 * to the server. 
	 * If autoConnectionHandling is enabled each flush will result in a new
	 * connection, flush, and disconnect
	 * @param confirm TRUE to wait for confirmation,
	 * 				  FALSE to flush asynchronously (default).
	 * @throws SAPIException in the event of failure
	 */
	public synchronized  void flush(boolean confirm) throws SAPIException{
		boolean alreadyConnected = isConnected();
		if(autoConnectionHandling && !alreadyConnected) connect();
		source.Flush(cmap,confirm);
		for(SimpleSourceListener call : listeners) call.onFlush();
		if(autoConnectionHandling && !alreadyConnected) close();
	}
	
	/**
	 * Open connection to server <br/><br/>
	 * 
	 * <b>Note:</b>
	 * If autoConnectionHandling is enabled then this will be done automaticaly
	 * during flush.
	 */
	public synchronized void connect() throws SAPIException{
		source.OpenRBNBConnection(getFullServerURL(), sourceName);
		for(SimpleSourceListener call : listeners) call.onConnect();
	}	
	/**
	 * Closes the connection to RBNB server 
	 * (this will try to flush all remaining data)
	 */
	public synchronized void close(){
		source.Detach(); // Tell RBNB to keep the data once we close
		for(SimpleSourceListener call : listeners) call.onDisconect();
	}
	
	/**
	 * Adds a listener that will be invoked when operations on this source are
	 * performed
	 * @param listener to add
	 * @since 2.0
	 */
	public void addListener(SimpleSourceListener listener){
		listeners.add(listener);
	}	
	/**
	 * Removes listener.
	 * Uses .equals() method of listener for comparison
	 * @param listener to remove
	 * @return true if removed
	 * @since 2.0
	 */
	public boolean removeListener(SimpleSourceListener listener){
		return listeners.remove(listener);
	}
	
	/**
	 * Helper function that extracts the port component of a url,
	 * If error parsing the string or no port found returns DEFAULT_RBNB_PORT
	 * @since 1.4
	 */
	 public static int parseUrlPort( String rbnbHostName){
		 try{ return Integer.parseInt(rbnbHostName.split(":")[1]); }
		 catch (Exception e) {return DEFAULT_RBNB_PORT;}
	 }	 
	 /**
	 * Helper function that extracts extracts the address component of the url
	 * @since 1.4
	 */
	 public static String parseUrlAddress( String rbnbHostName){
		 try{ return rbnbHostName.split(":")[0]; }
		 catch (Exception e) { return "localhost";}
	 }
	/**
	 * Helper function that converts a Date Time string into a double timestamp 
	 * using the {@link SimpleDateFormat} Class  
	 * @param dateStr to convert
	 * @param DateFormat of the string
	 * @return the numerical timestamp
	 * @throws IOException
	 * @see {@link SimpleDateFormat}
	 * @since 1.5
	 */
	public static double 
		calcTimeStamp(String dateStr, String DateFormat) 
		throws IOException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat);
		Date date;
		try {
			date = dateFormat.parse(dateStr); 
		} catch (ParseException e) {
			throw new IOException("Cannot read date time recived '"+dateStr+
					"' but expected '"+DateFormat+"'");
		}
		return ((double)date.getTime())/1000;
	}
	
	
	/**
	 * Sets the timestamp of a block of data
	 * @param sizeOfData (Number of array entries) about to be added
	 * @param timestamp  in milliseconds 
	 */
	private void putTime(int sizeOfData, double timestamp){
		double[] time = new double[sizeOfData];
		
		for(int i =0; i<sizeOfData; i++)
			time[i] = timestamp;
		
		cmap.PutTimes(time);
	}	
	/**
	 * Retrieves the ID of a channel
	 * @param name of channel
	 * @return the id
	 * @throws SAPIException 
	 */
	private int getCh(String name) throws SAPIException{
		Integer i = chRecorder.get(name);
		if(i == null)
			throw new SAPIException("Channel '"+name+"' not set");
		
		return i;
	}
	
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, byte[] data, double timestamp){
		Object[] objects = new Object[data.length];
		for(int i=0; i<data.length; i++) objects[i] = data[i];
		callPutListener(channelName, objects, timestamp);
	}
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, short[] data, double timestamp){
		Object[] objects = new Object[data.length];
		for(int i=0; i<data.length; i++) objects[i] = data[i];
		callPutListener(channelName, objects, timestamp);
	}
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, int[] data, double timestamp){
		Object[] objects = new Object[data.length];
		for(int i=0; i<data.length; i++) objects[i] = data[i];
		callPutListener(channelName, objects, timestamp);
	}
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, long[] data, double timestamp){
		Object[] objects = new Object[data.length];
		for(int i=0; i<data.length; i++) objects[i] = data[i];
		callPutListener(channelName, objects, timestamp);
	}
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, float[] data, double timestamp){
		Object[] objects = new Object[data.length];
		for(int i=0; i<data.length; i++) objects[i] = data[i];
		callPutListener(channelName, objects, timestamp);
	}
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, double[] data, double timestamp){
		Object[] objects = new Object[data.length];
		for(int i=0; i<data.length; i++) objects[i] = data[i];
		callPutListener(channelName, objects, timestamp);
	}
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, String data, double timestamp){
		callPutListener(channelName, new Object[]{data}, timestamp);
	}
	/** Calls the onPut Listener */
	protected void callPutListener(String channelName, Object[] data, double timestamp){	
		for(SimpleSourceListener call : listeners)
			call.onPut(channelName, data, timestamp);
	}
	
	/**
	 * Compares two sources. They are equal if they have the same name and
	 * point to the same server and port.
	 * @param src to compare to.
	 * @return if the two sources point to same resource and share name
	 * @since 2.0
	 */
	@Override
	public boolean equals(Object o){
		return
			o instanceof SimpleSource &&
			this.getName().equals(((SimpleSource)o).getName()) &&
			this.getFullServerURL().equals(((SimpleSource)o).getFullServerURL());
	}
	/**
	 * Returns a hash code that is the product of the source name and target
	 * server.
	 * @return the hashCode
	 * @since 2.0
	 */
	@Override
	public int hashCode(){
		return (this.getName()+this.getFullServerURL()).hashCode();
	}
	/**
	 * Converts the Parameters of this source into an easy to print string
	 * @return String representation of the source
	 */
	@Override
	public String toString() {
		String out = "";
		out += getName()+" ";
		out += "@"+getFullServerURL()+" ";
		
		if(isConnected())	out += "<> ";
		else				out += ">< ";
			
		out += "A("+getArchiveSize()+") ";
		out += "C("+getCacheSize()+") ";
		out += getArchiveMode();
		return out;
	}
}
