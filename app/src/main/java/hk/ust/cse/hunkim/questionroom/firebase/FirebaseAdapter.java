package hk.ust.cse.hunkim.questionroom.firebase;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class FirebaseAdapter {
    public static final String FIREBASE_URL = "https://flickering-torch-4928.firebaseio.com/";
    private Firebase firebase;
    private Query query = null;

    public FirebaseAdapter(Activity activity) {
        Firebase.setAndroidContext(activity);
        firebase = new Firebase(FIREBASE_URL);
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public void addChildEventListener(ChildEventListener childEventListener) {
        if (query == null)
            firebase.addChildEventListener(childEventListener);
        else
            query.addChildEventListener(childEventListener);
    }

    public void addListenerForSingleValueEvent(ValueEventListener valueEventListener) {
        if (query == null)
            firebase.addListenerForSingleValueEvent(valueEventListener);
        else
            query.addListenerForSingleValueEvent(valueEventListener);
    }

    public void addValueEventListener(ValueEventListener valueEventListener) {
        if (query == null)
            firebase.addValueEventListener(valueEventListener);
        else
            query.addValueEventListener(valueEventListener);

    }

    public Firebase getFirebase() {
        return firebase;
    }
}
