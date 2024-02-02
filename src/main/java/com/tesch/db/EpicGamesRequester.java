package com.tesch.db;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.rmi.UnexpectedException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tesch.db.entities.EpicStoreGame;
import com.tesch.exceptions.InternalServerErrorException;
import com.tesch.exceptions.NotFoundException;

public class EpicGamesRequester {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .build();

    public static List<String> getFreeGames() {
        try {
            String getResponse = EpicGamesRequester.executeRequest("GET", "https://store-site-backend-static.ak.epicgames.com/freeGamesPromotions");
            JSONObject object = new JSONObject(getResponse);
            List<EpicStoreGame> freeGames = new ArrayList<>();
            object.getJSONObject("data").getJSONObject("Catalog").getJSONObject("searchStore").getJSONArray("elements").forEach(game -> {
                String title = ((JSONObject)game).getString("title");
                JSONObject promotions = ((JSONObject)game).getJSONObject("promotions");
                JSONArray promotionalOffers = promotions.getJSONArray("promotionalOffers");
                if (promotionalOffers.length() == 0) return;

                JSONObject promotionalOffers2 = promotionalOffers.getJSONObject(0);
                JSONArray promotionalOffers3 = promotionalOffers2.getJSONArray("promotionalOffers");
                JSONObject promotionalOffersValue = promotionalOffers3.getJSONObject(0);
                String startDateString = promotionalOffersValue.getString("startDate");
                LocalDate startDate = LocalDate.parse(startDateString.split("T")[0]);

                String endDateString = promotionalOffersValue.getString("endDate");
                LocalDate endDate = LocalDate.parse(endDateString.split("T")[0]);
                
                freeGames.add(new EpicStoreGame(title, startDate, endDate));
        });
            List<String> messageString = new ArrayList<>();
            freeGames.forEach(game -> {
                StringBuilder string = new StringBuilder();
                string.append("```lua\n");
                string.append("\"Jogo:\": " + game.getTitle().replace("'", "`") + "\n");
                string.append("\"Lugar:\": Epic Games\n");
                string.append("\"Período:\": Até " + game.getFreeEndDate().format(DateTimeFormatter.ofPattern("dd-LLLL")).replace("-", " de ") + "\n");
                string.append("```");
                messageString.add(string.toString());
            });

            return messageString;

       } catch (UnexpectedException e) {
           e.printStackTrace();
       } catch (NotFoundException e) {
           e.printStackTrace();
       } catch (InternalServerErrorException e) {
           e.printStackTrace();
       }

       return null;
   }

   private static String executeRequest(String method, String stringURI) throws NotFoundException, InternalServerErrorException, UnexpectedException {
        try {
            URI uri = new URI(stringURI);
            HttpRequest request = HttpRequest.newBuilder(uri)
                .method(method, BodyPublishers.noBody())
                .build();
                
            HttpResponse<String> response = EpicGamesRequester.HTTP_CLIENT.send(request, BodyHandlers.ofString());
            
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
