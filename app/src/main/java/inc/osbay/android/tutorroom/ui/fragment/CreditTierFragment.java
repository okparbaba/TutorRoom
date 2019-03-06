package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.CreditTierAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.model.CreditPackage;
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class CreditTierFragment extends BackHandledFragment implements CreditTierAdapter.OnItemClicked{
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.credit_rv)
    RecyclerView creditPackageRV;
    @BindView(R.id.main_rl)
    RelativeLayout mainRL;
    @BindView(R.id.no_data)
    TextView noDataTV;
    private ServerRequestManager mRequestManager;
    private String accountId;
    private List<CreditPackage> creditPackageList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        mRequestManager = new ServerRequestManager(getActivity());
        accountId = String.valueOf(sharedPreferenceData.getInt("account_id"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit_tier, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        mRequestManager.getPriceTierList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                progressDialog.dismiss();

                if (response.getCode() == 1) {
                    creditPackageList.clear();
                    mainRL.setVisibility(View.VISIBLE);
                    noDataTV.setVisibility(View.GONE);
                    JSONArray tagJsonArray;
                    try {
                        tagJsonArray = new JSONArray(response.getDataSt());
                        for (int i = 0; i < tagJsonArray.length(); i++) {
                            CreditPackage creditPackage = new CreditPackage(tagJsonArray.getJSONObject(i));
                            creditPackageList.add(creditPackage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showData();
                } else {
                    mainRL.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();

            }
        });
    }

    void showData() {
        CreditTierAdapter creditPackageAdapter = new CreditTierAdapter(creditPackageList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        creditPackageRV.setLayoutManager(mLayoutManager);
        creditPackageRV.setItemAnimator(new DefaultItemAnimator());
        creditPackageRV.setAdapter(creditPackageAdapter);
        creditPackageAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.store));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onItemClick(CreditPackage creditPackage) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.buy_credit_package_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView closeImg = dialog.findViewById(R.id.close_img);
        TextView creditContent = dialog.findViewById(R.id.credit_content);
        TextView creditTitle = dialog.findViewById(R.id.credit_title);
        TextView buy = dialog.findViewById(R.id.buy);
        creditContent.setText(String.valueOf(creditPackage.getPackageAmount()));
        creditTitle.setText(String.valueOf(creditPackage.getPackageCredit()));

        closeImg.setOnClickListener(view ->
                dialog.dismiss()
        );

        buy.setOnClickListener(view -> {
            dialog.dismiss();
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            mRequestManager.buyCredit(accountId, CommonConstant.buyStorePackage, null, creditPackage.getCreditID(),
                    new ServerRequestManager.OnRequestFinishedListener() {
                        @Override
                        public void onSuccess(ServerResponse response) {
                            if (response.getCode() == 1) {
                                progressDialog.dismiss();
                                String url = response.getDataSt();
                                Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
                                intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, WebviewFragment.class.getSimpleName());
                                intent.putExtra(WebviewFragment.WEBVIEW_EXTRA, url);
                                intent.putExtra(WebviewFragment.TITLE_EXTRA, getString(R.string.store));
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onError(ServerError err) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), getResources().getString(R.string.faq_8), Toast.LENGTH_LONG).show();
                        }
                    });
        });
        dialog.show();
    }
}
