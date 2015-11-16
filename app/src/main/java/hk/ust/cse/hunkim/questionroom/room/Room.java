package hk.ust.cse.hunkim.questionroom.room;

public class Room {
    public final String name;
    public final int questionCount;

    public Room(String name, int questionCount) {
        this.name = name;
        this.questionCount = questionCount;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Room)) return false;
        Room otherRoom = (Room) other;
        return name.equals(otherRoom.name);
    }

    public static boolean isNameValid(String roomName) {
        return !roomName.matches("^.*[^a-zA-Z0-9 ].*$");
    }
}
