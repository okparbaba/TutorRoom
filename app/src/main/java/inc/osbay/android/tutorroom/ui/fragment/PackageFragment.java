package inc.osbay.android.tutorroom.ui.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.PackageByAllTagAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.model.Tag;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class PackageFragment extends BackHandledFragment {

    SharedPreferenceData sharedPreferences;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.package_rv)
    RecyclerView tagRV;
    @BindView(R.id.no_data)
    TextView noDataTV;
    List<Tag> tagList = new ArrayList<>();
    private ServerRequestManager mServerRequestManager;

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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.packagee));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        mServerRequestManager.getPackageListByAllTag(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                progressDialog.dismiss();
                if (response.getCode() == 1) {
                    tagList.clear();
                    JSONArray tagJsonArray;
                    try {
                        tagJsonArray = new JSONArray(response.getDataSt());
                        for (int i = 0; i < tagJsonArray.length(); i++) {
                            Tag tag = new Tag(tagJsonArray.getJSONObject(i));
                            tagList.add(tag);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PackageByAllTagAdapter packageAdapter = new PackageByAllTagAdapter(tagList, getActivity());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    tagRV.setLayoutManager(mLayoutManager);
                    tagRV.setItemAnimator(new DefaultItemAnimator());
                    tagRV.setAdapter(packageAdapter);
                    packageAdapter.notifyDataSetChanged();
                } else {
                    tagRV.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.VISIBLE);
                    noDataTV.setText(getString(R.string.no_package));
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
