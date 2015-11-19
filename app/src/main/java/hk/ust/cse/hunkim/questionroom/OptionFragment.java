package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import hk.ust.cse.hunkim.questionroom.question.QuestionSort;
import hk.ust.cse.hunkim.questionroom.room.Room;

public class OptionFragment extends Fragment {
    public static final String limitPrefKey = "RoomListLimit";
    public static final String favPrefKey = "FavouriteRoom";
    public static final int defaultLimit = 0;
    public static final String defaultFavRoom = "all";
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences sharedPref;
    private EditText limitEditText;
    private Spinner questionSortSpinner;
    private QuestionSort questionSort;
    private EditText favEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_option, container, false);
        initialToolbar();
        initialDrawer();
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        limitEditText = (EditText) findViewById(R.id.option_limit);
        questionSortSpinner = (Spinner) findViewById(R.id.option_questionsortspinner);
        favEditText = (EditText) findViewById(R.id.option_fav);

        questionSort = new QuestionSort(getActivity());
        QuestionSort.Order[] enumValues = QuestionSort.Order.ECHO_ASC.getDeclaringClass().getEnumConstants();
        String[] items = new String[enumValues.length];
        for (int i = 0; i < enumValues.length; i++) {
            items[i] = enumValues[i].getText();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        questionSortSpinner.setAdapter(adapter);
        questionSortSpinner.setSelection(questionSort.readSortByEnum().getValue());

        findViewById(R.id.option_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                savePref();
            }
        });

        findViewById(R.id.option_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                resetPref();
            }
        });

        int limit = sharedPref.getInt(limitPrefKey, defaultLimit);
        if (limit != 0)
            limitEditText.setText(String.valueOf(limit));
        favEditText.setText(sharedPref.getString(favPrefKey, defaultFavRoom));
        return coordinatorLayout;
    }

    public static String readFavRoom(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(favPrefKey, defaultFavRoom);
    }

    public static boolean putFavRoom(Activity activity, String favRoom) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!Room.isNameValid(favRoom) || favRoom.isEmpty())
            return false;
        editor.putString(favPrefKey, favRoom);
        editor.apply();
        return true;
    }

    private void savePref() {
        SharedPreferences.Editor editor = sharedPref.edit();
        String temp = limitEditText.getText().toString();
        if (temp.isEmpty())
            temp = "0";
        editor.putInt(limitPrefKey, Integer.parseInt(temp));
        questionSort.saveSort(QuestionSort.intToEnum(questionSortSpinner.getSelectedItemPosition()));
        if (!putFavRoom(getActivity(), favEditText.getText().toString())) {
            Snackbar.make(coordinatorLayout, "Invalid Room Name", Snackbar.LENGTH_LONG).show();
            return;
        }
        editor.apply();
        Snackbar.make(coordinatorLayout, "Setting Saved", Snackbar.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void resetPref() {
        limitEditText.setText(String.valueOf(defaultLimit));
        favEditText.setText(defaultFavRoom);
        questionSortSpinner.setSelection(QuestionSort.defaultSort.getValue());
        Snackbar.make(coordinatorLayout, "Setting Reset. Apply to save.", Snackbar.LENGTH_LONG).show();
    }

    private void initialToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Option");
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
