package hk.ust.cse.hunkim.questionroom;

import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

public class RoomListFragmentTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private BaseActivity baseActivity;
    private NavigationView navigationView;
    private RoomListFragment roomListFragment;

    public RoomListFragmentTest() {
        super(BaseActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //wait for database access
        try { Thread.sleep(5000);} catch (InterruptedException e) {};

        baseActivity = getActivity();
        navigationView = (NavigationView) getActivity().findViewById(R.id.main_navigation);
        roomListFragment = (RoomListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment);
    }

    @SmallTest
    public void testPreconditions() {
        assertNotNull("Base Activity is not null", baseActivity);
        assertNotNull("NavigationView is not null", navigationView);
    }

    @MediumTest
    public void testEnterRoom() {
        baseActivity.enterRoom(OptionFragment.defaultFavRoom);
        //wait for database onDataChange
        try {Thread.sleep(5000); } catch (Exception e) {};
    }

    @MediumTest
    public void testOnQueryTextSubmi() {
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

}
