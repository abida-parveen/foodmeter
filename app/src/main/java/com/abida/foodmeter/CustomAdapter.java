package com.abida.foodmeter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context mContext;
    private List<DetailItem> mDataList;

    public CustomAdapter(Context mContext, List<DetailItem> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.custom_adapter_item, parent, false);
        return new MyViewHolder(rootView);
    }

    private String getDay(String date){
        String dayOfTheWeek = (String) DateFormat.format("EEE", Date.parse(date));
        //Toast.makeText(this, dayOfTheWeek, Toast.LENGTH_SHORT).show();
        return dayOfTheWeek;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        final DetailItem data = mDataList.get(position);

        String getDay = getDay(data.getTime());

        holder.tvTitle.setText(data.getTime());
        double  netCal = data.getNetCalories();
        if (netCal>=1500&&netCal<=2000)
            holder.tvTitle.setBackgroundColor(Color.GREEN);
        else
            holder.tvTitle.setBackgroundColor(Color.RED);

        holder.tvTotal.setText((data.getCaloriesUsed()+""));
        holder.tvBurned.setText((data.getCaloriesBurned()+""));
        holder.tvNet.setText((data.getNetCalories()+""));
        holder.tvTime.setText(getDay);

//        holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                String userId = p.getUid();
//                Utils.removeUser(mContext, userId);
//                return true;
//            }
//        });


    }

    @Override
    public int getItemCount(){
        return mDataList.size();
    }

    public static class MyViewHolder  extends RecyclerView.ViewHolder{

        TextView tvTotal, tvBurned, tvNet, tvTime, tvTitle;

        public MyViewHolder(View itemView){
            super(itemView);
            tvTitle   = itemView.findViewById(R.id.tvTitle);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvBurned = itemView.findViewById(R.id.tvBurned);
            tvNet = itemView.findViewById(R.id.tvNet);
            tvTime = itemView.findViewById(R.id.tvDay);
        }

    }
}
