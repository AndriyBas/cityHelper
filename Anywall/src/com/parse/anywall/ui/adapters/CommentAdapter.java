package com.parse.anywall.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.parse.anywall.R;
import com.parse.anywall.model.Comment;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {

    ArrayList<Comment> comments;

    public CommentAdapter(Context context, int resource, ArrayList<Comment> items) {
        super(context, resource, items);
        comments = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.comment_item, null);
        }

        Comment p = getItem(position);

        if (p != null) {

            TextView tt = (TextView) v.findViewById(R.id.tvCommTxt);
            TextView tt1 = (TextView) v.findViewById(R.id.tvCommAuthor);

            if (tt != null) {
                tt.setText(p.getText());
            }
            if (tt1 != null) {

                tt1.setText(p.getAuthorId());
            }
        }

        return v;
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    public void setData(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
