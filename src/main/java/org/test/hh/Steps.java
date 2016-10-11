package org.test.hh;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class Steps {

    public HttpResponse sendHttpReq(String url) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = null;

        try {
            HttpGet request = new HttpGet(url);
            response = httpClient.execute(request);
            System.out.println("Response Code : "+ response.getStatusLine().getStatusCode());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}