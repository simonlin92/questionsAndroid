package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.QuestionRoomFragment;

/**
 * Created by hunkim on 7/16/15.
 */
public class Question {

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */
    private String key = "";
    private String wholeMsg = "";
    private String head = "";
    private String headLastChar = "";
    private String desc = "";
    private String linkedDesc = "";
    private boolean completed;
    private long timestamp;
    private String tags = "";
    private int echo;
    private int order;
    private boolean newQuestion;
    private String dateString;
    private String trustedDesc;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Question() {
    }

    /**
     * Set question from a String message
     *
     * @param message string message
     */
    public Question(String message) {
        wholeMsg = message;
        echo = 0;
        head = getFirstSentence(message).trim();

        if (head.length() + 1 < message.length())
            desc = message.substring(head.length() + 1);

        // get the last char
        if (head.length() > 0)
            headLastChar = head.substring(head.length() - 1);
        else
            headLastChar = "";

        timestamp = new Date().getTime();
    }

    /**
     * Get first sentence from a message
     *
     * @param message
     * @return
     */
    public static String getFirstSentence(String message) {
        String[] tokens = {"\n"};
        int index = -1;

        for (String token : tokens) {
            int i = message.indexOf(token);
            if (i == -1) {
                continue;
            }

            if (index == -1) {
                index = i;
            } else {
                index = Math.min(i, index);
            }
        }
        if (index == -1)
            return message;
        return message.substring(0, index + 1);
    }

    public String getDateString() {
        return dateString;
    }

    public String getTrustedDesc() {
        return trustedDesc;
    }

    /* -------------------- Getters ------------------- */
    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }

    public int getEcho() {
        return echo;
    }

    public String getWholeMsg() {
        return wholeMsg;
    }

    public String getHeadLastChar() {
        return headLastChar;
    }

    public String getLinkedDesc() {
        return linkedDesc;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTags() {
        return tags;
    }

    public int getOrder() {
        return order;
    }

    public boolean isNewQuestion() {
        updateNewQuestion();
        return newQuestion;
    }

    private void updateNewQuestion() {
        newQuestion = this.timestamp > new Date().getTime() - 180000;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question)) {
            return false;
        }
        Question other = (Question) o;
        return key.equals(other.key) && echo == other.echo;
    }
}
