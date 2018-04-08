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
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @Description SFTP uploads multiple file(s).
 * 
 * @Since 2 Mar 2018
 * @Author Darren Tsai
 * @Version 2 Mar 2018, Darren Tsai, new
 **/
public class SftpUploadFiles {
	private static final Logger LOG = LoggerFactory.getLogger(SftpUploadFiles.class);

	public static void uploadFiles() throws Exception {
		final String ftpIp = InitialProperties.properties.getProperty("ftp_ip");
		final String username = InitialProperties.properties.getProperty("ftp_username");
		final String password = InitialProperties.properties.getProperty("ftp_password");
		final String remotePath = InitialProperties.properties.getProperty("ftp_remote_path");
		final String localPath = InitialProperties.properties.getProperty("ftp_local_path");
		final String DATE_PATTERN = InitialProperties.properties.getProperty("modified_time");
		FileInputStream inputStream = null;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			final JSch jsch = new JSch();
			session = jsch.getSession(username, ftpIp);
			session.setPassword(password);
			final Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(remotePath);
			String date = format(new Date(), DATE_PATTERN, Locale.ENGLISH);
			File localFile = new File(localPath);
			for (final File file : localFile.listFiles()) {
				String dateFile = format(new Date(file.lastModified()), DATE_PATTERN, Locale.ENGLISH);
				if (date.equals(dateFile)) {
					inputStream = new FileInputStream(file);
					try {
						channelSftp.put(inputStream, remotePath + file.getName());
						LOG.info("File: " + file.getName() + " uploaded successfully.");
					}
					catch (Exception e) {
						LOG.error(e.getMessage(), e);
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
				if (null != channelSftp) {
					channelSftp.exit();
				}
				if (null != channel) {
					channel.disconnect();
				}
				if (null != session) {
					session.disconnect();
				}
				LOG.info("SFTP closed connection.");
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
