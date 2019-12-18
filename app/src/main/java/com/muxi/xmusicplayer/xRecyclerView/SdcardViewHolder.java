package com.muxi.xmusicplayer.xRecyclerView;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.muxi.xmusicplayer.R;
public class SdcardViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_sdcard;
    public SdcardViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_sdcard = itemView.findViewById(R.id.tv_sdcard);
    }
}
