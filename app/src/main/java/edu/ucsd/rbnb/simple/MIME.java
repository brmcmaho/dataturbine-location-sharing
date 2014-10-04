package edu.ucsd.rbnb.simple;

/**
 * MIME.java
 * Created: Apr 27, 2012
 * @author Michael Nekrasov
 * 
 * Description: Contains a list of commonly used Internet media types also
 * reffered to as 
 * <a href="http://en.wikipedia.org/wiki/Internet_media_type"> MIME types. </a>
 *
 */
public class MIME {

	/** 
	 * MIME type for numerical data
	 * @see #addChannel(String, String)
	 */
	public static final String	BINARY	= "application/octet-stream";
	/** 
	 * MIME type for JPEG image
	 * @see #addChannel(String, String)
	 */
	public static final String	JPG 	= "image/jpeg";
	/** 
	 * MIME type for JPEG image
	 * @see #addChannel(String, String)
	 */
	public static final String	PNG 	= "image/png";
	/** 
	 * MIME type for audio
	 * @see #addChannel(String, String)
	 */
	public static final String	AUDIO 	= "audio/basic";
	/** 
	 * MIME type for audio
	 * @see #addChannel(String, String)
	 */
	public static final String	MP3 	= "audio/mpeg3";
	/** 
	 * MIME type for plain text
	 * @see #addChannel(String, String)
	 */
	public static final String	TEXT 	= "text/plain";
	/** 
	 * MIME type for plain text
	 * @see #addChannel(String, String)
	 */
	public static final String	XML 	= "text/xml";
	/** 
	 * MIME type for DataTurbine tuple (lat,long)
	 * @see #addChannel(String, String)
	 */
	public static final String	GPS 	= "application/x-gps";
	/** 
	 * MIME type for RDV event Marker
	 * @see #addChannel(String, String)
	 */
	public static final String 	RDV_EVENT	= "text/x-eventmarker";
	/** 
	 * MIME type for DataTurbine Metadata
	 * @see #addChannel(String, String)
	 */
	public static final String 	META	= "text/x-meta";
}
