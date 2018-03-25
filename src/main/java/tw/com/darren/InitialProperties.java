/*
 * InitialProperties.java 27 Feb 2018
 */
package tw.com.darren;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @Description Initial properties
 * 
 * @Since 27 Feb 2018
 * @Author Darren Tsai
 * @Version 27 Feb 2018, Darren Tsai, new
 **/
public class InitialProperties {
	private static final Logger	LOG			= Logger.getLogger(InitialProperties.class);
	public static Properties	properties	= new Properties();
	static {
		InputStream is = InitialProperties.class.getResourceAsStream("/ftpZip.properties");
		try {
			properties.load(is);
		}
		catch (FileNotFoundException e) {
			LOG.error("FileNotFoundException: " + e.toString(), e);
		}
		catch (IOException e) {
			LOG.error("IOException: " + e.toString(), e);
		}
	}
}
