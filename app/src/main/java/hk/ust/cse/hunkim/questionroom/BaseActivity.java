package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import hk.ust.cse.hunkim.questionroom.firebase.FirebaseAdapter;
import hk.ust.cse.hunkim.questionroom.room.Room;
import hk.ust.cse.hunkim.questionroom.room.RoomValueEventListener;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.main_navigation);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            Fragment newFragment = new RoomListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_fragment, newFragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void switchRoom(String roomName) {
        Bundle bundle = new Bundle();
        bundle.putString(QuestionRoomFragment.ROOM_NAME, roomName);
        navigationView.getMenu().findItem(R.id.menu_roomlist).setChecked(true);
        navigationView.getMenu().findItem(R.id.menu_roomlist).setChecked(false);
        fragmentTransaction(new QuestionRoomFragment(), bundle);
    }

    private void switchRoom(String roomName, String password) {
        Bundle bundle = new Bundle();
        bundle.putString(PrivateRoomFragment.ROOM_NAME, roomName);
        bundle.putString(PrivateRoomFragment.PASSWORD, password);
        navigationView.getMenu().findItem(R.id.menu_roomlist).setChecked(true);
        navigationView.getMenu().findItem(R.id.menu_roomlist).setChecked(false);
        fragmentTransaction(new PrivateRoomFragment(), bundle);
    }

    public void enterRoom(final String roomName) {
        FirebaseAdapter firebaseAdapter = new FirebaseAdapter(this);
        firebaseAdapter.setFirebase(firebaseAdapter.getFirebase().child(roomName));
        firebaseAdapter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Room room;
                try {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map == null) {
                        switchRoom(roomName);
                        return;
                    }
                    if (map.containsKey(RoomValueEventListener.PASSWORD_KEY))
                        room = new Room(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount(),
                                map.get(RoomValueEventListener.PASSWORD_KEY).toString());
                    else
                        room = new Room(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount());

                } catch (ClassCastException e) {
                    room = new Room(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount());
                }
                if (room.hasPassword())
                    switchRoom(room.name, room.password);
                else
                    switchRoom(room.name);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        navigate(menuItem.getItemId());
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (fragment instanceof QuestionRoomFragment || fragment instanceof PrivateRoomFragment)
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.menu_roomlist));
        else
            super.onBackPressed();
    }

    private void navigate(final int itemId) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        switch (itemId) {
            case R.id.menu_roomlist:
                if (!(fragment instanceof RoomListFragment))
                    fragmentTransaction(new RoomListFragment());
                break;
            case R.id.menu_option:
                if (!(fragment instanceof OptionFragment))
                    fragmentTransaction(new OptionFragment());
                break;

            case R.id.menu_favourite:
                String roomName = OptionFragment.readFavRoom(this);
                enterRoom(roomName);
                break;
        }
    }

    private void fragmentTransaction(Fragment fragment, Bundle bundle) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (bundle != null)
            fragment.setArguments(bundle);
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    private void fragmentTransaction(Fragment fragment) {
        fragmentTransaction(fragment, null);
    }
}