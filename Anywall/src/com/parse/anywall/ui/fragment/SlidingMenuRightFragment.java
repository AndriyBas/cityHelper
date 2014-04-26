package com.parse.anywall.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.anywall.R;
import com.parse.anywall.model.AnywallPost;
import com.parse.anywall.model.Tag;

/**
 * Created by bamboo on 26.04.14.
 */
public class SlidingMenuRightFragment extends Fragment {

    public final String[] statusList = new String[]{
            "Розглядається",
            "Активна",
            "Завершена",
            "Відхилена"
    };

    public final String[] tagsList = new String[]{
            "#ями_на_дорогах",
            "#реклама_на_стовпах",
            "#відкриті_люки",
            "#сміття",
            "#потрібне_озеленення",
            "#ремонт_дороги",
            "#темні_вулиці",
            "#дитячі_майданчики",
            "#бездомні_собаки",
            "#небезпечна_місцевість",
            "#шумна_територія",
            "#наркоманія",
            "#ями_на_дорогах",
            "#реклама_на_стовпах",
            "#відкриті_люки",
            "#сміття",
            "#потрібне_озеленення",
            "#ремонт_дороги",
            "#темні_вулиці",
            "#дитячі_майданчики",
            "#бездомні_собаки",
            "#небезпечна_місцевість",
            "#шумна_територія",
            "#наркоманія"
    };


    // Adapter for the Parse query
    private ParseQueryAdapter<Tag> tagsAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_right_menu, container, false);

        ListView listViewStatus = (ListView) v.findViewById(R.id.listViewStatus);
        listViewStatus.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                statusList));
        listViewStatus.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ListView listViewTags = (ListView) v.findViewById(R.id.listViewTags);
        listViewTags.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                tagsList));
        listViewTags.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);




        return v;

    }

    private void initTags()  {
        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Tag> factory =
                new ParseQueryAdapter.QueryFactory<Tag>() {
                    public ParseQuery<Tag> create() {

                        ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
                        query.orderByDescending("count");
                        query.setLimit(100);
                        return query;
                    }
                };

        // Set up the query adapter
        tagsAdapter = new ParseQueryAdapter<Tag>(getActivity(), factory) {
            @Override
            public View getItemView(Tag tag, View view, ViewGroup parent) {
//                if (view == null) {
//                    view = View.inflate(getContext(), R.layout.item_task_post, null);
//                }
//                TextView contentView = (TextView) view.findViewById(R.id.contentView);
//                TextView usernameView = (TextView) view.findViewById(R.id.usernameView);
//                contentView.setText(post.getText());
//                usernameView.setText(post.getUser().getUsername());
                return view;
            }
        };

        // Disable automatic loading when the adapter is attached to a view.
        tagsAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        tagsAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
//        ListView postsView = (ListView) this.findViewById(R.id.postsView);
//        postsView.setAdapter(tagsAdapter);
    }
}