package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.MainCreditPackageAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.model.CreditPackage;
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class StoreFragment extends BackHandledFragment implements MainCreditPackageAdapter.OnItemClicked {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    SharedPreferenceData sharedPreferences;
    @BindView(R.id.credit_ammount)
    TextView creditTV;
    @BindView(R.id.main_credit_package)
    RecyclerView creditPackageRV;
//    @BindView(R.id.sb_select_credit)
//    SeekBar creditBar;
//    @BindView(R.id.tv_selected_credit)
//    TextView buyingCreditTV;
//    @BindView(R.id.tv_total_cost)
//    TextView costTV;
//    @BindView(R.id.buy_dynamic)
//    TextView buyDynamicTV;
    private ServerRequestManager mServerRequestManager;
    private String accountId;
    private List<CreditPackage> creditPackageList = new ArrayList<>();
    private double creditAmount;
    private int minAmount = 1;
    private CreditPackage creditPackage;

    @Override
    public boolean onBackPressed() {
        getActivity().finish();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = new SharedPreferenceData(getActivity());
        accountId = String.valueOf(sharedPreferences.getInt("account_id"));
        creditAmount = sharedPreferences.getDouble("credit_amount");
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        creditBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                /*if (progress < minAmount) {
//                    progress = minAmount;
//                    seekBar.setProgress(progress);
//                }*/
//                seekBar.setProgress(i);
//                buyingCreditTV.setText(String.valueOf(i));
//                double amount = i * creditAmount;
//                costTV.setText(String.valueOf(amount));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        buyingCreditTV.setText(getString(R.string._1));
//        costTV.setText(String.valueOf(1 * creditAmount));
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.store));
        setDisplayHomeAsUpEnable(true);
    }

//    @OnClick(R.id.buy_dynamic)
//    void buyDynamic() {
//        buyCredit(CommonConstant.buyCredit, buyingCreditTV.getText().toString()
//                , null);
//    }
    @OnClick(R.id.contact)
    void goContactFra(){
        Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, ContactFragment.class.getSimpleName());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        mServerRequestManager.getAccountCredit(accountId, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                try {
                    JSONObject dataObj = new JSONObject(response.getDataSt());
                    String creditSt = dataObj.getString("credit");
                    creditTV.setText(creditSt);

                    mServerRequestManager.getPriceTier(new ServerRequestManager.OnRequestFinishedListener() {
                        @Override
                        public void onSuccess(ServerResponse result) {
                            progressDialog.dismiss();
                            if (result.getCode() == 1) {
                                creditPackageList.clear();
                                creditPackageRV.setVisibility(View.VISIBLE);
                                JSONArray tagJsonArray;
                                try {
                                    tagJsonArray = new JSONArray(result.getDataSt());
                                    for (int i = 0; i < tagJsonArray.length(); i++) {
                                        CreditPackage creditPackage = new CreditPackage(tagJsonArray.getJSONObject(i));
                                        creditPackageList.add(creditPackage);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                showData();
                            } else {
                                creditPackageRV.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(ServerError err) {
                            progressDialog.dismiss();
                            Log.i("Get Price Tier Failed", err.getMessage());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
            }
        });
    }

    void showData() {
        MainCreditPackageAdapter creditPackageAdapter = new MainCreditPackageAdapter(creditPackageList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        creditPackageRV.setLayoutManager(mLayoutManager);
        creditPackageRV.setItemAnimator(new DefaultItemAnimator());
        creditPackageRV.setAdapter(creditPackageAdapter);
        creditPackageAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.price_tier)
    void openCreditTier() {
        FragmentManager fm = getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new CreditTierFragment();
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

    public void buyCredit(int buyType, String credit, String packageID) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        mServerRequestManager.buyCredit(accountId, buyType, credit, packageID,
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onItemClick(final CreditPackage creditPackage) {
        this.creditPackage = creditPackage;
        final Dialog dialog = new Dialog(getActivity());
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

        closeImg.setOnClickListener(view -> dialog.dismiss());

        buy.setOnClickListener(view -> {
            dialog.dismiss();
            buyCredit(CommonConstant.buyStorePackage, null, creditPackage.getCreditID());
        });
        dialog.show();
    }
}