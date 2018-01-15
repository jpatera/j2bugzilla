/*
 * Copyright 2018 Jan Patera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2bugzilla.base;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransport;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.apache.xmlrpc.client.XmlRpcTransport;

import java.net.Proxy;

/**
 * Created by pateraj on 4.1.2018.
 */
public class XmlRpcProxyAndCookiesTransportFactory extends XmlRpcSun15HttpTransportFactory {
    private Proxy proxy;
    private String proxyUser;
    private String proxyPassword;

//    private XmlRpcTransport transport;
//    private XmlRpcProxyAndCookiesTransport transport;

    public XmlRpcProxyAndCookiesTransportFactory(XmlRpcClient client) {
        super(client);
//        setSSLSocketFactory();
//        transport = new XmlRpcProxyAndCookiesTransport(getClient());
//        transport.setSSLSocketFactory(getSSLSocketFactory());

//        this(client, null);
    }

//    public XmlRpcProxyAndCookiesTransportFactory(XmlRpcClient client, final Proxy proxy) {
//        this(client, proxy, null, null);
////        super.setProxy(pProxy);
////        this.proxy = proxy;
////        setTransport(pProxy);
//    }
//
//    public XmlRpcProxyAndCookiesTransportFactory(XmlRpcClient client, final Proxy proxy, final String proxyUser, final String proxyPassword) {
//        super(client);
//        super.setProxy(proxy);
//        this.proxy = proxy;
////        this.proxyUser = proxyUsername;
////        this.proxyPassword = proxyPassword;
////        setTransport();
//    }

//    public void setProxyAuthorization(String proxyUser, String proxyPassword) {
//        this.proxyUser = proxyUser;
//        this.proxyPassword = proxyPassword;
//    }

    /**
     * Saves proxy in this class because the parent XmlRpcSun15HttpTransportFactory
     * does not have a public method getProxy()
     * @param proxy proxy server for Bugzilla
     */
    @Override
    public void setProxy(Proxy proxy) {
        super.setProxy(proxy);
        this.proxy = proxy;
    }

    public void setProxyCredentials(final String proxyUser, final String proxyPassword) {
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
//        transport.setProxyCredentials(proxyUser, proxyPassword);
    }

//    /**
//     * @return the proxyUser
//     */
//    public String getProxyUser() {
//        return proxyUser;
//    }
//
//    /**
//     * @return the proxyPassword
//     */
//    public String getProxyPassword() {
//        return proxyPassword;
//    }

//    /**
//     * @return the proxy
//     */
//    public Proxy getProxy() {
//        return proxy;
//    }

//    private void setTransport(final Proxy proxy) {
////        transport = new XmlRpcProxyAndCookiesTransport(getClient());
////        transport.setSSLSocketFactory(getSSLSocketFactory());
////        transport.setProxy(proxy);
////        transport.setProxy(getProxy());
////        if (proxy != null) {
////            transport.setProxyCredentials(proxyUser, proxyPassword);
////        }
////        return transport;
//    }

    @Override
    public XmlRpcTransport getTransport() {
        XmlRpcProxyAndCookiesTransport transport = new XmlRpcProxyAndCookiesTransport(getClient());
//        transport.setSSLSocketFactory(getSSLSocketFactory());
        transport.setProxy(proxy);
        if (proxy != null) {
            transport.setProxyCredentials(proxyUser, proxyPassword);
        }
        return transport;
    }
}
