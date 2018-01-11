/*
 * Copyright 2011 Thomas Golden
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


import com.j2bugzilla.rpc.LogIn;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;


/**
 * The {@code BugzillaConnector} class handles all access to a given Bugzilla installation.
 * The Bugzilla API uses XML-RPC, implemented via the Apache XML-RPC library in this instance.
 * @author Tom
 * 
 * @see <a href="http://www.bugzilla.org/docs/tip/en/html/api/Bugzilla/WebService.html">WebService</a>
 * @see <a href="http://www.bugzilla.org/docs/tip/en/html/api/Bugzilla/WebService/Server/XMLRPC.html">XML-RPC</a>
 *
 */
public class BugzillaConnector {
	
	/**
	 * The {@link XmlRpcClient} handles all requests to Bugzilla by transforming method names and
	 * parameters into properly formatted XML documents, which it then transmits to the host.
	 */
	private XmlRpcClient client;	

	/**
	 * The token represents a login and is used in place of login cookies.
	 * See {@link com.j2bugzilla.rpc.LogIn#getToken()}
	 */
	private String token;

	public BugzillaConnector() {
//		initializeProxyAuthenticator("", "");
	}


	/**
	 * Use this method to designate a host to connect to. You must call this method 
	 * before executing any other methods of this object.
	 * 
	 * @param host A string pointing to the domain of the Bugzilla installation
	 * @throws ConnectionException if a connection cannot be established
	 */
    public void connectTo(String host) throws ConnectionException {
        connectTo(host,null,null);
    }

    /**
     * Use this method to designate a host to connect to. You must call this method 
     * before executing any other methods of this object.
     * 
     * If httpUser is not null, than the httpUser and the httpPasswd will be 
     * used to connect to the bugzilla server. This currently only supports basic
     * http authentication ( @see <a href="http://en.wikipedia.org/wiki/Basic_access_authentication">Basic access authentication</a>).
     * 
     * This is not used to login into bugzilla. To authenticate with your specific Bugzilla installation,
     * please see {@link com.j2bugzilla.rpc.LogIn LogIn}.
     * 
     * @param host A string pointing to the domain of the Bugzilla installation
     * @param httpUser username for an optional Basic access authentication
     * @param httpPasswd password for an optional Basic access authentication
     * @throws ConnectionException if a connection cannot be established
     */
    public void connectTo(final String host, final String httpUser, final String httpPasswd) throws ConnectionException {

//        String newHost = host;
//        if(!newHost.endsWith("xmlrpc.cgi")) {
//            if(newHost.endsWith("/")) {
//                newHost += "xmlrpc.cgi";
//            } else {
//                newHost += "/xmlrpc.cgi";
//            }
//        }
//
//        URL hostURL;
//        try {
//            hostURL = new URL(newHost);
//        } catch (MalformedURLException e) {
//            throw new ConnectionException("Host URL is malformed; URL supplied was " + newHost, e);
//        }
//        connectTo(hostURL, httpUser, httpPasswd, null, null, null);
        connectTo(host, httpUser, httpPasswd, null, null, null);
    }

	public void connectTo(final String host, final String httpUser, final String httpPasswd
						, final Proxy proxy, final String proxyUser, final String proxyPasswd)
			throws ConnectionException {

		String newHost = host;
		if(!newHost.endsWith("xmlrpc.cgi")) {
			if(newHost.endsWith("/")) {
				newHost += "xmlrpc.cgi";
			} else {
				newHost += "/xmlrpc.cgi";
			}
		}

		URL hostURL;
		try {
			hostURL = new URL(newHost);
		} catch (MalformedURLException e) {
			throw new ConnectionException("Host URL is malformed; URL supplied was " + newHost, e);
		}
		connectTo(hostURL, httpUser, httpPasswd, proxy, proxyUser, proxyPasswd);
	}

//    /**
//     * Use this method to designate a host to connect to. You must call this method
//     * before executing any other methods of this object.
//     *
//	 * This method is intended direct communication from client to a BugZilla server
//	 * If the BugZilla server is behind a proxy, use
//	 * {@code connectTo(URL host, String httpUser, String httpPasswd, Proxy httpProxy, String httpProxyUser, String httpProxyPasswd)}
//	 *
//     * If httpUser is not null, than the httpUser and the httpPasswd will be
//     * used to connect to the bugzilla server. This currently only supports basic
//     * http authentication ( @see <a href="http://en.wikipedia.org/wiki/Basic_access_authentication">Basic access authentication</a>).
//     *
//     * This is not used to login into bugzilla. To authenticate with your specific Bugzilla installation,
//     * please see {@link com.j2bugzilla.rpc.LogIn LogIn}.
//     *
//     * @param host A URL of form http:// + somedomain + /xmlrpc.cgi
//     * @param httpUser username for an optional Basic access authentication
//     * @param httpPasswd password for an optional Basic access authentication
//     *
//     */
//    public void connectTo(URL host, String httpUser, String httpPasswd)  throws ConnectionException {
//
////		Proxy httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("qa-sh-mail.prgqa.hpecorp.net", 8082));
//		connectTo(host, httpUser, httpPasswd, null, null, null);
//	}

