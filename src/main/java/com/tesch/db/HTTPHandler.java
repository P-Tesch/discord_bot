package com.tesch.db;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.rmi.UnexpectedException;
import java.util.Base64;

import com.tesch.exceptions.InternalServerErrorException;
import com.tesch.exceptions.NotFoundException;

public class HTTPHandler {

    private static final String AUTH_STRING = "Basic " + 
        Base64.getEncoder()
            .encodeToString(
                (System.getenv("DB_USER") + 
                ":" + 
                System.getenv("DB_PASSWORD")
            ).getBytes()
        );

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .build();

    public static String executeRequest(String method, String path) throws NotFoundException, InternalServerErrorException, UnexpectedException {
        return HTTPHandler.executeRequest(method, path, "");
    }

    public static String executeRequest(String method, String path, String body) throws NotFoundException, InternalServerErrorException, UnexpectedException {
        try {
            URI uri = new URI(System.getenv("REQUEST_URL") + path);
            HttpRequest request = HttpRequest.newBuilder(uri)
                .method(method, BodyPublishers.ofString(body))
                .headers("Authorization", HTTPHandler.AUTH_STRING, "Content-Type", "application/json", "Content-Lenght", "" + body.length())
                .build();
                
            HttpResponse<String> response = HTTPHandler.HTTP_CLIENT.send(request, BodyHandlers.ofString());
            
            switch (response.statusCode()) {
                case 200:
                    return response.body();
                case 201:
                    return response.body();
                case 204:
                    return response.body();
                case 404:
                    throw new NotFoundException(response.body());
                case 500:
                    throw new InternalServerErrorException(response.body());
                default:
                    throw new UnexpectedException(response.body());
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
