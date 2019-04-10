package inc.osbay.android.tutorroom.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.ParseException;
import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Notification;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;

public class NotificationDetailFragment extends BackHandledFragment {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.send_date)
    TextView sendDateTV;
    @BindView(R.id.desc)
    TextView desc;
    private DBAdapter dbAdapter;
    private String notiID;
    private Notification mNotification;
    private ServerRequestManager requestManager;

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notiID = getArguments().getString("noti_id");
        requestManager = new ServerRequestManager(getActivity());
        dbAdapter = new DBAdapter(getActivity());
        mNotification = dbAdapter.getNotiById(notiID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_detail, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        title.setText(mNotification.getTitle());
        desc.setText(mNotification.getContent());
        try {
            sendDateTV.setText(LGCUtil.convertToLocale(mNotification.getSendDate(), CommonConstant.DATE_TIME_FORMAT));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestManager.readNoti(notiID, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                if (response.getCode() == 1) {
                    dbAdapter.setNotiRead(notiID);
                }
            }

            @Override
            public void onError(ServerError err) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.notification));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
