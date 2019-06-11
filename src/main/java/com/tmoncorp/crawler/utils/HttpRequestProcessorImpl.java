package com.tmoncorp.crawler.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.tmoncorp.crawler.utils.HttpResponse;
import com.tmoncorp.crawler.utils.ProxyUtils;
import com.tmoncorp.crawler.utils.Utilities;

import javax.json.JsonException;
import java.io.IOException;


public class HttpRequestProcessorImpl implements HttpRequestProcessor{
    private static Logger LOG = LoggerFactory.getLogger(HttpRequestProcessorImpl.class);

    private static final String PROXY_ERROR_AUTH = "Auth Failed";
    private static final String PROXY_ERROR_MSG = "Proxy Error";
    private static final String[] WEB_BLOCK_ERROR = {"Web Page Blocked", "block_page"};
    private static final String WEB_BLOCK_MSG = "IP is blocked";
    private static final int MAX_RETRY = 100;
    private static final int DELAY_TIME = 1*1000;

    @Override
    public HttpResponse get(String url,
                            HttpMethod method,
                            boolean useProxy,
                            ResponseType responseType) {
        return get(url, method, useProxy, null, responseType, null);
    }

    @Override
    public HttpResponse get(String url,
                            HttpMethod method,
                            boolean useProxy,
                            String cookie,
                            ResponseType responseType) {
        return get(url, method, useProxy, cookie, responseType, null);
    }

    @Override
    public HttpResponse get(String url,
                            HttpMethod method,
                            boolean useProxy,
                            String cookie,
                            ResponseType responseType,
                            HttpConnector.Authentication authentication) {
        switch (method) {
            case GET:
                return get(url, useProxy, cookie, responseType, authentication);
            case POST:
                return post(url, useProxy, cookie, responseType, authentication);
        }
        return null;
    }

    private HttpResponse get(String url, boolean useProxy, String cookie, ResponseType responseType, HttpConnector.Authentication authentication) {
        int retry = 0;
        while (retry < MAX_RETRY) {
            try {
                HttpConnector.Response response = HttpConnector
                        .create()
                        .fromUrl(url)
                        .proxy(useProxy ? ProxyUtils.getInstance().getRandomProxyServer() : null)
                        .cookie(cookie)
                        .authentication(authentication == null ? HttpConnector.Authentication.DEFAULT_AUTH : authentication)
                        .createConnect()
                        .request();

                //In case check response success from server but it is 'Proxy Error' or IP blocked
                if (PROXY_ERROR_AUTH.equalsIgnoreCase(response.getMessageError())) {
                    throw new IOException(response.getMessageError());
                }
                if (PROXY_ERROR_MSG.equalsIgnoreCase(response.getResponseContent())
                        || StringUtils.containsIgnoreCase(response.getResponseContent(), PROXY_ERROR_MSG)) {
                    throw new IOException(response.getResponseContent());
                }
                if (StringUtils.containsIgnoreCase(response.getResponseContent(), WEB_BLOCK_ERROR[0])
                        || StringUtils.containsIgnoreCase(response.getResponseContent(), WEB_BLOCK_ERROR[1])) {
                    throw new IOException(WEB_BLOCK_MSG);
                }
                if (response.getCode() == 0) {//Validate request not response
                    throw new IOException(response.getMessageError());
                }
                return new HttpResponse(response.getCode(), response.getMessageError(), response.getResponseContent(), responseType);
            } catch (IOException | JsonException e) {
                retry++;
                if (retry == MAX_RETRY) {
                    LOG.error("Failed connect to {} at time {}. Reason: {}", url, retry, e.getMessage());
                }
//                LOG.error("Failed connect to {} at time {}. Reason: {}", url, retry, e.getMessage());
                Utilities.aWait(DELAY_TIME);
            }
        }
        return new HttpResponse();
    }

    private HttpResponse post(String url, boolean useProxy, String cookie, ResponseType responseType, HttpConnector.Authentication authentication) {
        int retry = 0;
        while (retry < MAX_RETRY) {
            try {
                HttpConnector.Response response = HttpConnector
                        .create()
                        .fromUrl(url)
                        .proxy(useProxy ? ProxyUtils.getInstance().getRandomProxyServer() : null)
                        .cookie(cookie)
                        .authentication(authentication == null ? HttpConnector.Authentication.DEFAULT_AUTH : authentication)
                        .createConnect()
                        .request(HttpMethod.POST);

                //In case check response success from server but it is 'Proxy Error' or IP blocked
                if (PROXY_ERROR_AUTH.equalsIgnoreCase(response.getMessageError())) {
                    throw new IOException(response.getMessageError());
                }
                if (PROXY_ERROR_MSG.equalsIgnoreCase(response.getResponseContent())
                        || StringUtils.containsIgnoreCase(response.getResponseContent(), PROXY_ERROR_MSG)) {
                    throw new IOException(response.getResponseContent());
                }
                if (StringUtils.containsIgnoreCase(response.getResponseContent(), WEB_BLOCK_ERROR[0])
                        || StringUtils.containsIgnoreCase(response.getResponseContent(), WEB_BLOCK_ERROR[1])) {
                    throw new IOException(WEB_BLOCK_MSG);
                }
                if (response.getCode() == 0) {//Validate request not response
                    throw new IOException(response.getMessageError());
                }
                return new HttpResponse(response.getCode(), response.getMessageError(), response.getResponseContent(), responseType);
            } catch (IOException | JsonException e) {
                retry++;
                if (retry == MAX_RETRY) {
                    LOG.error("Failed connect to {} at time {}. Reason: {}", url, retry, e.getMessage());
                }
//                LOG.error("Failed connect to {} at time {}. Reason: {}", url, retry, e.getMessage());
                Utilities.aWait(DELAY_TIME);
            }
        }
        return new HttpResponse();
    }
}