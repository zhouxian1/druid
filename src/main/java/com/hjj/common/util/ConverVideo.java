package com.hjj.common.util;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverVideo {
	    private String PATH;
	    private String tomcatPath;
	    private String filerealname; // 文件名 不包括扩展名
	    private String filename; // 包括扩展名
	    private String videofolder; // 别的格式视频的目录
	    private String flvfolder ; // flv视频的目录
	    private String ffmpegpath ; // ffmpeg.exe的目录
	    private String mencoderpath  ; // mencoder的目录
	    private String videoRealPath ; // 截图的视频目录;
	    private String imageRealPath ; // 截图的存放目录
	    private String flvPath ="video/flv/"; // MAP 返回调用的视频路径;
	    private String flvImagePath="video/img/"; // MAP 返回截取图片的路径
	 
	    public ConverVideo() {
	    
	    }

		public ConverVideo(String path,String tpath) {
	        PATH = path;
	        tomcatPath =tpath;
	        videofolder =tomcatPath +"video\\other\\";
	        flvfolder =tomcatPath+"video\\flv\\";
	        ffmpegpath =tomcatPath+"video\\FFMpeg\\ffmpeg.exe";
	        mencoderpath =tomcatPath+"video\\MEncoder\\mencoder.exe";
	        videoRealPath =tomcatPath+"video\\flv\\";
	        imageRealPath =tomcatPath+"video\\img\\";
	    }
	 
	    public String getPATH() {
	        return PATH;
	    }
	 
	    public void setPATH(String path) {
	        PATH = path;
	    }
	    
	    public String getTomcatPath() {
			return tomcatPath;
		}

		public void setTomcatPath(String tomcatPath) {
			this.tomcatPath = tomcatPath;
		}
	 
	    public Map<String,String> beginConver() {
	    	Map<String,String> map=new HashMap<String,String>();
	    	/**获取源文件*/
	        File fi = new File(PATH);
	        //带后缀的文件名称：Wildlife.wmv
	        filename = fi.getName();
	        //文件名：wildlife
	        filerealname = filename.substring(0, filename.lastIndexOf(".")).toLowerCase();
	        System.out.println("----接收到文件(" + PATH + ")需要转换-------------------------- ");
	        if (!checkfile(PATH)) {
	        	 map.put("success", "1");
	        	 map.put("error", "文件不存在");
	            return map;
	        }
	        System.out.println("----开始转文件(" + PATH + ")-------------------------- ");
	        /**根据upload文件夹中的原文件，转成flv文件，且存储到video/flv文件夹中，最后将upload文件夹中的视频原文件删除*/
	        if (process()) {
	        	/**生成视频文件的截图，且保存到：video\img\wildlife.jpg*/
	            if (processImg()) {
	            	map.put("success", "0");
	            	map.put("filepath", flvPath+filerealname+".flv"); //flv文件路径：video/flv/wildlife.flv
	            	map.put("videoCaptureUrl", flvImagePath+filerealname+".jpg"); //视频截图对应图片路径：video/img/wildlife.jpg
	            	map.put("fileName", filerealname+".flv"); //flv文件名称：wildlife.flv
	            	map.put("videoCaptureName", filerealname+".jpg"); //flv截图图片名称wildlife.jpg
	            } else {
	            	map.put("success", "1");
		        	map.put("error", "截图失败");
	            }
	            PATH = null;
	            tomcatPath = null;
	            return map;
	        } else {
	        	map.put("success", "1");
	        	map.put("error", "转换失败");
	            return map;
	        }
	    }
	 
	    public boolean processImg() {
	 
	        List commend = new java.util.ArrayList();
	        commend.add(ffmpegpath);
	        commend.add("-i");
	        commend.add(videoRealPath + filerealname + ".flv");
	        commend.add("-y");
	        commend.add("-f");
	        commend.add("image2");
	        commend.add("-ss");
	        commend.add("2");
	        commend.add("-t");
	        commend.add("0.001");
	        commend.add("-s");
	        commend.add("400x240");
	        commend.add(imageRealPath + filerealname + ".jpg");
	        try {
	            ProcessBuilder builder = new ProcessBuilder();
	            builder.command(commend);
	            Process p = builder.start();
	            doWaitFor(p);
	            p.destroy();
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
	    private boolean process() {
	        int type = checkContentType();
	        boolean status = false;
	        if (type == 0) {
	 
	            // status = processFLV(PATH);// 直接将文件转为flv文件
	            status = processFLV(PATH);
	        } else if (type == 1) {
	            String avifilepath = processAVI(type);
	            if (avifilepath == null)
	                return false;
	            // avi文件没有得到
	            else {
	                status = processFLV(avifilepath);// 将avi转为flv
	            }
	        }
	        return status;
	    }
	 
	    private int checkContentType() {
	        String type = PATH.substring(PATH.lastIndexOf(".") + 1, PATH.length()).toLowerCase();
	        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
	        if (type.equals("avi")) {
	            return 0;
	        } else if (type.equals("mpg")) {
	            return 0;
	        } else if (type.equals("wmv")) {
	            return 0;
	        } else if (type.equals("3gp")) {
	            return 0;
	        } else if (type.equals("mov")) {
	            return 0;
	        } else if (type.equals("mp4")) {
	            return 0;
	        } else if (type.equals("asf")) {
	            return 0;
	        } else if (type.equals("asx")) {
	            return 0;
	        } else if (type.equals("flv")) {
	            return 0;
	        }
	        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
	        // 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
	        else if (type.equals("wmv9")) {
	            return 1;
	        } else if (type.equals("rm")) {
	            return 1;
	        } else if (type.equals("rmvb")) {
	            return 1;
	        }
	        return 9;
	    }
	 
	    private boolean checkfile(String path) {
	        File file = new File(path);
	        if (!file.isFile()) {
	            return false;
	        } else {
	            return true;
	        }
	    }
	 
	    // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
	    private String processAVI(int type) {
	        List commend = new java.util.ArrayList();
	        commend.add(mencoderpath);
	        commend.add(PATH);
	        commend.add("-oac");
	        commend.add("mp3lame");
	        commend.add("-lameopts");
	        commend.add("preset=64");
	        commend.add("-ovc");
	        commend.add("xvid");
	        commend.add("-xvidencopts");
	        commend.add("bitrate=600");
	        commend.add("-of");
	        commend.add("avi");
	        commend.add("-o");
	        commend.add(videofolder + filerealname + ".avi");
	        // 命令类型：mencoder 1.rmvb -oac mp3lame -lameopts preset=64 -ovc xvid
	        // -xvidencopts bitrate=600 -of avi -o rmvb.avi
	        try {
	            ProcessBuilder builder = new ProcessBuilder();
	            builder.command(commend);
	            Process p = builder.start();
	 
	            doWaitFor(p);
	            return videofolder + filerealname + ".avi";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 
	    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
	    private boolean processFLV(String oldfilepath) {
	 
	        if (!checkfile(PATH)) {
	            return false;
	        }
	 
	        List commend = new java.util.ArrayList();
	        commend.add(ffmpegpath);
	        commend.add("-i");
	        commend.add(oldfilepath);
	        commend.add("-ab");
	        commend.add("64");
	        commend.add("-acodec");
	        commend.add("mp3");
	        commend.add("-ac");
	        commend.add("2");
	        commend.add("-ar");
	        commend.add("22050");
	        commend.add("-qscale");
	        commend.add("6");
	        commend.add("-r");
	        commend.add("24");
	        commend.add("-y");
	        commend.add(flvfolder + filerealname + ".flv");
	        try {
	            ProcessBuilder builder = new ProcessBuilder();
	            String cmd = commend.toString();
	            builder.command(commend);
	            Process p = builder.start();
	            doWaitFor(p);
	            p.destroy();
	            //删除upload文件夹中的原文件
	            deleteFile(oldfilepath);
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	 
	    public int doWaitFor(Process p) {
	        InputStream in = null;
	        InputStream err = null;
	        int exitValue = -1; 
	        try {
	            System.out.println("comeing");
	            in = p.getInputStream();
	            err = p.getErrorStream();
	            boolean finished = false; 
	 
	            while (!finished) {
	                try {
	                    while (in.available() > 0) {
	                        Character c = new Character((char) in.read());
	                    }
	                    while (err.available() > 0) {
	                        Character c = new Character((char) err.read());
	                    }	 
	                    exitValue = p.exitValue();
	                    finished = true;	 
	                } catch (IllegalThreadStateException e) {
	                    Thread.currentThread().sleep(500);
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("doWaitFor();: unexpected exception - "
	                    + e.getMessage());
	        } finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	 
	            } catch (IOException e) {
	                System.out.println(e.getMessage());
	            }
	            if (err != null) {
	                try {
	                    err.close();
	                } catch (IOException e) {
	                    System.out.println(e.getMessage());
	                }
	            }
	        }
	        return exitValue;
	    }
	 
	    public void deleteFile(String filepath) {
	        File file = new File(filepath);
	        if (null != PATH && PATH.equals(filepath)) {
	            if (file.delete()) {
	                System.out.println("文件" + filepath + "已删除");
	            }
	        } else {
	            if (file.delete()) {
	                System.out.println("文件" + filepath + "已删除 ");
	            }
	            if (null != PATH){
		            File filedelete2 = new File(PATH);
		            if (filedelete2.delete()) {
		                System.out.println("文件" + PATH + "已删除");
		            }
	            }
	        }
	    }
	 
	  
	}


