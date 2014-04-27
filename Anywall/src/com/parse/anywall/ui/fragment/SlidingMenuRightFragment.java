package com.parse.anywall.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.*;
import com.parse.anywall.R;
import com.parse.anywall.model.Tag;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;

/**
 * Created by bamboo on 26.04.14.
 */
public class SlidingMenuRightFragment extends Fragment {

    public final String[] statusList = new String[]{
            "Активна",
            "Розглядається",
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
    public static ParseQueryAdapter<Tag> tagsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_right_menu, container, false);

        ListView listViewStatus = (ListView) v.findViewById(R.id.listViewStatus);
        listViewStatus.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                statusList));
        listViewStatus.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

//        ListView listViewTags = (ListView) v.findViewById(R.id.listViewTags);
//        listViewTags.setAdapter(new ArrayAdapter<String>(
//                getActivity(),
//                android.R.layout.simple_list_item_multiple_choice,
//                tagsList));
//        listViewTags.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        initTags(v);

        return v;

    }

    private void initTags(View v) {
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
                if (view == null) {
                    view = View.inflate(getContext(), android.R.layout.simple_list_item_multiple_choice, null);
                }

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                TextView usernameView = (TextView) view.findViewById(R.id.usernameView);
                text1.setText(tag.getName());
//                usernameView.setText(post.getUser().getUsername());
                return view;
            }
        };

        // Disable automatic loading when the adapter is attached to a view.
        tagsAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        tagsAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView tagsView = (ListView) v.findViewById(R.id.listViewTags);
        tagsView.setAdapter(tagsAdapter);
        tagsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        tagsAdapter.loadObjects();
    }

    public static void reloadTags() {
        tagsAdapter.loadObjects();
    }

    public static void updateTags(JSONArray newTags) {

        HashSet<String> set = new HashSet<String>();

        try {
            for (int i = 0; i < newTags.length(); i++) {
                set.add(newTags.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < tagsAdapter.getCount(); i++) {
            Tag t = tagsAdapter.getItem(i);
            if (set.contains(t.getName())) {
                t.incCount();
                set.remove(t.getName());
                t.saveInBackground();
            }
        }

        for (String s : set) {
            Tag t = new Tag();
            t.setName(s);
            t.setCount(0);
            t.saveInBackground();
        }

        final Tag t = new Tag();
        t.setName("helper");
        t.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                t.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        SlidingMenuRightFragment.reloadTags();
                    }
                });
            }
        });


    }
}