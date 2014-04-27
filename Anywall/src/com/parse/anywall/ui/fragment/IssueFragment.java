package com.parse.anywall.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.anywall.R;
import com.parse.anywall.model.Issue;
import com.parse.anywall.ui.activity.MainActivity;

/**
 * Created by badgateway on 26.04.14.
 */
public class IssueFragment extends Fragment {

    private Issue mIssue;

    private ImageView imageViewPhoto;
    private ImageButton imageButtonTakePhoto;
    private TextView textViewAuthor;
    private EditText editTextTitle;
    private EditText editTextTag;

    private Button mButtonAddTag;


    private TextView mTextViewParticipants;
    private TextView mTextViewDonation;

    private EditText mEditTextDetails;

    private Button mButtonDate;
    private Button mButtonDonate;
    private Button mButtonIWillBeThere;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mIssue = MainActivity.CURRENT_ISSUE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_issue, container, false);

        init(v);

        return v;
    }

    private void init(View v) {
        imageViewPhoto = (ImageView) v.findViewById(R.id.issue_imageView);
        imageButtonTakePhoto = (ImageButton) v.findViewById(R.id.issue_imageButton);
        textViewAuthor = (TextView) v.findViewById(R.id.issue_author);
        editTextTitle = (EditText) v.findViewById(R.id.issue_title_editText);

        editTextTag = (EditText) v.findViewById(R.id.issue_editText_tag);
        mButtonAddTag = (Button) v.findViewById(R.id.issue_btn_add_tag);

        mTextViewParticipants = (TextView) v.findViewById(R.id.issue_participants_text);
        mTextViewDonation = (TextView) v.findViewById(R.id.issue_donation_text);

        mEditTextDetails = (EditText) v.findViewById(R.id.issue_details_textView);

        mButtonDate = (Button) v.findViewById(R.id.issue_btn_date);
        mButtonDate = (Button) v.findViewById(R.id.issue_btn_donate);
        mButtonIWillBeThere = (Button) v.findViewById(R.id.i_will_be_there_button);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_issue, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_save_issue:

                collectDataAndSaveIssue();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void collectDataAndSaveIssue() {
        if (mIssue.getAuthor() == null) {
            ParseACL acl = new ParseACL();
            // Give public read access
            acl.setPublicReadAccess(true);
            acl.setWriteAccess(ParseUser.getCurrentUser(), true);
            mIssue.setACL(acl);

            mIssue.setAuthor(ParseUser.getCurrentUser());
            mIssue.setDonation(0);
            mIssue.setParticipants(0);
        }

        mIssue.setTitle(editTextTitle.getText().toString());
        mIssue.setParticipants(Integer.parseInt(mTextViewParticipants.getText().toString()));

        mIssue.setDonation(Integer.parseInt(mTextViewDonation.getText().toString()));
        mIssue.setDetail(mEditTextDetails.getText().toString());

        mIssue.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Saved OK", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }
}