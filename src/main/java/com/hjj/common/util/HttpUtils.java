package com.hjj.common.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	private HttpRequestBase request; //请求对象
	private EntityBuilder builder; //Post, put请求的参数
	private URIBuilder uriBuilder; //get, delete请求的参数
	private LayeredConnectionSocketFactory socketFactory; //连接工厂
	private HttpClientBuilder clientBuilder; //构建httpclient
	private CloseableHttpClient httpClient; //
	private CookieStore cookieStore; //cookie存储器
	private Builder config; //请求的相关配置
	private boolean isHttps; //是否是https请求
	private int type; //请求类型1-post, 2-get, 3-put, 4-delete



	private HttpUtils (HttpRequestBase request) {
		this.request = request;

		this.clientBuilder = HttpClientBuilder.create();
		this.isHttps = request.getURI().getScheme().equalsIgnoreCase("https");
		this.config = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
		this.cookieStore = new BasicCookieStore();

		if (request instanceof HttpPost) {
			this.type = 1;
			this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());

		} else if(request instanceof HttpGet) {
			this.type = 2;
			this.uriBuilder = new URIBuilder();

		} else if(request instanceof HttpPut) {
			this.type = 3;
			this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());

		} else if(request instanceof HttpDelete) {
			this.type = 4;
			this.uriBuilder = new URIBuilder();
		}
	}

	private HttpUtils(HttpRequestBase request, HttpUtils clientUtils) {
		this(request);
		this.httpClient = clientUtils.httpClient;
		this.config = clientUtils.config;
		this.setHeaders(clientUtils.getAllHeaders());
		this.SetCookieStore(clientUtils.cookieStore);
	}

	private static HttpUtils create(HttpRequestBase request) {
		return new HttpUtils(request);
	}

	private static HttpUtils create(HttpRequestBase request, HttpUtils clientUtils) {
		return new HttpUtils(request, clientUtils);
	}

	/**
	 * 创建post请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils post(String url) {
		return create(new HttpPost(url));
	}

	/**
	 * 创建get请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils get(String url) {
		return create(new HttpGet(url));
	}

	/**
	 * 创建put请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils put(String url) {
		return create(new HttpPut(url));
	}

	/**
	 * 创建delete请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils delete(String url) {
		return create(new HttpDelete(url));
	}

	/**
	 * 创建post请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils post(URI uri) {
		return create(new HttpPost(uri));
	}

	/**
	 * 创建get请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils get(URI uri) {
		return create(new HttpGet(uri));
	}

	/**
	 * 创建put请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils put(URI uri) {
		return create(new HttpPut(uri));
	}

	/**
	 * 创建delete请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils delete(URI uri) {
		return create(new HttpDelete(uri));
	}

	/**
	 * 创建post请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils post(String url, HttpUtils clientUtils) {
		return create(new HttpPost(url), clientUtils);
	}

	/**
	 * 创建get请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils get(String url, HttpUtils clientUtils) {
		return create(new HttpGet(url), clientUtils);
	}

	/**
	 * 创建put请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils put(String url, HttpUtils clientUtils) {
		return create(new HttpPut(url), clientUtils);
	}

	/**
	 * 创建delete请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param url 请求地址
	 * @return
	 */
	public static HttpUtils delete(String url, HttpUtils clientUtils) {
		return create(new HttpDelete(url), clientUtils);
	}

	/**
	 * 创建post请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils post(URI uri, HttpUtils clientUtils) {
		return create(new HttpPost(uri), clientUtils);
	}

	/**
	 * 创建get请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils get(URI uri, HttpUtils clientUtils) {
		return create(new HttpGet(uri), clientUtils);
	}

	/**
	 * 创建put请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils put(URI uri, HttpUtils clientUtils) {
		return create(new HttpPut(uri), clientUtils);
	}

	/**
	 * 创建delete请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param uri 请求地址
	 * @return
	 */
	public static HttpUtils delete(URI uri, HttpUtils clientUtils) {
		return create(new HttpDelete(uri), clientUtils);
	}

	/**
	 * 添加参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public HttpUtils setParameters(final NameValuePair ...parameters) {
		if (builder != null) {
			builder.setParameters(parameters);
		} else {
			uriBuilder.setParameters(Arrays.asList(parameters));
		}
		return this;
	}

	/**
	 * 添加参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param name
	 * @param value
	 * @return
	 */
	public HttpUtils addParameter(final String name, final String value) {
		if (builder != null) {
			builder.getParameters().add(new BasicNameValuePair(name, value));
		} else {
			uriBuilder.addParameter(name, value);
		}
		return this;
	}

	/**
	 * 添加参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public HttpUtils addParameters(final NameValuePair ...parameters) {
		if (builder != null) {
			builder.getParameters().addAll(Arrays.asList(parameters));
		} else {
			uriBuilder.addParameters(Arrays.asList(parameters));
		}
		return this;
	}

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public HttpUtils setParameters(final Map<String, String> parameters) {
		NameValuePair [] values = new NameValuePair[parameters.size()];
		int i = 0;

		for (Entry<String, String> parameter : parameters.entrySet()) {
			values[i++] = new BasicNameValuePair(parameter.getKey(), parameter.getValue());
		}

		setParameters(values);
		return this;
	}

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param file
	 * @return
	 */
	public HttpUtils setParameter(final File file) {
		if(builder != null) {
			builder.setFile(file);
		} else {
			throw new UnsupportedOperationException();
		}
		return this;
	}

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param binary
	 * @return
	 */
	public HttpUtils setParameter(final byte[] binary) {
		if(builder != null) {
			builder.setBinary(binary);
		} else {
			throw new UnsupportedOperationException();
		}
		return this;
	}

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param serializable
	 * @return
	 */
	public HttpUtils setParameter(final Serializable serializable) {
		if(builder != null) {
			builder.setSerializable(serializable);
		} else {
			throw new UnsupportedOperationException();
		}
		return this;
	}

	/**
	 * 设置参数为Json对象
	 * @author Mr成
	 * @date 2015年7月27日
	 * @param parameter 参数对象
	 * @return
	 */
	public HttpUtils setParameterJson(final Object parameter) {
		if(builder != null) {
			try {
				builder.setBinary(mapper.writeValueAsBytes(parameter));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			throw new UnsupportedOperationException();
		}
		return this;
	}

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param stream
	 * @return
	 */
	public HttpUtils setParameter(final InputStream stream) {
		if(builder != null) {
			builder.setStream(stream);
		} else {
			throw new UnsupportedOperationException();
		}
		return this;
	}

	/**
	 * 设置请求参数,会覆盖之前的参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param text
	 * @return
	 */
	public HttpUtils setParameter(final String text) {
		if(builder != null) {
			builder.setText(text);
		} else {
			uriBuilder.setParameters(URLEncodedUtils.parse(text, Consts.UTF_8));
		}
		return this;
	}

	/**
	 * 设置内容编码
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param encoding
	 * @return
	 */
	public HttpUtils setContentEncoding(final String encoding) {
		if(builder != null) builder.setContentEncoding(encoding);
		return this;
	}

	/**
	 * 设置ContentType
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param contentType
	 * @return
	 */
	public HttpUtils setContentType(ContentType contentType) {
		if(builder != null) builder.setContentType(contentType);
		return this;
	}

	/**
	 * 设置ContentType
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param mimeType
	 * @param charset 内容编码
	 * @return
	 */
	public HttpUtils setContentType(final String mimeType, final Charset charset) {
		if(builder != null) builder.setContentType(ContentType.create(mimeType, charset));
		return this;
	}

	/**
	 * 添加参数
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param parameters
	 * @return
	 */
	public HttpUtils addParameters(Map<String, String> parameters) {
		List<NameValuePair> values = new ArrayList<>(parameters.size());

		for (Entry<String, String> parameter : parameters.entrySet()) {
			values.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
		}

		if(builder != null) {
			builder.getParameters().addAll(values);
		} else {
			uriBuilder.addParameters(values);
		}
		return this;
	}

	/**
	 * 添加Header
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param name
	 * @param value
	 * @return
	 */
	public HttpUtils addHeader(String name, String value) {
		request.addHeader(name, value);
		return this;
	}

	/**
	 * 添加Header
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param headers
	 * @return
	 */
	public HttpUtils addHeaders(Map<String, String> headers) {
		for (Entry<String, String> header : headers.entrySet()) {
			request.addHeader(header.getKey(), header.getValue());
		}

		return this;
	}

	/**
	 * 设置Header,会覆盖所有之前的Header
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param headers
	 * @return
	 */
	public HttpUtils setHeaders(Map<String, String> headers) {
		Header [] headerArray = new Header[headers.size()];
		int i = 0;

		for (Entry<String, String> header : headers.entrySet()) {
			headerArray[i++] = new BasicHeader(header.getKey(), header.getValue());
		}

		request.setHeaders(headerArray);
		return this;
	}

	public HttpUtils setHeaders(Header [] headers) {
		request.setHeaders(headers);
		return this;
	}

	/**
	 * 获取所有Header
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	public Header[] getAllHeaders() {
		return request.getAllHeaders();
	}

	/**
	 * 移除指定name的Header列表
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param name
	 */
	public HttpUtils removeHeaders(String name){
		request.removeHeaders(name);
		return this;
	}

	/**
	 * 移除指定的Header
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param header
	 */
	public HttpUtils removeHeader(Header header){
		request.removeHeader(header);
		return this;
	}

	/**
	 * 移除指定的Header
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param name
	 * @param value
	 */
	public HttpUtils removeHeader(String name, String value){
		request.removeHeader(new BasicHeader(name, value));
		return this;
	}

	/**
	 * 是否存在指定name的Header
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param name
	 * @return
	 */
	public boolean containsHeader(String name){
		return request.containsHeader(name);
	}

	/**
	 * 获取Header的迭代器
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	public HeaderIterator headerIterator(){
		return request.headerIterator();
	}

	/**
	 * 获取协议版本信息
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	public ProtocolVersion getProtocolVersion(){
		return request.getProtocolVersion();
	}

	/**
	 * 获取请求Url
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	public URI getURI(){
		return request.getURI();
	}

	/**
	 * 设置请求Url
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	public HttpUtils setURI(URI uri){
		request.setURI(uri);
		return this;
	}

	/**
	 * 设置请求Url
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	public HttpUtils setURI(String uri){
		return setURI(URI.create(uri));
	}

	/**
	 * 设置一个CookieStore
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param cookieStore
	 * @return
	 */
	public HttpUtils SetCookieStore(CookieStore cookieStore){
		if(cookieStore == null) return this;
		this.cookieStore = cookieStore;
		return this;
	}

	/**
	 * 添加Cookie
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param cookie
	 * @return
	 */
	public HttpUtils addCookie(Cookie ...cookies){
		if(cookies == null) return this;

		for (int i = 0; i < cookies.length; i++) {
			cookieStore.addCookie(cookies[i]);
		}
		return this;
	}

	/**
	 * 设置网络代理
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param hostname
	 * @param port
	 * @return
	 */
	public HttpUtils setProxy(String hostname, int port) {
		HttpHost proxy = new HttpHost(hostname, port);
		return setProxy(proxy);
	}

	/**
	 * 设置网络代理
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param hostname
	 * @param port
	 * @param schema
	 * @return
	 */
	public HttpUtils setProxy(String hostname, int port, String schema) {
		HttpHost proxy = new HttpHost(hostname, port, schema);
		return setProxy(proxy);
	}

	/**
	 * 设置网络代理
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param address
	 * @return
	 */
	public HttpUtils setProxy(InetAddress address) {
		HttpHost proxy = new HttpHost(address);
		return setProxy(proxy);
	}

	/**
	 * 设置网络代理
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param host
	 * @return
	 */
	public HttpUtils setProxy(HttpHost host) {
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(host);
		clientBuilder.setRoutePlanner(routePlanner);
		return this;
	}

	/**
	 * 设置双向认证的JKS
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param jksFilePath jks文件路径
	 * @param password 密码
	 * @return
	 */
	public HttpUtils setJKS(String jksFilePath, String password) {
		return setJKS(new File(jksFilePath), password);
	}

	/**
	 * 设置双向认证的JKS
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param jksFile jks文件
	 * @param password 密码
	 * @return
	 */
	public HttpUtils setJKS(File jksFile, String password) {
		try (InputStream instream = new FileInputStream(jksFile)) {
			return setJKS(instream, password);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 设置双向认证的JKS, 不会关闭InputStream
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param instream jks流
	 * @param password 密码
	 * @return
	 */
	public HttpUtils setJKS(InputStream instream, String password) {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(instream, password.toCharArray());
			return setJKS(keyStore);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 设置双向认证的JKS
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param keyStore jks
	 * @return
	 */
	public HttpUtils setJKS(KeyStore keyStore) {
		try {
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(keyStore).build();
			socketFactory = new SSLConnectionSocketFactory(sslContext);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		return this;
	}

	/**
	 * 设置Socket超时时间,单位:ms
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param socketTimeout
	 * @return
	 */
	public HttpUtils setSocketTimeout(int socketTimeout){
		config.setSocketTimeout(socketTimeout);
		return this;
	}

	/**
	 * 设置连接超时时间,单位:ms
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param connectTimeout
	 * @return
	 */
	public HttpUtils setConnectTimeout(int connectTimeout) {
		config.setConnectTimeout(connectTimeout);
		return this;
	}

	/**
	 * 设置请求超时时间,单位:ms
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param connectionRequestTimeout
	 * @return
	 */
	public HttpUtils setConnectionRequestTimeout(int connectionRequestTimeout) {
		config.setConnectionRequestTimeout(connectionRequestTimeout);
		return this;
	}

	/**
	 * 设置是否允许服务端循环重定向
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param circularRedirectsAllowed
	 * @return
	 */
	public HttpUtils setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
		config.setCircularRedirectsAllowed(circularRedirectsAllowed);
		return this;
	}

	/**
	 * 设置是否启用调转
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param redirectsEnabled
	 * @return
	 */
	public HttpUtils setRedirectsEnabled(boolean redirectsEnabled) {
		config.setRedirectsEnabled(redirectsEnabled);
		return this;
	}

	/**
	 * 设置重定向的次数
	 * @author Mr成
	 * @date 2015年7月18日
	 * @param maxRedirects
	 * @return
	 */
	public HttpUtils maxRedirects(int maxRedirects){
		config.setMaxRedirects(maxRedirects);
		return this;
	}

	/**
	 * 执行请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	public ResponseWrap execute() {
		settingRequest();
		if(httpClient == null) {
			httpClient = clientBuilder.build();
		}

		try {
			HttpClientContext context = HttpClientContext.create();
			CloseableHttpResponse response = httpClient.execute(request, context);
			return new ResponseWrap(httpClient, request, response, context, mapper);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 执行请求
	 * @author Mr成
	 * @date 2015年7月17日
	 * @param responseHandler
	 * @return
	 */
	public <T> T execute(final ResponseHandler<? extends T> responseHandler) {
		settingRequest();
		if(httpClient == null) httpClient = clientBuilder.build();

		try {
			return httpClient.execute(request, responseHandler);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
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
	 * 获取LayeredConnectionSocketFactory 使用ssl单向认证
	 * @author Mr成
	 * @date 2015年7月17日
	 * @return
	 */
	private LayeredConnectionSocketFactory getSSLSocketFactory() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return sslsf;
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void settingRequest() {
		URI uri = null;
		if(uriBuilder != null && uriBuilder.getQueryParams().size() != 0) {
			try {
				uri = uriBuilder.setPath(request.getURI().toString()).build();
			} catch (URISyntaxException e) {
				logger.warn(e.getMessage(), e);
			}
		}

		HttpEntity httpEntity = null;

		switch (type) {
			case 1:
				httpEntity = builder.build();
				if(httpEntity.getContentLength() > 0) ((HttpPost)request).setEntity(builder.build());
				break;

			case 2:
				HttpGet get = ((HttpGet)request);
				if (uri != null)  get.setURI(uri);
				break;

			case 3:
				httpEntity = builder.build();
				if(httpEntity.getContentLength() > 0) ((HttpPut)request).setEntity(httpEntity);
				break;

			case 4:
				HttpDelete delete = ((HttpDelete)request);
				if (uri != null) delete.setURI(uri);

				break;
		}

		if (isHttps && socketFactory != null ) {
			clientBuilder.setSSLSocketFactory(socketFactory);

		} else if(isHttps) {
			clientBuilder.setSSLSocketFactory(getSSLSocketFactory());
		}

		clientBuilder.setDefaultCookieStore(cookieStore);
		request.setConfig(config.build());
	}



	//json转换器
	public static ObjectMapper mapper = new ObjectMapper();
	static{
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.getFactory().enable(Feature.ALLOW_COMMENTS);
		mapper.getFactory().enable(Feature.ALLOW_SINGLE_QUOTES);
	}
}