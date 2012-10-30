/**
 * ========= CONFIDENTIAL =========
 *
 * Copyright (C) 2012 enStratus Networks Inc - ALL RIGHTS RESERVED
 *
 * ====================================================================
 *  NOTICE: All information contained herein is, and remains the
 *  property of enStratus Networks Inc. The intellectual and technical
 *  concepts contained herein are proprietary to enStratus Networks Inc
 *  and may be covered by U.S. and Foreign Patents, patents in process,
 *  and are protected by trade secret or copyright law. Dissemination
 *  of this information or reproduction of this material is strictly
 *  forbidden unless prior written permission is obtained from
 *  enStratus Networks Inc.
 * ====================================================================
 */
package org.dasein.cloud.cloudsigma;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dasein.cloud.CloudErrorType;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ProviderContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Handles communication with the SoftLayer REST endpoint by abstracting out the specifics of authentication and
 * HTTP negotiation.
 * <p>Created by George Reese: 10/25/12 7:43 PM</p>
 * @author George Reese
 * @version 2012.09 initial version
 * @since 2012.02
 */
public class SoftLayerMethod {
    static private final Logger logger = SoftLayer.getLogger(SoftLayerMethod.class);
    static private final Logger wire   = SoftLayer.getWireLogger(SoftLayerMethod.class);

    /**
     * 200	OK	Command succeeded, data returned (possibly 0 length)
     */
    static public final int OK             = 200;
    /**
     * 204	No Content	Command succeeded, no data returned (by definition)
     */
    static public final int NO_CONTENT     = 204;

    /**
     * 400	Bad Request
     */
    static public final int BAD_REQUEST     = 400;

    /**
     * 404	Not Found	Command, drive, server or other object not found
     */
    static public final int NOT_FOUND      = 404;


    static public @Nullable String seekValue(@Nonnull String body, @Nonnull String key) {
        body = body.trim();
        if( body.length() > 0 ) {
            String[] lines = body.split("\n");

            for( String line : lines ) {
                line = line.trim();
                int idx = line.indexOf(" ");

                if( idx == -1 ) {
                    if( line.equals(key) ) {
                        return null;
                    }
                }
                else {
                    String k = line.substring(0, idx);

                    if( k.equals(key) ) {
                        return line.substring(idx+1);
                    }
                }
            }
        }
        return null;
    }

    static public @Nonnull Map<String,String> toMap(@Nonnull String body) {
        HashMap<String,String> values = new HashMap<String, String>();

        body = body.trim();
        if( body.length() > 0 ) {
            String[] lines = body.split("\n");

            for( String line : lines ) {
                line = line.trim();

                int idx = line.indexOf(" ");

                if( idx == -1 ) {
                    values.put(line, null);
                }
                else {
                    String k = line.substring(0, idx);

                    values.put(k, line.substring(idx+1));
                }
            }
        }
        return values;
    }

    private SoftLayer provider;

    public SoftLayerMethod(@Nonnull SoftLayer provider) { this.provider = provider; }

    public @Nullable Map<String,String> getObject(@Nonnull String resource) throws InternalException, CloudException {
        String body = getString(resource);

        if( body == null || body.trim().length() < 1 ) {
            return null;
        }
        return toMap(body);
    }

