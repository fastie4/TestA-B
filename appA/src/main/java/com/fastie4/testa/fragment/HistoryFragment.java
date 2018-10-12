package com.fastie4.testa.fragment;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fastie4.common.db.HistoryContract;
import com.fastie4.testa.R;
import com.fastie4.testa.adapter.HistoryRecyclerViewAdapter;
import com.fastie4.testa.listener.OnLinkListener;
import com.fastie4.testa.pojo.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryFragment extends Fragment {
    private static final String LIST_ITEMS = "list_items";
    private OnLinkListener mListener;
    private List<Item> mItems;
    private HistoryRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            if (savedInstanceState != null) {
                mItems = savedInstanceState.getParcelableArrayList(LIST_ITEMS);
            }
            if (mItems == null) {
                mItems = new ArrayList<>();
                loadItems();
            }
            mAdapter = new HistoryRecyclerViewAdapter(mItems, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_date) {
            sortByDate();
        } else if (item.getItemId() == R.id.action_sort_status) {
            sortByStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortByDate() {
        Collections.sort(mItems, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                return (int) (item2.getTime() - item.getTime());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    private void sortByStatus() {
        Collections.sort(mItems, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                return item.getStatus() - item2.getStatus();
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnLinkListener) context;
        getContext().getContentResolver().registerContentObserver(HistoryContract.HistoryEntry.CONTENT_URI,
                true, mContentObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        getContext().getContentResolver().unregisterContentObserver(mContentObserver);
    }

    private void loadItems() {
        Cursor cursor = getContext().getContentResolver().query(HistoryContract.HistoryEntry.CONTENT_URI,
                null, null, null,
                HistoryContract.HistoryEntry._ID + " DESC");
        mItems.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry._ID)));
                item.setLink(cursor.getString(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_LINK)));
                item.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_STATUS)));
                item.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_TIME)));
                mItems.add(item);
            }
            cursor.close();
        }
    }

    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            loadItems();
            mAdapter.notifyDataSetChanged();
            super.onChange(selfChange);
        }
    };
}