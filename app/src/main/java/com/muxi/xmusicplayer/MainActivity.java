package com.muxi.xmusicplayer;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.muxi.xmusicplayer.Bean.JsonRoot;
import com.muxi.xmusicplayer.Utils.Stream2String;
import com.muxi.xmusicplayer.xRecyclerView.SdcardAdapter;
import com.muxi.xmusicplayer.xRecyclerView.XAdapter;
import com.muxi.xmusicplayer.xRecyclerView.XOnItemClickedListener;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private XAdapter xAdapter;
    private JsonRoot fromJson;
    private Intent mIntent;
    ArrayList<String> dataList = new ArrayList<String>();
    private SdcardAdapter sdcardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            //检测是否有写的权限
            int write_permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            int read_permission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
            if (write_permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else if (read_permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void requestNet() {
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
                xAdapter = new XAdapter(MainActivity.this, fromJson.result.tracks);
                mRecyclerView.setAdapter(xAdapter);
                xAdapter.notifyDataSetChanged();
                xAdapter.setXOnClickedListener(new XOnItemClickedListener() {
                    @Override
                    public void xOnItemClick(int position) {
                        mIntent = new Intent(MainActivity.this, PlayActivity.class);
                        mIntent.putExtra("position", position);
                        mIntent.putExtra("category", "network");
                        startActivity(mIntent);
                    }
                });
            }
        };
        asyncTask.execute(url);
    }

    public void scanSdcard(View view) {
        openDir();
        sdcardAdapter = new SdcardAdapter(MainActivity.this, dataList);
        mRecyclerView.setAdapter(sdcardAdapter);
        sdcardAdapter.notifyDataSetChanged();
        sdcardAdapter.setXOnClickedListener(new XOnItemClickedListener() {
            @Override
            public void xOnItemClick(int position) {
                mIntent = new Intent(MainActivity.this, PlayActivity.class);
                mIntent.putExtra("position", position);
                mIntent.putExtra("category", "sdcard");
                startActivity(mIntent);
            }
        });
    }

    public void toWyMusic(View view) {
        mIntent = new Intent(this, PlayActivity.class);
        requestNet();
    }

    private void openDir() {
//        File scanFile = new File(Environment.getExternalStorageDirectory().getPath() + "/xMusic");
        File scanFile = new File(Environment.getExternalStorageDirectory().getPath());
        if (scanFile.isDirectory()) {
            for (File file : scanFile.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".mp3") || path.endsWith(".flac")) {
                    dataList.add(file.getName());
                }
            }
        }
    }
}
