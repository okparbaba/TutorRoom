package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.LessonBookingAdapter;
import inc.osbay.android.tutorroom.adapter.TagAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Lesson;
import inc.osbay.android.tutorroom.sdk.model.Packagee;
import inc.osbay.android.tutorroom.sdk.model.Tag;

public class SingleBookingChooseLessonFragment extends BackHandledFragment implements LessonBookingAdapter.OnItemClicked {
    public static String Booking_EXTRA = "Booking_EXTRA";
    TagAdapter mTagAdapter;
    LessonBookingAdapter mLessonBookingAdapter;
    @BindView(R.id.package_spinner)
    Spinner tagSpinner;
    @BindView(R.id.lesson_rv)
    RecyclerView lessonRV;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.no_data)
    TextView noDataTV;
    private ServerRequestManager mServerRequestManager;
    private List<Tag> tagList = new ArrayList<>();
    private List<Lesson> lessonList = new ArrayList<>();
    private int lessonID;
    private String tagID;
    private String packageID;
    private String lessonType;
    private DBAdapter dbAdapter;

    @Override
    public boolean onBackPressed() {
        if (getArguments().getString(Booking_EXTRA).equals(LessonDetailFragment.class.getSimpleName()))
            getFragmentManager().popBackStack();
        else
            getActivity().finish();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbAdapter = new DBAdapter(getActivity());
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());
        lessonType = getArguments().getString("lesson_type");
        if (getArguments().getString(Booking_EXTRA).equals(LessonDetailFragment.class.getSimpleName())) {
            tagID = getArguments().getString("tag_id");
            lessonID = Integer.parseInt(getArguments().getString("lesson_id"));
            if (tagID.equalsIgnoreCase("0")) packageID = getArguments().getString("package_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_booking_choose_lesson, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.single_book));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        if (getArguments().getString(Booking_EXTRA).equals(LessonDetailFragment.class.getSimpleName())) {
            if (tagID.equalsIgnoreCase("0")) {
                tagSpinner.setVisibility(View.GONE);
                mServerRequestManager.getLessonListByPackageID(packageID, new ServerRequestManager.OnRequestFinishedListener() {
                    @Override
                    public void onSuccess(ServerResponse response) {
                        lessonList.clear();
                        progressDialog.dismiss();
                        if (response.getCode() == 1) {
                            JSONArray lessonJsonArray;
                            JSONArray packageJsonArray;
                            try {
                                packageJsonArray = new JSONArray(response.getDataSt());
                                for (int i = 0; i < packageJsonArray.length(); i++) {
                                    Packagee packagee = new Packagee(packageJsonArray.getJSONObject(i));
                                    lessonJsonArray = packagee.getLessonJsonArray();
                                    for (int j = 0; j < lessonJsonArray.length(); j++) {
                                        Lesson packageObj = new Lesson(lessonJsonArray.getJSONObject(j));
                                        lessonList.add(packageObj);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dbAdapter.insertLessons(lessonList);
                            setLessonList();
                        } else {
                            noDataTV.setVisibility(View.VISIBLE);
                            noDataTV.setText(getString(R.string.no_lesson));
                            lessonRV.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ServerError err) {
                        progressDialog.dismiss();
                    }
                });
            } else {
                showTagList(progressDialog);
            }
        } else if (getArguments().getString(Booking_EXTRA).equals(ScheduleFragment.class.getSimpleName())) {
            showTagList(progressDialog);
        }
    }

    void showTagList(ProgressDialog progressDialog) {
        mServerRequestManager.getTagList(String.valueOf(CommonConstant.LessonTag), new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                if (getActivity() != null) {
                    tagList.clear();
                    JSONArray tagJsonArray;
                    try {
                        tagJsonArray = new JSONArray(result.getDataSt());
                        for (int i = 0; i < tagJsonArray.length(); i++) {
                            Tag tag = new Tag(tagJsonArray.getJSONObject(i));
                            tagList.add(tag);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mTagAdapter = new TagAdapter(getActivity(), tagList);
                    tagSpinner.setAdapter(mTagAdapter);
                    /*tagSpinner.setDropDownListItem(packageList);
                    tagSpinner.setOnSelectionListener(new OnDropDownSelectionListener() {
                        @Override
                        public void onItemSelected(DropDownView view, int position) {
                            //Do something with the selected position
                        }
                    });*/
                    for (int i = 0; i < tagList.size(); i++) {
                        if (tagList.get(i).getTagID().equals(tagID)) {
                            tagSpinner.setSelection(i);
                            break;
                        }
                    }

                    tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            mServerRequestManager.getLessonByTagID(tagList.get(pos).getTagID(), new ServerRequestManager.OnRequestFinishedListener() {
                                @Override
                                public void onSuccess(ServerResponse result) {
                                    progressDialog.dismiss();
                                    if (result.getCode() == 1) //For Success Situation
                                    {
                                        lessonList.clear();
                                        JSONArray lessonJsonArray;
                                        JSONArray tagJsonArray;
                                        try {
                                            tagJsonArray = new JSONArray(result.getDataSt());
                                            for (int i = 0; i < tagJsonArray.length(); i++) {
                                                Tag tag = new Tag(tagJsonArray.getJSONObject(i));
                                                lessonJsonArray = tag.getLessonArray();
                                                for (int j = 0; j < lessonJsonArray.length(); j++) {
                                                    Lesson packageObj = new Lesson(lessonJsonArray.getJSONObject(j));
                                                    lessonList.add(packageObj);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        dbAdapter.insertLessons(lessonList);
                                        setLessonList();
                                    } else //For No Data Situation
                                    {
                                        lessonRV.setVisibility(View.GONE);
                                        noDataTV.setVisibility(View.VISIBLE);
                                        noDataTV.setText(getString(R.string.no_lesson));
                                    }
                                }

                                @Override
                                public void onError(ServerError err) {
                                    progressDialog.dismiss();
                                    if (getActivity() != null) {
                                        Toast.makeText(getActivity(), getString(R.string.lesson_lst_refresh_failed), Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                        }

                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.tu_lst_refresh_failed), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    void setLessonList() {
        mLessonBookingAdapter = new LessonBookingAdapter(lessonList, lessonID, getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lessonRV.setLayoutManager(mLayoutManager);
        lessonRV.setItemAnimator(new DefaultItemAnimator());
        lessonRV.setAdapter(mLessonBookingAdapter);
        lessonRV.setVisibility(View.VISIBLE);
        noDataTV.setVisibility(View.GONE);
    }

    @OnClick(R.id.next_tv)
    void clickNext() {
        if (lessonID == 0) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.select_lesson), Toast.LENGTH_LONG)
                    .show();
        } else {
            FragmentManager fm = getFragmentManager();
            Fragment frg = fm.findFragmentById(R.id.framelayout);
            Fragment fragment = new SingleBookingChooseDateFragment();

            Bundle bundle = new Bundle();
            bundle.putString("lesson_id", String.valueOf(lessonID));
            bundle.putString("lesson_type", lessonType);
            fragment.setArguments(bundle);
            if (frg == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.framelayout, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onItemClick(String itemID) {
        lessonID = Integer.parseInt(itemID);
    }
}
