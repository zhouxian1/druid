package com.hjj.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Mr成
 * @date 2015年7月17日
 */
public class ResponseWrap {
    private Logger logger = LoggerFactory.getLogger(ResponseWrap.class);

    private CloseableHttpResponse response;
    private CloseableHttpClient httpClient;
    private HttpEntity entity;
    private HttpRequestBase request;
    private HttpClientContext context;
    private static ObjectMapper mapper;

    public ResponseWrap(CloseableHttpClient httpClient, HttpRequestBase request, CloseableHttpResponse response, HttpClientContext context, ObjectMapper _mapper){
        this.response = response;
        this.httpClient = httpClient;
        this.request = request;
        this.context = context;
        mapper = _mapper;

        try {
            HttpEntity entity = response.getEntity();
            if(entity != null) {
                this.entity =  new BufferedHttpEntity(entity);
            } else {
                this.entity = new BasicHttpEntity();
            }

            EntityUtils.consumeQuietly(entity);
            this.response.close();
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }


    /**
     * 终止请求
     * @author Mr成
     * @date 2015年7月18日
     */
    public void abort(){
        request.abort();
    }

    /**
     * 获取重定向的地址
     * @author Mr成
     * @date 2015年7月18日
     * @return
     */
    public List<URI> getRedirectLocations(){
        return context.getRedirectLocations();
    }

    /**
     * 关闭连接
     * @author Mr成
     * @date 2015年7月18日
     */
    @SuppressWarnings("deprecation")
    public void shutdown(){
        httpClient.getConnectionManager().shutdown();
    }

    /**
     * 获取响应内容为String,默认编码为 "UTF-8"
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public String getString() {
        return getString(Consts.UTF_8);
    }

    /**
     * 获取响应内容为String
     * @author Mr成
     * @date 2015年7月17日
     * @param defaultCharset 指定编码
     * @return
     */
    public String getString(Charset defaultCharset) {
        try {
            return EntityUtils.toString(entity, defaultCharset);
        } catch (ParseException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 获取响应的类型
     * @author Mr成
     * @date 2015年7月18日
     * @return
     */
    public Header getContentType() {
        return entity.getContentType();
    }

    /**
     * 获取响应编码,如果是文本的话
     * @author Mr成
     * @date 2015年7月18日
     * @return
     */
    public Charset getCharset() {
        ContentType contentType = ContentType.get(entity);
        if(contentType == null) return null;
        return contentType.getCharset();
    }

    /**
     * 获取响应内容为字节数组
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public byte[] getByteArray() {
        try {
            return EntityUtils.toByteArray(entity);
        } catch (ParseException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 获取所有Header
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public Header[] getAllHeaders() {
        return response.getAllHeaders();
    }

    /**
     * 获取知道名称的Header列表
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public Header[] getHeaders(String name) {
        return response.getHeaders(name);
    }

    /**
     * 获取响应状态信息
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public StatusLine getStatusLine(){
        return response.getStatusLine();
    }

    /**
     * 移除指定name的Header列表
     * @author Mr成
     * @date 2015年7月17日
     * @param name
     */
    public void removeHeaders(String name){
        response.removeHeaders(name);
    }

    /**
     * 移除指定的Header
     * @author Mr成
     * @date 2015年7月17日
     * @param header
     */
    public void removeHeader(Header header){
        response.removeHeader(header);
    }

    /**
     * 移除指定的Header
     * @author Mr成
     * @date 2015年7月17日
     * @param name
     * @param value
     */
    public void removeHeader(String name, String value){
        response.removeHeader(new BasicHeader(name, value));
    }

    /**
     * 是否存在指定name的Header
     * @author Mr成
     * @date 2015年7月17日
     * @param name
     * @return
     */
    public boolean containsHeader(String name){
        return response.containsHeader(name);
    }

    /**
     * 获取Header的迭代器
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public HeaderIterator headerIterator(){
        return response.headerIterator();
    }

    /**
     * 获取协议版本信息
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public ProtocolVersion getProtocolVersion(){
        return response.getProtocolVersion();
    }

    /**
     * 获取CookieStore
     * @author Mr成
     * @date 2015年7月18日
     * @return
     */
    public CookieStore getCookieStore(){
        return context.getCookieStore();
    }

    /**
     * 获取Cookie列表
     * @author Mr成
     * @date 2015年7月18日
     * @return
     */
    public List<Cookie> getCookies(){
        return getCookieStore().getCookies();
    }

    /**
     * 获取InputStream,需要手动关闭流
     * @author Mr成
     * @date 2015年7月17日
     * @return
     */
    public InputStream getInputStream(){
        try {
            return entity.getContent();
        } catch (IllegalStateException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 获取BufferedReader
     * @author Mr成
     * @date 2015年7月18日
     * @return
     */
    public BufferedReader getBufferedReader(){
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharset()));
    }

    /**
     * 响应内容写入到文件
     * @author Mr成
     * @date 2015年7月17日
     * @param filePth 路径
     */
    public void transferTo(String filePth) {
        transferTo(new File(filePth));
    }

    /**
     * 响应内容写入到文件
     * @author Mr成
     * @date 2015年7月17日
     * @param file
     */
    public void transferTo(File file) {
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
            transferTo(fileOutputStream);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 写入到OutputStream,并不会关闭OutputStream
     * @author Mr成
     * @date 2015年7月17日
     * @param outputStream OutputStream
     */
    public void transferTo(OutputStream outputStream) {
        try {
            entity.writeTo(outputStream);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 获取JSON对象
     * @author Mr成
     * @date 2015年7月24日
     * @param clazz
     * @return
     */
    public <T> T getJson(Class<T> clazz) {
        try {
            return mapper.readValue(getByteArray(), clazz);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 把Json转换成List
     * @author Mr成
     * @date 2015年7月24日
     * @param clazz
     * @return
     */
    public <T> List<T> getJsonList(Class<?> clazz) {
        try {
            return mapper.readValue(getByteArray(), new TypeReference<List<T>>() {});
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}