package com.parse.anywall.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parse.anywall.R;

/**
 * Created by badgateway on 26.04.14.
 */
public class IssueFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_issue, container, false);

        return v;
    }
}
