package com.hjj.common.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zzy on 2015/11/27.
 */
public class RuntimeUtils {

    private static final Logger logger = Logger.getLogger(RuntimeUtils.class);
    /**
     * 从数据库导出项目数据
     * @return
     */
    public static String exec(String cmds) {
    	logger.warn("执行脚本------"+cmds);
        String info = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            info = "导入出错";
        }
        boolean shouldClose = false;
        try {
            InputStreamReader isr = new InputStreamReader(process.getErrorStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                logger.info(line);
                if (line.indexOf("????") != -1) {
                    shouldClose = true;
                    break;
                }
            }
        } catch (IOException ioe) {
            shouldClose = true;
            info = "运行命令出错";
        }
        if (shouldClose)
            process.destroy();
        int exitVal;
        try {
            exitVal = process.waitFor();
            logger.info(exitVal);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
            info = "运行命令出错";
        }
        return info;
    }
}
