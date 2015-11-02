package hk.ust.cse.hunkim.questionroom;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import hk.ust.cse.hunkim.questionroom.question.Question;

public class QuestionActivityTest extends ActivityInstrumentationTestCase2<QuestionActivity> {

    private TextView headView;
    private TextView descView;

    private ListView questionListView;
    private ImageView sendButton;
    private EditText inputField;

    public QuestionActivityTest() {
        super(QuestionActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        build();
    }

    private void build()
    {
        sendButton = (ImageView) getActivity().findViewById(R.id.sendButton);
        questionListView = (ListView) getActivity().findViewById(R.id.question_list);
        inputField = (EditText) getActivity().findViewById(R.id.messageInput);
    }

    @MediumTest
    public void testPreconditions()
    {
        assertNotNull("Question activity is running", getActivity());
        assertNotNull("Send button is available", sendButton);
        assertNotNull("Question view is available", questionListView);
        assertNotNull("Input field is available", inputField);
        assertEquals("Correct room name ", "Room name: all", getActivity().getTitle());
    }

    @MediumTest
    public void testLikeDislikeButtons() {
        try { Thread.sleep(6000);} catch (InterruptedException e) {};
        int total = questionListView.getCount();
        askThisQuestion("Test like", "Like");
        likeThisQuestion(total);
        askThisQuestion("Test like and then dislike", "Like and then dislike");
        likeThisQuestion(total + 1);
        dislikeThisQuestion(total + 1);
        askThisQuestion("Test dislike", "Dislike");
        dislikeThisQuestion(total + 2);
        askThisQuestion("Test dislike and then like", "Dislike and then like");
        dislikeThisQuestion(total + 3);
        likeThisQuestion(total + 3);
        try { Thread.sleep(3000);} catch (InterruptedException e) {};
    }

    private void likeThisQuestion(final int targetPosition){
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getQuestionView(targetPosition).findViewById(R.id.echo).performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    private void dislikeThisQuestion(final int targetPosition){
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getQuestionView(targetPosition).findViewById(R.id.echoDown).performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @MediumTest
    public void testAskQuestion(){
      //  askThisQuestion(" Test question name as div", "<div>div region</div>");
      //  askThisQuestion("Test question name as a tag", "<a href=\"http://www.ust.hk/\">link</a>");
       // askThisQuestion("testAskQuestion - Test question name as img tag", "<img src=\"abc.jpg/\"></img>");
       // askThisQuestion("testAskQuestion - Test question name as script", "<script>alert(\"test\");</script>");
      //  askThisQuestion("testAskQuestion - Test question name as font", "<font color=blue>NEW </font>");

    }

    private void askThisQuestion(String message, final String input)  {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputField.setText(input);
                sendButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getInstrumentation().runOnMainSync((new Runnable() {
            @Override
            public void run() {

                View questionView =  getQuestionView(questionListView.getCount() - 1);
                assertNotNull("Last question is available", questionView);
                headView = (TextView) questionView.findViewById(R.id.Question_Title);
                assertNotNull("Question head is available", headView);
                descView = (TextView) questionView.findViewById(R.id.head_desc);
                assertNotNull("Question description is available", descView);
            }
        }));
        getInstrumentation().waitForIdleSync();

        String head = Question.getFirstSentence(input);
        String desc = "";
        if (head.length()+1 < input.length()) {
            desc = input.substring(head.length()+1);
        }
        assertEquals(message, head, headView.getText().toString());
        assertEquals(message, desc, descView.getText().toString());
    }

    private View getQuestionView(int targetPosition)
    {
        int firstPosition = questionListView.getFirstVisiblePosition() - questionListView.getHeaderViewsCount();
        int targetChild = targetPosition - firstPosition;
        return questionListView.getChildAt(targetChild);
    }

}
