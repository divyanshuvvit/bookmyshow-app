package com.bookmyshow;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URI;

public class BookMyShowSmokeTest {

    @Test(description = "Verify BookMyShow application is reachable")
    public void verifyApplicationIsAvailable() throws Exception {

        String url = System.getenv("BOOKMYSHOW_URL");

        if (url == null || url.isBlank()) {
            url = "http://k8s-default-bookmysh-1bf751842e-dfc06e98ef95d481.elb.ap-south-1.amazonaws.com";
        }

        HttpURLConnection connection =
                (HttpURLConnection) URI.create(url).toURL().openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int statusCode = connection.getResponseCode();

        System.out.println("BookMyShow URL: " + url);
        System.out.println("HTTP Status: " + statusCode);

        Assert.assertEquals(
                statusCode,
                200,
                "BookMyShow application should return HTTP 200"
        );

        connection.disconnect();
    }
}