	public void connectTo(final URL host, final String httpUser, final String httpPasswd
						, final Proxy proxy, final String proxyUser, final String proxyPasswd)
			throws ConnectionException{


		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		if (httpUser != null) {
			config.setBasicUserName(httpUser);
			config.setBasicPassword(httpPasswd);
		}
		config.setServerURL(host);

		client = new XmlRpcClient();
		client.setConfig(config);

// Using Java HttpClient for transport:
//		XmlRpcProxyAndCookiesTransportFactory factory = new XmlRpcProxyAndCookiesTransportFactory(client);
//		if (proxy != null) {
//			factory.setProxy(proxy);
//			if (proxyUser != null && proxyPasswd != null) {
//				factory.setProxyCredentials(proxyUser, proxyPasswd);
//			}
//		}


// Using Apache commons HttpClient for transport:
//		final XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(client);
		HttpHost proxyHost = new HttpHost("qa-sh-mail.prgqa.hpecorp.net", 8082, "http");
		final CloseableHttpClient httpClient = getNewClient(proxyHost, proxyUser, proxyPasswd);
		final XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(client);

		// FIXME:
		factory.setHttpClient(httpClient);

		client.setTransportFactory(factory);
//		final HttpState httpState = client.getState();





		/**
		 * Here, we override the default behavior of the transport factory to properly
		 * handle cookies for authentication
		 */
		// TODO: maybe not necessary?
		client.setTransportFactory(factory);
	}

	private CloseableHttpClient getNewClient(final HttpHost proxyHost, final String proxyUser, final String proxyPasswd) {
		final CredentialsProvider credsProvider = new BasicCredentialsProvider();
//		credsProvider.setCredentials(new AuthScope(bugzillaTarget), new UsernamePasswordCredentials(username, password));

		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setConnectTimeout(1 * 1000)    // Timeout for receiving a free connection from pooling connection manager
				// As we are using a dedicated connection manager per API call
				//   there should be always free connections available.
				.setConnectionRequestTimeout(5 * 1000)  // Taking 5 seconds as an acceptable timeout for waiting
				//   for an answer to the http(s) request.
				.setSocketTimeout(10 * 1000) // Taking 10 seconds as an acceptable timeout for waiting
				//   for data to be sent from target to the client.
				.build();

		// Following headers can change a default behaviour of some proxies in terms of that the data sent from target
		// to proxy are not cached on the proxy. As a result http client will receive always current data from the target.
		List<Header> defaultClientHeaders = new ArrayList<Header>();
		defaultClientHeaders.add(new BasicHeader(HttpHeaders.PRAGMA, "no-cache"));
		defaultClientHeaders.add(new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"));

////		HttpHost sscProxy = resolveSscProxy(config, bugzillaProtocol);
//		String proxyHostName = "qa-sh-mail.prgqa.hpecorp.net";
//		int proxyPortNum = 8082;
//		String proxyScheme = "http";
//		HttpHost proxy = new HttpHost(proxyHostName, proxyPortNum, proxyScheme);

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
//				.setConnectionManager(connMan)
				.setDefaultCredentialsProvider(credsProvider)
				.setDefaultRequestConfig(defaultRequestConfig)
				.setDefaultHeaders(defaultClientHeaders)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
				// Setting the previous requestSentRetryEnabled=true helped filing bugs through less responsive proxy
				// (specifically a Tiny proxy). This parameter affects retrying only non idempotent methods (POST in case of ALM)
				// idempotent methods GET and PUT are always retried.
				// Note: if responses to POST are not being returned but the requests are reaching ALM it could happen
				// that unwanted/not tracked/dead resources are created on the bug tracker provider side. In this case
				// it should be considered switching POST retries off.
				.setDefaultCookieStore(new BasicCookieStore());

		if (proxyHost == null) {
			httpClientBuilder
					.useSystemProperties(); // Keeping this for backward plugin compatibility if the  SSC proxy is not used
			// Among other system properties http(s).proxyHost, http(s).proxyPort httpNonProxyHosts are taken into account
			// For the complete list see http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/impl/client/HttpClientBuilder.html
		} else {
//			Credentials proxyCreds = resolveSscProxyCredentials(config, bugzillaProtocol);
//					, config.get(ProxyField.HTTP_PROXY_PASSWORD.getFieldName()));;
			Credentials proxyCreds = new UsernamePasswordCredentials(proxyUser, proxyPasswd);
			if (proxyCreds != null) {
				credsProvider.setCredentials(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), proxyCreds);
			}
			httpClientBuilder
					.setProxy(proxyHost);
		}
		return httpClientBuilder.build();
	}

