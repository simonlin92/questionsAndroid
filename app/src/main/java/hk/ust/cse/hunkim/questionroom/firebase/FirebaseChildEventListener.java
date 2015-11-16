package hk.ust.cse.hunkim.questionroom.firebase;

import android.support.v7.widget.RecyclerView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.ust.cse.hunkim.questionroom.RecyclerViewAnimateAdapter;

public abstract class FirebaseChildEventListener<T, U extends RecyclerView.ViewHolder> implements ChildEventListener {
    private RecyclerViewAnimateAdapter<T, U> adapter;
    private List<T> list;
    private Comparator<? super T> comparator = null;
    private Class<T> itemClass;
    private Map<String, T> keys;
    private boolean sort = true;

    public FirebaseChildEventListener(RecyclerViewAnimateAdapter<T, U> adapter, List<T> list, Class<T> itemClass) {
        this.adapter = adapter;
        this.list = list;
        this.itemClass = itemClass;
        keys = new HashMap<>();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        T model = dataSnapshot.getValue(itemClass);
        String modelName = dataSnapshot.getKey();
        keys.put(modelName, model);
        setKey(modelName, model);

        if (s == null) {
            list.add(0, model);
        } else {
            T previousModel = keys.get(s);
            int previousIndex = list.indexOf(previousModel);
            int nextIndex = previousIndex + 1;
            if (nextIndex == list.size()) {
                list.add(model);
            } else {
                list.add(nextIndex, model);
            }
        }
        sortList();
        adapter.animateTo(list);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String modelName = dataSnapshot.getKey();
        T oldModel = keys.get(modelName);
        T newModel = dataSnapshot.getValue(itemClass);
        setKey(modelName, newModel);

        int index = list.indexOf(oldModel);
        list.set(index, newModel);
        keys.put(modelName, newModel);
        sortList();
        adapter.animateTo(list);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String modelName = dataSnapshot.getKey();
        T oldModel = keys.get(modelName);
        list.remove(oldModel);
        keys.remove(modelName);
        sortList();
        adapter.animateTo(list);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        String modelName = dataSnapshot.getKey();
        T oldModel = keys.get(modelName);
        T newModel = dataSnapshot.getValue(itemClass);
        setKey(modelName, newModel);

        int index = list.indexOf(oldModel);
        list.remove(index);
        if (s == null) {
            list.add(0, newModel);
        } else {
            T previousModel = keys.get(s);
            int previousIndex = list.indexOf(previousModel);
            int nextIndex = previousIndex + 1;
            if (nextIndex == list.size()) {
                list.add(newModel);
            } else {
                list.add(nextIndex, newModel);
            }
        }
        sortList();
        adapter.animateTo(list);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
    }

    public void setComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    public void setSort(boolean sort){
        this.sort=sort;
    }

    protected abstract void setKey(String key, T model);

    private void sortList() {
        if (comparator != null&&sort)
            Collections.sort(list, comparator);
    }
}