package com.parse.anywall.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.ParseUser;
import com.parse.anywall.Logger;
import com.parse.anywall.R;
import com.parse.anywall.model.Comment;
import com.parse.anywall.ui.adapters.CommentAdapter;

import java.util.ArrayList;

public class IssueFragment extends Fragment implements View.OnClickListener {

    private boolean hide = false;
    private ListView commentLv;
    private TextView detLabel;
    private Button donateButton;
    private Button dateButton;
    private Button takePartButton;
    private EditText commentInput;

    private Button addComment;
    private ArrayList<Comment> comments;
    private CommentAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_issue, container, false);
        initView(v);
        setupViews(v);
        return v;
    }

    private void initView(View v) {
        detLabel = (TextView) v.findViewById(R.id.details_label);
        donateButton = (Button) v.findViewById(R.id.issue_donate_button);
        dateButton = (Button) v.findViewById(R.id.issue_date);
        takePartButton = (Button) v.findViewById(R.id.i_will_be_there_button);
        commentLv = (ListView) v.findViewById(R.id.lvComments);
        comments = new ArrayList<Comment>();
        comments.add(new Comment("blabla", ParseUser.getCurrentUser().getUsername()));
        comments.add(new Comment("blabla2", ParseUser.getCurrentUser().getUsername()));
        comments.add(new Comment("blabla3", ParseUser.getCurrentUser().getUsername()));
        adapter = new CommentAdapter(v.getContext(), 0, comments);
        commentLv.setAdapter(adapter);
        addComment = (Button) v.findViewById(R.id.addCommentButton);
        commentInput = (EditText) v.findViewById(R.id.commentInput);
    }

    private void setupViews(View v) {
        detLabel.setOnClickListener(this);
        addComment.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.details_label): {
                if (hide == false) {
                    donateButton.setVisibility(View.GONE);
                    dateButton.setVisibility(View.GONE);
                    takePartButton.setVisibility(View.GONE);
                    hide = true;
                } else {
                    donateButton.setVisibility(View.VISIBLE);
                    dateButton.setVisibility(View.VISIBLE);
                    takePartButton.setVisibility(View.VISIBLE);
                    hide = false;
                }
                break;
            }
            case (R.id.addCommentButton): {
                if (commentInput.getText() != null && commentInput.getText().toString().length() > 0) {
                    comments.add(new Comment(commentInput.getText().toString(), ParseUser.getCurrentUser().getUsername()));
                    adapter = new CommentAdapter(v.getContext(), 0, comments);
                    commentLv.setAdapter(adapter);
                    commentInput.setText("");
                    Logger.d("comment added");
//                    adapter.setData();
//                    adapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }
}
