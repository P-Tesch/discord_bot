package com.tesch.db;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesch.db.entities.Genre;
import com.tesch.exceptions.InternalServerErrorException;
import com.tesch.exceptions.NotFoundException;

public class MusicleRequester {   

    public static List<Genre> getAllGenres() {
        try {
            String response = HTTPHandler.executeRequest("GET", "genres/");

            ObjectMapper objectMapper = new ObjectMapper();
            Genre[] genres = objectMapper.readValue(response, Genre[].class);

            return Arrays.asList(genres);

        } catch (UnexpectedException e) {
            e.printStackTrace();
            return null;
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (InternalServerErrorException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
