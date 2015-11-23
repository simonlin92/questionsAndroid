package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.Menu;
import android.view.MenuItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class RoomListFragmentTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private NavigationView navigationView;
    private RoomListFragment roomListFragment;
    private DrawerLayout drawerLayout;

    public RoomListFragmentTest() {
        super(BaseActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //wait for database access
        try { Thread.sleep(5000);} catch (InterruptedException e) {};

        navigationView = (NavigationView) getActivity().findViewById(R.id.main_navigation);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        getInstrumentation().waitForIdleSync();

        roomListFragment = (RoomListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment);
    }

   /* @MediumTest
    public void testEnterRoom() {
        getActivity().enterRoom(OptionFragment.defaultFavRoom);
        //wait for database onDataChange
        try {Thread.sleep(5000); } catch (Exception e) {};
    }*/

    @MediumTest
    public void testEnterPrivateRoom() {

        getActivity().enterRoom(OptionFragment.defaultFavRoom);
        try { Thread.sleep(5000);} catch (InterruptedException e) {};
        QuestionRoomFragment questionRoomFragment = (QuestionRoomFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment);

        questionRoomFragment.setPassword(OptionFragment.defaultFavRoom + " private", "1234", true);
        try { Thread.sleep(2000);} catch (InterruptedException e) {};

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        getInstrumentation().waitForIdleSync();
        Menu menu = navigationView.getMenu();
        final MenuItem loginMenu = menu.findItem(R.id.menu_roomlist);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().onNavigationItemSelected(loginMenu);
            }
        });

        getActivity().enterRoom(OptionFragment.defaultFavRoom + " private");
        try { Thread.sleep(2000);} catch (InterruptedException e) {};

        onView(withId(R.id.privateroom_password)).perform(typeText("1234"), closeSoftKeyboard());
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.privateroom_submit)).perform(click());
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
    }
/*
    @MediumTest
    public void testOnQueryTextSubmit() {
        roomListFragment.onQueryTextSubmit("all");
        try {Thread.sleep(2000); } catch (Exception e) {};
        roomListFragment.onQueryTextSubmit("!@#$%^&*()");
        try {Thread.sleep(1000); } catch (Exception e) {};
    }

    @SmallTest
    public void testBackButton() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @SmallTest
    public void testRecyclerViewOnClick() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
                recyclerView.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
    }
*/
}
