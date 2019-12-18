package com.muxi.xmusicplayer;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import com.muxi.xmusicplayer.Service.MusicService;
public class PlayActivity extends AppCompatActivity {
    private MusicService.MusicBinder MusicBinder;
    private ServiceConnection mSdcardServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder = (MusicService.MusicBinder) service;
            String category = getIntent().getStringExtra("category");
            int position = getIntent().getIntExtra("position", 0);
            if (MusicBinder != null) {
                MusicBinder.helpInit(PlayActivity.this, position, category);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent service = new Intent(this, MusicService.class);
        startService(service);
        bindService(service, mSdcardServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MusicBinder != null) {
            unbindService(mSdcardServiceConnection);
        }
    }
}
