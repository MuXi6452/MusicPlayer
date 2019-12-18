package com.muxi.xmusicplayer.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.muxi.xmusicplayer.Bean.JsonRoot;
import com.muxi.xmusicplayer.R;
import com.muxi.xmusicplayer.Utils.Stream2String;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicService extends Service implements View.OnClickListener {
    private MediaPlayer mp;
    private Context mCtx;
    private ImageView mIvFore;
    private ImageView mIvPlay;
    private ImageView mIvNext;
    private TextView mTvTitle;
    private TextView tv_currentDuration;
    private TextView tv_totalDuration;
    private ImageView mIvAlbum;
    private SeekBar mSeekBar;
    private int mPosition;
    private int totalSong;
    private String mUrl;
    private String mCategory;
    private JsonRoot fromJson;
    ArrayList<String> mFilePathList = new ArrayList<String>();
    ArrayList<String> mFileNameList = new ArrayList<String>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }
    public class MusicBinder extends Binder {//定义Binde调用Service里的方法
        public void helpInit(Context context, int position, String category) {
            mCtx = context;
            mPosition = position;
            mCategory = category;
            initData();
        }
    }
    private void initData() {
        mTvTitle = ((Activity) mCtx).findViewById(R.id.tv_title);
        mIvFore = ((Activity) mCtx).findViewById(R.id.iv_fore);
        mIvPlay = ((Activity) mCtx).findViewById(R.id.iv_play);
        mIvNext = ((Activity) mCtx).findViewById(R.id.iv_next);
        mIvAlbum = ((Activity) mCtx).findViewById(R.id.iv_album);
        tv_currentDuration = ((Activity) mCtx).findViewById(R.id.tv_currentDuration);
        tv_totalDuration = ((Activity) mCtx).findViewById(R.id.tv_totalDuration);
        mSeekBar = ((Activity) mCtx).findViewById(R.id.seekBar);
        mIvPlay.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        mIvFore.setOnClickListener(this);
        Glide.with(mCtx).load(R.mipmap.default_albulm).dontAnimate().into(mIvAlbum);
        if (mCategory.equals("sdcard")){
            scanSdcardDir();
            totalSong = mFileNameList.size();
            startPlayLocal();
        }else {
            loadNetwork();
        }
    }

    private void startPlayLocal() {
        mTvTitle.setText(mFileNameList.get(mPosition));
        mUrl = mFilePathList.get(mPosition);
        start();
    }

    private void scanSdcardDir() {
//        File scanFile = new File(Environment.getExternalStorageDirectory().getPath() + "/xMusic");
        File scanFile = new File(Environment.getExternalStorageDirectory().getPath());
        if (scanFile.isDirectory()) {
            for (File file : scanFile.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".mp3") || path.endsWith(".flac")) {
                    mFileNameList.add(file.getName());
                    mFilePathList.add(file.getAbsolutePath());
                }
            }
        }
    }
    private void loadNetwork() {
        String url = "http://music.163.com/api/playlist/detail?id=3778678";
        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = connection.getInputStream();//拿到网络输入流
                        String json = Stream2String.parseStream(inputStream);//转成字符串
                        return json;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Gson gson = new Gson();
                fromJson = gson.fromJson(result, JsonRoot.class);
                totalSong = fromJson.result.tracks.size();
                String url ="http://music.163.com/song/media/outer/url?id=";
                String id = fromJson.result.tracks.get(mPosition).id;
                String title = fromJson.result.tracks.get(mPosition).name;
                String picUrl = fromJson.result.tracks.get(mPosition).album.picUrl;
                mTvTitle.setText(title);
                Glide.with(mCtx).load(picUrl).dontAnimate().into(mIvAlbum);
                mUrl = url+id;
                start();
            }
        };
        asyncTask.execute(url);
    }

    @Override
    public void onClick(View v) {   //controller点击事件
        switch (v.getId()) {
            case R.id.iv_play:
                if (mp != null && mp.isPlaying()) {
                    mIvPlay.setImageResource(R.mipmap.play);
                    mp.stop();
                    mp.reset();
                } else {
                    mIvPlay.setImageResource(R.mipmap.stop);
                    start();
                }
                break;
            case R.id.iv_next:
                if (mPosition == totalSong) {
                    break;
                }
                if (mp != null) {
                    mIvPlay.setImageResource(R.mipmap.stop);
                    mp.seekTo(mp.getDuration());
                }
                break;
            case R.id.iv_fore:
                if (mPosition == 0) {
                    break;
                }
                if (mp != null) {
                    mIvPlay.setImageResource(R.mipmap.stop);
                    mPosition--;
                    nextSong();
                }
                break;
        }
    }

    private void nextSong() {
        if(mCategory.equals("sdcard")){
            startPlayLocal();
        }else {
            loadNetwork();
        }
    }

    private void start() { //开始播放
        if (mp != null) {
            mp.stop();
            mp.reset();
        }else {
            mp = new MediaPlayer();
        }
        try {
            mp.setDataSource(mCtx, Uri.parse(mUrl));
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    int p = mp.getDuration();
                    mSeekBar.setMax(p);
                    String min = String.format("%02d",TimeUnit.MILLISECONDS.toMinutes(p));
                    String sec = String.format("%02d",TimeUnit.MILLISECONDS.toSeconds(p)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(p)));
                    tv_totalDuration.setText("/"+min+":"+sec);
                    getProgress();
                    setListener();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPosition++;
                nextSong();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());//将进度条的进度赋值给歌曲
                getProgress();//开始音乐继续获取歌曲的进度
            }
        });
    }

    private Handler mHander = new Handler();
    private void getProgress() {
        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                int p = mp.getCurrentPosition();//获取歌曲的进度
                String min = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(p));
                String sec = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(p) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(p)));
                mSeekBar.setProgress(p);//将获取歌曲的进度赋值给seekbar
                tv_currentDuration.setText(min + ":" + sec);
                mHander.postDelayed(this, 1000);
            }
        }, 1000);//延时1s执行
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp!=null){
            mp.release();
            mp = null;
        }
        if(mHander!=null){
            mHander.removeCallbacksAndMessages(null);
            mHander = null;
        }
    }
}
