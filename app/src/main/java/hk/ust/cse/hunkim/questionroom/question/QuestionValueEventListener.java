package hk.ust.cse.hunkim.questionroom.question;

import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class QuestionValueEventListener implements ValueEventListener {

    private int questionCount;
    private int lastQuestionCount;
    private Toast toast;

    public QuestionValueEventListener(Toast toast ) {
        this.toast = toast;
        this.lastQuestionCount = -1;
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
       questionCount = (int) snapshot.getChildrenCount();

        Log.d("debug", "onDataChange " + questionCount + " " + lastQuestionCount);
        if (questionCount > lastQuestionCount && lastQuestionCount!=-1)
            popUp();
        lastQuestionCount = questionCount;
    }

    @Override
    public void onCancelled (FirebaseError firebaseError){}

    public void popUp() {
        int gain = questionCount - lastQuestionCount;
        String message = (gain > 0)?((gain > 1)?gain+" new questions":gain+" new question"):"No new question";
        toast.setText(message);
        toast.show();
    }
}
