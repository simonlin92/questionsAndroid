package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;
import java.util.Date;
import hk.ust.cse.hunkim.questionroom.question.Question;

public class QuestionTest extends TestCase {

    private Question question;
    private String key = "";
    private String wholeMsg = "";
    private String head = "";
    private String headLastChar = "";
    private String desc = "";
    private String linkedDesc = "";
    private boolean completed;
    private String tags = "";
    private int echo;
    private int order;
    private boolean newQuestion;
    private String dateString;
    private String trustedDesc;

    protected void setUp() throws Exception {
        super.setUp();

        String message = "First Sentence\nSecond Sentence";

        question = new Question("");
        question = new Question(message);

        wholeMsg = message;
        echo = 0;
        head = Question.getFirstSentence(message).trim();

        if (head.length() + 1 < message.length())
            desc = message.substring(head.length() + 1);

        // get the last char
        if (head.length() > 0)
            headLastChar = head.substring(head.length() - 1);
        else
            headLastChar = "";
    }

    @SmallTest
    public void testGetDateString()
    {
        assertEquals(question.getDateString(),null);
    }

    @SmallTest
    public void testGetTrustedDesc()
    {
        assertEquals(question.getTrustedDesc(),null);
    }

    @SmallTest
    public void testGetHead()
    {
        assertEquals(question.getHead(),head);
    }

    @SmallTest
    public void testGetDesc()
    {
        assertEquals(question.getDesc(),desc);
    }

    @SmallTest
    public void testGetEcho() {
        assertEquals(question.getEcho(),echo);
    }

    @SmallTest
    public void testGetWholeMsg() {
        assertEquals(question.getWholeMsg(),wholeMsg);
    }

    @SmallTest
    public void testGetHeadLastChar() {
        assertEquals(question.getHeadLastChar(),headLastChar);
    }

    @SmallTest
    public void testGetLinkedDesc() {
        assertEquals(question.getLinkedDesc(),linkedDesc);
    }

    @SmallTest
    public void testIsCompleted() {
        assertEquals(question.isCompleted(),completed);
    }

    @SmallTest
    public void testGetTimestamp() {
        Date date  = new Date(question.getTimestamp());
        assertEquals(question.getTimestamp(),date.getTime());
    }

    @SmallTest
    public void testGetTags() {
        assertEquals(question.getTags(),tags);
    }

    @SmallTest
    public void testGetOrder() {
        assertEquals(question.getOrder(),order);
    }

    @SmallTest
    public void testIsNewQuestion() {
        assertEquals(question.isNewQuestion(),( question.getTimestamp() > new Date().getTime() - 180000));
    }


    @SmallTest
    public void testGetKey() {
        assertEquals(question.getKey(),key);
    }

    @SmallTest
    public void testSetKey() {
        String newKey = question.getKey()+"newKey";
        question.setKey(newKey);
        assertEquals(question.getKey(), newKey);
    }

}
