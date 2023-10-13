package comp5216.sydney.edu.au.mentalhealth.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.MyAppMent;

public class MyAppointmentAdapter extends RecyclerView.Adapter<MyAppointmentAdapter.ViewHolder> {

    private List<MyAppMent> dataList;
    private Context context;

    public OnButtonClick mOnButtonClick;
    public MyAppointmentAdapter(Context context, List<MyAppMent> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_appointment, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyAppMent data = dataList.get(position);
        holder.textView.setText(data.getDate()+data.getTime());
        holder.CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnButtonClick.onCancelBtnClick(position);
            }
        });
        holder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnButtonClick.oncompleteBtnClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public Button CancelBtn,complete;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.myTime);
            CancelBtn = itemView.findViewById(R.id.CancelBtn);
            complete = itemView.findViewById(R.id.complete);
        }
    }
    public interface OnButtonClick{
        void onCancelBtnClick(int position);
        void oncompleteBtnClick(int position);
    }
}