package com.tmoncorp.crawler.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;
import java.util.Random;

public class HttpConnector {
	private final Logger LOG = LoggerFactory.getLogger(HttpConnector.class);


//	private static final String USERNAME = "lum-customer-tmon-zone-residential";
//	private static final String PASSWORD = "9442f33a4edd";

	//static(Data Center) - Any Country
//	public static final String USERNAME = "lum-customer-tmon-zone-static";
//	public static final String PASSWORD = "uodixcecqzhr";

	public static final int REQ_TIMEOUT = 45*1000;
	private CloseableHttpClient httpClient;
	private final String url;
	private final HttpHost httpHost;
	private String sessionId;
	private String cookie;

	public HttpConnector(ConnectionBuilder connectionBuilder) throws IOException {
		this.url = connectionBuilder.url;
		if (connectionBuilder.proxy != null) {
			InetSocketAddress inetSocketAddress = ((InetSocketAddress)connectionBuilder.proxy.address());
			this.httpHost = new HttpHost(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
		} else {
			this.httpHost = null;
		}
		this.cookie = connectionBuilder.cookie;
		createSessionConnector(connectionBuilder);
	}

	public void createSessionConnector(ConnectionBuilder connectionBuilder) {
		sessionId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
		newHttpClientConnector(connectionBuilder);
	}

	public void newHttpClientConnector(ConnectionBuilder connectionBuilder) {
		boolean useProxy = connectionBuilder.proxy != null;

		close();
		String login = connectionBuilder.authentication.getUserAuthentication() + (StringUtils.isNotBlank(connectionBuilder.country) ? "-country-"+connectionBuilder.country : "")
				+"-session-" + sessionId;

		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(REQ_TIMEOUT)
				.setConnectionRequestTimeout(REQ_TIMEOUT)
				.build();
		PoolingHttpClientConnectionManager connMgr =
				new PoolingHttpClientConnectionManager();
		connMgr.setDefaultMaxPerRoute(Integer.MAX_VALUE);
		connMgr.setMaxTotal(Integer.MAX_VALUE);
		connMgr.setValidateAfterInactivity(1000);

		HttpClientBuilder httpClientBuilder = HttpClients.custom()
				.setConnectionManager(connMgr)
				.setDefaultRequestConfig(config);
		if (useProxy) {
			CredentialsProvider credProvider = new BasicCredentialsProvider();
			credProvider.setCredentials(new AuthScope(httpHost),
					new UsernamePasswordCredentials(login, connectionBuilder.authentication.getPasswordAuthentication()));
			httpClientBuilder.setProxy(httpHost)
					.setDefaultCredentialsProvider(credProvider);
			LOG.info("Proxy using: " + connectionBuilder.proxy);
		}
		httpClientBuilder.disableAutomaticRetries();//disable it because if it cannot connect then it this proxy ip is blocked. we will handle exception after
		httpClientBuilder.setRedirectStrategy(HttpConnectorUtils.get().createCustomRedirectStrategy());

		httpClient = httpClientBuilder.build();
	}

//	class CustomRedirectStrategy extends DefaultRedirectStrategy {
//		@Override
//		protected URI createLocationURI(String location) throws ProtocolException {
//			try {
//				return super.createLocationURI(location);
//			} catch (Exception e) {
//				URL url = null;
//				try {
//					url = new URL(URLDecoder.decode(location, "UTF-8"));
//					return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
//				} catch (MalformedURLException e1) {
//					e1.printStackTrace();
//				} catch (UnsupportedEncodingException e1) {
//					e1.printStackTrace();
//				}catch (URISyntaxException e1) {
//					e1.printStackTrace();
//				}
//				return null;
//			}
//		}
//	}

	private CloseableHttpResponse connect(HttpMethod method) throws IOException {
		HttpUriRequest request = new HttpGet(url);
		if (method.equals(HttpMethod.POST)) {
			request = new HttpPost(url);
		}
		request.setHeader("User-Agent", RequestApiUtils.getUserAgent());
		if (!StringUtils.isBlank(cookie)) {
			request.setHeader("Cookie", cookie);
		}
		return httpClient.execute(request);
	}

	public Response request(HttpMethod method, HttpContext httpContext) throws IOException {
		String strResponse = "";
		int code = 0;
		String messageError = "";

		try {
			HttpUriRequest request = new HttpGet(url);
			if (method.equals(HttpMethod.POST)) {
				request = new HttpPost(url);
			}
			request.setHeader("User-Agent", RequestApiUtils.getUserAgent());
			if (!StringUtils.isBlank(cookie)) {
				request.setHeader("Cookie", cookie);
			}
			CloseableHttpResponse closeableHttpResponse;
			if (httpContext != null) {
				closeableHttpResponse = httpClient.execute(request, httpContext);
			} else {
				closeableHttpResponse = httpClient.execute(request, httpContext);
			}
			strResponse = HttpConnectorUtils.get().getContentAsString(closeableHttpResponse);
			code = closeableHttpResponse.getStatusLine().getStatusCode();
			messageError = closeableHttpResponse.getStatusLine().getReasonPhrase();
		} catch (Exception e) {
			LOG.error("Fail request() : {}", url);
			throw new IOException(e);
		} finally {
			close();
		}
		return new Response(code, messageError, strResponse, httpContext);
	}

	public void close() {
		if (httpClient != null)
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		httpClient = null;
	}

	public Response request() throws IOException {
		String strResponse = "";
		int code = 0;
		String messageError = "";
		try(CloseableHttpResponse response = connect(HttpMethod.GET)) {
			strResponse = HttpConnectorUtils.get().getContentAsString(response);
			code = response.getStatusLine().getStatusCode();
			messageError = response.getStatusLine().getReasonPhrase();
		} catch (Exception e) {
			LOG.error("Fail request() : {}", url);
			return new Response(code, e.getMessage(), strResponse);
		} finally {
			close();
		}
		return new Response(code, messageError, strResponse);
	}

	public Response request(HttpMethod method) throws IOException {
		String strResponse = "";
		int code = 0;
		String messageError = "";
		try(CloseableHttpResponse response = connect(method)) {
			strResponse = HttpConnectorUtils.get().getContentAsString(response);
			code = response.getStatusLine().getStatusCode();
			messageError = response.getStatusLine().getReasonPhrase();
		} catch (Exception e) {
			LOG.error("Fail request() : {}", url);
			return new Response(code, e.getMessage(), strResponse);
		} finally {
			close();
		}
		return new Response(code, messageError, strResponse);
	}

	public static ConnectionBuilder create() {
		return new ConnectionBuilder();
	}

	public static final class ConnectionBuilder {
		private String url;
		private Proxy proxy;
		private String cookie;
		private String country; // Change proxy to data center, no need country
		private Authentication authentication;

		public ConnectionBuilder(){
			authentication = new Authentication();
		}

		public ConnectionBuilder fromUrl(String url) {
			this.url = url;
			return this;
		}

		public ConnectionBuilder cookie(String cookie) {
			this.cookie = cookie;
			return this;
		}

		public ConnectionBuilder country(String country) {
			this.country = country;
			return this;
		}

		public ConnectionBuilder proxy(Proxy proxy) {
			this.proxy = proxy;
			return this;
		}

		public ConnectionBuilder authentication(Authentication authentication) {
			this.authentication = authentication;
			return this;
		}

		public HttpConnector createConnect() throws IOException{
			return new HttpConnector(this);
		}
	}

	public class Response {
		private int code = 0;
		private String messageError = "";
		private String responseContent = "";
		private HttpContext httpContext;

		public Response(int code, String messageError, String responseContent) {
			this.code = code;
			this.messageError = messageError;
			this.responseContent = responseContent;
		}

		public Response(int code, String messageError, String responseContent, HttpContext httpContext) {
			this.code = code;
			this.messageError = messageError;
			this.responseContent = responseContent;
			this.httpContext = httpContext;
		}

		public String getResponseContent() {
			return responseContent;
		}

		public int getCode() {
			return code;
		}

		public String getMessageError() {
			return messageError;
		}

		public HttpContext getHttpContext() {
			return httpContext;
		}
	}

	public static class Authentication {
		private String userAuthentication;
		private String passwordAuthentication;
		public static final Authentication DEFAULT_AUTH = new Authentication();
		private Authentication() {
			userAuthentication = "lum-customer-tmon-zone-static";
			passwordAuthentication = "uodixcecqzhr";
		}

		public Authentication(String userAuthentication, String passwordAuthentication) {
			this.userAuthentication = userAuthentication;
			this.passwordAuthentication = passwordAuthentication;
		}

		public String getUserAuthentication() {
			return userAuthentication;
		}

		public String getPasswordAuthentication() {
			return passwordAuthentication;
		}
	}

}
