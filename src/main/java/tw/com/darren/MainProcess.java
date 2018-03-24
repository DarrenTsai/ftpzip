/*
 * MainProcess.java 27 Feb 2018
 */
package tw.com.darren;

/**
 * @Description The major process, including unzip, text file(s) generated, zip, upload/download FTP
 * 
 * @Since 27 Feb 2018
 * @Author Darren Tsai
 * @Version 27 Feb 2018, Darren Tsai, new
 **/
public class MainProcess {
	public static void main(String[] args) {
		boolean unzipFileEmpty = UnzipUtil.unzipTask();
		if (unzipFileEmpty == false) {
			TxtFilesTransform.txtFilesTransformTask();
			ZipUtil.zipTask();
			FtpUploadFiles.uploadFiles();
			FtpDownloadFiles.downloadFiles();
		}
	}
}
