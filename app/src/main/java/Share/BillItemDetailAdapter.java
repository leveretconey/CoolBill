package Share;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leveretconey.coolbill.Activities.MainActivity;
import com.leveretconey.coolbill.R;

import java.util.List;

public class BillItemDetailAdapter extends RecyclerView.Adapter<BillItemDetailAdapter.ViewHolder>
implements View.OnClickListener{
    @Override
    public void onClick(View v) {

    }

    private static final String TAG = "BillItemDetailAdapter";
    MainActivity context;
    List<BillItem> billItems;

    public BillItemDetailAdapter(MainActivity context, List<BillItem> billItems) {
        this.context = context;
        this.billItems = billItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bill_item_detail,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.switchSelectedBillItemDetail
                        (v,billItems.get(holder.getAdapterPosition()));
            }
        });
        return holder;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        BillItem billItem;
        TextView dateTextView;
        TextView amountTextView;
        TextView mainTypeTextView;
        TextView subTypeTextView;


        public ViewHolder(View view){
            super(view);
            dateTextView =(TextView)view.findViewById(R.id.detail_date);
            amountTextView =(TextView)view.findViewById(R.id.detail_amount);
            mainTypeTextView =(TextView)view.findViewById(R.id.detail_main_type);
            subTypeTextView =(TextView)view.findViewById(R.id.detail_sub_type);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.billItem=billItems.get(position);
        BillItem billItem=holder.billItem;
        holder.dateTextView.setText(String.format(context.getResources().getString(R.string.year_month_day)
                ,billItem.getYear(),billItem.getMonth(),billItem.getDay()));
        holder.amountTextView.setText(String.valueOf(billItem.getAmount()));
        holder.mainTypeTextView.setText(billItem.getMainType());
        holder.subTypeTextView.setText(billItem.getSubType());
    }
    @Override
    public int getItemCount() {
        return billItems.size();
    }
}
