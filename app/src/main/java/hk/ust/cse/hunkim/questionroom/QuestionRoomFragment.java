package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseAdapter;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.QuestionChildEventListener;
import hk.ust.cse.hunkim.questionroom.question.QuestionSort;
import hk.ust.cse.hunkim.questionroom.question.QuestionValueEventListener;
import hk.ust.cse.hunkim.questionroom.room.RoomValueEventListener;

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
    private ImageView favImageView;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_questionroom, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        roomName = bundle.getString(ROOM_NAME, "all");

        initialToolbar();
        initialDrawer();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                intent.putExtra(PostActivity.TITLE_KEY, roomName);
                intent.putExtra(PostActivity.FABX_KEY, (int) fab.getX());
                intent.putExtra(PostActivity.FABY_KEY, (int) fab.getY());
                startActivityForResult(intent, 1);
            }
        });

        QuestionListAdapter adapter = new QuestionListAdapter(new ArrayList<Question>());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        QuestionValueEventListener questionValueEventListener = new QuestionValueEventListener(getActivity().getApplicationContext());
        questionChildEventListener = new QuestionChildEventListener<>(adapter, new ArrayList<Question>());
        questionSort = new QuestionSort(getActivity());
        questionChildEventListener.setComparator(questionSort.readSort());
        sortImageView = (ImageView) findViewById(R.id.questionroom_sort);
        sortImageView.setImageResource(questionSort.readSortByEnum().getIcon());

        firebaseAdapter = new FirebaseAdapter(getActivity());
        firebaseAdapter.setFirebase(firebaseAdapter.getFirebase().child(roomName).child("questions"));
        firebaseAdapter.addChildEventListener(questionChildEventListener);
        firebaseAdapter.addValueEventListener(questionValueEventListener);


        favImageView = (ImageView) findViewById(R.id.questionroom_fav);
        favImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionFragment.putFavRoom(getActivity(), roomName);
                setFav();
            }
        });

        findViewById(R.id.questionroom_pass).setVisibility((AdminLoginFragment.admin) ? View.VISIBLE : View.GONE);
        findViewById(R.id.questionroom_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Set Password");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPassword(roomName, input.getText().toString(), true);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        setFav();
        // get the DB Helper
        dbutil = new DBUtil(new DBHelper(getActivity()));
        return coordinatorLayout;
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

    public void setPassword(String roomName, final String password, final boolean overwrite) {
        final FirebaseAdapter passfirebaseAdapter = new FirebaseAdapter(getActivity());
        passfirebaseAdapter.setFirebase(passfirebaseAdapter.getFirebase().child(roomName).child(RoomValueEventListener.PASSWORD_KEY));
        passfirebaseAdapter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (overwrite)
                    passfirebaseAdapter.getFirebase().setValue(password);
                else if (dataSnapshot.getValue() == null)
                    passfirebaseAdapter.getFirebase().setValue(password);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void sendMessage(Question question) {
        setPassword(roomName, "", false);
        firebaseAdapter.getFirebase().push().setValue(question);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("HH:mm dd/MM", cal).toString();
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

    public void updateOrder(String key, final int order) {

        final Firebase orderRef = firebaseAdapter.getFirebase().child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        orderRef.setValue(order);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
    }

    public void deletePost(String key) {
        final Firebase orderRef = firebaseAdapter.getFirebase().child(key);
        orderRef.removeValue();
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

    private void setFav() {

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.main_navigation);
        String name = OptionFragment.readFavRoom(getActivity());
        if (name.equals(roomName)) {
            favImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(),
                    R.color.colorAccentDark), PorterDuff.Mode.SRC_ATOP);
            navigationView.getMenu().findItem(R.id.menu_favourite).setChecked(true);
        } else
            favImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(),
                    R.color.White), PorterDuff.Mode.SRC_ATOP);
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
        public void onBindViewHolder(final QuestionViewHolder holder, int position) {
            final Question question = list.get(position);
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

            holder.toFixedTop.setVisibility((AdminLoginFragment.admin) ? View.VISIBLE : View.GONE);
            holder.cancelFixedTop.setVisibility((AdminLoginFragment.admin) ? View.VISIBLE : View.GONE);
            holder.deletePost.setVisibility((AdminLoginFragment.admin) ? View.VISIBLE : View.GONE);


            holder.toFixedTop.setTag(question.getKey());
            holder.toFixedTop.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateOrder((String) view.getTag(), 1);
                            holder.color.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccentDark));
                        }
                    }
            );
            holder.cancelFixedTop.setTag(question.getKey());
            holder.cancelFixedTop.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateOrder((String) view.getTag(), 0);
                            holder.color.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorSub));
                        }
                    }
            );

            holder.deletePost.setTag(question.getKey());
            holder.deletePost.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deletePost((String) view.getTag());
                        }
                    }
            );
            holder.echoUp.setClickable(echoUpClickable && echoDownClickable);
            holder.echoUp.setEnabled(echoUpClickable && echoDownClickable);
            holder.echoDown.setClickable(echoUpClickable && echoDownClickable);
            holder.echoDown.setEnabled(echoUpClickable && echoDownClickable);
            holder.echoUp.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(),
                    echoUpClickable ? R.color.colorSub : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.echoDown.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(),
                    echoDownClickable ? R.color.colorSub : R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            if (question.getDesc() == null || question.getDesc().isEmpty())
                holder.content.setVisibility(View.GONE);
            else {
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setText(question.getDesc());
            }
            holder.echo.setText(String.valueOf(question.getEcho()));
            holder.date_Time.setText(String.valueOf(getDate(question.getTimestamp())));
            FirebaseAdapter firebaseAdapter_order = new FirebaseAdapter(getActivity());
            firebaseAdapter_order.setFirebase(firebaseAdapter_order.getFirebase().child(roomName).child("questions").child(question.getKey()).child("order"));
            firebaseAdapter_order.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null)
                        return;
                    if ((Long) dataSnapshot.getValue() == 1) {
                        holder.color.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccentDark));
                    } else {
                        holder.color.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorSub));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            FirebaseAdapter firebaseAdapter = new FirebaseAdapter(getActivity());
            firebaseAdapter.setFirebase(firebaseAdapter.getFirebase().child(roomName).child("replies").child(question.getKey()));
            firebaseAdapter.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.replyCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView newImage;
        private final ImageView echoUp;
        private final ImageView echoDown;
        private final ImageView toFixedTop;
        private final ImageView cancelFixedTop;
        private final ImageView deletePost;
        private final TextView title;
        private final TextView content;
        private final TextView echo;
        private final TextView date_Time;
        private final View color;
        private final TextView replyCount;

        public QuestionViewHolder(View v) {
            super(v);
            newImage = (ImageView) v.findViewById(R.id.Question_New);
            echoUp = (ImageView) v.findViewById(R.id.echoUp);
            echoDown = (ImageView) v.findViewById(R.id.echoDown);
            toFixedTop = (ImageView) v.findViewById(R.id.Set_Fixed);
            cancelFixedTop = (ImageView) v.findViewById(R.id.Cancel_Fixed);
            deletePost = (ImageView) v.findViewById(R.id.Delete_Post);
            title = (TextView) v.findViewById(R.id.Question_Title);
            content = (TextView) v.findViewById(R.id.Question_Content);
            echo = (TextView) v.findViewById(R.id.echo);
            date_Time = (TextView) v.findViewById(R.id.date_time);
            color = v.findViewById(R.id.question_color);
            v.findViewById(R.id.reply).setOnClickListener(this);
            replyCount = (TextView) v.findViewById(R.id.replyCount);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), QuestionActivity.class);
            intent.putExtra(QuestionActivity.ROOMNAME_KEY, roomName);
            intent.putExtra(QuestionActivity.QUESTION_KEY, (String) echoUp.getTag());
            intent.putExtra(QuestionActivity.TITLE_KEY, title.getText().toString());
            startActivity(intent);
        }
    }
}