    public @Nullable String getString(@Nonnull String resource) throws InternalException, CloudException {
        if( logger.isTraceEnabled() ) {
            logger.trace("ENTER - " + SoftLayer.class.getName() + ".getString(" + resource + ")");
        }

        try {
            String target = getEndpoint(resource);

            if( wire.isDebugEnabled() ) {
                wire.debug("");
                wire.debug(">>> [GET (" + (new Date()) + ")] -> " + target + " >--------------------------------------------------------------------------------------");
            }
            try {
                URI uri;

                try {
                    uri = new URI(target);
                }
                catch( URISyntaxException e ) {
                    throw new SoftLayerConfigurationException(e);
                }
                HttpClient client = getClient(uri);

                try {
                    ProviderContext ctx = provider.getContext();

                    if( ctx == null ) {
                        throw new NoContextException();
                    }
                    HttpGet get = new HttpGet(target);
                    String auth;

                    try {
                        String userName = new String(ctx.getAccessPublic(), "utf-8");
                        String password = new String(ctx.getAccessPrivate(), "utf-8");

                        auth = new String(Base64.encodeBase64((userName + ":" + password).getBytes()));
                    }
                    catch( UnsupportedEncodingException e ) {
                        throw new InternalException(e);
                    }
                    get.addHeader("Authorization", "Basic " + auth);

                    if( wire.isDebugEnabled() ) {
                        wire.debug(get.getRequestLine().toString());
                        for( Header header : get.getAllHeaders() ) {
                            wire.debug(header.getName() + ": " + header.getValue());
                        }
                        wire.debug("");
                    }
                    HttpResponse response;
                    StatusLine status;

                    try {
                        response = client.execute(get);
                        status = response.getStatusLine();
                    }
                    catch( IOException e ) {
                        logger.error("Failed to execute HTTP request due to a cloud I/O error: " + e.getMessage());
                        throw new CloudException(e);
                    }
                    if( logger.isDebugEnabled() ) {
                        logger.debug("HTTP Status " + status);
                    }
                    Header[] headers = response.getAllHeaders();

                    if( wire.isDebugEnabled() ) {
                        wire.debug(status.toString());
                        for( Header h : headers ) {
                            if( h.getValue() != null ) {
                                wire.debug(h.getName() + ": " + h.getValue().trim());
                            }
                            else {
                                wire.debug(h.getName() + ":");
                            }
                        }
                        wire.debug("");
                    }
                    if( status.getStatusCode() == NOT_FOUND ) {
                        return null;
                    }
                    if( status.getStatusCode() != OK && status.getStatusCode() != NO_CONTENT ) {
                        logger.error("Expected OK for GET request, got " + status.getStatusCode());
                        HttpEntity entity = response.getEntity();
                        String body;

                        if( entity == null ) {
                            throw new SoftLayerException(CloudErrorType.GENERAL, status.getStatusCode(), status.getReasonPhrase(), status.getReasonPhrase());
                        }
                        try {
                            body = EntityUtils.toString(entity);
                        }
                        catch( IOException e ) {
                            throw new SoftLayerException(e);
                        }
                        if( wire.isDebugEnabled() ) {
                            wire.debug(body);
                        }
                        wire.debug("");
                        if( status.getStatusCode() == BAD_REQUEST && body.contains("could not be found") ) {
                            return null;
                        }
                        throw new SoftLayerException(CloudErrorType.GENERAL, status.getStatusCode(), status.getReasonPhrase(), body);
                    }
                    else {
                        HttpEntity entity = response.getEntity();

                        if( entity == null ) {
                            return "";
                        }
                        String body;

                        try {
                            body = EntityUtils.toString(entity);
                        }
                        catch( IOException e ) {
                            throw new SoftLayerException(e);
                        }
                        if( wire.isDebugEnabled() ) {
                            wire.debug(body);
                        }
                        wire.debug("");
                        return body;
                    }
                }
                finally {
                    try { client.getConnectionManager().shutdown(); }
                    catch( Throwable ignore ) { }
                }
            }
            finally {
                if( wire.isDebugEnabled() ) {
                    wire.debug("<<< [GET (" + (new Date()) + ")] -> " + target + " <--------------------------------------------------------------------------------------");
                    wire.debug("");
                }
            }
        }
        finally {
            if( logger.isTraceEnabled() ) {
                logger.trace("EXIT - " + SoftLayer.class.getName() + ".getString()");
            }
        }
    }

