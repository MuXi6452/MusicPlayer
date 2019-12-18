package com.muxi.xmusicplayer.xRecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.muxi.xmusicplayer.Bean.JsonRoot;
import com.muxi.xmusicplayer.R;
import java.util.ArrayList;
public class XAdapter extends RecyclerView.Adapter<XViewHolder> {
    private Context ctx;
    private ArrayList<JsonRoot.Tracks> tracks;
    private XOnItemClickedListener listener;
    private String picUrl;
    public XAdapter(Context ctx, ArrayList<JsonRoot.Tracks> tracks) {
        this.ctx=ctx;
        this.tracks=tracks;
    }

    @NonNull
    @Override
    public XViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, null, false);
        return new XViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final XViewHolder holder, final int position) {
        picUrl = tracks.get(position).album.picUrl;
        Glide.with(ctx).load(picUrl).dontAnimate().override(25,25).into(holder.iv_des);

        holder.tv_name.setText(tracks.get(position).name);
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.xOnItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void setXOnClickedListener(XOnItemClickedListener xOnClickedListener){
        this.listener=xOnClickedListener;
    }
}
