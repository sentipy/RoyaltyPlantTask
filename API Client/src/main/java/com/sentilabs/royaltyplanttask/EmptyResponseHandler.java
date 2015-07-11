package com.sentilabs.royaltyplanttask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

/**
 * Created by AMachekhin on 09.07.2015.
 */
public class EmptyResponseHandler implements ResponseHandler {

    public static final EmptyResponseHandler INSTANCE = new EmptyResponseHandler();

    @Override
    public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        return null;
    }


}
