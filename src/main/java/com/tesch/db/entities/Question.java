package com.tesch.db.entities;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Question {

    @JsonProperty("Question_id")
    private Long id;

    @JsonProperty("Question")
    private String content;

    @JsonProperty("Subtopic")
    private Subtopic subtopic;

    @JsonProperty("Answers")
    private List<Answer> answers;
    
    public Question() {
    }

    public Question(Long id, String content, Subtopic subtopic, List<Answer> answers) {
        this.id = id;
        this.content = content;
        this.subtopic = subtopic;
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Subtopic getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(Subtopic subtopic) {
        this.subtopic = subtopic;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((subtopic == null) ? 0 : subtopic.hashCode());
        result = prime * result + ((answers == null) ? 0 : answers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Question other = (Question) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (subtopic == null) {
            if (other.subtopic != null)
                return false;
        } else if (!subtopic.equals(other.subtopic))
            return false;
        if (answers == null) {
            if (other.answers != null)
                return false;
        } else if (!answers.equals(other.answers))
            return false;
        return true;
    }
}
