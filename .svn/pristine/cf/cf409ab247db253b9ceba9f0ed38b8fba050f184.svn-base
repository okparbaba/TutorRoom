package inc.osbay.android.tutorroom.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.TRApplication;
import inc.osbay.android.tutorroom.adapter.OnlineSupportMessageAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.model.ChatMessage;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;
import inc.osbay.android.tutorroom.service.MessengerService;
import inc.osbay.android.tutorroom.ui.activity.CallSupportActivity;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;
import inc.osbay.android.tutorroom.utils.WSMessageClient;

public class OnlineSupportFragment extends BackHandledFragment {

    public static Context mContext;
    private static OnlineSupportMessageAdapter mOnlineSupportMessageAdapter;
    private static List<ChatMessage> mChatMessages = new ArrayList<>();
    SharedPreferenceData sharedPreferences;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.rv_chat_messages)
    RecyclerView messageRV;
    @BindView(R.id.edt_input_text)
    EditText inputET;
    private ServerRequestManager mServerRequestManager;
    private WSMessageClient mWSMessageClient;
    private String accountId;
    private ProgressDialog mProgressDialog;
    private String mConsultantId;
    private MenuItem mCallMenuItem;
    private Timer mTimer;
    private Messenger mMessenger;

    public static void showError(int errCode) {
        //trigger when Call Center Manager isn't online
        if (errCode == 106) {
            ChatMessage chatMessage = new ChatMessage();
            if (mContext != null)
                chatMessage.setBody(mContext.getString(R.string.os_no_consultants));
            else
                chatMessage.setBody("Sorry, Engleezi consultants are not available to take your " +
                        "call right now. Please leave a written message in the window and we'll " +
                        "get back to you as soon as we can.");
            chatMessage.setSender("C_auto");
            chatMessage.setMessageType("im_online");
            mChatMessages.add(chatMessage);

            mOnlineSupportMessageAdapter.notifyDataSetChanged();
        }
        if (errCode == 107) {
            ChatMessage chatMessage = new ChatMessage();
            if (mContext != null)
                chatMessage.setBody(mContext.getString(R.string.os_call_ended));
            else
                chatMessage.setBody("The call was ended.");

            chatMessage.setSender("C_auto");
            chatMessage.setMessageType("im_online");
            mChatMessages.add(chatMessage);

            mOnlineSupportMessageAdapter.notifyDataSetChanged();
        }
    }

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
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());

        mContext = getActivity();
        mMessenger = new Messenger(new IncomingHandler());
        mWSMessageClient = ((TRApplication) getActivity().getApplication()).getWSMessageClient();
        mWSMessageClient.addMessenger(mMessenger);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_support, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mServerRequestManager.getOnlineCallCentreManager(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                mProgressDialog.dismiss();
                if (result.getCode() == 1) {
                    try {
                        JSONArray dataArray = new JSONArray(result.getDataSt());
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject jsonObject = dataArray.getJSONObject(i);
                            mConsultantId = jsonObject.getString("id");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getMessages();
                }
            }

            @Override
            public void onError(ServerError err) {
                mProgressDialog.dismiss();
                Log.i("Online Support Error", err.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (!classType.equalsIgnoreCase("trial") && !classType.equalsIgnoreCase("demo")
                && !classType.equalsIgnoreCase("classroom")) {*/
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    if (mCallMenuItem != null) {
                        if (CallSupportActivity.sIsTalking) {
                            mCallMenuItem.setIcon(R.mipmap.ic_calling);
                        } else {
                            mCallMenuItem.setIcon(R.mipmap.ic_call);
                        }
                    }
                });
            }
        }, 1000, 1000);
        //}
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (!classType.equalsIgnoreCase("trial") && !classType.equalsIgnoreCase("demo")
                && !classType.equalsIgnoreCase("classroom")) {*/
        mTimer.cancel();
        //}
    }

    private void getMessages() {
        final ChatMessage chatMessage = new ChatMessage();
        mServerRequestManager.getMessages(mConsultantId, accountId, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                mProgressDialog.dismiss();
                showIntroMsg(chatMessage);
                if (result.getCode() == 1) {
                    List<ChatMessage> messages = new ArrayList<>();
                    JSONArray results = null;
                    try {
                        results = new JSONArray(result.getDataSt());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        for (int i = 0; i < results.length(); i++) {
                            int direction = results.getJSONObject(i).getInt("direction");
                            // 1. call fail, 2. call success
                            int isRead = results.getJSONObject(i).getInt("isread");
                            String sendDate = results.getJSONObject(i).getString("send_date");
                            String msgContent = results.getJSONObject(i).getString("message_content");
                            String msgId = results.getJSONObject(i).getString("message_id");
                            String callCenterName = results.getJSONObject(i).getString("call_center_id");

                            ChatMessage msg = new ChatMessage();

                            if (direction == 1) {
                                msg.setSender("S_" + accountId);
                                setMessages(messages, msgContent, msgId, msg);
                            } else if (direction == 0) {
                                msg.setSender("A_auto");
                                if (isRead == 2) {
                                    msgContent = mContext.getString(inc.osbay.android.tutorroom.sdk.R.string.os_call_support_success, callCenterName, LGCUtil.convertToLocale(sendDate, CommonConstant.DATE_TIME_FORMAT));
                                } else {
                                    msgContent = mContext.getString(inc.osbay.android.tutorroom.sdk.R.string.os_call_support_fail, callCenterName, LGCUtil.convertToLocale(sendDate, CommonConstant.DATE_TIME_FORMAT));
                                }
                                setMessages(messages, msgContent, msgId, msg);

                            } else {
                                msg.setSender("A_auto");
                                setMessages(messages, msgContent, msgId, msg);
                            }
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                mOnlineSupportMessageAdapter = new OnlineSupportMessageAdapter(mChatMessages, getActivity(), accountId);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                messageRV.setLayoutManager(layoutManager);
                messageRV.setAdapter(mOnlineSupportMessageAdapter);
                mOnlineSupportMessageAdapter.notifyDataSetChanged();
                messageRV.scrollToPosition(0);
            }

            @Override
            public void onError(ServerError err) {
                showIntroMsg(chatMessage);
                mOnlineSupportMessageAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
                Log.i("Online Support Error", err.getMessage());
            }
        });
    }

    private void setMessages(List<ChatMessage> messages, String msgContent, String msgId, ChatMessage msg) {
        msg.setBody(msgContent);
        //msg.setMessageType(messageType);
        msg.setMessageId(msgId);
        mChatMessages.add(msg);
    }

    @OnClick(R.id.tv_send_button)
    void send() {
        String text = inputET.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        messageRV.smoothScrollToPosition(0);
        /*mChatMessages.add(text);
        mOnlineSupportMessageAdapter.notifyDataSetChanged();*/

        android.os.Message message;
        Bundle bundle = new Bundle();
        mServerRequestManager.saveOnlineSupportMessage(mConsultantId, accountId, text, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
            }

            @Override
            public void onError(ServerError err) {
            }
        });
        message = android.os.Message.obtain(null, MessengerService.MSG_IM_CONSULTANT);
        bundle.putString("send_to", "A_" + mConsultantId);
        bundle.putString("message_content", text);
        message.setData(bundle);
        try {
            mWSMessageClient.sendMessage(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        inputET.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.online_support));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.opt_call_support:
                if (!CallSupportActivity.sIsTalking) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.os_call_ready))
                            .setMessage(getString(R.string.os_call_ready_msg))
                            .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                                mProgressDialog.show();
                                initializeTwilioSDK();
                            })
                            .setNegativeButton(getString(R.string.cr_leave_room_cancel), null)
                            .create()
                            .show();

                } else {
                    Intent callIntent = new Intent(getActivity(), CallSupportActivity.class);
                    startActivity(callIntent);
                }
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Disabling call function for Trial Request
        /*if (!classType.equalsIgnoreCase("trial") && !classType.equalsIgnoreCase("demo")
                && !classType.equalsIgnoreCase("classroom")) {*/
        inflater.inflate(R.menu.menu_support_call, menu);
        mCallMenuItem = menu.findItem(R.id.opt_call_support);
        //}
    }

    @Override
    public void onDestroy() {
        mWSMessageClient.removeMessenger(mMessenger);
        mChatMessages.clear();
        super.onDestroy();
    }

    private void initializeTwilioSDK() {
        mServerRequestManager.generateTwilioToken(accountId, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                mProgressDialog.dismiss();
                if (getActivity() != null && result.getCode() == 1) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.getDataSt());
                        String videoToken = jsonObject.getString("TokenVideo");
                        Intent intent = new Intent(getActivity(), CallSupportActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(CallSupportActivity.EXTRA_CONSULTANT_ID, mConsultantId);
                        intent.putExtra(CallSupportActivity.EXTRA_ACCESS_TOKEN, videoToken);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(ServerError err) {
                mProgressDialog.dismiss();

                if (getActivity() != null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.error))
                            .setMessage(err.getMessage())
                            .setPositiveButton(getString(R.string.ok), null)
                            .create()
                            .show();
                }
            }
        });
    }

    private void showIntroMsg(ChatMessage chatMessage) {
        /*if (classType.equalsIgnoreCase("trial")) {
            chatMessage.setBody(getString(R.string.trial_request_welcome));
            chatMessage.setMessageType("trial_request");
        } else {*/
        chatMessage.setBody(getString(R.string.os_msg_body));
        chatMessage.setMessageType("im_online");
        //}
        chatMessage.setSender("A_auto");
        mChatMessages.add(chatMessage);
    }

    /**
     * Handler of incoming messages from service.
     */
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessengerService.MSG_IM_CONSULTANT:
                    Bundle chatBundle = msg.getData();

                    ChatMessage message = new ChatMessage(chatBundle.getString("data"));

                    mChatMessages.add(message);
                    mOnlineSupportMessageAdapter.notifyDataSetChanged();
                    messageRV.smoothScrollToPosition(0);

                    /*** C_ is for Call Center Manager ***/
                    mServerRequestManager.updateMessageStatus(mConsultantId, accountId, new ServerRequestManager.OnRequestFinishedListener() {
                        @Override
                        public void onSuccess(ServerResponse response) {

                        }

                        @Override
                        public void onError(ServerError err) {

                        }
                    });
                    break;
                case MessengerService.MSG_IM_TRIAL_CONSULTANT:
                    Bundle chatBundle1 = msg.getData();

                    ChatMessage message1 = new ChatMessage(chatBundle1.getString("data"));

                    /*** G_ is for Consultant ***/
                    /*if (message1.getSender().startsWith("G_")) {
                        mServerRequestManager.updateMessageHistoryConsultant(new ServerRequestManager.OnRequestFinishedListener() {
                            @Override
                            public void onSuccess(ServerResponse result) {
                                mServerRequestManager.getStudentInfo(new ServerRequestManager.OnRequestFinishedListener() {
                                    @Override
                                    public void onSuccess(ServerResponse result) {
                                        if (getActivity() != null) {
                                        }
                                    }

                                    @Override
                                    public void onError(ServerError err) {
                                        if (getActivity() != null) {
                                            Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(ServerError err) {
                            }
                        });
                    }*/

                    mChatMessages.add(message1);
                    mOnlineSupportMessageAdapter.notifyDataSetChanged();
                    messageRV.smoothScrollToPosition(0);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
