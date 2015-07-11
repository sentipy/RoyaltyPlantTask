package com.sentilabs.royaltyplanttask;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sentipy on 09/07/15.
 */
public class APIClient {

    private CloseableHttpClient httpClient;
    private String hostname;
    private int port;
    private String protocol;

    public APIClient(String protocol, String hostname, int port) {
        this.protocol = protocol;
        this.hostname = hostname;
        this.port = port;
        CookieStore cookieStore = new BasicCookieStore();
        this.httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    private URI buildURI(String path) throws URISyntaxException {
        return new URIBuilder()
                .setScheme(this.protocol)
                .setHost(this.hostname)
                .setPort(port).setPath(path).build();
    }

    public <T> T registerUser(String username, String password, ResponseHandler<? extends T> responseHandler)
            throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/registerUser");
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

    public <T> T loginUser(String username, String password, ResponseHandler<? extends T> responseHandler)
            throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/login");
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

    public <T> T openAccount(ResponseHandler<? extends T> responseHandler) throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/openAccount");
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> parameters = new ArrayList<>();
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

    public <T> T openAccount(String account, ResponseHandler<? extends T> responseHandler) throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/openAccount");
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("account", account));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

    public <T> T getMyAccounts(ResponseHandler<? extends T> responseHandler) throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/getAccountsForCurrentClient");
        HttpPost httpPost = new HttpPost(uri);
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

    public <T> T borrowMoney(String account, BigDecimal sum, ResponseHandler<? extends T> responseHandler) throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/borrow");
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("accountNumber", account));
        parameters.add(new BasicNameValuePair("sum", sum.toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

    public <T> T transferMoney(String fromAccount, String toAccount, BigDecimal sum, ResponseHandler<? extends T> responseHandler) throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/transfer");
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("fromAccount", fromAccount));
        parameters.add(new BasicNameValuePair("toAccount", toAccount));
        parameters.add(new BasicNameValuePair("sum", sum.toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

    public <T> T getDocumentsForAccount(String account, ResponseHandler<? extends T> responseHandler) throws URISyntaxException, IOException {
        final URI uri = buildURI("/api/getDocumentsForAccountNumber");
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("accountNumber", account));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        T result = httpClient.execute(httpPost, responseHandler);
        return result;
    }

}
