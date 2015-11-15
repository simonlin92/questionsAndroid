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
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private static final String FIREBASE_URL = "https://flickering-torch-4928.firebaseio.com/";
    private Firebase mFirebaseRef;
    private RecyclerView recyclerView;
    private RoomListAdapter adapter;
    private List<RoomInfo> dataSet;
    private InputDialog inputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataSet = new ArrayList<>();
        inputDialog = new InputDialog(this);

        // TODO: Remove menu input
        // Get Room list from firebase and add them to dataSet
        // Firebase should able to update if new room is added same as question
        // Call adapter.flushFilter(); after you update dataSet
        mFirebaseRef = new Firebase(FIREBASE_URL);
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot Snapshot : snapshot.getChildren()) {
                    dataSet.add(new RoomInfo(Snapshot.getKey(), (int) Snapshot.child("/questions").getChildrenCount()));
                }
                Collections.sort(dataSet, new RoomInfoCountComparator(false));
                adapter = new RoomListAdapter(new ArrayList<>(dataSet));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
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
            final String text = model.Name.toLowerCase();
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


    private class RoomInfoCountComparator implements Comparator<RoomInfo> {
        private boolean isAscending;
        RoomInfoCountComparator(){
            this(true);
        }
        RoomInfoCountComparator(boolean isAscending){
            this.isAscending=isAscending;
        }
        @Override
        public int compare(RoomInfo lhs, RoomInfo rhs) {
            if (lhs.Count == rhs.Count)
                return 0;
            if (lhs.Count > rhs.Count&&isAscending)
                return 1;
            else
                return -1;
        }
    }

    private void EnterRoom(String Name) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(QuestionActivity.ROOM_NAME, Name);
        startActivity(intent);
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
            holder.Name.setText(roomInfo.Name);
            if (roomInfo.Count <= 999)
                holder.Count.setText(String.valueOf(roomInfo.Count));
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