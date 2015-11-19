package hk.ust.cse.hunkim.questionroom.room;

import android.support.v7.widget.RecyclerView;

import com.firebase.client.DataSnapshot;

import java.util.List;
import java.util.Map;

import hk.ust.cse.hunkim.questionroom.RecyclerViewAnimateAdapter;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseValueEventListener;

public class RoomValueEventListener<T extends RecyclerView.ViewHolder> extends FirebaseValueEventListener<Room, T> {
    public static final String PASSWORD_KEY = "password";

    public RoomValueEventListener(RecyclerViewAnimateAdapter<Room, T> adapter, List<Room> list) {
        super(adapter, list);
    }

    @Override
    protected Room changeData(DataSnapshot snapshot) {
        Room room;
        try {
            Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
            if (map.containsKey(PASSWORD_KEY))
                room = new Room(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount(), map.get(PASSWORD_KEY).toString());
            else
                room = new Room(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount());

        } catch (ClassCastException e) {
            room = new Room(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount());
        }
        return room;
    }
}
