package com.sentilabs.royaltyplanttask.helper;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AMachekhin on 08.07.2015.
 */
public class UserCreator {

    public static void main(String[] args) {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        String userCreationUrl = Constants.PROTOCOL + "://" + Constants.BASE_URL + "/api/registerUser";
        HttpPost httpPost = new HttpPost(userCreationUrl);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("username", "user1"));
        parameters.add(new BasicNameValuePair("password", "password1"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            System.out.println(IOUtils.toString(httpResponse.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
