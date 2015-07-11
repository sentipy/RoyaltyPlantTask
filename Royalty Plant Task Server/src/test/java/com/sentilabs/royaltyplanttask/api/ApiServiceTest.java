package com.sentilabs.royaltyplanttask.api;

import com.sentilabs.royaltyplanttask.RoyaltyPlantTaskServerApplication;
import junit.framework.TestCase;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AMachekhin on 08.07.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RoyaltyPlantTaskServerApplication.class)
//@WebAppConfiguration
@WebIntegrationTest
@ActiveProfiles(profiles = "inmemory")
public class ApiServiceTest extends TestCase {

    private CloseableHttpClient createHttpClient() {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        return httpClient;
    }

    private CloseableHttpResponse createUser(String username, String password) throws Exception{
        CloseableHttpClient httpClient = createHttpClient();
        String userCreationUrl = "http" + "://" + "localhost:8080" + "/api/registerUser";
        HttpPost httpPost = new HttpPost(userCreationUrl);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        return httpResponse;
    }

    private CloseableHttpResponse loginUser(String username, String password, CloseableHttpClient httpClient) throws Exception{
        String userCreationUrl = "http" + "://" + "localhost:8080" + "/api/login";
        HttpPost httpPost = new HttpPost(userCreationUrl);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        return httpResponse;
    }

    @Test
    public void testRegisterUser() throws Exception {
        CloseableHttpResponse httpResponse = createUser("user1", "password1");
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.CREATED.value());
    }

    @Test
    public void testLoginUser() throws Exception {
        String username = "user2", password = "password2";
        createUser(username, password);
        CloseableHttpResponse httpResponse = loginUser(username, password, createHttpClient());
        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.OK.value());
    }

    @Test
    public void testOpenAccount() throws Exception {

    }

    @Test
    public void testBorrow() throws Exception {

    }

    @Test
    public void testTransfer() throws Exception {

    }
}