package hk.ust.cse.hunkim.questionroom;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import android.support.design.widget.NavigationView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AdminLoginFragmentTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public AdminLoginFragmentTest() {
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

        Menu menu = navigationView.getMenu();
        final MenuItem loginMenu = menu.findItem(R.id.menu_login);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().onNavigationItemSelected(loginMenu);
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    @MediumTest
    public void testLogin()
    {
        onView(withId(R.id.admin_name)).perform(typeText("wrong name"), closeSoftKeyboard());
        try{Thread.sleep(2000);}catch(Exception e){};
        onView(withId(R.id.admin_password)).perform(typeText("wrong password"), closeSoftKeyboard());
        try{Thread.sleep(2000);}catch(Exception e){};
        onView(withId(R.id.admin_submit)).perform(click());
        //wait for changing
        try{Thread.sleep(2000);}catch(Exception e){};
        assertEquals("Cannot login",View.GONE,getActivity().findViewById(R.id.admin_logoutText).getVisibility());

        //reset text field
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((TextView) getActivity().findViewById(R.id.admin_name)).setText("");
                ((TextView) getActivity().findViewById(R.id.admin_password)).setText("");
            }
        });
        getInstrumentation().waitForIdleSync();

        onView(withId(R.id.admin_name)).perform(typeText("test@email.com"), closeSoftKeyboard());
        try{Thread.sleep(2000);}catch(Exception e){};
        onView(withId(R.id.admin_password)).perform(typeText("123456"), closeSoftKeyboard());
        try{Thread.sleep(2000);}catch(Exception e){};
        onView(withId(R.id.admin_submit)).perform(click());
        //wait for changing
        try{Thread.sleep(3000);}catch(Exception e){};
        assertEquals("Login successfully", View.VISIBLE, getActivity().findViewById(R.id.admin_logoutText).getVisibility());

        onView(withId(R.id.admin_logout)).perform(click());
        try{Thread.sleep(2000);}catch(Exception e){};
    }

}
