package com.j2bugzilla.base;

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransport;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.ws.commons.util.Base64;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by pateraj on 4.1.2018.
 */
public class XmlRpcProxyAndCookiesTransport extends XmlRpcSun15HttpTransport {
    private String proxyUser;
    private String proxyPassword;

    public XmlRpcProxyAndCookiesTransport(XmlRpcClient pClient) {
        super(pClient);
    }

    public void setProxyAuthorization(String proxyUser, String proxyPassword) {
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
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

    protected URLConnection newURLConnection(URL pURL) throws IOException {
//        URLConnection rc = super.newURLConnection(pURL);
        conn = super.newURLConnection(pURL);
        if ((getProxy() != null) && (getProxyUser() != null) && (getProxyPassword() != null)) {
            String creds = getProxyUser()+":"+getProxyPassword();
//            conn.setRequestProperty("Proxy-Authorization", "Basic " + Base64.encode(creds.getBytes("UTF-8")));
            conn.setRequestProperty("Proxy-Authorization", "Basic " + DatatypeConverter.printBase64Binary(creds.getBytes(StandardCharsets.UTF_8)));
        }
        return conn;
    }






    /**
     * A {@code List} of cookies received from the installation, used for authentication
     */
    private List<String> cookies = new ArrayList<String>();

//    /**
//     * Creates a new {@link TransportWithCookies} object.
//     * @param pClient The {@link XmlRpcClient} that does the heavy lifting.
//     */
//    public TransportWithCookies(XmlRpcClient pClient) {
//        super(pClient);
//    }

    private URLConnection conn;

//    @Override
//    protected URLConnection newURLConnection(URL pURL) throws IOException {
//        conn = super.newURLConnection(pURL);
//        return conn;
//    }

    /**
     * This is the meat of these two overrides -- the HTTP header data now includes the
     * cookies received from the Bugzilla installation on login and will pass them every
     * time a connection is made to transmit or receive data.
     */
    @Override
    protected void initHttpHeaders(XmlRpcRequest request) throws XmlRpcClientException {
        super.initHttpHeaders(request);
        if(cookies.size()>0) {
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
        getCookies(conn);
    }

    /**
     * Retrieves cookie values from the HTTP header of Bugzilla responses
     * @param conn
     */
    private void getCookies(URLConnection conn) {
        if(cookies.size()==0) {
            Map<String, List<String>> headers = conn.getHeaderFields();
            if(headers.containsKey("Set-Cookie")) {//avoid NPE
                List<String> vals = headers.get("Set-Cookie");
                for(String str : vals) {
                    cookies.add(str);
                }
            }
        }

    }
}
