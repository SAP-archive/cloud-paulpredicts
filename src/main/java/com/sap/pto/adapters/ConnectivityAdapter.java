package com.sap.pto.adapters;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.DestinationException;
import com.sap.core.connectivity.api.http.HttpDestination;

/**
 * This class is used for fetching data from external web service with Connectivity Service.
 * Connectivity Service is provided by the NetWeaver Cloud Platform. In the current application 
 * Connectivity is used for connecting to an external data provider.
 */
public class ConnectivityAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ConnectivityAdapter.class);
    private static HttpClient httpClient;

    public static String fetchContent(String destinationName, String url) throws IOException, ClientProtocolException {
        if (httpClient == null) {
            initHttpClient(destinationName);
        }
        HttpGet get = new HttpGet(url);
        HttpResponse resp = httpClient.execute(get);
        String content = EntityUtils.toString(resp.getEntity(), "UTF8");

        return content;
    }

    private static void initHttpClient(String destinationName) {
        try {
            Context ctx = new InitialContext();
            HttpDestination destination = (HttpDestination) ctx.lookup("java:comp/env/" + destinationName);
            httpClient = destination.createHttpClient();
        } catch (NamingException e) {
            logger.error("Could not find destination.", e);
        } catch (DestinationException e) {
            logger.error("Could not get http client.", e);
        }
    }

}
