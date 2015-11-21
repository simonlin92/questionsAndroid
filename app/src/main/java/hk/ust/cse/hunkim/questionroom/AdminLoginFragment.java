package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import hk.ust.cse.hunkim.questionroom.firebase.FirebaseAdapter;

public class AdminLoginFragment extends Fragment {
    public static boolean admin = false;
    private FirebaseAdapter ref;
    private CoordinatorLayout coordinatorLayout;
    private EditText adminNameEditText;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ref = new FirebaseAdapter(getActivity());
        coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_admin, container, false);
        initialToolbar();
        initialDrawer();
        adminNameEditText = (EditText) findViewById(R.id.admin_name);
        passwordEditText = (EditText) findViewById(R.id.admin_password);
        findViewById(R.id.admin_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                ref.getFirebase().authWithPassword(adminNameEditText.getText().toString(), passwordEditText.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Login(true);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Wrong email or password", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        });

        findViewById(R.id.admin_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(false);
            }
        });


        return coordinatorLayout;
    }

    private void Login(boolean login) {
        admin = login;
        if (admin) {
            ((ImageView) getActivity().findViewById(R.id.profile_image)).setImageResource(R.drawable.admin_img);
            ((TextView) getActivity().findViewById(R.id.profile_name)).setText("Administrator");
            adminNameEditText.setVisibility(View.GONE);
            adminNameEditText.setText("");
            passwordEditText.setVisibility(View.GONE);
            adminNameEditText.setText("");
            findViewById(R.id.admin_submit).setVisibility(View.GONE);
            findViewById(R.id.admin_logout).setVisibility(View.VISIBLE);
            findViewById(R.id.admin_logoutText).setVisibility(View.VISIBLE);

        } else {
            ((ImageView) getActivity().findViewById(R.id.profile_image)).setImageResource(R.drawable.user_img);
            ((TextView) getActivity().findViewById(R.id.profile_name)).setText("User");
            adminNameEditText.setVisibility(View.VISIBLE);
            passwordEditText.setVisibility(View.VISIBLE);
            findViewById(R.id.admin_submit).setVisibility(View.VISIBLE);
            findViewById(R.id.admin_logout).setVisibility(View.GONE);
            findViewById(R.id.admin_logoutText).setVisibility(View.GONE);
        }

    }

    private void initialToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Admin Login");
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

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
