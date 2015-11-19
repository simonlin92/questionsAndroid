package hk.ust.cse.hunkim.questionroom;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import hk.ust.cse.hunkim.questionroom.question.QuestionSort;
import hk.ust.cse.hunkim.questionroom.question.QuestionValueEventListener;

public class QuestionRoomFragment extends Fragment {
    public static final String ROOM_NAME = "Room_name";
    public static String sort_type;
    private DBUtil dbutil;
    private FirebaseAdapter firebaseAdapter;
    private QuestionSort questionSort;
    private QuestionChildEventListener<QuestionViewHolder> questionChildEventListener;
    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private String roomName;
    private ImageView sortImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_questionroom, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        roomName = bundle.getString(ROOM_NAME, "all");

        initialToolbar();
        initialDrawer();

        List<Question> dataSet = new ArrayList<>();
        QuestionListAdapter adapter = new QuestionListAdapter(new ArrayList<Question>());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        QuestionValueEventListener questionValueEventListener = new QuestionValueEventListener(getActivity().getApplicationContext());
        questionChildEventListener = new QuestionChildEventListener<>(adapter, dataSet);
        questionSort = new QuestionSort(getActivity());
        questionChildEventListener.setComparator(questionSort.readSort());
        sortImageView = (ImageView) findViewById(R.id.questionroom_sort);
        sortImageView.setImageResource(questionSort.readSortByEnum().getIcon());

        firebaseAdapter = new FirebaseAdapter(getActivity());
        firebaseAdapter.setFirebase(firebaseAdapter.getFirebase().child(roomName).child("questions"));
        firebaseAdapter.addChildEventListener(questionChildEventListener);
        firebaseAdapter.addValueEventListener(questionValueEventListener);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputText = (EditText) findViewById(R.id.messageInput);
                String input = inputText.getText().toString();
                if (!input.equals("")) {
                    sendMessage(new Question(input));
                    inputText.setText("");
                }
            }
        });

        // get the DB Helper
        dbutil = new DBUtil(new DBHelper(getActivity()));
        return coordinatorLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, QuestionSort.Order.TIME_DESC.getText());
        menu.add(0, 1, 0, QuestionSort.Order.ECHO_DESC.getText());
        menu.add(0, 2, 0, QuestionSort.Order.TIME_ASC.getText());
        menu.add(0, 3, 0, QuestionSort.Order.ECHO_ASC.getText());
    }

    //According to the menu choice, turn to its sort type
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                questionSort.saveSort(QuestionSort.Order.TIME_DESC);
                break;
            case 1:
                questionSort.saveSort(QuestionSort.Order.ECHO_DESC);
                break;
            case 2:
                questionSort.saveSort(QuestionSort.Order.TIME_ASC);
                break;
            case 3:
                questionSort.saveSort(QuestionSort.Order.ECHO_ASC);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        questionChildEventListener.setComparator(questionSort.readSort());
        sortImageView.setImageResource(questionSort.readSortByEnum().getIcon());
        scrollToTop();
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(Question question) {
        firebaseAdapter.getFirebase().push().setValue(question);
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

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    private void initialToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(roomName);
        }
    }

    private View findViewById(int id) {
        return coordinatorLayout.findViewById(id);
    }

    private void initialDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
                (Toolbar) findViewById(R.id.toolbar), R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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
            holder.echoUp.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(),
                    echoUpClickable ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.echoDown.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(),
                    echoDownClickable ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            if (question.getDesc() == null || question.getDesc().isEmpty())
                holder.content.setVisibility(View.GONE);
            else {
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setText(question.getDesc());
            }
            holder.echo.setText(String.valueOf(question.getEcho()));
        }
    }

    private static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView newImage;
        private final ImageView echoUp;
        private final ImageView echoDown;
        private final TextView title;
        private final TextView content;
        private final TextView echo;

        public QuestionViewHolder(View v) {
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
