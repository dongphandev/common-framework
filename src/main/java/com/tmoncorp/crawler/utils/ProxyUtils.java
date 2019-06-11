package com.tmoncorp.crawler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.Random;


public class ProxyUtils {

    private final Logger LOG = LoggerFactory.getLogger(ProxyUtils.class);

    private static final int PROXY_DEFAULT_PORT = 22225;
    private static final String PROXY_DEFAULT_HOST = "zproxy.lum-superproxy.io";

    private ProxyUtils(){}

    private static class ProxyUtilsHelper{
        private static final ProxyUtils INSTANCE = new ProxyUtils();
    }

    public static ProxyUtils getInstance(){
        return ProxyUtilsHelper.INSTANCE;
    }

    public Proxy getRandomProxyServer() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host() , PROXY_DEFAULT_PORT));
    }

    private String host(){
        int retry = 0;
        int MAX_RETRY = 60;
        long DELAY_TIME = 1000;
        while (retry < MAX_RETRY) {
            try {
                int proxySessionId = new Random().nextInt(Integer.MAX_VALUE);
                InetAddress address = InetAddress.getByName("session-" + proxySessionId + "." + PROXY_DEFAULT_HOST);
                return address.getHostAddress();
            } catch (UnknownHostException e) {
                LOG.error(e.getMessage());
                LOG.error("Trying get supper proxy at time {}",(++retry));
                Utilities.aWait(DELAY_TIME);
            }
        }

        return null;
    }
}
