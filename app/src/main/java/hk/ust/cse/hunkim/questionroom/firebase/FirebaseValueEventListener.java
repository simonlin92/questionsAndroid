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
    private boolean sort = true;

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
        sortList();
        adapter.animateTo(list);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    public void setComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
        sortList();
        adapter.animateTo(list);
    }

    private void sortList() {
        if (comparator != null && sort)
            Collections.sort(list, comparator);
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    protected abstract T changeData(DataSnapshot snapshot);
}