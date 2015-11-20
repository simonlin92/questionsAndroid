package hk.ust.cse.hunkim.questionroom.firebase;

import android.app.Activity;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class FirebaseAdapter {
    public static final String FIREBASE_URL = "https://flickering-torch-4928.firebaseio.com/";
    private Firebase firebase;

    public FirebaseAdapter(Activity activity) {
        Firebase.setAndroidContext(activity);
        firebase = new Firebase(FIREBASE_URL);
    }

    public void addChildEventListener(ChildEventListener childEventListener) {
        firebase.addChildEventListener(childEventListener);
    }

    public void addListenerForSingleValueEvent(ValueEventListener valueEventListener) {
        firebase.addListenerForSingleValueEvent(valueEventListener);
    }

    public void addValueEventListener(ValueEventListener valueEventListener) {
        firebase.addValueEventListener(valueEventListener);
    }

    public void setFirebase(Firebase firebase) {
        this.firebase = firebase;
    }

    public Firebase getFirebase() {
        return firebase;
    }
}