	private void initializeProxyAuthenticator(final String proxyUser, final String proxyPasswd) {
		if (proxyUser != null && proxyPasswd != null) {
			Authenticator.setDefault(
				new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								proxyUser, proxyPasswd.toCharArray()
						);
					}
				}
			);
		}
	}


	/**
	 * Allows the API to execute any properly encoded XML-RPC method.
	 * If the method completes properly, the {@link BugzillaMethod#setResultMap(Map)}
	 * method will be called, and the implementation class will provide
	 * methods to access any data returned.
	 *
	 * @param method A {@link BugzillaMethod} to call on the connected installation
	 * @throws BugzillaException If the XML-RPC library returns a fault, a {@link BugzillaException}
	 * with a descriptive error message for that fault will be thrown.
	 */
	@SuppressWarnings("unchecked")//Must cast Object from client.execute()
	public void executeMethod(BugzillaMethod method) throws BugzillaException {
		if(client == null) { 
			throw new IllegalStateException("Cannot execute a method without connecting!");
		}//We are not currently connected to an installation
		Map<Object, Object> params = new HashMap<Object, Object>();
		if (token != null) {
			params.put("Bugzilla_token", token);
		}
		
		params.putAll(method.getParameterMap());
		Object[] obj = {params};
		try {
			Object results = client.execute(method.getMethodName(), obj);
			if(!(results instanceof Map<?, ?>)) { results = Collections.emptyMap(); }
			Map<Object, Object> readOnlyResults = Collections.unmodifiableMap((Map<Object, Object>)results);
			method.setResultMap(readOnlyResults);
			if (method instanceof LogIn) {
				LogIn login = (LogIn)method;
				setToken(login.getToken());
			}
		} catch (XmlRpcException e) {
			BugzillaException wrapperException = XmlExceptionHandler.handleFault(e);
			throw wrapperException;
		}
	}

	public void setToken(String t) {
		token = t;
	}
	
//	/**
//	 * We need a transport class which will correctly handle cookies set by Bugzilla. This private
//	 * subclass will appropriately set the Cookie HTTP headers.
//	 *
//	 * Cookies are not support by Bugzilla 4.4.3+.
//	 *
//	 * @author Tom
//	 *
//	 */
//	private static final class TransportWithCookies extends XmlRpcSun15HttpTransport {
//
//		/**
//		 * A {@code List} of cookies received from the installation, used for authentication
//		 */
//		private List<String> cookies = new ArrayList<String>();
//
//		/**
//		 * Creates a new {@link TransportWithCookies} object.
//		 * @param pClient The {@link XmlRpcClient} that does the heavy lifting.
//		 */
//		public TransportWithCookies(XmlRpcClient pClient) {
//			super(pClient);
//		}
//
//		private URLConnection conn;
//
//		@Override
//		protected URLConnection newURLConnection(URL pURL) throws IOException {
//            conn = super.newURLConnection(pURL);
//            return conn;
//		}
//
//		/**
//		 * This is the meat of these two overrides -- the HTTP header data now includes the
//		 * cookies received from the Bugzilla installation on login and will pass them every
//		 * time a connection is made to transmit or receive data.
//		 */
//		@Override
//		protected void initHttpHeaders(XmlRpcRequest request) throws XmlRpcClientException {
//	        super.initHttpHeaders(request);
//	        if(cookies.size()>0) {
//	        	StringBuilder commaSep = new StringBuilder();
//
//	        	for(String str : cookies) {
//	        		commaSep.append(str);
//	        		commaSep.append(",");
//	        	}
//	        	setRequestHeader("Cookie", commaSep.toString());
//
//	        }
//
//	    }
//
//		@Override
//		protected void close() throws XmlRpcClientException {
//            getCookies(conn);
//		}
//
//		/**
//		 * Retrieves cookie values from the HTTP header of Bugzilla responses
//		 * @param conn
//		 */
//		private void getCookies(URLConnection conn) {
//	    	  if(cookies.size()==0) {
//	    		  Map<String, List<String>> headers = conn.getHeaderFields();
//	    		  if(headers.containsKey("Set-Cookie")) {//avoid NPE
//	    			  List<String> vals = headers.get("Set-Cookie");
//			    	  for(String str : vals) {
//			    		  cookies.add(str);
//			    	  }
//	    		  }
//	    	  }
//
//	    }
//
//	}
}
