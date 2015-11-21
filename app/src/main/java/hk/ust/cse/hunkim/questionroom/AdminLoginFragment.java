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
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class AdminLoginFragment extends Fragment{
    Firebase ref = new Firebase("https://flickering-torch-4928.firebaseio.com/");
    AuthData authData = ref.getAuth();
    private CoordinatorLayout coordinatorLayout;
    private EditText adminNameEditText;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_admin, container, false);
        initialToolbar();
        initialDrawer();
        adminNameEditText = (EditText) findViewById(R.id.admin_name);
        passwordEditText = (EditText) findViewById(R.id.admin_password);
        findViewById(R.id.admin_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                ref.authWithPassword(adminNameEditText.getText().toString(), passwordEditText.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "You have logged in.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Wrong email or password", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
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
