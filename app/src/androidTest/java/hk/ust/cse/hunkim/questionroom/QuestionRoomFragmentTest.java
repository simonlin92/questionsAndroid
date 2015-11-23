package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class QuestionRoomFragmentTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private QuestionRoomFragment questionRoomFragment;

    public QuestionRoomFragmentTest() {
        super(BaseActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        getActivity().enterRoom(OptionFragment.defaultFavRoom);
        try { Thread.sleep(5000);} catch (InterruptedException e) {};

        navigationView = (NavigationView) getActivity().findViewById(R.id.main_navigation);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);

        questionRoomFragment = (QuestionRoomFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        getInstrumentation().runOnMainSync((new Runnable() {
            @Override
            public void run() {
                questionRoomFragment.scrollToTop();
            }
        }));
        getInstrumentation().waitForIdleSync();
    }

    @MediumTest
    public void testSendQuestion() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(PostActivity.class.getName(), null, false);
        onView(withId(R.id.fab)).perform(click());
        getInstrumentation().waitForIdleSync();
        PostActivity postActivity = (PostActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor,10000);
        onView(withId(R.id.question_title)).perform(typeText("android test title"), closeSoftKeyboard());
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.question_content)).perform(typeText("android test content"), closeSoftKeyboard());
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.sendButton)).perform(click());
    }

    @MediumTest
    public void testReplyQuestion() {
        testSendQuestion();
        try { Thread.sleep(5000);} catch (InterruptedException e) {};
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(QuestionActivity.class.getName(), null, false);

        final RecyclerView  recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        assertNotNull("RecyclerView is not null", recyclerView);

        getInstrumentation().runOnMainSync((new Runnable() {
            @Override
            public void run() {
                ImageView replyView = (ImageView) recyclerView.getChildAt(0).findViewById(R.id.reply);
                assertNotNull("Reply View is not null", replyView);
                replyView.performClick();
                try { Thread.sleep(2000);} catch (InterruptedException e) {};
                ImageView likeView = (ImageView) recyclerView.getChildAt(0).findViewById(R.id.echoUp);
                assertNotNull("Like button is not null", likeView);
                likeView.performClick();
                try { Thread.sleep(2000);} catch (InterruptedException e) {};
                ImageView dislikeView = (ImageView) recyclerView.getChildAt(0).findViewById(R.id.echoDown);
                assertNotNull("Dislike button is not null", dislikeView);
                dislikeView.performClick();
                try { Thread.sleep(2000);} catch (InterruptedException e) {};
            }
        }));
        getInstrumentation().waitForIdleSync();

        QuestionActivity questionActivity = (QuestionActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor,5000);
        //test small functions
        questionActivity.deletePost("");
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        questionActivity.updateEcho("", 0, false);

        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.fab)).perform(click());
        getInstrumentation().waitForIdleSync();
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.question_title)).perform(typeText("android test reply title"), closeSoftKeyboard());
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.question_content)).perform(typeText("android test reply content"), closeSoftKeyboard());
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.sendButton)).perform(click());
    }

    @SmallTest
    public void testSetFavourite() {
        onView(withId(R.id.questionroom_fav)).perform(click());
    }

    @LargeTest
    public void testAdminFunctions() {
        //login
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(GravityCompat.START);
                Menu menu = navigationView.getMenu();
                final MenuItem optionMenu = menu.findItem(R.id.menu_login );
                getActivity().onNavigationItemSelected(optionMenu);
            }
        });
        getInstrumentation().waitForIdleSync();

        onView(withId(R.id.admin_name)).perform(typeText("test@email.com"), closeSoftKeyboard());
        try{Thread.sleep(2000);}catch(Exception e){};
        onView(withId(R.id.admin_password)).perform(typeText("123456"), closeSoftKeyboard());
        try{Thread.sleep(2000);}catch(Exception e){};
        onView(withId(R.id.admin_submit)).perform(click());
        try{Thread.sleep(2000);}catch(Exception e){};

        getActivity().enterRoom(OptionFragment.defaultFavRoom);
        try{Thread.sleep(2000);}catch(Exception e){};

        //send addition question
        testSendQuestion();
        try { Thread.sleep(2000);} catch (InterruptedException e) {};

        //test admin functions
        final RecyclerView  recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        assertNotNull("RecyclerView is not null", recyclerView);

        getInstrumentation().runOnMainSync((new Runnable() {
            @Override
            public void run() {
                ImageView setFixedView = (ImageView) recyclerView.getChildAt(0).findViewById(R.id.Set_Fixed);
                assertNotNull("Set fixed button is not null", setFixedView);
                setFixedView.performClick();
                ImageView cancelFixedView = (ImageView) recyclerView.getChildAt(0).findViewById(R.id.Cancel_Fixed);
                assertNotNull("Cancel fixed button is not null", cancelFixedView);
                cancelFixedView.performClick();
                ImageView deleteView = (ImageView) recyclerView.getChildAt(0).findViewById(R.id.Delete_Post);
                assertNotNull("Delete button is not null", deleteView);
                deleteView.performClick();
            }
        }));
        getInstrumentation().waitForIdleSync();

        //test set password
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
        onView(withId(R.id.questionroom_pass)).perform(click());
        try { Thread.sleep(2000);} catch (InterruptedException e) {};
    }
}