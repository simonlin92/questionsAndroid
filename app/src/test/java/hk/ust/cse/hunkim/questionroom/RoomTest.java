package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;
import hk.ust.cse.hunkim.questionroom.room.Room;

public class RoomTest  extends TestCase {

    private Room publicRoom;
    private Room privateRoom;

    protected void setUp() throws Exception {
        super.setUp();
        publicRoom = new Room("Public room",1);
        privateRoom = new Room("Private room",1,"password");
    }

    @SmallTest
    public void testHasPassword()
    {
        assertEquals("Public room has no password",publicRoom.hasPassword(),false);
        assertEquals("Private room has password",privateRoom.hasPassword(),true);
    }

    @SmallTest
    public void testEquals()
    {
        assertEquals("Null room",publicRoom.equals(null),false);

        assertEquals("Compare to itself",publicRoom.equals(publicRoom),true);

        assertEquals("Compare to anothe object",publicRoom.equals(new Object()),false);

        Room room = new Room(publicRoom.name,publicRoom.questionCount);
        assertEquals("Equal object",publicRoom.equals(room),true);
        room = new Room(publicRoom.name,publicRoom.questionCount,"password");
        assertEquals("Not equal object",publicRoom.equals(room),false);
    }

    @SmallTest
    public void testIsNameValid()
    {
        assertEquals("Valid name",Room.isNameValid("abc"),true);
        assertEquals("Invalid name",Room.isNameValid("!@#$%^&*()"),false);
    }

}