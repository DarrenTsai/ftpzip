/*
 * ZipUtil.java 27 Feb 2018
 */
package tw.com.darren;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * @Description Zip utility, compresses multiple file(s) with password.
 * 
 * @Since 27 Feb 2018
 * @Author Darren Tsai
 * @Version 27 Feb 2018, Darren Tsai, new
 **/
public class ZipUtil {
	private static final Logger	LOG		= LoggerFactory.getLogger(ZipUtil.class);
	private static final String	ZIPNAME	= "_P7.zip";

	public static void zipTask() {
		zipMultiFiles(InitialProperties.properties.getProperty("text_file_path"), InitialProperties.properties.getProperty("zip_name"));
	}

	private static void zipMultiFiles(final String filepath, final String zippath) {
		final String DATE_PATTERN = InitialProperties.properties.getProperty("modified_time");
		final String TIME_PATTERN = InitialProperties.properties.getProperty("zip_date_time");
		ZipParameters parameters = null;
		try {
			String date = format(new Date(), DATE_PATTERN, Locale.ENGLISH);
			String dateTime = format(new Date(), TIME_PATTERN, Locale.ENGLISH);
			File file = new File(filepath);
			ZipFile zipFile = new ZipFile(zippath + dateTime + ZIPNAME);
			ArrayList<File> fileList = new ArrayList<File>();
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					String dateFile = format(new Date(file.lastModified()), DATE_PATTERN, Locale.ENGLISH);
					if (date.equals(dateFile)) {
						fileList.add(files[i]);
					}
				}
			}
			parameters = new ZipParameters();
			parameters.setEncryptFiles(true);
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
			parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
			parameters.setPassword(InitialProperties.properties.getProperty("zip_password"));
			zipFile.addFiles(fileList, parameters);
			LOG.info("File(s) are compressed with password successfully.");
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static String format(final Date date, final String pattern, final Locale locale) {
		final SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.applyPattern(pattern);
		return sdf.format(date);
	}
}
