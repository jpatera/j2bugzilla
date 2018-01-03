package com.j2bugzilla.base;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.apache.xmlrpc.client.XmlRpcTransport;

import java.net.Proxy;

/**
 * Created by pateraj on 4.1.2018.
 */
public class XmlRpcProxyAndCookiesTransportFactory extends XmlRpcSun15HttpTransportFactory {
    private String proxyUser;
    private String proxyPassword;
    private Proxy proxy;

    public XmlRpcProxyAndCookiesTransportFactory(XmlRpcClient pClient) {
        super(pClient);
        setTransport();
    }

    public XmlRpcProxyAndCookiesTransportFactory(XmlRpcClient pClient, final Proxy pProxy) {
        super(pClient);
        super.setProxy(pProxy);
        this.proxy = pProxy;
        setTransport();
    }

    public XmlRpcProxyAndCookiesTransportFactory(XmlRpcClient pClient, final Proxy proxy, final String proxyUsername, final String proxyPassword) {
        super(pClient);
        super.setProxy(proxy);
        this.proxy = proxy;
        this.proxyUser = proxyUsername;
        this.proxyPassword = proxyPassword;
        setTransport();
    }

//    public void setProxyAuthorization(String proxyUser, String proxyPassword) {
//        this.proxyUser = proxyUser;
//        this.proxyPassword = proxyPassword;
//    }

    /**
     * Does nothing, suppresses the parent method
     * We want the proxy to be set only in constructor
     * @param proxy Not used, can be anything.
     */
    @Override
    public void setProxy(Proxy proxy) {
    }

    /**
     * @return the proxyUser
     */
    public String getProxyUser() {
        return proxyUser;
    }

    /**
     * @return the proxyPassword
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * @return the proxy
     */
    public Proxy getProxy() {
        return proxy;
    }

    private XmlRpcTransport setTransport() {
        transport = new XmlRpcProxyAndCookiesTransport(getClient());
        transport.setSSLSocketFactory(getSSLSocketFactory());
        transport.setProxy(getProxy());
        if (getProxy() != null) {
            transport.setProxyAuthorization(getProxyUser(), getProxyPassword());
        }
        return transport;
    }

    @Override
    public XmlRpcTransport getTransport() {
        return transport;
    }

//    private final XmlRpcTransport transport = new XmlRpcProxyAndCookiesTransport(client);
    private XmlRpcProxyAndCookiesTransport transport;

}
