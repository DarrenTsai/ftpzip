/*
 * FtpUploadFiles.java 2 Mar 2018
 */
package tw.com.darren;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description FTP uploads multiple file(s)
 * 
 * @Since 2 Mar 2018
 * @Author Darren Tsai
 * @Version 2 Mar 2018, Darren Tsai, new
 **/
public class FtpUploadFiles {
	private static final Logger LOG = LoggerFactory.getLogger(FtpUploadFiles.class);

	public static void uploadFiles() {
		final String ftpIp = InitialProperties.properties.getProperty("ftp_ip");
		final String username = InitialProperties.properties.getProperty("ftp_username");
		final String password = InitialProperties.properties.getProperty("ftp_password");
		final String remotePath = InitialProperties.properties.getProperty("ftp_remote_path");
		final String localPath = InitialProperties.properties.getProperty("ftp_local_path");
		final String DATE_PATTERN = InitialProperties.properties.getProperty("modified_time");
		FTPClient ftpClient = null;
		FileInputStream inputStream = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpIp);
			ftpClient.login(username, password);
			ftpClient.changeWorkingDirectory(remotePath);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			String date = format(new Date(), DATE_PATTERN, Locale.ENGLISH);
			File localFile = new File(localPath);
			for (final File file : localFile.listFiles()) {
				String dateFile = format(new Date(file.lastModified()), DATE_PATTERN, Locale.ENGLISH);
				if (date.equals(dateFile)) {
					inputStream = new FileInputStream(file);
					boolean done = ftpClient.storeFile(remotePath + file.getName(), inputStream);
					if (done) {
						LOG.info("File: " + file.getName() + " uploaded successfully.");
					}
					else {
						LOG.info("Failed to upload files.");
					}
				}
			}
			FileUtils.cleanDirectory(new File(InitialProperties.properties.getProperty("zip_file_path")));
			LOG.info("Original files are deleted successfully.");
		}
		catch (IOException e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
		finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != ftpClient) {
					ftpClient.logout();
					ftpClient.disconnect();
					LOG.info("FTP closed connection.");
				}
			}
			catch (IOException e) {
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
