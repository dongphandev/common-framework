package com.tmoncorp.crawler.utils;

import java.io.StringReader;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
    private static Logger LOG = LoggerFactory.getLogger(HttpResponse.class);

    private int statusCode;
    private String messageError;
    private Object content;


    public HttpResponse() {}

    public HttpResponse(int statusCode, String content, HttpRequestProcessor.ResponseType responseType) {
        this.statusCode = statusCode;
        if (responseType.equals(HttpRequestProcessor.ResponseType.JSON)) {
            this.content = parseJson(content);
        } else {
            this.content = content;
        }
    }

    public HttpResponse(int statusCode, String messageError, String content, HttpRequestProcessor.ResponseType responseType) {
        this.statusCode = statusCode;
        this.messageError = messageError;
        if (responseType.equals(HttpRequestProcessor.ResponseType.JSON)) {
            this.content = parseJson(content);
        } else {
            this.content = content;
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessageError() {
        return messageError;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    private JsonObject parseJson(String content) {
        JsonReader reader = Json.createReader(new StringReader(content));
        try {
            return reader.readObject();
        } finally {
            if(Objects.nonNull(reader)) {
                reader.close();
            }
        }
    }

    public boolean isConnectionReset() {
        return StringUtils.endsWithIgnoreCase("Connection reset", getMessageError());
    }

}


