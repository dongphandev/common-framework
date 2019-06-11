package com.tmoncorp.crawler.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestApiUtils {
	private static Logger LOG = LoggerFactory.getLogger(RequestApiUtils.class);

	private static List<String> USER_AGENT = new ArrayList<>();

	static {
		USER_AGENT.add(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

	}

	public static String getApiUrl(String url, String[] searchList, String[] params) {
		if (searchList == null)
			return url;
		return StringUtils.replaceEach(url, searchList, params);
	}

	public static String getUserAgent() {
		int randIndex = ThreadLocalRandom.current().nextInt(0, USER_AGENT.size());
		if (USER_AGENT.isEmpty())
			return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
		return USER_AGENT.get(randIndex);
	}

	private static class ClassHelper {
		private static final RequestApiUtils INSTANCE = new RequestApiUtils();
	}

	public static HttpRequestProcessor getHttpRequest() {
		return ClassHelper.INSTANCE.createHttpRequest();
	}

	private HttpRequestProcessor createHttpRequest() {
		return new HttpRequestProcessorImpl();
	}

	public static String getURL(HttpServletRequest req) {

		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();
		String queryString = req.getQueryString();
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);
		if (serverPort != 80 && serverPort != 443) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath).append(servletPath);

		if (pathInfo != null) {
			url.append(pathInfo);
		}
		if (queryString != null) {
			url.append("?").append(queryString);
		}
		return url.toString();
	}
}
