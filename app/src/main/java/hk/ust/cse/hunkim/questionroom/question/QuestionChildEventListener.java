package hk.ust.cse.hunkim.questionroom.question;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import java.util.List;

import hk.ust.cse.hunkim.questionroom.RecyclerViewAnimateAdapter;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseChildEventListener;

public class QuestionChildEventListener<T extends RecyclerView.ViewHolder> extends FirebaseChildEventListener<Question, T> {

    private int lastQuestionCount;
    private int questionAddedCount;
    private boolean enableNotification;
    private Toast toast;

    public QuestionChildEventListener(RecyclerViewAnimateAdapter<Question, T> adapter, List<Question> list,Toast toast,int lastQuestionCount) {
        super(adapter, list, Question.class);
        this.lastQuestionCount = lastQuestionCount;
        this.toast = toast;
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }

    @Override
    protected void onChildAfter(DataSnapshot dataSnapshot, State state)
    {
        if(enableNotification && state == State.ADDED) {
            popUp();
            return;
        }
        switch(state){
            case ADDED: questionAddedCount++;break;
        }
        if(questionAddedCount >=lastQuestionCount)
            enableNotification =true;
    }

    public void popUp()
    {
        toast.show();
    }
}
