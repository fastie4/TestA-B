package com.fastie4.testa.adapter;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastie4.common.Common;
import com.fastie4.common.LinkStatus;
import com.fastie4.testa.R;
import com.fastie4.testa.listener.OnLinkListener;
import com.fastie4.testa.pojo.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {
    private List<Item> mValues;
    private final OnLinkListener mListener;
    private final DateFormat mFormat;

    public HistoryRecyclerViewAdapter(List<Item> items, OnLinkListener listener) {
        mValues = items;
        mListener = listener;
        mFormat = SimpleDateFormat.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Item item = mValues.get(position);
        holder.mLink.setText(item.getLink());
        holder.mTime.setText(mFormat.format(new Date(item.getTime())));
        int resId = getColor(item.getStatus());
        holder.mView.setBackgroundColor(holder.mView.getResources().getColor(resId));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.openLink(Common.ACTION_OPEN_FROM_HISTORY, item.getLink(),
                            item.getId(), item.getStatus());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @ColorRes
    private int getColor(int status) {
        if (status == LinkStatus.LOADED) {
            return R.color.loaded;
        }
        if (status == LinkStatus.ERROR) {
            return R.color.error;
        }
        return R.color.unknown;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mLink;
        final TextView mTime;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mLink = view.findViewById(R.id.item_link);
            mTime = view.findViewById(R.id.item_time);
        }
    }
}
