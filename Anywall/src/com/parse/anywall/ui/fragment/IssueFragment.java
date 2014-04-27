package com.parse.anywall.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.parse.ParseUser;
import com.parse.anywall.Const;
import com.parse.anywall.Logger;
import com.parse.anywall.R;
import com.parse.anywall.model.Comment;
import com.parse.anywall.model.ImageProcessor;
import com.parse.anywall.model.Issue;
import com.parse.anywall.ui.adapters.CommentAdapter;

import java.util.ArrayList;

public class IssueFragment extends Fragment implements View.OnClickListener {

    private boolean hide = false;
    private boolean willGo = false;
    private ListView commentLv;
    private TextView detLabel;
    private EditText commentInput;

    private Button addComment;
    private ArrayList<Comment> comments;
    private CommentAdapter adapter;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_issue, container, false);
        init(v);
        setupViews();
        return v;
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
        comments = new ArrayList<Comment>();
        comments.add(new Comment("blabla", ParseUser.getCurrentUser().getUsername()));
        comments.add(new Comment("blabla2", ParseUser.getCurrentUser().getUsername()));
        comments.add(new Comment("blabla3", ParseUser.getCurrentUser().getUsername()));
        adapter = new CommentAdapter(v.getContext(), 0, comments);
        commentLv.setAdapter(adapter);
        addComment = (Button) v.findViewById(R.id.addCommentButton);
        commentInput = (EditText) v.findViewById(R.id.commentInput);

        tagsContainer = (LinearLayout) v.findViewById(R.id.tagsContainer);

        mTextViewDonation = (TextView) v.findViewById(R.id.issue_donation_text);
        mTextViewParticipants = (TextView) v.findViewById(R.id.issue_participants_text);

    }

    private void setupViews() {
        detLabel.setOnClickListener(this);
        addComment.setOnClickListener(this);
        imageButtonTakePhoto.setOnClickListener(this);
        mButtonAddTag.setOnClickListener(this);
        mButtonDonate.setOnClickListener(this);
        mButtonIWillBeThere.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.details_label): {
                if (hide == false) {
                    mButtonDonate.setVisibility(View.GONE);
                    mButtonDate.setVisibility(View.GONE);
                    mButtonIWillBeThere.setVisibility(View.GONE);
                    hide = true;
                } else {
                    mButtonDonate.setVisibility(View.VISIBLE);
                    mButtonDonate.setVisibility(View.VISIBLE);
                    mButtonDonate.setVisibility(View.VISIBLE);
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
                        mButtonIWillBeThere.setText("I WILL BE THERE");
                    } else {
                        Logger.e("prev count = 0");
                    }
                } else {
                    int prevCount = Integer.parseInt(mTextViewParticipants.getText().toString());
                    mTextViewParticipants.setText("" + (prevCount + 1));
                    willGo = true;
                    mButtonIWillBeThere.setText("CANCEL");
                }
                break;
            }
        }
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
        if (mIssue == null) {
            mIssue = new Issue();
        }
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
                    Bitmap bitMap = (Bitmap) extras.get("data");
                    imageViewPhoto.setImageBitmap(bitMap);
                } else {
                    Logger.d("from camera data == null");
                }
            }
            if (requestCode == Const.FROM_GALLERY) {
                Logger.d("requestCode == FROM_GALLERY");
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(((Context) activity).getContentResolver().openInputStream(data.getData()));
                    imageViewPhoto.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Logger.e("IssueFragment:  Error while FROM_GALLERY");
                }
            }

        }
    }
}