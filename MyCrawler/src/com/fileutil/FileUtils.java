package com.fileutil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.kmgh.ConfigurationRead;
public class FileUtils {

	public static void mkDir(String path) {
		File folder = new File(path);
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
	}

	public static boolean rmFile(String path) {
		System.out.println(path);
		Boolean isSuccess = false;
		File file = new File(path);
		if (file.exists()) {
			isSuccess = file.delete();
		}
		return isSuccess;
	}

	public static String chFileName(String fileName) {
		java.util.Random r = new java.util.Random();
		String[] ex = fileName.split("\\.");
		int ram = r.nextInt(90000) + 10000;
		fileName = String.valueOf(System.currentTimeMillis() + "" + ram);
		fileName = fileName + "." + ex[ex.length - 1];
		return fileName.toLowerCase();
	}
	public static Map<String, String> UpfileFromFormIstream(String path, HttpServletRequest request) {
		Map<String, String> caseMap = new HashMap<String, String>();
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					String upfile = item.getFieldName();
					InputStream stream = item.openStream();
					if (item.isFormField()) {
						String value = Streams.asString(stream, request.getCharacterEncoding());
						caseMap.put(upfile, value);
					} else { 
						if (stream.available() != 0) {
							String filename = item.getName(); 
							if (filename != null) {
								filename = FilenameUtils.getName(filename);
							}
							mkDir(path);
							caseMap.put("filename", filename);
							filename = chFileName(filename);
							Streams.copy(stream, new FileOutputStream(path + filename), true);
							String[] ex = filename.split("\\.");
							String exe = ex[ex.length - 1].toLowerCase();
							if (exe.equals("jpg") || exe.equals("png") || exe.equals("gif") || exe.equals("bmp")) {
								caseMap.put("title_pic", filename);
							} else {
								caseMap.put("file_url", filename);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caseMap;
	}

	public static Map<String, Object> UpMultiFileFromStream(String path, HttpServletRequest request) {
		Map<String, Object> caseMap = new HashMap<String, Object>();
		List<String> pics = new ArrayList<String>();
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					String upfile = item.getFieldName();
					InputStream stream = item.openStream();
					if (item.isFormField()) {
						String value = Streams.asString(stream, request.getCharacterEncoding());
						caseMap.put(upfile, value);
					} else { 
						if (stream.available() != 0) {
							String filename = item.getName(); 
							if (filename != null) {
								filename = FilenameUtils.getName(filename);
							}
							mkDir(path);
							caseMap.put("filename", filename);
							filename = chFileName(filename);
							Streams.copy(stream, new FileOutputStream(path + filename), true);
							String[] ex = filename.split("\\.");
							String exe = ex[ex.length - 1].toLowerCase();
							if (exe.equals("jpg") || exe.equals("png") || exe.equals("gif") || exe.equals("bmp")) {
								caseMap.put("title_pic", filename);
								pics.add(filename);
							} else {
								caseMap.put("file_url", filename);
							}
						}
					}
				}// end iter
				caseMap.put("pics", pics);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caseMap;
	}
	public static void string2File(String content, String filePath, String fileName) {
		CreateFile(filePath, fileName);
		FileWriter writer = null;
		try {
			writer = new FileWriter(filePath + fileName, false);
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void writeToFile(String fileContent, String filePath, String fileName) {
		CreateDir(filePath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fileContent = new String(fileContent.getBytes("UTF-8")).trim();
			fw = new FileWriter(filePath + fileName);
			bw = new BufferedWriter(fw);
			bw.write("<p><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head></p>"
					+ "\n" + fileContent);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void writeStr2File(String fileContent, String filePath, String fileName) {
		// �ョ��褰�涓�瀛���,��寤烘��浠剁��褰�
		CreateDir(filePath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fileContent = new String(fileContent.getBytes("UTF-8")).trim();
			fw = new FileWriter(filePath + fileName);
			bw = new BufferedWriter(fw);
			bw.write(fileContent);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static boolean CreateDir(String filePath) {
		// 杩���������寤烘��浠剁��褰�����
		String fileFullPath = filePath;

		try {
			File FolderPath = new File(fileFullPath);
			if (!FolderPath.exists()) {
				FolderPath.mkdirs();
			}
			FolderPath = null;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public static boolean CreateFile(String filePath, String fileContent) {
		boolean fileDir = CreateDir(filePath);
		boolean blResult = false;
		if (fileDir) {
			File file = new java.io.File(filePath + fileContent);
			if (!file.exists()) {
				File FolderPath = new File(filePath + fileContent);
				try {
					FolderPath.createNewFile();
					blResult = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			blResult = false;
		}
		return blResult;
	}
	public static void copyFile(String source, String dest) {
		try {
			File in = new File(source);
			File out = new File(dest);
			FileInputStream inFile = new FileInputStream(in);
			FileOutputStream outFile = new FileOutputStream(out);
			byte[] buffer = new byte[1024];
			int i = 0;
			while ((i = inFile.read(buffer)) != -1) {
				outFile.write(buffer, 0, i);
			}// end while
			inFile.close();
			outFile.close();
		}// end try
		catch (Exception e) {
			e.printStackTrace();

		}// end catch
	}// end copyFile

	public static void copyDict(String source, String dest) {
		String source1;
		String dest1;

		File[] file = (new File(source)).listFiles();
		if (file != null && file.length > 0) {
			for (int i = 0; i < file.length; i++)
				if (file[i].isFile()) {
					source1 = source + "/" + file[i].getName();
					dest1 = dest + "/" + file[i].getName();
					copyFile(source1, dest1);
				}// end if
			for (int i = 0; i < file.length; i++)
				if (file[i].isDirectory()) {
					source1 = source + "/" + file[i].getName();
					dest1 = dest + "/" + file[i].getName();
					File dest2 = new File(dest1);
					dest2.mkdir();
					copyDict(source1, dest1);
				}// end if
		}

	}// end copyDict

	public static void delFiles(String filepath, boolean isDelDir) throws IOException {
		File f = new File(filepath);// 瀹�涔���浠惰矾寰�
		if (f.exists() && f.isDirectory()) {// �ゆ������浠惰�����褰?
			File delFile[] = f.listFiles();
			int fileNum = delFile.length;
			if (0 != fileNum) {// �ョ��褰�涓�����浠跺�����ゅ�ㄩ�ㄦ��浠�
				for (int i = 0; i < fileNum; i++) {
					delFile[i].delete();// ���ゆ��浠�
				}
			}
			if (isDelDir) {
				f.delete();
			}
		} else if (f.exists() && f.isFile()) {
			f.delete();
		}
	}

	public static void delFile(String filepath) throws IOException {
		File file = new File(filepath);
		if (file.exists()) {
			file.delete();

		}
	}

	public static void delFiles(List<File> files) throws IOException {
		for (File file : files) {
			if (file.exists()) {
				file.delete();

			}
		}
	}

	public static void deleteFolder(String dir) {
		File delfolder = new File(dir);
		File oldFile[] = delfolder.listFiles();
		try {
			for (int i = 0; i < oldFile.length; i++) {
				if (oldFile[i].isDirectory()) {
					deleteFolder(dir + oldFile[i].getName() + "//"); // ��褰�娓�绌哄����浠跺す
				}
				oldFile[i].delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String UpSinglefileFromFormIstream(HttpServletRequest request) {
		String filePath = null;
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					item.getFieldName();
					InputStream stream = item.openStream();
					if (item.isFormField()) {
						Streams.asString(stream, request.getCharacterEncoding());
					} else { 
						if (stream.available() != 0) {
							String filename = item.getName();
							if (filename != null) {
								filename = FilenameUtils.getName(filename);
							}
							String path = request.getRealPath("/backend/tempfile/");
							deleteFolder(path);
							mkDir(path);
							filename = chFileName(filename);
							Streams.copy(stream, new FileOutputStream(path + "/" + filename), true);
							filePath = filename;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	public static String upFileByswf(String path, HttpServletRequest request) {
		String result = "";
		String fileName = "";
		try {

			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				item.getFieldName();
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					continue;
				} else {
					String type = item.getName().split("\\.")[1].toLowerCase();// �峰����浠剁被��
					String oldName = item.getName().trim().toLowerCase();
					mkDir(path);
					Streams.copy(stream, new FileOutputStream(path + "/" + oldName), true);
					if (type.equals("jpg") || type.equals("png") || type.equals("gif") || type.equals("bmp")) {
						result = oldName + "," + oldName;
					} else {
						result = oldName + "||" + oldName;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (fileName != null && fileName.length() > 0) {
				FileUtils.rmFile(path + "/" + fileName);
			}

		}
		return result;
	}

	public static Map<String, String> upNewsByswf(String path, HttpServletRequest request) {
		Map<String, String> fileNameMaps = new HashMap<String, String>();
		String fileName = "";
		try {

			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				item.getFieldName();
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					continue;
				} else {
					item.getName().split("\\.")[1].toLowerCase();
					String oldName = item.getName().trim().toLowerCase();
					String newName = chFileName(item.getName());
					fileNameMaps.put("filename", oldName);
					fileNameMaps.put("fileMapname", newName);
					fileName = newName;
					mkDir(path);
					Streams.copy(stream, new FileOutputStream(path + "/" + newName), true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (fileName != null && fileName.length() > 0) {
				FileUtils.rmFile(path + "/" + fileName);
			}

		}
		return fileNameMaps;
	}

	public static Map<String, String> fileList(File file, Map<String, String> fileMap) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					String[] tempB = f.getAbsolutePath().split("\\" + String.valueOf(File.separatorChar));
					String tempBstr = tempB[tempB.length - 1].toLowerCase().trim();
					if (tempBstr.equals("video") || tempBstr.equals("card") || tempBstr.equals("case")
							|| tempBstr.equals("ebook")) {
						File[] subFiles = (new File(f.getAbsolutePath())).listFiles();

						for (File subF : subFiles) {
							String[] tempArr = subF.getName().split("\\.");
							String ext = tempArr[tempArr.length - 1].toLowerCase().trim();
							if (!ext.equals("db") && !ext.equals("html")) {
								fileMap.put(subF.getName().trim(), subF.getPath());
							}
							fileList(subF, fileMap);
						}
					}
				}
			}
		}
		return fileMap;
	}

	public static String strFilter(String str) {
		str = StringEscapeUtils.escapeHtml(str);
		str = StringEscapeUtils.escapeJava(str);
		str = StringEscapeUtils.escapeSql(str);
		return str;
	}

	public static String unStrFilter(String str) {
		str = StringEscapeUtils.unescapeHtml(str);
		str = StringEscapeUtils.unescapeJava(str);

		return str;
	}
	public static String getImgSrc(String htmlStr) {
		String img = "";
		Pattern p_image;
		Matcher m_image;
		String pics = null;
		String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(htmlStr);
		while (m_image.find()) {
			img = img + "," + m_image.group();
			Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);

			if (m.find()) {
				pics = m.group(1);
			}
		}
		return pics;
	}

	public static void writeFile(String serverUrl, String targetPath, String fileName) {
		URL url = null;
		try {
			url = new URL(serverUrl);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStream is = null;
		try {
			is = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStream os = null;
		File f = new File(targetPath);
		f.mkdirs();
		try {
			os = new FileOutputStream(targetPath + "/" + fileName);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			try {
				while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static List<File> getFileList(String filePath) {
		File[] files = (new File(filePath)).listFiles();
		List<File> fileList = new ArrayList<File>();
		if (files != null && files.length > 0) {
			for (File file : files) {
				fileList.add(file);
			}
		}
		return fileList;
	}

	public static void delDir(String filePath) {
		try {
			delAllFile(filePath); 
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); 

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delDir(path + "/" + tempList[i]);
			}
		}
	}

	public static void download(HttpServletRequest request, HttpServletResponse response, String fileName,
			Boolean delete) throws IOException {

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			response.reset();
			response.setHeader("Accept-Ranges", "bytes");
			long p = 0;
			long l = 0;

			URI uri = new URI(fileName);
			File file = new File(uri.toString());
			file.getPath();
			System.out.println("name锛?" + file.getAbsolutePath());
			System.out.println("file.getPath():" + file.getPath());
			System.out.println("length():" + file.length());
			System.out.println("getName锛?" + file.getName());
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(response.getOutputStream());

			byte[] buff = new byte[2048];
			l = file.length();

			if (request.getHeader("Range") != null) 
			{
				response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);// 206
				String pstr = request.getHeader("Range").replaceAll("bytes=", "");
				String ps = pstr.split("-")[0];
				p = Long.parseLong(ps);
			}
			response.setHeader("Content-Length", new Long(l - p).toString());
			if (p != 0) {
				response.setHeader(
						"Content-Range",
						"bytes " + new Long(p).toString() + "-" + new Long(l - 1).toString() + "/"
								+ new Long(l).toString());
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
			bis.skip(p);
			response.setCharacterEncoding("UTF-8");
			int bytesRead;
			while ((bytesRead = bis.read(buff)) != -1) {
				try {
					bos.write(buff, 0, bytesRead);
				} catch (Exception e) {

				}
			}
			bos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null)
				bos.close();
			if (bis != null)
				bis.close();
			if (delete)
				FileUtils.delFile(fileName);
		}
	}

	public static String getFilePath(String resName, String keyName) {
		ConfigurationRead cfr = ConfigurationRead.getInstance();
		Properties prop = cfr.propertiesCreate(resName);
		String path = prop.getProperty(keyName).trim();
		return path;
	}

	public String getClassPath() {
		String strClassName = getClass().getName();
		String strPackageName = "";
		if (getClass().getPackage() != null) {
			strPackageName = getClass().getPackage().getName();
		}
		System.out.println("ClassName:" + strClassName);
		System.out.println("PackageName:" + strPackageName);
		String strClassFileName = "";
		if (!"".equals(strPackageName)) {
			strClassFileName = strClassName.substring(strPackageName.length() + 1, strClassName.length());
		} else {
			strClassFileName = strClassName;
		}
		System.out.println("ClassFileName:" + strClassFileName);
		URL url = null;
		url = getClass().getResource(strClassFileName + ".class");
		String strURL = url.toString();
		System.out.println(strURL);
		strURL = strURL.substring(strURL.indexOf('/') + 1, strURL.lastIndexOf('/'));
		strURL = strURL.replace("com/archermind/mdm/util", "");
		strURL = strURL.replace("/", "\\");
		return strURL.toString();
	}
	public static String getPicBASE64(String picPath) {
		String content = null;
		try {
			FileInputStream fis = new FileInputStream(picPath);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			content = Base64.encodeBase64String(bytes); // �蜂���缂����规�?
			fis.close();
			// System.out.println(content.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String updateUserPic(String path, HttpServletRequest request) {
		String fileName = "";
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			if (iter.hasNext()) {
				FileItemStream item = iter.next();
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					return fileName;
				} else {
					String newName = chFileName(item.getName());
					fileName = newName;
					mkDir(path);
					Streams.copy(stream, new FileOutputStream(path + newName), true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (fileName != null && fileName.length() > 0) {
				FileUtils.rmFile(path + "/" + fileName);
			}

		}
		return fileName;
	}

	public static void downloadFile(HttpServletResponse response, String filePath) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			File file = new File(filePath);
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while ((bytesRead = bis.read(buff)) != -1) {
				try {
					bos.write(buff, 0, bytesRead);
				} catch (Exception e) {

				}
			}
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (bos != null) {
				bos.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

    public static byte[] streamToByte(InputStream is)
        throws Exception
    {
        byte[] readed = null;
        int totallength = 0;
        byte[] readedtemp = new byte[1024];
        int truelen = 0;
        while ((truelen = is.read(readedtemp, 0, readedtemp.length)) != -1)
        {
            totallength += truelen;
            if (totallength - truelen > 0)
            {
                byte[] newReaded = new byte[totallength];
                System.arraycopy(readed, 0, newReaded, 0, totallength - truelen);
                readed = newReaded;
            }
            else
                readed = new byte[truelen];
            System.arraycopy(readedtemp, 0, readed, totallength - truelen, truelen);
        }
        readedtemp = null;
        return readed;
    }
}
