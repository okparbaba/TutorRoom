package inc.osbay.android.tutorroom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.ChatMessage;

public class OnlineSupportMessageAdapter extends RecyclerView.Adapter<OnlineSupportMessageAdapter.ViewHolder> {
    private List<ChatMessage> chatMessages;
    private Context context;
    private String accountID;

    public OnlineSupportMessageAdapter(List<ChatMessage> chatMessages, Context context, String accountID) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.accountID = accountID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat_support, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage msg = chatMessages.get(chatMessages.size() - ++position);
        //Log.e("OnlineSupport", "account id - " + accountId + ", sender - " + msg.getSender());

        // Messages sent by Student
        if (accountID.equals(msg.getSender().split("_")[1])) {
            holder.llChatLayout.setGravity(Gravity.END);
            holder.imvSupportIcon.setVisibility(View.GONE);
            holder.tvSendMessage.setText(msg.getBody());
            holder.tvSendMessage.setVisibility(View.VISIBLE);
            holder.tvReceivedMessage.setVisibility(View.GONE);
        }// Messages sent by Consultant or Call Center Manager
        else {
            holder.llChatLayout.setGravity(Gravity.START);
            holder.tvSendMessage.setVisibility(View.GONE);
            /*if (msg.getMessageType().equalsIgnoreCase(TRIAL_MESSAGE_TYPE)
                    || msg.getMessageType().equalsIgnoreCase(NORMAL_MESSAGE_TYPE)
                    || msg.getMessageType().equalsIgnoreCase("")) {*/
                //holder.trialConfirmLayout.setVisibility(View.GONE);
                holder.tvReceivedMessage.setText(msg.getBody());
                holder.tvReceivedMessage.setVisibility(View.VISIBLE);
                holder.imvSupportIcon.setVisibility(View.VISIBLE);
                holder.llChatLayout.setVisibility(View.VISIBLE);
            //}
                /*if (msg.getMessageType().equalsIgnoreCase(TRIAL_COMFIRM_TYPE)) {
                    //FlurryAgent.logEvent("Trial today scheduled");
                    holder.trialConfirmLayout.setVisibility(View.VISIBLE);
                    holder.llChatLayout.setVisibility(View.GONE);
                    String messageContent = msg.getBody();
                    try {
                        JSONObject msgJson = new JSONObject(messageContent);
                        final String bookStartDateTime = msgJson.getString("bookstart_datetime");
                        String bookEndDateTime = msgJson.getString("bookend_datetime");
                        String tutorName = msgJson.getString("tutor_name");
                        String tutorAvatar = msgJson.getString("tutor_avatar");

                        holder.tutorName.setText(tutorName);
                        holder.tutorImg.setImageURI(tutorAvatar);
                        holder.scheduleDate.setText(convertUTCToLocale(bookStartDateTime));
                        holder.scheduleTime.setText(getStartTimeFromUTC(bookStartDateTime) + " - " + getStartTimeFromUTC(bookEndDateTime));
                        if (isScheduledTrialClassExpired(bookEndDateTime)) {
                            holder.confirm.setClickable(false);
                            holder.confirm.setTextColor(getResources().getColor(R.color.view_light_gray));
                            holder.confirm.setBackground(getResources().getDrawable(R.drawable.trial_class_confirm_btn_disabled_bg));
                        } else {
                            holder.confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FlurryAgent.logEvent("Click confirm trial today");
                                    String token = prefs.getString("access_token", null);
                                    String accountId = prefs.getString("account_id", null);

                                    EditNumberFragment editNumberFragment = new EditNumberFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EditNumberFragment.sourceFragment, OnlineSupportFragment.class.getSimpleName());
                                    editNumberFragment.setArguments(bundle);

                                    if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(token)) {
                                        AccountAdapter accountAdapter = new AccountAdapter(getActivity());
                                        Account account = accountAdapter.getAccountById(accountId);
                                        if (account.getStatus() == Account.Status.REQUEST_TRIAL && TextUtils.isEmpty(account.getPhoneNumber())) {
                                            getFragmentManager().beginTransaction()
                                                    .replace(R.id.fl_trial_submit, editNumberFragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        } else {
                                            getActivity().finish();
                                        }
                                    } else {
                                        getFragmentManager().beginTransaction()
                                                .replace(R.id.fl_trial_submit, editNumberFragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }*/
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReceivedMessage;
        private TextView tvSendMessage;
        private LinearLayout llChatLayout;
        private ImageView imvSupportIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            imvSupportIcon = itemView.findViewById(R.id.imv_support_icon);
            llChatLayout = itemView.findViewById(R.id.ll_chat_layout);
            tvReceivedMessage = itemView.findViewById(R.id.tv_receive_message);
            tvSendMessage = itemView.findViewById(R.id.tv_send_message);
        }
    }
}
