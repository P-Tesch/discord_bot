package com.tesch.db;

import java.io.IOException;
import java.io.StringWriter;
import java.rmi.UnexpectedException;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesch.db.entities.Botuser;
import com.tesch.db.entities.Score;
import com.tesch.exceptions.InternalServerErrorException;
import com.tesch.exceptions.NotFoundException;
import com.tesch.games.quiz.musicle.MusicleManager;
import com.tesch.games.quiz.trivia.TriviaManager;
import com.tesch.db.entities.Item;

public class GameRequester {

    public static void updateUserScore(Long discordId, Boolean win, Class<?> gameManager) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Botuser botuser;

            try {
                String getResponse = HTTPHandler.executeRequest("GET", "botusers/?discord_id=" + discordId);
                botuser = objectMapper.readValue(getResponse, Botuser[].class)[0];
            }
            catch (IllegalArgumentException e) {
                botuser = null;
            }
            catch (UnexpectedException e) {
                botuser = null;
            }
            catch (NullPointerException e) {
                botuser = null;
            }

            if (botuser == null) {
                StringWriter body = new StringWriter();
                Score score = new Score(null, 0, 0, 0, 0, 0, 0, 0, 0);
                objectMapper.writeValue(body, score);
                HTTPHandler.executeRequest("POST", "scores/", body.toString());

                String scoreResponse = HTTPHandler.executeRequest("GET", "scores/");
                Score[] scores = objectMapper.readValue(scoreResponse, Score[].class);
                score = scores[scores.length - 1];

                botuser = new Botuser();
                botuser.setItems((List<Item>)(new ArrayList<Item>()));
                botuser.setId(null);
                botuser.setDiscordId(discordId);
                botuser.setCurrency(0);
                botuser.setScore(score);
                body = new StringWriter();
                objectMapper.writeValue(body, botuser);
                HTTPHandler.executeRequest("POST", "botusers/", body.toString());
            }

            if (gameManager == MusicleManager.class) {
                botuser.getScore().setMusicleTotal(botuser.getScore().getMusicleTotal() + 1);
                if (win) {
                    botuser.getScore().setMusicleWin(botuser.getScore().getMusicleWin() + 1);
                }
            }
            if (gameManager == TriviaManager.class) {
                botuser.getScore().setTriviaTotal(botuser.getScore().getTriviaTotal() + 1);
                if (win) {
                    botuser.getScore().setTriviaWin(botuser.getScore().getTriviaWin() + 1);
                }
            }

            StringWriter body = new StringWriter();
            objectMapper.writeValue(body, botuser.getScore());
            HTTPHandler.executeRequest("PUT", "scores/", body.toString());

        } catch (UnexpectedException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (InternalServerErrorException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Score getUserScore(Long discordId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String getResponse = HTTPHandler.executeRequest("GET", "botusers/?discord_id=" + discordId);
            Botuser botuser = objectMapper.readValue(getResponse, Botuser[].class)[0];
            return botuser.getScore();

        } catch (UnexpectedException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (InternalServerErrorException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
