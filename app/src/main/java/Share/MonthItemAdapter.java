package Share;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leveretconey.coolbill.R;

import java.util.List;

public class MonthItemAdapter extends RecyclerView.Adapter<MonthItemAdapter.ViewHolder> {
    private static final String TAG = "MonthItemAdapter";
    List<Integer> months;

    public MonthItemAdapter(List<Integer> months) {
        this.months = months;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.month_item_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView monthTextView;
        public ViewHolder(View view){
            super(view);
            monthTextView =(TextView)view.findViewById(R.id.month_text_view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int month=months.get(position);
        holder.monthTextView.setText(String.valueOf(month));
    }
    @Override
    public int getItemCount() {
        return months.size();
    }
}
