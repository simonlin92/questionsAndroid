package hk.ust.cse.hunkim.questionroom.question;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import hk.ust.cse.hunkim.questionroom.RecyclerViewAnimateAdapter;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseChildEventListener;

public class QuestionChildEventListener<T extends RecyclerView.ViewHolder> extends FirebaseChildEventListener<Question, T> {

    public QuestionChildEventListener(RecyclerViewAnimateAdapter<Question, T> adapter, List<Question> list) {
        super(adapter, list, Question.class);
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }
}
