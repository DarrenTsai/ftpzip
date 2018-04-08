/*
 * FtpDownloadFiles.java 2 Mar 2018
 */
package tw.com.darren;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description FTP downloads multiple file(s)
 * 
 * @Since 2 Mar 2018
 * @Author Darren Tsai
 * @Version 2 Mar 2018, Darren Tsai, new
 **/
public class FtpDownloadFiles {
	private static final Logger LOG = LoggerFactory.getLogger(FtpDownloadFiles.class);

	public static void downloadFiles() {
		final String ftpIp = InitialProperties.properties.getProperty("ftp_ip");
		final String username = InitialProperties.properties.getProperty("ftp_username");
		final String password = InitialProperties.properties.getProperty("ftp_password");
		final String remotePath = InitialProperties.properties.getProperty("ftp_remote_path");
		final String downloadPath = InitialProperties.properties.getProperty("ftp_download_path");
		final String DATE_PATTERN = InitialProperties.properties.getProperty("modified_time");
		final String sleepInterval = InitialProperties.properties.getProperty("o2s.ftp.sleep.interval");
		final String sleep = InitialProperties.properties.getProperty("o2s.ftp.sleep");
		final Long interval = Long.parseLong(sleep) * 1000;
		FTPClient ftpClient = null;
		FileOutputStream outputStream = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpIp);
			ftpClient.login(username, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.changeWorkingDirectory(remotePath);
			String date = format(new Date(), DATE_PATTERN, Locale.ENGLISH);
			for (final FTPFile item : ftpClient.listFiles("*.zip")) {
				String dateItem = format(item.getTimestamp().getTime(), DATE_PATTERN, Locale.ENGLISH);
				if (date.equals(dateItem)) {
					String filename = item.getName();
					if (null != filename) {
						try {
							outputStream = new FileOutputStream(downloadPath + filename);
							ftpClient.retrieveFile(remotePath + filename, outputStream);
							LOG.info("File:" + downloadPath + filename + " downloaded successfully.");
						}
						catch (FileNotFoundException e) {
							LOG.warn("File path not found, check file path: " + downloadPath + " does it exist.");
						}
						catch (IOException e) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		finally {
			try {
				if (null != outputStream) {
					outputStream.flush();
					outputStream.close();
				}
				if (null != ftpClient) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			}
			catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private static String format(final Date date, final String pattern, final Locale locale) {
		final SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.applyPattern(pattern);
		return sdf.format(date);
	}
}
