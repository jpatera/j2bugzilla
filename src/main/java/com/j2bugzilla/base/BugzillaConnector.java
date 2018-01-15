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

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


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


	/**
	 * Use this method to designate a host to connect to. You must call one of connectTo(...) methods
	 * at least once before executing any other methods of this object.
	 *
	 * This method is intended for a direct communication from client to a Bugzilla server.
	 * If the Bugzilla server is behind a proxy, use one of connectTo(...) methods with proxy parameters.
	 *
	 * @param bugzillaDomain A string pointing to the domain of the Bugzilla installation
	 *
	 * @throws ConnectionException if a connection cannot be established
	 */
    public void connectTo(String bugzillaDomain) throws ConnectionException {
        connectTo(bugzillaDomain,null,null);
    }


    /**
     * Use this method to designate a host to connect to. You must call one of connectTo(...) methods
     * at least once before executing any other methods of this object.
     *
	 * This method is intended for a direct communication from client to a Bugzilla server.
	 * If the Bugzilla server is behind a proxy, use one of connectTo(...) methods with proxy parameters.
	 *
	 * If httpUser is not null, than the httpUser and the httpPasswd will be
     * used to connect to the bugzilla server. This currently only supports basic
     * http authentication ( @see <a href="http://en.wikipedia.org/wiki/Basic_access_authentication">Basic access authentication</a>).
     * 
     * This is not used to login into bugzilla. To authenticate with your specific Bugzilla installation,
     * please see {@link com.j2bugzilla.rpc.LogIn LogIn}.
     * 
     * @param bugzillaDomain A string pointing to the domain of the Bugzilla installation
     * @param httpUser username for an optional Basic access authentication
     * @param httpPasswd password for an optional Basic access authentication
	 *
     * @throws ConnectionException if a connection cannot be established
     */
    public void connectTo(final String bugzillaDomain, final String httpUser, final String httpPasswd)
			throws ConnectionException {

        connectTo(bugzillaDomain, httpUser, httpPasswd, null, null, null);
    }


	/**
	 * Use this method to designate a Bugzilla host to connect to. You must call one of connectTo(...) methods
	 * at least once before executing any other methods of this object.
	 *
	 * This method is intended for a proxied communication from client to a Bugzilla server.
	 *
	 * If httpUser is not null, than the httpUser and the httpPasswd will be
	 * used to connect to the bugzilla server. This currently only supports basic
	 * http authentication ( @see <a href="http://en.wikipedia.org/wiki/Basic_access_authentication">Basic access authentication</a>).
	 *
	 * This is not used to login into bugzilla. To authenticate with your specific Bugzilla installation,
	 * please see {@link com.j2bugzilla.rpc.LogIn LogIn}.
	 *
	 * @param bugzillaDomain a string pointing to the domain of the Bugzilla installation
	 * @param httpUser username for an optional Basic access authentication
	 * @param httpPasswd password for an optional Basic access authentication
	 * @param proxy a proxy you want to use; if it is null a direct communication will be set up
	 * @param proxyUser username for an optional Basic proxy authentication; if it is null, no authentication will be performed
	 * @param proxyPasswd password for an optional Basic proxy authentication
	 *
	 * @throws ConnectionException if a connection cannot be established
	 */
	public void connectTo(final String bugzillaDomain, final String httpUser, final String httpPasswd
			, final Proxy proxy, final String proxyUser, final String proxyPasswd)
			throws ConnectionException {

		connectToInternal(getBugzillaHostURL (buildBugzillaHost(bugzillaDomain))
				, httpUser, httpPasswd, proxy, proxyUser, proxyPasswd);
	}


    /**
     * Use this method to designate a Bugzilla host to connect to. You must call one of connect(...) methods
     * at least once before executing any other methods of this object.
     *
	 * This method is intended for a proxied communication from client to a Bugzilla server.
	 *
     * If httpUser is not null, than the httpUser and the httpPasswd will be
     * used to connect to the bugzilla server. This currently only supports basic
     * http authentication ( @see <a href="http://en.wikipedia.org/wiki/Basic_access_authentication">Basic access authentication</a>).
     *
     * This is not used to login into Bugzilla. To authenticate with your specific Bugzilla installation,
     * please see {@link com.j2bugzilla.rpc.LogIn LogIn}.
     *
     * @param bugzillaHostURL A URL of form http:// + bugzilladomain + /xmlrpc.cgi
     * @param httpUser username for an optional Basic access authentication
     * @param httpPasswd password for an optional Basic access authentication
	 * @param proxy a proxy you want to use; if it is null a direct communication will be set up
	 * @param proxyUser username for an optional Basic proxy authentication; if it is null, no authentication will be performed
	 * @param proxyPasswd password for an optional Basic proxy authentication
	 *
	 * @throws ConnectionException if a connection cannot be established
     */
	public void connectTo(final URL bugzillaHostURL, final String httpUser, final String httpPasswd
						, final Proxy proxy, final String proxyUser, final String proxyPasswd)
			throws ConnectionException{

		connectToInternal(bugzillaHostURL, httpUser, httpPasswd, proxy, proxyUser, proxyPasswd);
	}


	private void connectToInternal(final URL bugzillaHostUrl, final String httpUser, final String httpPasswd
			, final Proxy proxy, final String proxyUser, final String proxyPasswd)
			throws ConnectionException{

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		if (httpUser != null) {
			config.setBasicUserName(httpUser);
			config.setBasicPassword(httpPasswd);
		}
		config.setServerURL(bugzillaHostUrl);

		client = new XmlRpcClient();
		client.setConfig(config);

		XmlRpcProxyAndCookiesTransportFactory factory = new XmlRpcProxyAndCookiesTransportFactory(client);
		if (proxy != null) {
			factory.setProxy(proxy);
			if (proxyUser != null && proxyPasswd != null) {
				factory.setProxyCredentials(proxyUser, proxyPasswd);
			}
		}

		/**
		 * Here, we override the default behavior of the transport factory to properly
		 * handle cookies for BZ authentication and to add a proxy support
		 */
		client.setTransportFactory(factory);
	}


	private String buildBugzillaHost (String bugzillaDomainHost) {

		String bugzillaHost = bugzillaDomainHost;
		if(!bugzillaHost.endsWith("xmlrpc.cgi")) {
			if(bugzillaHost.endsWith("/")) {
				bugzillaHost += "xmlrpc.cgi";
			} else {
				bugzillaHost += "/xmlrpc.cgi";
			}
		}
		return bugzillaHost;
	}


	private URL getBugzillaHostURL (String bugzillaHost)
			throws ConnectionException {

		URL hostURL;
		try {
			hostURL = new URL(bugzillaHost);
		} catch (MalformedURLException e) {
			throw new ConnectionException("Bugzilla host URL is malformed; URL supplied was " + bugzillaHost, e);
		}
		return hostURL;
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
}
