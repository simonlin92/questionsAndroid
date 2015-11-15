package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.firebase.FirebaseAdapter;
import hk.ust.cse.hunkim.questionroom.firebase.FirebaseValueEventListener;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private FirebaseAdapter firebaseAdapter;
    private RecyclerView recyclerView;
    private RoomListAdapter adapter;
    private List<RoomInfo> dataSet;
    private InputDialog inputDialog;
    private RoomInfoValueEventListener roomInfoValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        dataSet = new ArrayList<>();
        adapter = new RoomListAdapter(new ArrayList<>(dataSet));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        inputDialog = new InputDialog(this);
        roomInfoValueEventListener=new RoomInfoValueEventListener(adapter,dataSet);
        roomInfoValueEventListener.setComparator(new RoomInfoCountComparator(false));

        firebaseAdapter = new FirebaseAdapter(this);
        firebaseAdapter.addValueEventListener(roomInfoValueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        MenuItemCompat.setOnActionExpandListener(item, this);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (InputDialog.isEmailValid(query)) {
            EnterRoom(query);
            return true;
        }
        Snackbar.make(findViewById(R.id.root_layout), "Only a-z, A-Z and 0-9 can be used.", Snackbar.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter == null)
            return true;
        final List<RoomInfo> filteredModelList = filter(dataSet, newText);
        adapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public void onClick(View view) {
        inputDialog.show();
    }

    public InputDialog getInputDialog() {
        return this.inputDialog;
    }

    private List<RoomInfo> filter(List<RoomInfo> roomInfos, String query) {
        query = query.toLowerCase();
        final List<RoomInfo> filteredRoomInfoList = new ArrayList<>();
        for (RoomInfo model : roomInfos) {
            final String text = model.name.toLowerCase();
            if (text.contains(query)) {
                filteredRoomInfoList.add(model);
            }
        }
        return filteredRoomInfoList;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).setExpanded(false);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }


    private void EnterRoom(String Name) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(QuestionActivity.ROOM_NAME, Name);
        startActivity(intent);
    }

    //=====================================Private Class=====================================

    private class RoomInfoValueEventListener extends FirebaseValueEventListener<RoomInfo, RoomViewHolder> {
        private RecyclerViewAnimateAdapter<RoomInfo, RoomViewHolder> adapter;

        public RoomInfoValueEventListener(RecyclerViewAnimateAdapter<RoomInfo, RoomViewHolder> adapter, List<RoomInfo> list) {
            super(adapter, list);
        }

        @Override
        protected RoomInfo changeData(DataSnapshot snapshot) {
            return new RoomInfo(snapshot.getKey(), (int) snapshot.child("/questions").getChildrenCount());
        }
    }

    private class RoomInfo {
        public final String name;
        public final int count;

        public RoomInfo(String Name, int Count) {
            this.name = Name;
            this.count = Count;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (!(other instanceof RoomInfo)) return false;
            RoomInfo otherRoomInfo = (RoomInfo) other;
            return name.equals(otherRoomInfo.name);
        }
    }

    private class RoomInfoCountComparator implements Comparator<RoomInfo> {
        private boolean isAscending;

        RoomInfoCountComparator(boolean isAscending) {
            this.isAscending = isAscending;
        }

        @Override
        public int compare(RoomInfo lhs, RoomInfo rhs) {
            if (lhs.count == rhs.count)
                return 0;
            if (lhs.count < rhs.count)
                return isAscending ? -1 : 1;
            else
                return isAscending ? 1 : -1;
        }
    }

    private class RoomListAdapter extends RecyclerViewAnimateAdapter<RoomInfo, RoomViewHolder> implements View.OnClickListener {

        public RoomListAdapter(List<RoomInfo> list) {
            super(list);
        }

        @Override
        public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_room, parent, false);
            itemView.setOnClickListener(this);
            return new RoomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RoomViewHolder holder, int position) {
            RoomInfo roomInfo = list.get(position);
            holder.Name.setText(roomInfo.name);
            if (roomInfo.count <= 999)
                holder.Count.setText(String.valueOf(roomInfo.count));
            else
                holder.Count.setText("999+");

        }

        @Override
        public void onClick(View v) {
            TextView Name = (TextView) v.findViewById(R.id.card_room_name);
            EnterRoom(Name.getText().toString());
        }
    }

    private class RoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView Name;
        private final TextView Count;

        public RoomViewHolder(View v) {
            super(v);
            Name = (TextView) v.findViewById(R.id.card_room_name);
            Count = (TextView) v.findViewById(R.id.card_room_count);
        }
    }
}