    private @Nonnull HttpClient getClient(URI uri) throws InternalException, CloudException {
        ProviderContext ctx = provider.getContext();

        if( ctx == null ) {
            throw new NoContextException();
        }
        boolean ssl = uri.getScheme().startsWith("https");
        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        //noinspection deprecation
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUserAgent(params, "");

        Properties p = ctx.getCustomProperties();

        if( p != null ) {
            String proxyHost = p.getProperty("proxyHost");
            String proxyPort = p.getProperty("proxyPort");

            if( proxyHost != null ) {
                int port = 0;

                if( proxyPort != null && proxyPort.length() > 0 ) {
                    port = Integer.parseInt(proxyPort);
                }
                params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxyHost, port, ssl ? "https" : "http"));
            }
        }
        return new DefaultHttpClient(params);
    }

    private @Nonnull String getEndpoint(@Nonnull String resource) throws NoContextException {
        ProviderContext ctx = provider.getContext();

        if( ctx == null ) {
            throw new NoContextException();
        }
        String endpoint = ctx.getEndpoint();

        if( endpoint == null || endpoint.trim().equals("") ) {
            endpoint = "https://api.zrh.cloudsigma.com";
        }
        if( resource.startsWith("/") ) {
            while( endpoint.endsWith("/") && !endpoint.equals("/") ) {
                endpoint = endpoint.substring(0, endpoint.length()-1);
            }
            return endpoint + resource;
        }
        else if( endpoint.endsWith("/") ) {
            return endpoint + resource;
        }
        return (endpoint + "/" + resource);
    }

    public @Nullable List<Map<String,String>> list(@Nonnull String resource) throws InternalException, CloudException {
        String body = getString(resource);

        if( body == null ) {
            return null;
        }
        ArrayList<Map<String,String>> objects = new ArrayList<Map<String, String>>();

        body = body.trim();
        if( body.length() > 0 ) {
            HashMap<String,String> currentObject = new HashMap<String, String>();
            String[] lines = body.split("\n");

            for( String line : lines ) {
                line = line.trim();
                if( line.length() < 1 ) {
                    if( !currentObject.isEmpty() ) {
                        objects.add(currentObject);
                        currentObject = new HashMap<String, String>();
                    }
                    continue;
                }
                int idx = line.indexOf(" ");

                if( idx == -1 ) {
                    currentObject.put(line, null);
                }
                else {
                    String k = line.substring(0, idx);

                    currentObject.put(k, line.substring(idx+1));
                }
            }
            if( !currentObject.isEmpty() ) {
                objects.add(currentObject);
            }
        }
        return objects;
    }

    public @Nullable Map<String,String> postObject(@Nonnull String resource, @Nonnull String body) throws InternalException, CloudException {
        String response = postString(resource, body);

        if( response == null || response.trim().length() < 1 ) {
            return null;
        }
        return toMap(response);
    }

    public @Nullable String postString(@Nonnull String resource, @Nonnull String body) throws InternalException, CloudException {
        if( logger.isTraceEnabled() ) {
            logger.trace("ENTER - " + SoftLayer.class.getName() + ".postString(" + resource + "," + body + ")");
        }

        try {
            String target = getEndpoint(resource);

            if( wire.isDebugEnabled() ) {
                wire.debug("");
                wire.debug(">>> [POST (" + (new Date()) + ")] -> " + target + " >--------------------------------------------------------------------------------------");
            }
            try {
                URI uri;

                try {
                    uri = new URI(target);
                }
                catch( URISyntaxException e ) {
                    throw new SoftLayerConfigurationException(e);
                }
                HttpClient client = getClient(uri);

                try {
                    ProviderContext ctx = provider.getContext();

                    if( ctx == null ) {
                        throw new NoContextException();
                    }
                    HttpPost post = new HttpPost(target);
                    String auth;

                    try {
                        String userName = new String(ctx.getAccessPublic(), "utf-8");
                        String password = new String(ctx.getAccessPrivate(), "utf-8");

                        auth = new String(Base64.encodeBase64((userName + ":" + password).getBytes()));
                    }
                    catch( UnsupportedEncodingException e ) {
                        throw new InternalException(e);
                    }
                    post.addHeader("Authorization", "Basic " + auth);
                    try {
                        post.setEntity(new StringEntity(body, "utf-8"));
                    }
                    catch( UnsupportedEncodingException e ) {
                        logger.error("Unsupported encoding UTF-8: " + e.getMessage());
                        throw new InternalException(e);
                    }

                    if( wire.isDebugEnabled() ) {
                        wire.debug(post.getRequestLine().toString());
                        for( Header header : post.getAllHeaders() ) {
                            wire.debug(header.getName() + ": " + header.getValue());
                        }
                        wire.debug("");
                        wire.debug(body);
                        wire.debug("");
                    }
                    HttpResponse response;
                    StatusLine status;

                    try {
                        response = client.execute(post);
                        status = response.getStatusLine();
                    }
                    catch( IOException e ) {
                        logger.error("Failed to execute HTTP request due to a cloud I/O error: " + e.getMessage());
                        throw new CloudException(e);
                    }
                    if( logger.isDebugEnabled() ) {
                        logger.debug("HTTP Status " + status);
                    }
                    Header[] headers = response.getAllHeaders();

                    if( wire.isDebugEnabled() ) {
                        wire.debug(status.toString());
                        for( Header h : headers ) {
                            if( h.getValue() != null ) {
                                wire.debug(h.getName() + ": " + h.getValue().trim());
                            }
                            else {
                                wire.debug(h.getName() + ":");
                            }
                        }
                        wire.debug("");
                    }
                    if( status.getStatusCode() == NOT_FOUND ) {
                        return null;
                    }
                    if( status.getStatusCode() != OK && status.getStatusCode() != NO_CONTENT ) {
                        logger.error("Expected OK for POST request, got " + status.getStatusCode());
                        HttpEntity entity = response.getEntity();

                        if( entity == null ) {
                            throw new SoftLayerException(CloudErrorType.GENERAL, status.getStatusCode(), status.getReasonPhrase(), status.getReasonPhrase());
                        }
                        try {
                            body = EntityUtils.toString(entity);
                        }
                        catch( IOException e ) {
                            throw new SoftLayerException(e);
                        }
                        if( wire.isDebugEnabled() ) {
                            wire.debug(body);
                        }
                        wire.debug("");
                        throw new SoftLayerException(CloudErrorType.GENERAL, status.getStatusCode(), status.getReasonPhrase(), body);
                    }
                    else {
                        HttpEntity entity = response.getEntity();

                        if( entity == null ) {
                            return "";
                        }
                        try {
                            body = EntityUtils.toString(entity);
                        }
                        catch( IOException e ) {
                            throw new SoftLayerException(e);
                        }
                        if( wire.isDebugEnabled() ) {
                            wire.debug(body);
                        }
                        wire.debug("");
                        return body;
                    }
                }
                finally {
                    try { client.getConnectionManager().shutdown(); }
                    catch( Throwable ignore ) { }
                }
            }
            finally {
                if( wire.isDebugEnabled() ) {
                    wire.debug("<<< [POST (" + (new Date()) + ")] -> " + target + " <--------------------------------------------------------------------------------------");
                    wire.debug("");
                }
            }
        }
        finally {
            if( logger.isTraceEnabled() ) {
                logger.trace("EXIT - " + SoftLayer.class.getName() + ".postString()");
            }
        }
    }
}
