package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.QuestionFragment;

/**
 * Created by hunkim on 7/16/15.
 */
public class Question implements Comparable<Question> {

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */
    private String key="";
    private String wholeMsg="";
    private String head="";
    private String headLastChar="";
    private String desc="";
    private String linkedDesc="";
    private boolean completed;
    private long timestamp;
    private String tags="";
    private int echo;
    private int order;
    private boolean newQuestion;

    public enum sort_order {
        timestamp,
        echo
    }

    public String getDateString() {
        return dateString;
    }

    private String dateString;

    public String getTrustedDesc() {
        return trustedDesc;
    }

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

    /**
     * New one/high echo goes bottom
     *
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {
        // Push new on top

        other.updateNewQuestion(); // update NEW button
        this.updateNewQuestion();
        /*
        if (this.newQuestion != other.newQuestion) {
            return this.newQuestion ? 1 : -1; // this is the winner
        }*/

        sort_order currentSort = sort_order.valueOf(QuestionFragment.sort_type);
        switch (currentSort) {
            case timestamp:
                if (other.timestamp == this.timestamp) {
                    return 0;
                }
                return other.timestamp > this.timestamp ? -1 : 1;
            case echo:
                if (this.echo == other.echo) {
                    if (other.timestamp == this.timestamp) {
                        return 0;
                    }
                    return other.timestamp > this.timestamp ? -1 : 1;
                }
                return this.echo - other.echo;
            default:
                if (other.timestamp == this.timestamp) {
                    return 0;
                }
                return other.timestamp > this.timestamp ? -1 : 1;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question)) {
            return false;
        }
        Question other = (Question) o;
        return key.equals(other.key) && echo == other.echo;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
