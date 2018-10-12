package com.fastie4.testa.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastie4.common.Common;
import com.fastie4.testa.R;
import com.fastie4.testa.listener.OnLinkListener;

public class TestFragment extends Fragment implements View.OnClickListener {
    private OnLinkListener mListener;
    private TextView mTextLink;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        mTextLink = view.findViewById(R.id.link);
        view.findViewById(R.id.ok).setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnLinkListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (null != mListener) {
            String text = mTextLink.getText().toString();
            if (!text.isEmpty()) {
                mListener.openLink(Common.ACTION_OPEN_FROM_TEST, text, -1, -1);
            }
        }
    }
}