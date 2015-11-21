package hk.ust.cse.hunkim.questionroom;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;

public class PostActivity extends AppCompatActivity {
    public static final String RESULT_KEY = "Result";
    public static final String TITLE_KEY = "Title";
    public static final String FABX_KEY = "FabX";
    public static final String FABY_KEY = "FabY";
    private CoordinatorLayout rootLayout;
    private boolean animed = false;
    private int fabX = -1;
    private int fabY = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        rootLayout = (CoordinatorLayout) findViewById(R.id.root);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        Bundle extras = getIntent().getExtras();
        String roomName = "";
        if (extras != null) {
            roomName = extras.getString(TITLE_KEY);
            fabX = extras.getInt(FABX_KEY, -1);
            fabY = extras.getInt(FABY_KEY, -1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(roomName);
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

    private void sendMessage() {
        EditText titleEditText = (EditText) findViewById(R.id.question_title);
        EditText contentEditText = (EditText) findViewById(R.id.question_content);
        String result = titleEditText.getText().toString() + "\n"
                + contentEditText.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESULT_KEY, result);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void circularRevealActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.DarkWhite));
            int cx = fabX == -1 ? rootLayout.getWidth() / 2 : fabX;
            int cy = fabY == -1 ? rootLayout.getHeight() / 2 : fabY;

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
            circularReveal.setDuration(500);

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        }
    }
}
