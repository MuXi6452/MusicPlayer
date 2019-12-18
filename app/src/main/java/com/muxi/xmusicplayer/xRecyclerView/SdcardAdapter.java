package com.muxi.xmusicplayer.xRecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.muxi.xmusicplayer.R;

import java.util.ArrayList;

public class SdcardAdapter extends RecyclerView.Adapter<SdcardViewHolder> {
    private Context mCtx;
    ArrayList<String> mDataList;
    private XOnItemClickedListener listener;
    public SdcardAdapter(Context ctx, ArrayList<String> dataList) {
        mCtx =  ctx;
        mDataList = dataList;
    }

    @NonNull
    @Override
    public SdcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.sdcard_item, null);
        return new SdcardViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SdcardViewHolder holder, final int position) {
        holder.tv_sdcard.setText(mDataList.get(position));
        holder.tv_sdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.xOnItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setXOnClickedListener(XOnItemClickedListener listener){
        this.listener=listener;
    }
}
