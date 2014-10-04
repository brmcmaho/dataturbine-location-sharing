package edu.ucsd.rbnb.simple;

/**
 * 
 * SimpleSourceListener.java
 * Created: Apr 27, 2012
 * @author Michael Nekrasov
 * 
 * Description: Listens to the actions performed by the source.
 * Useful for feedback to the user when using the SimpleSource.
 *
 */
public interface SimpleSourceListener {
	
	/**
	 * Called when data is put into source, ready to be flushed
	 * @param channelName to send data to 
	 * @param data put for sending. If single point added find it at index [0]
	 * @param timestamp of data
	 */
	public void onPut(String channelName, Object[] data, double timestamp);
	/**
	 * Called when channel is added to the source
	 */
	public void onAddChannel(String channelName, String mimeType);
	/**
	 * Called when data is flushed (sent) to server
	 */
	public void onFlush();
	/**
	 * Called when connecting to Server
	 */
	public void onConnect();
	/**
	 * Called when disconnecting from Server
	 */
	public void onDisconect();
}