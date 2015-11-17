package hk.ust.cse.hunkim.questionroom.room;

import android.support.v7.widget.RecyclerView;

import com.firebase.client.DataSnapshot;

import java.util.List;

import hk.ust.cse.hunkim.questionroom.RecyclerViewAnimateAdapter;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseValueEventListener;

public class RoomValueEventListener<T extends RecyclerView.ViewHolder> extends FirebaseValueEventListener<Room, T> {
    public RoomValueEventListener(RecyclerViewAnimateAdapter<Room, T> adapter, List<Room> list) {
        super(adapter, list);
    }

    @Override
    protected Room changeData(DataSnapshot snapshot) {
        return new Room(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount());
    }

    public int getQuestionCount(String roomName)
    {
        for(int i=0;i<list.size();i++)
            if(list.get(i).name.equals(roomName))
                return list.get(i).questionCount;
        return 0;
    }
}
