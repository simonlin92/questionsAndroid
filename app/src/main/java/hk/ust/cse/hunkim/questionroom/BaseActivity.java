package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<RoomInfo> dataSet;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataSet = new ArrayList<>();

        // TODO: Remove menu input
        // Get Room list from firebase and add them to dataSet
        // Firebase should able to update if new room is added same as question
        // When trying to update the list DONT create a new one, use the add(), insert(), remove(), and clear() instead
        // After update call adapter.notifyDataSetChanged();
        dataSet.add(new RoomInfo("asd", 1));
        dataSet.add(new RoomInfo("eee", 2));
        dataSet.add(new RoomInfo("fff", 3));
        dataSet.add(new RoomInfo("sad", 4));
        dataSet.add(new RoomInfo("asd", 1));
        dataSet.add(new RoomInfo("eee", 2));
        dataSet.add(new RoomInfo("fff", 3));
        dataSet.add(new RoomInfo("sad", 4));
        dataSet.add(new RoomInfo("asd", 1));
        dataSet.add(new RoomInfo("eee", 2));
        dataSet.add(new RoomInfo("fff", 3));
        dataSet.add(new RoomInfo("sad", 4));
        dataSet.add(new RoomInfo("asd", 1));
        dataSet.add(new RoomInfo("eee", 2));
        dataSet.add(new RoomInfo("fff", 3));
        dataSet.add(new RoomInfo("sad", 4));
        dataSet.add(new RoomInfo("asd", 1));
        dataSet.add(new RoomInfo("eee", 2));
        dataSet.add(new RoomInfo("fff", 3));
        dataSet.add(new RoomInfo("sad", 4));

        adapter = new RoomAdapter(dataSet);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<RoomInfo> filteredModelList = filter(dataSet, newText);
        adapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public void onClick(View view) {
        InputDialog inputDialog = new InputDialog(this);
        inputDialog.show();
    }

    private List<RoomInfo> filter(List<RoomInfo> roomInfos, String query) {
        query = query.toLowerCase();

        final List<RoomInfo> filteredRoomInfoList = new ArrayList<>();
        for (RoomInfo model : roomInfos) {
            final String text = model.Name.toLowerCase();
            if (text.contains(query)) {
                filteredRoomInfoList.add(model);
            }
        }
        return filteredRoomInfoList;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        return true;
    }

    private class RoomInfo {
        public final String Name;
        public final int Count;

        public RoomInfo(String Name, int Count) {
            this.Name = Name;
            this.Count = Count;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (!(other instanceof RoomInfo)) return false;
            RoomInfo otherRoomInfo = (RoomInfo) other;
            return Name.equals(otherRoomInfo.Name);
        }
    }

    private void EnterRoom(String Name) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(QuestionActivity.ROOM_NAME, Name);
        startActivity(intent);
    }

    private class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> implements View.OnClickListener {
        private List<RoomInfo> roomInfoList;

        public RoomAdapter(List<RoomInfo> roomInfoList) {
            this.roomInfoList = roomInfoList;
        }

        @Override
        public void onClick(final View view) {
            TextView Name = (TextView) view.findViewById(R.id.card_room_name);
            EnterRoom(Name.getText().toString());
        }

        @Override
        public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.card_room, parent, false);
            itemView.setOnClickListener(this);
            return new RoomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RoomViewHolder holder, int position) {
            RoomInfo roomInfo = roomInfoList.get(position);
            holder.Name.setText(roomInfo.Name);
            holder.Count.setText(String.valueOf(roomInfo.Count));
        }

        @Override
        public int getItemCount() {
            return roomInfoList.size();
        }

        public RoomInfo removeItem(int position) {
            final RoomInfo roomInfo = roomInfoList.remove(position);
            notifyItemRemoved(position);
            return roomInfo;
        }

        public void addItem(int position, RoomInfo model) {
            roomInfoList.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final RoomInfo roomInfo = roomInfoList.remove(fromPosition);
            roomInfoList.add(toPosition, roomInfo);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void animateTo(List<RoomInfo> roomInfo) {
            applyAndAnimateRemovals(roomInfo);
            applyAndAnimateAdditions(roomInfo);
            applyAndAnimateMovedItems(roomInfo);
        }

        private void applyAndAnimateRemovals(List<RoomInfo> roomInfos) {
            for (int i = roomInfos.size() - 1; i >= 0; i--) {
                final RoomInfo roomInfo = roomInfoList.get(i);
                if (!roomInfos.contains(roomInfo)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<RoomInfo> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final RoomInfo roomInfo = newModels.get(i);
                if (!roomInfoList.contains(roomInfo)) {
                    addItem(i, roomInfo);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<RoomInfo> roomInfos) {
            for (int toPosition = roomInfos.size() - 1; toPosition >= 0; toPosition--) {
                final RoomInfo roomInfo = roomInfos.get(toPosition);
                final int fromPosition = roomInfoList.indexOf(roomInfo);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        public class RoomViewHolder extends RecyclerView.ViewHolder {
            private final TextView Name;
            private final TextView Count;

            public RoomViewHolder(View v) {
                super(v);
                Name = (TextView) v.findViewById(R.id.card_room_name);
                Count = (TextView) v.findViewById(R.id.card_room_count);
            }
        }
    }
}