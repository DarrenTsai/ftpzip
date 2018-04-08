/*
 * TxtFilesTransform.java 8 Mar 2018
 */
package tw.com.darren;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description Reading, writing multiple txt file(s) and converting to String.
 * 
 * @Since 8 Mar 2018
 * @Author Darren Tsai
 * @Version 8 Mar 2018, Darren Tsai, new
 **/
public class TxtFilesTransform {
	private static final Logger	LOG			= LoggerFactory.getLogger(TxtFilesTransform.class);
	private static final String	TEXTNAME	= "_P7.txt";

	public static void txtFilesTransformTask() {
		List<byte[]> textResult = null;
		final String DATE_PATTERN = InitialProperties.properties.getProperty("modified_time");
		File folder = new File(InitialProperties.properties.getProperty("file_path"));
		String date = LocalDateTime.now().toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE);
		try {
			if (folder.isDirectory()) {
				for (final File file : folder.listFiles()) {
					String dateFile = format(new Date(file.lastModified()), DATE_PATTERN, Locale.ENGLISH);
					if (date.equals(dateFile)) {
						textResult = textFileReading(file);
						String[] fileArray = file.getName().split("\\.");
						String fileName = fileArray[0];
						textResult.forEach(detail -> textFileWriting(fileName, TEXTNAME, detail));
					}
				}
				LOG.info("New text file(s) generated successfully.");
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static List<byte[]> textFileReading(final File file) {
		String textLine = null;
		List<byte[]> textList = new ArrayList<byte[]>();
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while (null != (textLine = bufferedReader.readLine())) {
				textList.add(textLine.getBytes());
			}
			bufferedReader.close();
			fileReader.close();
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return textList;
	}

	private static void textFileWriting(final String fileName, final String textName, final byte[] textData) {
		try {
			FileWriter fileWriter = new FileWriter(InitialProperties.properties.getProperty("text_file_path") + fileName + textName, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(new String(textData));
			bufferedWriter.close();
			fileWriter.close();
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static String format(final Date date, final String pattern, final Locale locale) {
		final SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.applyPattern(pattern);
		return sdf.format(date);
	}
}
