package hk.ust.cse.hunkim.questionroom;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.Menu;
import android.view.MenuItem;

public class FavouriteFragmentTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public FavouriteFragmentTest() {
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

        navigationView = (NavigationView) getActivity().findViewById(R.id.main_navigation);

        Menu menu = navigationView.getMenu();
        final MenuItem favouriteMenu = menu.findItem(R.id.menu_favourite);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().onNavigationItemSelected(favouriteMenu);
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @SmallTest
    public void testPreconditions()
    {
        assertNotNull("Question activity is not null", getActivity());
        assertNotNull("Drawer Layout is not null", drawerLayout);
        assertNotNull("NavigationView is not null", navigationView);
    }
}
