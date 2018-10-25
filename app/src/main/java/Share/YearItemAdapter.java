package Share;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leveretconey.coolbill.Activities.MainActivity;
import com.leveretconey.coolbill.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class YearItemAdapter extends RecyclerView.Adapter<YearItemAdapter.ViewHolder>
{
    private static final String TAG = "YearItemAdapter";
    private List<List<Integer>> yearMonthAvailableList=new ArrayList<List<Integer>>();
    private List<Integer> years=new ArrayList<Integer>();
    private MainActivity context;
    private HashMap<Integer,LinearLayout> year2yearItemLayout
            =new HashMap<Integer, LinearLayout>();
    private HashMap<Integer,HashMap<Integer,TextView>> yearMonth2MonthItem
            =new HashMap<Integer, HashMap<Integer, TextView>>();

    public LinearLayout getYearItemLayout(int year){
        if (year2yearItemLayout.containsKey(year))
            return year2yearItemLayout.get(year);
        else
            return null;
    }
    public TextView getMonthTextView(int year,int month){
        if(!yearMonth2MonthItem.containsKey(year))
            return null;
        HashMap<Integer,TextView> map=yearMonth2MonthItem.get(year);
        if (!map.containsKey(month))
            return null;
        return map.get(month);
    }

    public YearItemAdapter(TreeMap<Integer, TreeSet<Integer>> yearMonthAvailable, MainActivity context) {
        for(int year : yearMonthAvailable.keySet()){
            List<Integer> months=new ArrayList<Integer>();
            for(int month : yearMonthAvailable.get(year)){
                months.add(month);
            }
            yearMonthAvailableList.add(months);
            years.add(year);
        }
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.year_item_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView yearTextView;
        Button toggleButton;

        LinearLayout monthLinearLayout;
        RelativeLayout yearSelectionLayout;
        LinearLayout yearItemLayout;
        public ViewHolder(View view){
            super(view);
            yearTextView =(TextView)view.findViewById(R.id.year_title);
            toggleButton =(Button) view.findViewById(R.id.year_toggle);
            monthLinearLayout =(LinearLayout) view.findViewById(R.id.year_months_layout);
            yearSelectionLayout=(RelativeLayout)view.findViewById(R.id.year_select_layout);
            yearItemLayout=(LinearLayout)view.findViewById(R.id.year_item_layout);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final int year=years.get(position);

        List<Integer> months=yearMonthAvailableList.get(position);
        holder.yearTextView.setText(String.valueOf(year));
        year2yearItemLayout.put(year,holder.yearItemLayout);
        yearMonth2MonthItem.put(year,new HashMap<Integer, TextView>());
        LayoutInflater inflater=(LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for(int i=0;i<months.size();i++){
            View view=inflater.inflate(R.layout.month_item_view,holder.monthLinearLayout);

            final int month=months.get(i);
            TextView textView=(TextView)(((LinearLayout)view).getChildAt(i));
            yearMonth2MonthItem.get(year).put(month,textView);
            textView.setText(String.valueOf(months.get(i)));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.selectYearMonth(year,month);
                    context.refreshBillItemDetail();
                }
            });
        }

        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.toggleYearItem(year);
                context.refreshBillItemDetail();
            }
        });

        holder.yearSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.selectYearMonth(year,Integer.MIN_VALUE);
                context.refreshBillItemDetail();
            }
        });
    }
    @Override
    public int getItemCount() {
        return yearMonthAvailableList.size();
    }
}
