package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseAdapter;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.QuestionChildEventListener;

public class QuestionActivity extends AppCompatActivity {
    public static final String ROOM_NAME = "Room_name";
    public static String sort_type;
    private RecyclerView recyclerView;
    private String roomName;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;
    private DBUtil dbutil;
    private FirebaseAdapter firebaseAdapter;
    private List<Question> dataSet;
    private QuestionListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //initialized the sort_type
        if (read_sort(this).equals("default")) {
            save_sort("timestamp");
        }
        sort_type = read_sort(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_question);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        assert (intent != null);

        roomName = intent.getStringExtra(QuestionActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }
        setTitle("Room name: " + roomName);

        dataSet = new ArrayList<>();
        adapter = new QuestionListAdapter(new ArrayList<Question>());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        QuestionChildEventListener questionChildEventListener = new QuestionChildEventListener(adapter, dataSet);

        firebaseAdapter = new FirebaseAdapter(this);
        firebaseAdapter.setFirebase(firebaseAdapter.getFirebase().child(roomName).child("questions"));
        firebaseAdapter.addChildEventListener(questionChildEventListener);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    //Save and read the data from the SharedPreferences
    public void save_sort(String choice) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sort_choice", choice);
        editor.apply();
    }

    public static String read_sort(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String string_temp = sharedPref.getString("sort_choice", "default");
        return string_temp;
    }

    public void setSort(String sort) {
        /*
        final ListView listView = (ListView) findViewById(R.id.question_list);
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild(sort).limitToFirst(200),
                this, R.layout.question, roomName);
        save_sort(sort);
        sort_type=sort;
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Latest");
        menu.add(0, 1, 0, "Most Likes");
        return true;
    }

    //According to the menu choice, turn to its sort type
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                save_sort("timestamp");
                break;
            case 1:
                save_sort("echo");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        Log.w("debug", "Sort_type switch: " + read_sort(this));
        setSort(read_sort(this));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
/*
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = (ListView) findViewById(R.id.question_list);
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild(read_sort(this)).limitToFirst(200),
                this, R.layout.question, roomName);
        Log.w("debug","Sort_type: " + read_sort(this));
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Snackbar.make(findViewById(R.id.question_root), "Connected to Firebase", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.question_root), "Disconnected from Firebase", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });*/
    }

    @Override
    public void onStop() {
        super.onStop();
        /*
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();*/
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Question question = new Question(input);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            firebaseAdapter.getFirebase().push().setValue(question);
            inputText.setText("");
        }
    }

    public DBUtil getDbutil() {
        return dbutil;
    }

    public void updateEcho(String key, final int value, boolean echo) {
        if (dbutil.contains(key, true) || dbutil.contains(key, false)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase echoRef = firebaseAdapter.getFirebase().child(key).child("echo");
        echoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long echoValue = (Long) dataSnapshot.getValue();
                        Log.e("Echo update:", "" + echoValue);
                        echoRef.setValue(echoValue + value);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key, echo);
    }

    //=====================================Private Class=====================================
    private class QuestionListAdapter extends RecyclerViewAnimateAdapter<Question, QuestoinViewHolder> {
        public QuestionListAdapter(List<Question> list) {
            super(list);
        }

        @Override
        public QuestoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question, parent, false);
            return new QuestoinViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(QuestoinViewHolder holder, int position) {
            Question question = list.get(position);
            holder.title.setText(question.getHead());
            holder.newImage.setVisibility(question.isNewQuestion() ? View.VISIBLE : View.GONE);


            boolean echoUpclickable = !dbutil.contains(question.getKey(), true);
            boolean echoDownclickable = !dbutil.contains(question.getKey(), false);

            holder.echoUp.setTag(question.getKey());
            holder.echoUp.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateEcho((String) view.getTag(), 1, true);
                        }
                    }
            );

            holder.echoDown.setTag(question.getKey());
            holder.echoDown.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateEcho((String) view.getTag(), -1, false);
                        }
                    }
            );
            holder.echoUp.setClickable(echoUpclickable && echoDownclickable);
            holder.echoUp.setEnabled(echoUpclickable && echoDownclickable);
            holder.echoDown.setClickable(echoUpclickable && echoDownclickable);
            holder.echoDown.setEnabled(echoUpclickable && echoDownclickable);
            holder.echoUp.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    echoUpclickable ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.echoDown.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    echoDownclickable ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            if (question.getDesc().isEmpty())
                holder.content.setVisibility(View.INVISIBLE);
            else
                holder.content.setText(question.getDesc());
            holder.echo.setText(question.getEcho() + "");
        }
    }

    private static class QuestoinViewHolder extends RecyclerView.ViewHolder {
        private final ImageView newImage;
        private final ImageView echoUp;
        private final ImageView echoDown;
        private final TextView title;
        private final TextView content;
        private final TextView echo;

        public QuestoinViewHolder(View v) {
            super(v);
            newImage = (ImageView) v.findViewById(R.id.Question_New);
            echoUp = (ImageView) v.findViewById(R.id.echoUp);
            echoDown = (ImageView) v.findViewById(R.id.echoDown);
            title = (TextView) v.findViewById(R.id.Question_Title);
            content = (TextView) v.findViewById(R.id.Question_Content);
            echo = (TextView) v.findViewById(R.id.echo);
        }
    }
}
