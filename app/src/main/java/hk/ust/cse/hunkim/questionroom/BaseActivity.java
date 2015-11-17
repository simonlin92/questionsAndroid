package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hk.ust.cse.hunkim.questionroom.firebase.FirebaseAdapter;
import hk.ust.cse.hunkim.questionroom.room.Room;
import hk.ust.cse.hunkim.questionroom.room.RoomReplyCountComparator;
import hk.ust.cse.hunkim.questionroom.room.RoomValueEventListener;

public class BaseActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private RecyclerView recyclerView;
    private RoomListAdapter adapter;
    private List<Room> dataSet;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initialToolbar();
        initialDrawer();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchItem.expandActionView();
            }
        });

        dataSet = new ArrayList<>();
        adapter = new RoomListAdapter(new ArrayList<Room>());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        RoomValueEventListener<RoomViewHolder> roomValueEventListener = new RoomValueEventListener<>(adapter, dataSet);
        roomValueEventListener.setComparator(new RoomReplyCountComparator(false));

        FirebaseAdapter firebaseAdapter = new FirebaseAdapter(this);
        firebaseAdapter.addValueEventListener(roomValueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Enter Room Name");
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Room.isNameValid(query)) {
            EnterRoom(query);
            return true;
        }
        Snackbar.make(findViewById(R.id.root_layout), "Only a-z, A-Z and 0-9 can be used.", Snackbar.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter == null)
            return true;
        final List<Room> filteredModelList = filter(dataSet, newText);
        adapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private List<Room> filter(List<Room> rooms, String query) {
        query = query.toLowerCase();
        final List<Room> filteredRoomList = new ArrayList<>();
        for (Room model : rooms) {
            final String text = model.name.toLowerCase();
            if (text.contains(query)) {
                filteredRoomList.add(model);
            }
        }
        return filteredRoomList;
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

    @Override
    public void onStart(){
        super.onStart();
        randomLogo();
    }

    private void EnterRoom(String Name) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(QuestionActivity.ROOM_NAME, Name);
        startActivity(intent);
    }

    private void initialToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initialDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                (Toolbar) findViewById(R.id.toolbar), R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void randomLogo(){
        ImageView logo = (ImageView) findViewById(R.id.base_logo);
        int[] logoID=new int[]{R.drawable.l1,R.drawable.l2};
        Random rand = new Random();
        logo.setImageResource(logoID[rand.nextInt(logoID.length)]);
    }

    //=====================================Private Class=====================================
    private class RoomListAdapter extends RecyclerViewAnimateAdapter<Room, RoomViewHolder> implements View.OnClickListener {

        public RoomListAdapter(List<Room> list) {
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
            Room room = list.get(position);
            holder.Name.setText(room.name);
            holder.Count.setText(room.questionCount <= 999 ? String.valueOf(room.questionCount) : "999+");
        }

        @Override
        public void onClick(View v) {
            TextView Name = (TextView) v.findViewById(R.id.card_room_name);
            EnterRoom(Name.getText().toString());
        }
    }

    private static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView Name;
        private final TextView Count;

        public RoomViewHolder(View v) {
            super(v);
            Name = (TextView) v.findViewById(R.id.card_room_name);
            Count = (TextView) v.findViewById(R.id.card_room_count);
        }
    }
}