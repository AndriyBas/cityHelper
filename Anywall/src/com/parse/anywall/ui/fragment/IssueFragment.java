package com.parse.anywall.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import com.parse.*;
import com.parse.anywall.Const;
import com.parse.anywall.Logger;
import com.parse.anywall.R;
import com.parse.anywall.model.Comment;
import com.parse.anywall.model.ImageProcessor;
import com.parse.anywall.model.Issue;
import com.parse.anywall.ui.activity.MainActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class IssueFragment extends Fragment implements View.OnClickListener {

    private boolean hide = false;
    private boolean willGo = false;

    private ListView commentLv;
    private TextView detLabel;
    private EditText commentInput;

    private Button addComment;

    private Issue mIssue;

    private ImageView imageViewPhoto;
    private ImageButton imageButtonTakePhoto;
    private TextView textViewAuthor;
    private EditText editTextTitle;
    private EditText editTextTag;

    private Button mButtonAddTag;

    private Activity activity;
    private Fragment fragment;

    private TextView mTextViewParticipants;
    private TextView mTextViewDonation;

    private EditText mEditTextDetails;

    private Button mButtonDate;
    private Button mButtonDonate;
    private Button mButtonIWillBeThere;

    private LinearLayout tagsContainer;
    private Bitmap issuePhoto;

    private String[] statuses = {Const.STATUS_ACTIVE, Const.STATUS_CONSIDERING, Const.STATUS_FINISHED, Const.STATUS_REJECTED};

    ParseQueryAdapter<Comment> mCommentAdapter;

    private Spinner statusSpinner;

    int Year;
    int month;
    int day;

    MenuItem saveItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);


        mIssue = MainActivity.CURRENT_ISSUE;


    }

    private void populateFragment() {

        textViewAuthor.setText("АВТОР : " + mIssue.getAuthor().getUsername());
        editTextTitle.setText(mIssue.getTitle());

        mTextViewParticipants.setText(Integer.toString(mIssue.getParticipants()));
        mTextViewDonation.setText(Integer.toString(mIssue.getDonation()));

        mEditTextDetails.setText(mIssue.getDetail());


        statusSpinner.setSelection(3);
        String status = mIssue.getStatus();
        if (status != null) {
            for (int i = 0; i < statuses.length; i++) {
                if (status.equals(statuses[i])) {
                    statusSpinner.setSelection(i);
                    break;
                }
            }
        }


        tagsContainer.removeAllViews();

        try {
            JSONArray tags = mIssue.getTags();
            for (int i = 0; i < tags.length(); i++) {
//            tags.get(i).toString()
                TextView txt = new TextView(activity);

                txt.setText(tags.get(i).toString());

                txt.setTextSize(20);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 5;
                tagsContainer.addView(txt, params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {


        ParseFile f = mIssue.getPhoto();

        if (f != null) {
            Logger.e(f.getUrl());
            Picasso.with(activity)
                    .load(Uri.parse(f.getUrl()))
                    .resize(300, 300)
                    .centerCrop()
                    .placeholder(R.drawable.stub)
                    .into(imageViewPhoto);
        }
            /*if (f != null) {

                byte[] data = f.getData();

                f.getUrl()

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                imageViewPhoto.setImageBitmap(bitmap);
            }else {
                Logger.e("file == null");
            }*/

//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        if (!ParseUser.getCurrentUser().getObjectId().equals(mIssue.getAuthor().getObjectId())) {

            statusSpinner.setEnabled(false);
            editTextTitle.setEnabled(false);
            editTextTag.setEnabled(false);
            mButtonAddTag.setEnabled(false);
            mEditTextDetails.setEnabled(false);
            imageButtonTakePhoto.setEnabled(false);
            mButtonDate.setEnabled(false);
            saveItem.setEnabled(false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_issue, container, false);
        init(v);
        setupViews();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            populateFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViews() {
        detLabel.setOnClickListener(this);
        addComment.setOnClickListener(this);
        imageButtonTakePhoto.setOnClickListener(this);
        mButtonAddTag.setOnClickListener(this);
        mButtonDonate.setOnClickListener(this);
        mButtonIWillBeThere.setOnClickListener(this);


        ParseQueryAdapter.QueryFactory<Comment> factory =
                new ParseQueryAdapter.QueryFactory<Comment>() {
                    public ParseQuery<Comment> create() {

                        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

                        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                        query.include("author");
                        query.whereEqualTo("issue", mIssue);
                        query.orderByAscending("createdAt");

                        query.setLimit(100);
                        return query;
                    }
                };

        // Set up the query adapter
        mCommentAdapter = new ParseQueryAdapter<Comment>(getActivity(), factory) {
            @Override
            public View getItemView(Comment c, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.comment_item, null);
                }
                TextView tt = (TextView) view.findViewById(R.id.tvCommTxt);
                TextView tt1 = (TextView) view.findViewById(R.id.tvCommAuthor);

                tt.setText(c.getText());
                tt1.setText(c.getAuthor().getUsername());
                return view;
            }
        };

        // Disable automatic loading when the adapter is attached to a view.
        mCommentAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        mCommentAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        commentLv.setAdapter(mCommentAdapter);

        mCommentAdapter.loadObjects();


        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(activity,
                android.R.layout.simple_spinner_dropdown_item,
                statuses);
        statusSpinner.setAdapter(spinnerArrayAdapter);

        statusSpinner.setSelection(3);
        addComment.setOnClickListener(this);
        imageButtonTakePhoto.setOnClickListener(this);
        mButtonAddTag.setOnClickListener(this);
        mButtonDate.setOnClickListener(this);
        mButtonDonate.setOnClickListener(this);
        mButtonIWillBeThere.setOnClickListener(this);
    }

    private void init(View v) {
        activity = getActivity();
        fragment = this;
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
        mButtonDonate = (Button) v.findViewById(R.id.issue_btn_donate);
        mButtonIWillBeThere = (Button) v.findViewById(R.id.i_will_be_there_button);

        mButtonAddTag = (Button) v.findViewById(R.id.issue_btn_add_tag);

        detLabel = (TextView) v.findViewById(R.id.details_label);
        commentLv = (ListView) v.findViewById(R.id.lvComments);

        addComment = (Button) v.findViewById(R.id.addCommentButton);
        commentInput = (EditText) v.findViewById(R.id.commentInput);

        tagsContainer = (LinearLayout) v.findViewById(R.id.tagsContainer);

        mTextViewDonation = (TextView) v.findViewById(R.id.issue_donation_text);
        mTextViewParticipants = (TextView) v.findViewById(R.id.issue_participants_text);

        statusSpinner = (Spinner) v.findViewById(R.id.statuSpinner);

        saveItem = (MenuItem) v.findViewById(R.id.menu_item_save_issue);
    }


    @Override
    public void onResume() {
        super.onResume();
        mCommentAdapter.loadObjects();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.addCommentButton): {
                if (commentInput.getText() != null && commentInput.getText().toString().length() > 0) {

                    Comment c = new Comment();
                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    c.setACL(acl);

                    c.setText(commentInput.getText().toString());
                    c.setAuthor(ParseUser.getCurrentUser());
                    c.setIssue(mIssue);

                    c.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            doCommentQuery();
                        }
                    });

                    commentInput.setText("");

                } else {
                    Toast.makeText(getActivity(), "Please, enter your comment first", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            case (R.id.issue_imageButton): {
                ImageProcessor.showImagePickerDialog(fragment, false);
                break;
            }
            case (R.id.issue_btn_add_tag): {
                if (editTextTag.getText() != null && editTextTag.getText().toString().length() > 0) {
                    TextView txt = new TextView((Context) activity);
                    txt.setText("#" + editTextTag.getText().toString());
                    txt.setTextSize(20);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 5;
                    tagsContainer.addView(txt, params);
                    editTextTag.setText("");
                }
                break;
            }
            case (R.id.issue_btn_donate): {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Enter sum you want to donate:");

                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
//                input.setWidth(500);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() > 0) {
                            int prevSum = Integer.parseInt(mTextViewDonation.getText().toString());
                            int donateSum = Integer.parseInt(input.getText().toString());
                            mTextViewDonation.setText("" + (prevSum + donateSum));
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            }
            case (R.id.i_will_be_there_button): {
                if (willGo) {
                    int prevCount = Integer.parseInt(mTextViewParticipants.getText().toString());
                    if (prevCount != 0) {
                        mTextViewParticipants.setText("" + (prevCount - 1));
                        willGo = false;
                        mButtonIWillBeThere.setText("Я ПРИЙДУ");
                    } else {
                        Logger.e("prev count = 0");
                    }
                } else {
                    int prevCount = Integer.parseInt(mTextViewParticipants.getText().toString());
                    mTextViewParticipants.setText("" + (prevCount + 1));
                    willGo = true;
                    mButtonIWillBeThere.setText("СКАСУВАТИ");
                }
                break;
            }
            case (R.id.issue_btn_date): {

                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Year = year;
                        month = monthOfYear;
                        day = dayOfMonth;
                        updateDisplay();
                    }
                };
                Calendar cal = Calendar.getInstance();
                Year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog d = new DatePickerDialog(activity, mDateSetListener, Year, month, day);
                d.show();
                break;

            }
        }
    }

    private void updateDisplay() {
        GregorianCalendar c = new GregorianCalendar(Year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        mButtonDate.setText(sdf.format(c.getTime()));
    }

    private void doCommentQuery() {
        mCommentAdapter.loadObjects();
    }

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
       /* if (mIssue.getAuthor() == null) {
            ParseACL acl = new ParseACL();
            // Give public read access
            acl.setPublicReadAccess(true);
            acl.setWriteAccess(ParseUser.getCurrentUser(), true);
            mIssue.setACL(acl);

            mIssue.setAuthor(ParseUser.getCurrentUser());
            mIssue.setDonation(0);
            mIssue.setParticipants(0);
        }*/

        Logger.d(mIssue.getObjectId());

        mIssue.setTitle(editTextTitle.getText().toString());
        mIssue.setParticipants(Integer.parseInt(mTextViewParticipants.getText().toString()));

        mIssue.setDonation(Integer.parseInt(mTextViewDonation.getText().toString()));
        mIssue.setDetail(mEditTextDetails.getText().toString());

        mIssue.setStatus(statusSpinner.getSelectedItem().toString());

        Logger.d(statusSpinner.getSelectedItem().toString());

        JSONArray tags = new JSONArray();
        for (int i = 0; i < tagsContainer.getChildCount(); i++) {

            TextView t = (TextView) tagsContainer.getChildAt(i);
            tags.put(t.getText().toString());
        }

        SlidingMenuRightFragment.updateTags(tags);

        mIssue.setTags(tags);
        if (issuePhoto != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            issuePhoto.compress(Bitmap.CompressFormat.JPEG, 35, stream);
            byte[] data = stream.toByteArray();
            ParseFile file = new ParseFile(System.currentTimeMillis() + ".jpeg", data);
            mIssue.setPhoto(file);
        } else {
            Logger.e("need add photo");
        }


        mIssue.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Saved OK", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("onActivityResult from fr");
        if (resultCode == activity.RESULT_OK) {
            if (requestCode == Const.FROM_CAMERA) {
                Logger.d("requestCode == FROM_CAMERA");
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    issuePhoto = bitmap;
                    imageViewPhoto.setImageBitmap(bitmap);
                } else {
                    Logger.d("from camera data == null");
                }
            }
            if (requestCode == Const.FROM_GALLERY) {
                Logger.d("requestCode == FROM_GALLERY");
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(((Context) activity).getContentResolver().openInputStream(data.getData()));
                    issuePhoto = bitmap;
                    imageViewPhoto.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Logger.e("IssueFragment:  Error while FROM_GALLERY");
                }
            }

        }
    }


}