package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.Collections;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class QuestionListAdapter extends FirebaseListAdapter<Question> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    QuestionActivity activity;

    public QuestionListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, Question.class, layout, activity);

        // Must be MainActivity
        assert (activity instanceof QuestionActivity);

        this.activity = (QuestionActivity) activity;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param question An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Question question) {
        DBUtil dbUtil = activity.getDbutil();

        // Map a Chat object to an entry in our listview
        int echo = question.getEcho();
        ImageView echoButton = (ImageView) view.findViewById(R.id.echo);
        TextView echoText = (TextView) view.findViewById(R.id.echo_text);
        echoText.setText("" + echo);


        echoButton.setTag(question.getKey()); // Set tag for button

        echoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        QuestionActivity m = (QuestionActivity) view.getContext();
                        m.updateEcho((String) view.getTag());
                    }
                }

        );

        String msgString = "";

        question.updateNewQuestion();
        if (question.isNewQuestion()) {
            msgString += "<font color=red>NEW </font>";
        }

        msgString += "<B>" + Html.escapeHtml(question.getHead()) + "</B>" + Html.escapeHtml(question.getDesc());

        ((TextView) view.findViewById(R.id.head_desc)).setText(Html.fromHtml(msgString));
        view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MainActivity m = (MainActivity) view.getContext();
                                        m.updateEcho((String) view.getTag());
                                    }
                                }

        );

        // check if we already clicked
        boolean clickable = !dbUtil.contains(question.getKey());

        echoButton.setClickable(clickable);
        echoButton.setEnabled(clickable);
        view.setClickable(clickable);


        // http://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
        // grey out our button
        if (clickable) {
            echoButton.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        } else {
            echoButton.setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }


        view.setTag(question.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Question> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }

}
