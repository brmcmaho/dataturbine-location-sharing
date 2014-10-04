//package edu.ucsd.rbnb.simple;
//
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//import javax.imageio.ImageIO;
//
//import com.rbnb.sapi.SAPIException;
//
//public class SimpleSourcePlus extends SimpleSource{
//
//	/**
//	 * SimpleSource.java ( RBNB )
//	 * Created: Jan 17, 2010
//	 * Updated:	Apr 27, 2012
//	 * @author Michael Nekrasov
//	 * @version 1.4
//	 *
//	 * Description:	Extends Simple source to also allow methods for quickly
//	 * 				putting images. This program requires
//	 * 				{@link #java.awt.image.BufferedImage}. <br/><br/>
//	 *
//	 * 				The classes are split between SimpleSource and
//	 * 				SimpleSourcePlus to allow programs to be coded without the
//	 * 				dependency on other libraries (like the awt library) which
//	 * 				is not compatible with certain operating systems
//	 * 				(like android);
//	 */
//
//
//	public SimpleSourcePlus(String srcName) throws SAPIException {
//		super(srcName);
//	}
//
//	public SimpleSourcePlus(String srcName, String serverPath, int port)
//			throws SAPIException {
//		super(srcName, serverPath, port);
//	}
//
//	public SimpleSourcePlus(String srcName, int cacheSize, int archiveSize)
//			throws SAPIException {
//		super(srcName, cacheSize, archiveSize);
//	}
//
//	public SimpleSourcePlus(String srcName, String serverPath, int port,
//			int cacheSize, int archiveSize) throws SAPIException {
//		super(srcName, serverPath, port, cacheSize, archiveSize);
//	}
//
//	public SimpleSourcePlus(String srcName,
//			String serverPath, int port,
//			int cacheSize, int archiveSize,
//			String archiveMode) throws SAPIException {
//		super(srcName, serverPath, port, cacheSize, archiveSize,  archiveMode);
//	}
//
//
//	/**
//	 * Put Image into RBNB Server
//	 * It will be added to the server as a jpg
//	 * @see #put(String, byte[], double)
//	 */
//	public synchronized BufferedImage
//		put(String chName, BufferedImage img)
//		throws SAPIException, IOException
//	{
//		put(chName, img, System.currentTimeMillis()/1000);
//		return img;
//	}
//
//	/**
//	 * Put Image into RBNB Server
//	 * It will be added to the server as a jpg
//	 * @see #put(String, byte[], double)
//	 */
//	public synchronized BufferedImage
//		put(String chName, BufferedImage img, double timestamp)
//		throws SAPIException, IOException
//	{
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		ImageIO.write(img,"jpg",out);
//		byte[] data = out.toByteArray();
//		out.close();
//		put(chName, data, timestamp);
//
//		return img;
//	}
//
//	/**
//	 * Put RDV_Label into RBNB Server
//	 * @throws SAPIException
//	 * @see #put(String, String, double)
//	 */
//	public synchronized RDV_Label
//		put(String chName, RDV_Label label) throws SAPIException
//	{
//		put(chName, label.toString());
//		return label;
//	}
//
//	/**
//	 * Put RDV_Label into RBNB Server
//	 * @see #put(String, String, double)
//	 */
//	public synchronized RDV_Label
//		put(String chName, RDV_Label label, double timestamp)
//		throws SAPIException
//	{
//		put(chName, label.toString(), timestamp);
//		return label;
//	}
//
//}
