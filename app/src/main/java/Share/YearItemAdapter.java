package Share;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leveretconey.coolbill.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class YearItemAdapter extends RecyclerView.Adapter<YearItemAdapter.ViewHolder> {
    private static final String TAG = "YearItemAdapter";
    private List<List<Integer>> yearMonthAvailableList=new ArrayList<List<Integer>>();
    private List<Integer> years=new ArrayList<Integer>();

    public YearItemAdapter(TreeMap<Integer, TreeSet<Integer>> yearMonthAvailable) {
        for(int year : yearMonthAvailable.keySet()){
            List<Integer> months=new ArrayList<Integer>();
            for(int month : yearMonthAvailable.get(year)){
                months.add(month);
            }
            yearMonthAvailableList.add(months);
            years.add(year);
        }
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
        RecyclerView monthRecycleView;
        public ViewHolder(View view){
            super(view);
            yearTextView =(TextView)view.findViewById(R.id.year_title);
            monthRecycleView =(RecyclerView) view.findViewById(R.id.months_recycle_view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int year=years.get(position);
        Log.d(TAG, "onBindViewHolder: year="+year);
        holder.yearTextView.setText(String.valueOf(year));
        RecyclerView recyclerView=holder.monthRecycleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        MonthItemAdapter monthItemAdapter=new MonthItemAdapter
                                            (yearMonthAvailableList.get(position));
        recyclerView.setAdapter(monthItemAdapter);
    }
    @Override
    public int getItemCount() {
        return yearMonthAvailableList.size();
    }
}
