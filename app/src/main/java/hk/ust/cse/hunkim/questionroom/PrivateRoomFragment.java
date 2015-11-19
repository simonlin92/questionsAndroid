package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PrivateRoomFragment extends Fragment {
    public static final String ROOM_NAME = "Room_name";
    public static final String PASSWORD = "Password";
    private String roomName;
    private String password;
    private TextView passwordTextView;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_privateroom, container, false);
        initialToolbar();
        initialDrawer();

        Bundle bundle = getArguments();
        roomName = bundle.getString(ROOM_NAME, "all");
        TextView roomNameTextView = (TextView) coordinatorLayout.findViewById(R.id.privateroom_hint);
        passwordTextView = (TextView) coordinatorLayout.findViewById(R.id.privateroom_password);
        roomNameTextView.setText(roomName + " is locked");
        password = bundle.getString(PASSWORD, "");

        coordinatorLayout.findViewById(R.id.privateroom_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals(passwordTextView.getText().toString())) {
                    BaseActivity baseActivity = (BaseActivity) getActivity();
                    baseActivity.switchRoom(roomName);
                } else {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Incorrect Password", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentDark));
                    snackbar.show();
                }
            }
        });
        return coordinatorLayout;
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
}
