package hk.ust.cse.hunkim.questionroom;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testLocal()throws Exception {
        Class<?> test = Class.forName("hk.ust.cse.hunkim.questionroom.RoomTest");
        JUnitCore junit = new JUnitCore();
        junit.run(test);
    }
}