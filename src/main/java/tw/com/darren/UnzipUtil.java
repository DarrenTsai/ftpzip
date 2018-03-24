/*
 * UnzipUtil.java 27 Feb 2018
 */
package tw.com.darren;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * @Description Unzip utility, extracts multiple file(s) with password.
 * 
 * @Since 27 Feb 2018
 * @Author Darren Tsai
 * @Version 27 Feb 2018, Darren Tsai, new
 **/
public class UnzipUtil {
	private static final Logger LOG = Logger.getLogger(UnzipUtil.class);

	public static boolean unzipTask() {
		return unzipMultiFiles(InitialProperties.properties.getProperty("zip_file_path"), InitialProperties.properties.getProperty("file_path"));
	}

	private static boolean unzipMultiFiles(final String zippath, final String outzippath) {
		final String DATE_PATTERN = InitialProperties.properties.getProperty("modified_time");
		FileNameExtensionFilter extensionFilter = null;
		List<File> newFileList = new ArrayList<File>();
		List<?> fileHeaderList = null;
		try {
			String date = format(new Date(), DATE_PATTERN, Locale.ENGLISH);
			extensionFilter = new FileNameExtensionFilter("N/A", "zip");
			File folder = new File(zippath);
			if (folder.isDirectory()) {
				for (final File file : folder.listFiles()) {
					String dateFile = format(new Date(file.lastModified()), DATE_PATTERN, Locale.ENGLISH);
					if (date.equals(dateFile)) {
						newFileList.add(file);
						ZipFile zipFile = new ZipFile(file);
						if (extensionFilter.accept(file)) {
							if (zipFile.isEncrypted()) {
								zipFile.setPassword(InitialProperties.properties.getProperty("unzip_password"));
							}
							fileHeaderList = zipFile.getFileHeaders();
							for (int i = 0; i < fileHeaderList.size(); i++) {
								FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
								zipFile.extractFile(fileHeader, outzippath);
								LOG.info("Extracting " + fileHeader.getFileName() + " with password successfully.");
								File outFile = new File(outzippath + File.separator + fileHeader.getFileName());
								if (!outFile.getParentFile().exists()) {
									outFile.getParentFile().mkdir();
								}
								if (!outFile.exists()) {
									outFile.createNewFile();
								}
							}
						}
					}
				}
				if (!newFileList.isEmpty()) {
					LOG.info(String.valueOf(newFileList.size()) + " new zip file(s) extracted in the directory.");
				}
				else {
					LOG.info("No new zip file(s) in the directory.");
				}
			}
			else {
				LOG.error("This directory doesn't exist.");
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return newFileList.isEmpty();
	}

	private static String format(final Date date, final String pattern, final Locale locale) {
		final SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.applyPattern(pattern);
		return sdf.format(date);
	}
}
