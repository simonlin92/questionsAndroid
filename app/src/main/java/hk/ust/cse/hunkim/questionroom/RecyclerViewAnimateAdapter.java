package hk.ust.cse.hunkim.questionroom;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class RecyclerViewAnimateAdapter<T, U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<U> {
    protected List<T> list;

    public RecyclerViewAnimateAdapter(List<T> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public T removeItem(int position) {
        final T item = list.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    public void addItem(int position, T item) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final T roomInfo = list.remove(fromPosition);
        list.add(toPosition, roomInfo);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<T> list) {
        applyAndAnimateRemovals(list);
        applyAndAnimateAdditions(list);
        applyAndAnimateMovedItems(list);
    }

    private void applyAndAnimateRemovals(List<T> newList) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final T item = list.get(i);
            if (!newList.contains(item)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> newList) {
        for (int i = 0, count = newList.size(); i < count; i++) {
            final T item = newList.get(i);
            if (!list.contains(item)) {
                addItem(i, item);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> newList) {
        for (int toPosition = newList.size() - 1; toPosition >= 0; toPosition--) {
            final T item = newList.get(toPosition);
            final int fromPosition = list.indexOf(item);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
