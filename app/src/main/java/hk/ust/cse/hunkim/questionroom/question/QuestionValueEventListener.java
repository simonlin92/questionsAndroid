package hk.ust.cse.hunkim.questionroom.question;

import android.content.Context;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class QuestionValueEventListener implements ValueEventListener {

    private int questionCount;
    private int lastQuestionCount;
    private Context context;

    public QuestionValueEventListener(Context context) {
        this.context = context;
        this.lastQuestionCount = -1;
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        questionCount = (int) snapshot.getChildrenCount();
        
        if (questionCount > lastQuestionCount && lastQuestionCount!=-1)
            popUp();
        lastQuestionCount = questionCount;
    }

    @Override
    public void onCancelled (FirebaseError firebaseError){}

    public void popUp() {
        int gain = questionCount - lastQuestionCount;
        String message = (gain > 0)?"New question(s) have arrived":"No new question";
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }
}
