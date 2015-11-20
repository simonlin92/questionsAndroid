package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

public class RoomListFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private RecyclerView recyclerView;
    private RoomListAdapter adapter;
    private List<Room> dataSet;
    private MenuItem searchItem;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_roomlist, container, false);
        setHasOptionsMenu(true);

        initialToolbar();
        initialDrawer();

        dataSet = new ArrayList<>();
        adapter = new RoomListAdapter(new ArrayList<Room>());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        RoomValueEventListener<RoomListAdapter.RoomViewHolder> roomValueEventListener = new RoomValueEventListener<>(adapter, dataSet);
        roomValueEventListener.setComparator(new RoomReplyCountComparator(false));
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        roomValueEventListener.setSizeLimit(sharedPref.getInt(OptionFragment.limitPrefKey, OptionFragment.defaultLimit));

        FirebaseAdapter firebaseAdapter = new FirebaseAdapter(getActivity());
        firebaseAdapter.addValueEventListener(roomValueEventListener);

        return coordinatorLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.base_menu, menu);
        searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Enter Room Name");
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        randomLogo();
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
    public boolean onQueryTextSubmit(String query) {
        if (Room.isNameValid(query)) {
            enterRoom(query);
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

    private void initialToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initialDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
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

    private View findViewById(int id) {
        return coordinatorLayout.findViewById(id);
    }

    private void randomLogo() {
        ImageView logo = (ImageView) findViewById(R.id.base_logo);
        int[] logoID = new int[]{R.drawable.l1, R.drawable.l2, R.drawable.l3, R.drawable.l4, R.drawable.l5, R.drawable.l6};
        Random rand = new Random();
        logo.setImageResource(logoID[rand.nextInt(logoID.length)]);
    }

    private void enterRoom(String name) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.enterRoom(name);
    }

    //=====================================Private Class=====================================
    private class RoomListAdapter extends RecyclerViewAnimateAdapter<Room, RoomListAdapter.RoomViewHolder> {

        public RoomListAdapter(List<Room> list) {
            super(list);
        }

        @Override
        public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_room, parent, false);
            return new RoomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RoomViewHolder holder, int position) {
            Room room = list.get(position);
            holder.name.setText(room.name);
            holder.count.setText(room.questionCount <= 999 ? String.valueOf(room.questionCount) : "999+");
            holder.lock.setVisibility(room.hasPassword() ? View.VISIBLE : View.GONE);
        }

        class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView name;
            private final TextView count;
            private final ImageView lock;

            public RoomViewHolder(View v) {
                super(v);
                name = (TextView) v.findViewById(R.id.card_room_name);
                count = (TextView) v.findViewById(R.id.card_room_count);
                lock = (ImageView) v.findViewById(R.id.card_room_lock);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Room room = list.get(getAdapterPosition());
                enterRoom(room.name);
            }
        }
    }
}
