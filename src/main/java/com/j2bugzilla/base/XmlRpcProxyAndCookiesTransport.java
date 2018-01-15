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

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransport;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.xml.bind.DatatypeConverter;

/**
 * XML-RPC transport implementation for j2bugzilla library, based on the {@link java.net.HttpURLConnection} class.
 * Adds support for the basic proxy authentication.
 * The headers & cookies processing from previous j2bugzilla version is preserved
 *
 * Created by pateraj on 4.1.2018.
 */
public class XmlRpcProxyAndCookiesTransport extends XmlRpcSun15HttpTransport {

    private URLConnection urlConn;
    private String proxyUser;
    private String proxyPassword;

    /**
     * A {@code List} of cookies received from the installation, used for Bugzilla authentication
     */
    private List<String> cookies = new ArrayList<String>();


    /**
     * Creates a new {@link XmlRpcProxyAndCookiesTransport} object.
     * @param client The {@link XmlRpcClient} that does the heavy lifting.
     */
    public XmlRpcProxyAndCookiesTransport(final XmlRpcClient client) {
        super(client);
    }

    /**
     * Sets optional proxy user and password for proxy authentication.
     * Neither {@code proxyUser} nor {@code proxyPassword} should be null, otherwise proxy authentication will not be triggered.
     *
     * Note: https requests through authenticated proxies are not supported
     *
     * @param proxyUser A plain text proxy user name.
     * @param proxyPassword A plain text proxy password.
     */
    public void setProxyCredentials(final String proxyUser, final String proxyPassword) {
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
    }


    @Override
    protected URLConnection newURLConnection(URL url) throws IOException {

        urlConn = super.newURLConnection(url);
        if ((getProxy() != null) && (proxyUser != null) && (proxyPassword != null)) {
            String proxyCreds = proxyUser + ":" + proxyPassword;
            urlConn.setRequestProperty("Proxy-Authorization", "Basic " + DatatypeConverter.printBase64Binary(proxyCreds.getBytes("UTF-8")));
        }
        return urlConn;
    }


    /**
     * This is the meat of these two overrides -- the HTTP header data now includes the
     * cookies received from the Bugzilla installation on login and will pass them every
     * time a connection is made to transmit or receive data.
     */
    @Override
    protected void initHttpHeaders(XmlRpcRequest request) throws XmlRpcClientException {

        super.initHttpHeaders(request);
        if(cookies.size() > 0) {
            StringBuilder commaSep = new StringBuilder();

            for(String str : cookies) {
                commaSep.append(str);
                commaSep.append(",");
            }
            setRequestHeader("Cookie", commaSep.toString());
        }
    }


    @Override
    protected void close() throws XmlRpcClientException {
        getCookies(urlConn);
    }


    /**
     * Retrieves cookie values from the HTTP header of Bugzilla responses
     * @param conn URL connection with the Bugzilla cookies
     */
    private void getCookies(URLConnection conn) {

        if(cookies.size()==0) {
            Map<String, List<String>> headers = conn.getHeaderFields();
            if(headers.containsKey("Set-Cookie")) { //avoid NPE
                List<String> vals = headers.get("Set-Cookie");
                for(String str : vals) {
                    cookies.add(str);
                }
            }
        }
    }
}
