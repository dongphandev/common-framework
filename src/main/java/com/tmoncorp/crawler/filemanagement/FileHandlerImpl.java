package com.tmoncorp.crawler.filemanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tmoncorp.crawler.exception.CommonException;
import com.tmoncorp.crawler.exception.ExceptionCode;

/**
 * Created by danhnguyen on 4/29/19.
 */
@Service
public class FileHandlerImpl implements FileHandler {

	public void writeArrayOfStringToFile(List<String> lst, String fileName) {
		BufferedWriter bufferedWriter = null;

		try {
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
			for (String s : lst) {
				if (!s.endsWith("\\n"))
					bufferedWriter.write(s + "\n");
				else
					bufferedWriter.write(s);
			}
		} catch (IOException e) {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void writeArrayOfLongToFile(List<Long> lst, String fileName) {
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
			for (Long element : lst) {
				bufferedWriter.write(element.toString()+"\n");
			}
		} catch (IOException e) {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void saveContent(String category, String title, String imageTitle, String content, String folder,
			String dealId) {
		String fileName = folder + File.separator + dealId + ".csv";
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF8"));
			bufferedWriter.append(category).append("\r\n");
			bufferedWriter.append(title).append("\r\n");
			bufferedWriter.append(imageTitle).append("\r\n");
			bufferedWriter.append(content).append("\r\n");
		} catch (IOException e) {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void saveImage(String url, String dealId, String folderPath) {
		InputStream is = null;
		OutputStream os = null;
		try {
			URL imageUrl = new URL(url);
			is = imageUrl.openStream();
			String file = folderPath + File.separator + dealId + url.substring(url.lastIndexOf("."), url.length());
			os = new FileOutputStream(file);
			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

		} catch (IOException e) {
			throw new CommonException(ExceptionCode.CAN_NOT_SAVE_IMAGE, e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public Set<String> readByLine(String fileName) {
		Set<String> emails = new HashSet<>();
		BufferedReader bufferedReader = null;
		try {
			File file = new File(fileName);
			if (!file.exists())
				return emails;
			bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				emails.add(line);
			}
		} catch (IOException e) {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return emails;
	}
}
