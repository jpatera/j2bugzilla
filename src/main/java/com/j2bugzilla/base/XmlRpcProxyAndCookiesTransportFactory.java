/*
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
 * A custom XML-RPC transport derived from the original j2buzilla enhanced by a possibility to use http proxy.<BR>
 * Cooperates with {@link com.j2bugzilla.base.XmlRpcProxyAndCookiesTransport}<BR>
 * Replaces previous similar j2bugzilla anonymous and static factory and transport from {@link com.j2bugzilla.base.BugzillaConnector)
 * <BR>
 * @author Jan Patera, MicroFocus
 * @created 4.1.2018.
 */
public class XmlRpcProxyAndCookiesTransportFactory extends XmlRpcSun15HttpTransportFactory {

    private Proxy proxy;            // Proxy to be used for communication with Bugzilla
    private String proxyUser;       // Optional proxy user if proxy requires basic authentication
    private String proxyPassword;   // Password for proxy authentication


    public XmlRpcProxyAndCookiesTransportFactory(XmlRpcClient client) {
        super(client);
    }


    /**
     * Saves proxy in this factory instance because the parent XmlRpcSun15HttpTransportFactory
     * does not have a public method getProxy() for a later retrieval.
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
    }


    @Override
    public XmlRpcTransport getTransport() {
        XmlRpcProxyAndCookiesTransport transport = new XmlRpcProxyAndCookiesTransport(getClient());
        transport.setProxy(proxy);
        if (proxy != null) {
            transport.setProxyCredentials(proxyUser, proxyPassword);
        }
        return transport;
    }
}
