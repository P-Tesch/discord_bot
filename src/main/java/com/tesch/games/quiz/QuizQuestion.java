package com.tesch.games.quiz;

import java.util.Map;

public class QuizQuestion {
    
    private String question;
    private Map<String, Boolean> answers;

    public QuizQuestion() {
    }

    public QuizQuestion(String question, Map<String, Boolean> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Boolean> answers) {
        this.answers = answers;
    }
}
