package hk.ust.cse.hunkim.questionroom.room;

public class Room {
    public final String name;
    public final int questionCount;
    public final String password;

    public Room(String name, int questionCount, String password) {
        this.name = name;
        this.questionCount = questionCount;
        this.password = password;
    }

    public Room(String name, int questionCount) {
        this(name, questionCount, "");
    }

    public static boolean isNameValid(String roomName) {
        return !roomName.matches("^.*[^a-zA-Z0-9 ].*$");
    }

    public boolean hasPassword() {
        return !password.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Room)) return false;
        Room otherRoom = (Room) other;
        return name.equals(otherRoom.name) && questionCount == otherRoom.questionCount;
    }
}
