package com.hjj.common.util;

/**
 * Created by JQ3 on 2016/6/7.
 */
public class SysBehaviorTraceEnable {

    private static Boolean enable = true;

    public static void enable(){
        enable = true;
    }

    public static void disenable(){
        enable = false;
    }

    public static boolean isEnable(){
        return enable;
    }
}
