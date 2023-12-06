package com.tesch.db.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Answer {
    
    @JsonProperty("Answer_id")
    private Long id;

    @JsonProperty("Answer")
    private String content;

    @JsonProperty("Correct")
    private Boolean correct;

    @JsonProperty("Question_id")
    private Long questionId;

    public Answer() {
    }

    public Answer(Long id, String content, Boolean correct, Long questionId) {
        this.id = id;
        this.content = content;
        this.correct = correct;
        this.questionId = questionId;
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

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((correct == null) ? 0 : correct.hashCode());
        result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
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
        Answer other = (Answer) obj;
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
        if (correct == null) {
            if (other.correct != null)
                return false;
        } else if (!correct.equals(other.correct))
            return false;
        if (questionId == null) {
            if (other.questionId != null)
                return false;
        } else if (!questionId.equals(other.questionId))
            return false;
        return true;
    }
}
