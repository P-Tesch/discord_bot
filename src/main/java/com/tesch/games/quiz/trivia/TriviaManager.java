package com.tesch.games.quiz.trivia;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.tesch.db.TriviaRequester;
import com.tesch.db.entities.Question;
import com.tesch.db.entities.Topic;
import com.tesch.games.quiz.QuizManager;
import com.tesch.games.quiz.QuizQuestion;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TriviaManager extends QuizManager{

    public TriviaManager() {
        super(Color.blue, "Trivia");
    }

    public void onTriviaCommand(MessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw().split(" ");
        String topicName = split.length > 1 ? split[1] : null;
        Topic topic = validadeTopic(topicName);

        Set<User> players = new HashSet<>();
        players.add(event.getAuthor());
        event.getMessage().getMentions().getUsers().forEach(players::add);

        List<Question> questions = TriviaRequester.getQuestionsByTopic(topic);
        Question question = questions.get((int)(Math.random() * (questions.size() - 1)));
        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setQuestion(question.getContent());
        Map<String, Boolean> answers = new HashMap<>();
        question.getAnswers().forEach(answer -> answers.put(answer.getContent(), answer.getCorrect()));
        quizQuestion.setAnswers(answers);

        this.createLobby(quizQuestion, event.getChannel().asTextChannel(), players);
    }
    
    private Topic validadeTopic(String topicName) {
        List<Topic> topics = TriviaRequester.getAllTopics();
        for (Topic topic : topics) {
            if (topic.getName().equalsIgnoreCase(topicName)) {
                return topic;
            }
        }
        
        return topics.get(Math.toIntExact(Math.round(Math.random() * (topics.size() - 1))));
    }
}
