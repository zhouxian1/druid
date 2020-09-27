package com.hjj.common.util;

import java.io.*;

public class ObjectToByteUtils {

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) throws IOException{
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            throw ex;
        } finally {
            if(null != oos){
                oos.close();
            }
            if(null != bos){
                bos.close();
            }
        }
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) throws IOException,ClassNotFoundException {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        } finally {
            if(null != bis){
                bis.close();
            }
            if(null != ois){
                ois.close();
            }
        }
    }
}
