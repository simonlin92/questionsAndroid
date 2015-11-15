package hk.ust.cse.hunkim.questionroom.firebase;

import android.support.v7.app.AppCompatActivity;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class FirebaseAdapter {
    public static final String FIREBASE_URL = "https://flickering-torch-4928.firebaseio.com/";
    private static Firebase firebase=null;
    private Query query;
    public FirebaseAdapter(AppCompatActivity activity,Query query) {
        Firebase.setAndroidContext(activity);
        this.query=query;
    }

    public void addChildEventListener(ChildEventListener childEventListener){
        query.addChildEventListener(childEventListener);
    }

    public void addListenerForSingleValueEvent(ValueEventListener valueEventListener){
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    public static Firebase getFirebase(){
        if(firebase==null)
            firebase = new Firebase(FIREBASE_URL);
        return firebase;
    }
}
