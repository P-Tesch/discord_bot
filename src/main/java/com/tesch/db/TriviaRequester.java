package com.tesch.db;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesch.db.entities.Question;
import com.tesch.db.entities.Topic;
import com.tesch.exceptions.InternalServerErrorException;
import com.tesch.exceptions.NotFoundException;

public class TriviaRequester {
    
    public static List<Topic> getAllTopics() {
        try {
            String response = HTTPHandler.executeRequest("GET", "topics/");

            ObjectMapper objectMapper = new ObjectMapper();
            Topic[] topics = objectMapper.readValue(response, Topic[].class);

            return Arrays.asList(topics);

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

    public static List<Question> getQuestionsByTopic(Topic topic) {
         try {
            String response = HTTPHandler.executeRequest("GET", "questions/?topic_id=" + topic.getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Question[] questions = objectMapper.readValue(response, Question[].class);

            return Arrays.asList(questions);

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
