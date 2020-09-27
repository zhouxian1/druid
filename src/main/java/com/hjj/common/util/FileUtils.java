package com.hjj.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

/**
 * 文件工具类.
 *
 * @author : chain
 * @date : 2013-10-19 下午4:41:56
 */
public class FileUtils{

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 生成随机的文件名 将原始文件名去掉,改为一个UUID的文件名,后缀名以原文件名的后缀为准
     *
     * @param fileName 原始文件名+后缀
     * @return
     */
    public static String generateUUIDFileName(String fileName) {

        UUID uuid = UUID.randomUUID();
        String str = fileName;
        str = uuid.toString() + "." + str.substring(str.lastIndexOf(".") + 1);
        return str;
    }

    /**
     * 目录不存在的话创建目录
     *
     * @param Directorypath
     */
    public static void createDirectory(String Directorypath) {
        File file = new File(Directorypath);
        if (!file.exists()) {
            file.mkdir();
            file.mkdirs();
        }
    }

    /**
     * 目录不存在的话创建目录
     *
     * @param dir
     */
    public static String checkSaveDir(String dir) {

        File dirFile = new File(dir);
        boolean flag = true;
        if (!dirFile.exists()) {
            flag = dirFile.mkdirs();
        }
        if (flag)
            return dirFile.getAbsolutePath();
        else
            return null;
    }

    /**
     * 删除文件
     *
     * @param files
     */
    public static void deleteFile(File... files) {
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /**
     * 删除文件夹
     *
     * @param files
     */
    public static void deleteDirectory(File...files) {
        for(File file : files){
            if (file.isDirectory()) {
                deleteDirectory(file.listFiles());
            }
            file.delete();
        }
    }

    /**
     * 将文件大小转换成通俗形式
     *
     * @param fileSize 文件大小
     * @return
     */
    public static String getHumanSize(long fileSize) {
        // 设置位数
        DecimalFormat df = new DecimalFormat("#.##");
        String[] units = new String[]{"字节", "KB", "MB", "GB"};
        int i = 0;
        double size = fileSize;
        while (size > 1024) {
            size = size / 1024;
            i++;
        }
        return (df.format(size)) + units[i];
    }

    /**
     * 将文件下载到客户端浏览器
     *
     * @param fileName
     * @param source
     * @param contentType
     * @param response
     * @throws IOException
     */
    public static void download(String fileName, File source, String contentType, boolean inline, HttpServletResponse response) throws IOException {
        OutputStream outp = null;
        FileInputStream br = null;
        int len = 0;
        try {
            br = new FileInputStream(source);
            response.reset();
            outp = response.getOutputStream();
            response.setContentType(contentType);
            response.setContentLength((int) source.length());
            String header = (inline ? "inline" : "attachment") + ";filename=" + new String(fileName.getBytes(), "ISO8859-1");
            response.addHeader("Content-Disposition", header);
            byte[] buf = new byte[1024];
            while ((len = br.read(buf)) != -1) {
                outp.write(buf, 0, len);
            }
            outp.flush();
            outp.close();
            source.delete();
        } finally {
            if (br != null) {
                if (0 == br.available()) {
                    br.close();
                }
            }
        }
    }

    /**
     * 获得后缀
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName) {
        if (fileName != null && fileName.indexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return null;
    }

    /**
     * 获取无后缀名的文件名
     */
    public static String getFileNameNoSuffix(String fileName) {
        if (fileName != null && fileName.indexOf(".") != -1) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * 文件上传
     */
    public static String upload(HttpServletRequest request1, String field) throws IOException {
        if (request1 instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest request = (MultipartHttpServletRequest) request1;
            Map<String, MultipartFile> fileMap = request.getFileMap();
            // 处理上传图片
            MultipartFile fileItem = fileMap.get(field);
            if (null != fileItem) {
                // 返回服务器保存并可供页面直接访问的路径
                return upload(request, fileItem);
            }
        }
        return null;
    }

    /**
     * 文件上传
     */
    public static String upload(HttpServletRequest request, MultipartFile fileItem) throws IOException {
        /** 上传文件名,例如：Wildlife.wmv*/
        String photoName = fileItem.getOriginalFilename();
        // 服务器保存并可供页面直接访问路径
//				String uploadWebPath = "upload/img/" + Calendar.getInstance().get(Calendar.YEAR) + "/"
//						+ (Calendar.getInstance().get(Calendar.MONTH) + 1);
        String uploadWebPath = "upload";
        /** 服务器保存硬盘路径，例如：“D:\apache-tomcat-8.0.21\\webapps\\epzh\\upload\”*/
        String uploadDiskPath = request.getServletContext().getRealPath("/" + uploadWebPath);
//        String uploadDiskPath = PropertiesUtils.getProperty("import.path") + uploadWebPath;
        File uploadFolder = new File(uploadDiskPath);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

//        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // 服务器保存文件名
//        String newFileName = photoName.substring(0, photoName.lastIndexOf(".")) + "."
//                + format.format(new Date()) + photoName.substring(photoName.lastIndexOf("."));
        /** 服务器保存文件名的绝对路径*/
        String outFilePath = uploadDiskPath + "/" + photoName;

        /** 将附件写入硬盘*/
        //org.apache.commons.io.FileUtils.copyInputStreamToFile(fileItem.getInputStream(), new File(outFilePath));

        // 返回服务器保存并可供页面直接访问的路径
        return outFilePath;
    }

    /**
     * 如果上传文件类型为视频，将上传的视频转为flv文件
     * @param t 
     * @param request
     * @param file
     * @throws IOException
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */
	public static void copyVideoFile(Object t, HttpServletRequest request, MultipartFile file) throws IOException, 
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Method _mt = t.getClass().getMethod("getMtlxnm");
		String mtlxnm = (String) _mt.invoke(t);
		if (null != mtlxnm && mtlxnm.equals("04")) {
			//epzh服务器路径
			//String tapth = SysContent.getRequest().getServletContext().getRealPath("");
			//将视频文件上传到服务器绝对路径
			String uploadDiskPath = FileUtils.upload(request, file);
			
			//调用视频转flv文件
			//ConverVideo cv = new ConverVideo(uploadDiskPath, tapth);
//			Map<String, String> map = cv.beginConver();
//
//			if(StringUtils.isNotBlank(map.get("fileName"))){
//				//ep服务器绝对路径(读取文件的路径)
//				String epServicePath = tapth.substring(0, tapth.length()-3) + File.separator + "resources\\";
//
//				FileUtils.copyFile(new File(tapth + map.get("filepath")), new File(epServicePath + "video\\" + map.get("fileName")));
//				FileUtils.copyFile(new File(tapth + map.get("videoCaptureUrl")), new File(epServicePath + "v_img\\" + map.get("videoCaptureName")));
//				cv.deleteFile(tapth + map.get("videoCaptureUrl"));
			//}
		}
	}

}
