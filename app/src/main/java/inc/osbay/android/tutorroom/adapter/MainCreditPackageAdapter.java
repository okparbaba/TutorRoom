package inc.osbay.android.tutorroom.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.CreditPackage;

public class MainCreditPackageAdapter extends RecyclerView.Adapter<MainCreditPackageAdapter.ViewHolder> {

    private List<CreditPackage> creditPackageList;
    private OnItemClicked onClick;

    public MainCreditPackageAdapter(List<CreditPackage> creditPackageList, OnItemClicked listner) {
        this.creditPackageList = creditPackageList;
        this.onClick = listner;
    }

    @NonNull
    @Override
    public MainCreditPackageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_package_item, parent, false);
        return new MainCreditPackageAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainCreditPackageAdapter.ViewHolder holder, final int position) {
        CreditPackage creditPackage = creditPackageList.get(position);
        holder.creditTitle.setText(creditPackage.getPackageName());
        holder.creditTitle.setText(String.valueOf(creditPackage.getPackageCredit()));
        holder.creditContent.setText(String.valueOf(creditPackage.getPackageAmount()));
        holder.buy.setOnClickListener(view ->
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
        TextView creditTitle;
        TextView creditContent;
        TextView buy;

        ViewHolder(View itemView) {
            super(itemView);
            creditTitle = itemView.findViewById(R.id.credit_title);
            creditContent = itemView.findViewById(R.id.credit_content);
            buy = itemView.findViewById(R.id.buy);
        }
    }
}
