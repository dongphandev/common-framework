package com.tmoncorp.crawler.utils;


import org.springframework.http.HttpMethod;


public interface HttpRequestProcessor {
    HttpResponse get(String url, HttpMethod method, boolean useProxy, ResponseType responseType);
    HttpResponse get(String url, HttpMethod method, boolean useProxy, String cookie, ResponseType responseType);
    HttpResponse get(String url, HttpMethod method, boolean useProxy, String cookie, ResponseType responseType, HttpConnector.Authentication authentication);
    enum ResponseType {
        JSON, HTML
    }
}
