package com.abida.foodmeter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.MyViewHolder> {


    private Context mContext;
    private List<DetailItem> mDataList;

    public CustomListAdapter(Context mContext, List<DetailItem> mDataList) {
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
        final DetailItem chat = mDataList.get(position);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ""+chat.getTime(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.tvBurned.setText(chat.getCaloriesBurned()+"");

        holder.tvTotal.setText(chat.getCaloriesUsed()+"");
        String getDay = getDay(chat.getTime());
        holder.tvDay.setText(getDay);
        holder.tvTitle.setText(chat.getTime());
        double  netCal = chat.getNetCalories();
        if (netCal>=1500&&netCal<=2000)
            holder.tvTitle.setBackgroundColor(Color.GREEN);
        else
            holder.tvTitle.setBackgroundColor(Color.RED);
        holder.tvNetCal.setText((netCal+""));

    }

    @Override
    public int getItemCount(){
        return mDataList.size();
    }

    public static class MyViewHolder  extends RecyclerView.ViewHolder{

        TextView  tvTotal;
        TextView tvBurned;
        TextView tvTitle;
        TextView tvNetCal;
        TextView tvDay;

        ConstraintLayout parent;

        public MyViewHolder(View itemView){
            super(itemView);

            parent    = itemView.findViewById(R.id.layout_parent);

            tvTitle   = itemView.findViewById(R.id.tvTitle);
            tvTotal   = itemView.findViewById(R.id.tvTotal);
            tvBurned = itemView.findViewById(R.id.tvBurned);
            tvNetCal = itemView.findViewById(R.id.tvNet);
            tvDay = itemView.findViewById(R.id.tvDay);
        }

    }
}
