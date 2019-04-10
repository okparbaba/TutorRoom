package inc.osbay.android.tutorroom.adapter;

import android.support.annotation.NonNull;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.CreditPackage;

public class CreditTierAdapter extends RecyclerView.Adapter<CreditTierAdapter.ViewHolder> {
    private List<CreditPackage> creditPackageList;
    private OnItemClicked onClick;

    public CreditTierAdapter(List<CreditPackage> creditPackageList, OnItemClicked listner) {
        this.creditPackageList = creditPackageList;
        this.onClick = listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_tier_item, parent, false);
        return new CreditTierAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        CreditPackage creditPackage = creditPackageList.get(position);
        holder.creditTitle.setText(creditPackage.getPackageName());
        holder.credit.setText(String.valueOf(creditPackage.getPackageCredit()));
        holder.creditAmount.setText(String.valueOf(creditPackage.getPackageAmount()));
        holder.percentRL.setOnClickListener(view ->
                onClick.onItemClick(creditPackage)
        );
    }

    @Override
    public int getItemCount() {
        return creditPackageList.size();
    }

    public interface OnItemClicked {
        void onItemClick(CreditPackage creditPackage);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        PercentRelativeLayout percentRL;
        TextView creditTitle;
        TextView credit;
        TextView creditAmount;

        ViewHolder(View itemView) {
            super(itemView);
            percentRL = itemView.findViewById(R.id.percent_rl);
            creditTitle = itemView.findViewById(R.id.packagee);
            credit = itemView.findViewById(R.id.credit);
            creditAmount = itemView.findViewById(R.id.credit_ammount);
        }
    }
}
