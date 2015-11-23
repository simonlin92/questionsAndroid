package hk.ust.cse.hunkim.questionroom;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import hk.ust.cse.hunkim.questionroom.question.QuestionSort;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

public class OptionFragmentTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private OptionFragment optionFragment;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public OptionFragmentTest() {
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
        final MenuItem optionMenu = menu.findItem(R.id.menu_option);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().onNavigationItemSelected(optionMenu);
            }
        });
        getInstrumentation().waitForIdleSync();

        optionFragment = (OptionFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment);
    }

    @SmallTest
    public void testPreconditions()
    {
        assertNotNull("Question activity is not null", getActivity());
        assertNotNull("OptionFragmentTest is not null", optionFragment);
        assertNotNull("Drawer Layout is not null", drawerLayout);
        assertNotNull("NavigationView is not null", navigationView);
    }

    @MediumTest
    public void testOptions() {

        onView(withId(R.id.option_limit)).perform(typeText("20"), closeSoftKeyboard());

        QuestionSort.Order[] enumValues = QuestionSort.Order.ECHO_ASC.getDeclaringClass().getEnumConstants();
        for (int i = 0; i < enumValues.length; i++) {
            try{Thread.sleep(1000);}catch(Exception e){};
            onView(withId(R.id.option_questionsortspinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is(enumValues[i].getText()))).perform(click());
            onView(withId(R.id.option_questionsortspinner)).check(matches(withSpinnerText(containsString(enumValues[i].getText()))));
        }

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((TextView) getActivity().findViewById(R.id.option_fav)).setText("");
            }
        });
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.option_fav)).perform(typeText("new room"), closeSoftKeyboard());

        onView(withId(R.id.option_reset)).perform(click());
        //wait for changing
        try{Thread.sleep(2000);}catch(Exception e){};
        onView(withId(R.id.option_apply)).perform(click());
    }

}
