package hk.ust.cse.hunkim.questionroom;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivityTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    public BaseActivityTest() {
        super(BaseActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        try { Thread.sleep(5000);} catch (InterruptedException e) {};
        navigationView = (NavigationView) getActivity().findViewById(R.id.main_navigation);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
    }

    @SmallTest
    public void testBackPressed() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @SmallTest
    public void testNavigationView() {
        navigateTo(R.id.menu_roomlist);
        navigateTo(R.id.menu_favourite);
        navigateTo(R.id.menu_option);
        navigateTo(R.id.menu_login);
    }

    private void navigateTo(final int id)
    {
        for(int i=0;i < 1;i++) {
            try { Thread.sleep(2000);} catch (InterruptedException e) {};
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.openDrawer(GravityCompat.START);
                    Menu menu = navigationView.getMenu();
                    final MenuItem optionMenu = menu.findItem(id);
                    getActivity().onNavigationItemSelected(optionMenu);
                }
            });
            getInstrumentation().waitForIdleSync();

            Menu menu = navigationView.getMenu();
            final MenuItem optionMenu = menu.findItem(id);
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    getActivity().onNavigationItemSelected(optionMenu);
                }
            });
            getInstrumentation().waitForIdleSync();
        }
    }
}
