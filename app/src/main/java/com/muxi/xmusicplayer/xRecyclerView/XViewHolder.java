package com.muxi.xmusicplayer.xRecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.muxi.xmusicplayer.R;

public class XViewHolder extends RecyclerView.ViewHolder {
    public ImageView iv_des;
    public TextView tv_name;
    public XViewHolder(@NonNull View itemView) {
        super(itemView);
        iv_des = itemView.findViewById(R.id.iv_des);
        tv_name = itemView.findViewById(R.id.tv_name);
    }
}
