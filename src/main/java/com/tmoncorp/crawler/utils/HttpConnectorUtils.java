package com.tmoncorp.crawler.utils;

import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * Created by danhnguyen on 4/3/19.
 */
public class HttpConnectorUtils {

    private static class ClassHelper{
        private static final HttpConnectorUtils INSTANCE = new HttpConnectorUtils();
    }

    public static HttpConnectorUtils get(){
        return HttpConnectorUtils.ClassHelper.INSTANCE;
    }


    public String getContentAsString(org.apache.http.HttpResponse httpResponse) {
        StringBuffer strResponse = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                strResponse.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strResponse.toString();
    }

    public DefaultRedirectStrategy createCustomRedirectStrategy() {
        return new CustomRedirectStrategy();
    }

    private class CustomRedirectStrategy extends DefaultRedirectStrategy {
        @Override
        protected URI createLocationURI(String location) throws ProtocolException {
            try {
                return super.createLocationURI(location);
            } catch (Exception e) {
                URL url = null;
                try {
                    url = new URL(URLDecoder.decode(location, "UTF-8"));
                    return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        }
    }
}
