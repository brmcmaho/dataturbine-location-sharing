package edu.ucsd.rbnb.simple;

/**
 * 
 * RDV_Label.java
 * Created: Mar 14, 2011
 * @author Michael Nekrasov
 * 
 * Description: Object representation of an RDV Label
 *
 */
public class RDV_Label implements Comparable<RDV_Label> {
	
	public static final String ANNOTATION		= "annotation";
	public static final String MIN				= "min";
	public static final String MAX 				= "max";
	public static final String START 			= "start";
	public static final String STOP 			= "stop";
	
	private String type;
	private double timestamp;
	private String content;

	/**
	 * Construct a new label
	 * @param type of label (see Constants)
	 * @param timestamp in seconds
	 * @param message contained in lable 
	 */
	public RDV_Label(String type, double timestamp, String message) {
		this.type = type;
		this.timestamp = timestamp;
		this.content = message;
	}
	
	@Override
	public String toString() {
		String out ="";
		out += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
		out += "<!DOCTYPE properties " +
				"SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n";
		out += "<properties>\n";
		out += "<entry key=\"type\">"+type+"</entry>\n";
		out += "<entry key=\"content\">"+content+"</entry>\n";
		out += "<entry key=\"timestamp\">"+timestamp+"</entry>\n";
		out += "</properties>\n";
		return out;
	}
	
	
	public String getContent(){
		return content;
	}
	
	public double getTimestamp(){
		return timestamp;
	}

	@Override
	public int compareTo(RDV_Label o) {
		return (int)(timestamp - o.timestamp);
	}

	
}
