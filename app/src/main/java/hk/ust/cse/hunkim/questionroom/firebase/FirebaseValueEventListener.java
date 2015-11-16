package hk.ust.cse.hunkim.questionroom.firebase;


import android.support.v7.widget.RecyclerView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.RecyclerViewAnimateAdapter;

public abstract class FirebaseValueEventListener<T, U extends RecyclerView.ViewHolder> implements ValueEventListener {
    private RecyclerViewAnimateAdapter<T, U> adapter;
    private List<T> list;
    private Comparator<? super T> comparator = null;

    public FirebaseValueEventListener(RecyclerViewAnimateAdapter<T, U> adapter, List<T> list) {
        this.adapter = adapter;
        this.list = list;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        list.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            list.add(changeData(snapshot));
        }
        if (comparator != null)
            Collections.sort(list, comparator);
        adapter.animateTo(list);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    public void setComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    protected abstract T changeData(DataSnapshot snapshot);
}