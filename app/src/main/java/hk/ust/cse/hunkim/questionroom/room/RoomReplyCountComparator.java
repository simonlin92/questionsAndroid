package hk.ust.cse.hunkim.questionroom.room;

import java.util.Comparator;

public class RoomReplyCountComparator implements Comparator<Room> {
    private boolean isAscending;

    public RoomReplyCountComparator(boolean isAscending) {
        this.isAscending = isAscending;
    }

    @Override
    public int compare(Room lhs, Room rhs) {
        if (lhs.questionCount == rhs.questionCount)
            return 0;
        if (lhs.questionCount < rhs.questionCount)
            return isAscending ? -1 : 1;
        else
            return isAscending ? 1 : -1;
    }
}