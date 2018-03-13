package com.xily.note.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xily.note.Data;
import com.xily.note.R;
import com.xily.note.SettingsData;

import java.util.List;

/**
 * Created by Xily on 2017/10/23.
 */
public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.LinearViewHolder> {
    private Context mContext;
    private OnItemLongClickListener mLongListener;
    private OnItemClickListener mListener;
    private List<Data> Text;
    private int type;
    private int maxline;
    public LinearAdapter(Context context,List<Data> Text,int type,OnItemClickListener listener,OnItemLongClickListener longlistener){
        this.mContext=context;
        this.mListener=listener;
        this.mLongListener=longlistener;
        this.Text=Text;
        this.type=type;
        maxline=new SettingsData(context).getValue("maxline",3);
    }
    public void refreshAll(List<Data> Text){
        this.Text=Text;
    }
    @Override
    public LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(type==1?R.layout.layout_linear_item:R.layout.layout_grid_item,parent,false));
    }
    @Override
    public void onBindViewHolder(final LinearViewHolder holder, final int position) {
        Data noteList=Text.get(position);
        holder.textView1.setText(noteList.getTitle());
        holder.textView2.setText(noteList.getTime());
        holder.textView3.setText(noteList.getContent());
        if(maxline>0) {
            holder.textView3.setMaxLines(maxline);
            holder.textView3.setEllipsize(TextUtils.TruncateAt.END);
        }holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(position,holder);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLongListener.onClick(position,holder);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Text.size();
    }
    public class LinearViewHolder extends RecyclerView.ViewHolder{
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        public LinearViewHolder(View itemView) {
            super(itemView);
            textView1=itemView.findViewById(R.id.tv_title);
            textView2=itemView.findViewById(R.id.tv_time);
            textView3=itemView.findViewById(R.id.tv_text);
        }
    }
    public interface OnItemLongClickListener{
        void onClick(int pos, LinearViewHolder holder);
    }
    public interface OnItemClickListener{
        void onClick(int pos, LinearViewHolder holder);
    }
}
