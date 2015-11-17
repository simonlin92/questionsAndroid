package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final Handler handler = new Handler();
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private int navItemId = 0;
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
        navigationView.getMenu().findItem(R.id.menu_roomlist).setChecked(false);
    }

    public void switchRoom(String roomName) {
        Bundle bundle = new Bundle();
        bundle.putString(QuestionFragment.ROOM_NAME, roomName);
        if (navItemId != 0)
            navigationView.getMenu().findItem(navItemId).setChecked(false);
        fragmentTransaction(new QuestionFragment(), bundle);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        menuItem.setChecked(true);
        navItemId = menuItem.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    private void navigate(final int itemId) {
        switch (itemId) {
            case R.id.menu_roomlist:
                fragmentTransaction(new RoomListFragment());
                break;
        }
    }

    private void fragmentTransaction(Fragment fragment, Bundle bundle) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_l2r, R.anim.slide_out_l2r, R.anim.slide_in_r2l, R.anim.slide_out_r2l);
        if (bundle != null)
            fragment.setArguments(bundle);
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    private void fragmentTransaction(Fragment fragment) {
        fragmentTransaction(fragment, null);
    }
}