package com.hjj.common.util;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipInputStream;


public class ZipUtils {
	private static final Logger logger = Logger.getLogger(ZipUtils.class);
	/**
	 * 创建zip压缩包，参数为文件源（集合），要输出的文件地址
	 * @param file
	 * @param zipFilePath
	 * @throws IOException
	 */
	public static void zip(File file,String zipFilePath) throws IOException{
		if(null == file || !file.exists()){
			throw new IOException("文件不存在");
		}	
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
		zipOut.setEncoding(System.getProperty("sun.jnu.encoding"));
		putZipFile(file,zipOut,"");
		zipOut.close();
	}

	public static void zip(File[] files,String zipFilePath) throws IOException{
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
		zipOut.setEncoding(System.getProperty("sun.jnu.encoding"));
		for(File file : files){
			putZipFile(file,zipOut,"");
		}
		zipOut.close();
	}

	public static void zip(List<File> files,String zipFilePath) throws IOException{
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
		zipOut.setEncoding(System.getProperty("sun.jnu.encoding"));
		for(File file : files){
			putZipFile(file,zipOut,"");
		}
		zipOut.close();
	}
	
	private static void putZipFile(File file, ZipOutputStream zipOut,String path) throws IOException{
		if (file.isDirectory()) {
			path += file.getName()+"/";
			File[] children = file.listFiles();
			for (File item : children) {
				putZipFile(item, zipOut,path);
			}
		}else{
			ZipEntry entry = new ZipEntry(file,path+file.getName());
			zipOut.putNextEntry(entry);
			org.apache.commons.io.FileUtils.copyFile(file, zipOut);
			zipOut.closeEntry();
		}
	}

	public static void unzip(File zipFile,File destFile) throws IOException {
		Expand expand = new Expand();
		expand.setSrc(zipFile);
		expand.setDest(destFile);
		Project p = new Project();
		expand.setProject(p);
		expand.execute();
	}

	/**
	 * 调用org.apache.tools.zip实现解压缩，支持目录嵌套和中文名
	 * 也可以使用java.util.zip不过如果是中文的话，解压缩的时候文件名字会是乱码。原因是解压缩软件的编码格式跟java.util.zip.
	 * ZipInputStream的编码字符集(固定是UTF-8)不同
	 *
	 * @param zipFileName 要解压缩的文件
	 * @param outputDirectory 要解压到的目录
	 * @throws Exception
	 */
	public static void unZip(String zipFileName, String outputDirectory) throws IOException {
		ZipFile zipFile = null;
		try {
			logger.debug("开始解压文件：" + zipFileName+"\t解压目录："+outputDirectory);
			
			zipFile = new ZipFile(zipFileName, System.getProperty("sun.jnu.encoding"));
			
			Enumeration<ZipEntry> zipEntrys = zipFile.getEntries();
			FileUtils.createDirectory(outputDirectory);
			while (zipEntrys.hasMoreElements()) {
				ZipEntry zipEntry = zipEntrys.nextElement();
				String fileName = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					FileUtils.createDirectory(outputDirectory + File.separator + fileName);
				} else {
					FileUtils.copyInputStreamToFile(zipFile.getInputStream(zipEntry), new File(outputDirectory + File.separator + zipEntry.getName()));
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if(null != zipFile){
				zipFile.close();
			}
		}
	}
	
	
	public static void unZip(InputStream in, String outputDirectory) throws IOException {
		ZipInputStream zipInputStream = null;
		try {
//			logger.debug("开始解压文件：" + zipFileName+"\t解压目录："+outputDirectory);
			
			zipInputStream = new ZipInputStream(in, Charset.forName(System.getProperty("sun.jnu.encoding")));
			
			FileUtils.createDirectory(outputDirectory);
			java.util.zip.ZipEntry zipEntry = null;
			while (null != (zipEntry = zipInputStream.getNextEntry())) {
				String fileName = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					FileUtils.createDirectory(outputDirectory + "/" + fileName);
				} else {
					FileOutputStream fos = new FileOutputStream(outputDirectory + "/" + zipEntry.getName());
					byte[] b = new byte[1024*1024*30];
					int len = 0;
					while((len = zipInputStream.read(b))>0){
						fos.write(b,0,len);
					}
					fos.close();
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if(null != zipInputStream){
				zipInputStream.close();
			}
		}
	}
	
	public static void unZipToSmbFile(InputStream in, String outputDirectory) throws IOException {
		ZipInputStream zipInputStream = null;
		try {
			zipInputStream = new ZipInputStream(in, Charset.forName(System.getProperty("sun.jnu.encoding")));
			
			FileUtils.createDirectory(outputDirectory);
			java.util.zip.ZipEntry zipEntry = null;
			while (null != (zipEntry = zipInputStream.getNextEntry())) {
				String fileName = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					SmbFile smbfile = new SmbFile(outputDirectory + "/" + fileName);
					smbfile.mkdirs();
				} else {
					BufferedOutputStream bos = new BufferedOutputStream(new SmbFileOutputStream(outputDirectory + "/" + zipEntry.getName()));
					byte[] b = new byte[1024*1024*30];
					int len = 0;
					while((len = zipInputStream.read(b))>0){
						bos.write(b,0,len);
					}
					bos.close();
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if(null != zipInputStream){
				zipInputStream.close();
			}
		}
	}
}
