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
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.LessonAdapter;
import inc.osbay.android.tutorroom.adapter.TagAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.model.Lesson;
import inc.osbay.android.tutorroom.sdk.model.Tag;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class LessonFragment extends BackHandledFragment implements LessonAdapter.OnItemClicked {

    SharedPreferenceData sharedPreferences;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.package_rv)
    RecyclerView lessonRV;
    TagAdapter mTagAdapter;
    LessonAdapter lessonAdapter;
    @BindView(R.id.package_spinner)
    Spinner lessonSpinner;
    @BindView(R.id.no_data)
    TextView noData;
    private ServerRequestManager mServerRequestManager;
    private List<Tag> tagList = new ArrayList<>();
    private List<Lesson> lessonList = new ArrayList<>();
    private String tagID;

    @Override
    public boolean onBackPressed() {
        getActivity().finish();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = new SharedPreferenceData(getActivity());
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        lessonSpinner.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.lesson));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        if (tagList.size() == 0) {
            progressDialog.show();
        }

        mServerRequestManager.getTagList(String.valueOf(CommonConstant.LessonTag), new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                if (getActivity() != null) {
                    if (result.getCode() == 1) {
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
                        lessonSpinner.setAdapter(mTagAdapter);
                    /*packageSpinner.setDropDownListItem(packageList);
                    packageSpinner.setOnSelectionListener(new OnDropDownSelectionListener() {
                        @Override
                        public void onItemSelected(DropDownView view, int position) {
                            //Do something with the selected position
                        }
                    });*/
                        lessonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                mServerRequestManager.getLessonByTagID(tagList.get(pos).getTagID(), new ServerRequestManager.OnRequestFinishedListener() {
                                    @Override
                                    public void onSuccess(ServerResponse result) {
                                        progressDialog.dismiss();
                                        if (result.getCode() == 1) //For Success Situation
                                        {
                                            tagID = tagList.get(pos).getTagID();
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
                                            setLessonList();
                                        } else //For No Data Situation
                                        {
                                            lessonRV.setVisibility(View.GONE);
                                            noData.setVisibility(View.VISIBLE);
                                            noData.setText(getString(R.string.no_lesson));
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
                                Toast.makeText(getActivity(), getString(R.string.lesson_lst_refresh_failed), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
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

    public void setLessonList() {
        lessonRV.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        lessonAdapter = new LessonAdapter(lessonList, getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lessonRV.setLayoutManager(mLayoutManager);
        lessonRV.setItemAnimator(new DefaultItemAnimator());
        lessonRV.setAdapter(lessonAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onItemClick(Lesson lesson) {
        FragmentManager fm = getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new LessonDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("lesson", lesson);
        bundle.putString("tag_id", tagID);
        bundle.putString("lesson_type", String.valueOf(CommonConstant.singleLessonType));
        fragment.setArguments(bundle);
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
    }
}