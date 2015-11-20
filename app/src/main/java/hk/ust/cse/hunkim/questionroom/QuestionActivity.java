package hk.ust.cse.hunkim.questionroom;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseAdapter;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.QuestionChildEventListener;
import hk.ust.cse.hunkim.questionroom.question.QuestionSort;

public class QuestionActivity extends AppCompatActivity {
    public static final String QUESTION_KEY = "QuestionKey";
    public static final String ROOMNAME_KEY = "RoomNameKey";
    public static final String TITLE_KEY = "TitleKey";
    private CoordinatorLayout rootLayout;
    private boolean animed = false;
    private DBUtil dbutil;
    private FirebaseAdapter firebaseAdapter;
    private FloatingActionButton fab;
    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        rootLayout = (CoordinatorLayout) findViewById(R.id.root);
        dbutil = new DBUtil(new DBHelper(this));

        firebaseAdapter = new FirebaseAdapter(this);
        Bundle extras = getIntent().getExtras();
        roomName = "";
        String title = "";
        String question = "";
        if (extras != null) {
            roomName = extras.getString(ROOMNAME_KEY);
            question = extras.getString(QUESTION_KEY);
            title = extras.getString(TITLE_KEY);
        } else
            finish();

        QuestionListAdapter adapter = new QuestionListAdapter(new ArrayList<Question>());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        QuestionChildEventListener<QuestionViewHolder> questionChildEventListener = new QuestionChildEventListener<>(adapter, new ArrayList<Question>());
        questionChildEventListener.setComparator(QuestionSort.getComparator(QuestionSort.Order.TIME_ASC));
        firebaseAdapter.setFirebase(firebaseAdapter.getFirebase().child(roomName).child("replies").child(question));
        firebaseAdapter.addChildEventListener(questionChildEventListener);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra(PostActivity.TITLE_KEY, roomName);
                intent.putExtra(PostActivity.FABX_KEY, (int) fab.getX());
                intent.putExtra(PostActivity.FABY_KEY, (int) fab.getY());
                startActivityForResult(intent, 1);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(title);
        }

        if (savedInstanceState == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rootLayout.setVisibility(View.INVISIBLE);
                ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (!animed) {
                                circularRevealActivity();
                                animed = true;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra(PostActivity.RESULT_KEY);
                sendMessage(new Question(result));
            }
        }
    }

    public void sendMessage(Question question) {
        firebaseAdapter.getFirebase().push().setValue(question);
    }

    private void circularRevealActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.DarkWhite));
            int cx = rootLayout.getWidth() / 2;
            int cy = rootLayout.getHeight() / 2;

            float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    rootLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.White));
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            circularReveal.setDuration(1000);

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        }
    }

    //=====================================Private Class=====================================

    private class QuestionListAdapter extends RecyclerViewAnimateAdapter<Question, QuestionViewHolder> {
        public QuestionListAdapter(List<Question> list) {
            super(list);
        }

        @Override
        public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_question, parent, false);
            return new QuestionViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(QuestionViewHolder holder, int position) {
            Question question = list.get(position);
            holder.title.setText(question.getHead());
            holder.newImage.setVisibility(question.isNewQuestion() ? View.VISIBLE : View.GONE);
            boolean echoUpClickable = !dbutil.contains(question.getKey(), true);
            boolean echoDownClickable = !dbutil.contains(question.getKey(), false);
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
            holder.echoUp.setClickable(echoUpClickable && echoDownClickable);
            holder.echoUp.setEnabled(echoUpClickable && echoDownClickable);
            holder.echoDown.setClickable(echoUpClickable && echoDownClickable);
            holder.echoDown.setEnabled(echoUpClickable && echoDownClickable);
            holder.echoUp.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    echoUpClickable ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.echoDown.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                    echoDownClickable ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            if (question.getDesc() == null || question.getDesc().isEmpty())
                holder.content.setVisibility(View.GONE);
            else {
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setText(question.getDesc());
            }
            holder.echo.setText(String.valueOf(question.getEcho()));
            holder.date_Time.setText(String.valueOf(getDate(question.getTimestamp())));
            if (question.getOrder() == 1) {
                holder.fixedTop.setVisibility(View.VISIBLE);
                holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.FixedColor));
            } else {
                holder.fixedTop.setVisibility(View.GONE);
                holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSub));
            }
        }
    }

    public void updateEcho(String key, final int value, boolean echo) {
        if (dbutil.contains(key, true) || dbutil.contains(key, false))
            return;

        final Firebase echoRef = firebaseAdapter.getFirebase().child(key).child("echo");
        echoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long echoValue = (Long) dataSnapshot.getValue();
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

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("HH:mm dd/MM", cal).toString();
    }

    private static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView newImage;
        private final ImageView echoUp;
        private final ImageView echoDown;
        private final ImageView fixedTop;
        private final TextView title;
        private final TextView content;
        private final TextView echo;
        private final TextView date_Time;
        private final RelativeLayout relativeLayout;

        public QuestionViewHolder(View v) {
            super(v);
            newImage = (ImageView) v.findViewById(R.id.Question_New);
            echoUp = (ImageView) v.findViewById(R.id.echoUp);
            echoDown = (ImageView) v.findViewById(R.id.echoDown);
            fixedTop = (ImageView) v.findViewById(R.id.FixedTop);
            title = (TextView) v.findViewById(R.id.Question_Title);
            content = (TextView) v.findViewById(R.id.Question_Content);
            echo = (TextView) v.findViewById(R.id.echo);
            date_Time = (TextView) v.findViewById(R.id.date_time);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.Question_TitlLayout);
            v.findViewById(R.id.reply).setVisibility(View.GONE);
        }
    }
}
