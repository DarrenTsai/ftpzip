/*
 * SftpDownloadFiles.java 26 Mar 2018
 */
package tw.com.darren;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @Description SFTP downloads multiple file(s).
 * 
 * @Since 26 Mar 2018
 * @Author Darren Tsai
 * @Version 26 Mar 2018, Darren Tsai, new
 **/
public class SftpDownloadFiles {
	private static final Logger LOG = Logger.getLogger(SftpDownloadFiles.class);

	public static void downloadFiles() throws Exception {
		final String ftpIp = InitialProperties.properties.getProperty("ftp_ip");
		final String username = InitialProperties.properties.getProperty("ftp_username");
		final String password = InitialProperties.properties.getProperty("ftp_password");
		final String remotePath = InitialProperties.properties.getProperty("ftp_remote_path");
		final String downloadPath = InitialProperties.properties.getProperty("ftp_download_path");
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, ftpIp);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(remotePath);
			byte[] buffer = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(channelSftp.get("*.zip"));
			File newFile = new File(downloadPath);
			OutputStream os = new FileOutputStream(newFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bis.close();
			bos.close();
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		finally {
			if (null != channelSftp) {
				channelSftp.disconnect();
			}
			if (null != channel) {
				channel.disconnect();
			}
			if (null != session) {
				session.disconnect();
			}
		}
	}
}
