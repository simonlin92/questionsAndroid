package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.EditText;

import java.util.concurrent.Semaphore;


public class BaseActivityTest extends ActivityInstrumentationTestCase2<BaseActivity> {

    private BaseActivity baseActivity;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private TextInputLayout textInputLayout;
    private EditText editText;

    public BaseActivityTest() {
        super(BaseActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        baseActivity = getActivity();
        recyclerView  = (RecyclerView) baseActivity.findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
    }

    @MediumTest
    public void testInputDialogPreconditions()
    {
        assertNotNull("Base Activity is not null", baseActivity);
        assertNotNull("Recycler View is not null", recyclerView);
        assertNotNull("Floating Action Button is not null", fab);
    }

    @MediumTest
    public void testEnterValidRoomName() {
        assertEquals("Room name is valid", true, enterRoom("123"));
        assertEquals("Room name is valid", true, enterRoom("456"));
        assertEquals("Room name is valid", true, enterRoom("789"));
    }

    @MediumTest
    public void testEnterInvalidRoomName() {
        assertEquals("Room name is invalid", false, enterRoom("..."));
        assertEquals("Room name is invalid", false, enterRoom("AS."));
        assertEquals("Room name is invalid", false, enterRoom("12."));
    }

    private boolean enterRoom(String roomName) {
        final Semaphore semaphore = new Semaphore(1);
        try{semaphore.acquire();}catch(Exception e){};
        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.performClick();
                semaphore.release();
            }
        });
/*
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View input_dialog = inflater.inflate(R.layout.input_dialog, null);
                textInputLayout = (TextInputLayout) input_dialog.findViewById(R.id.textInput);
                editText = textInputLayout.getEditText();
                editText.requestFocus();
            }
        });*/
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(roomName);
        getInstrumentation().waitForIdleSync();

        assertNotNull("Text Input Layout  is not null", textInputLayout);
        assertNotNull("Edit Text is not null", editText);
        semaphore.release();

        try{Thread.sleep(1000);}catch(Exception e){};
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(QuestionRoomFragment.class.getName(), null, false);

        try{Thread.sleep(1000);}catch(Exception e){};
        //QuestionRoomFragment questionFragment = (QuestionRoomFragment) getInstrumentation().waitForMonitorWithTimeout(activityMonitor,2000);
        try{Thread.sleep(1000);}catch(Exception e){};
/*
        boolean result = (questionFragment != null);
        if(result)
            questionFragment.finish();*/
        return true;
    }
}
