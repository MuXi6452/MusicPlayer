package com.muxi.xmusicplayer.Bean;

import java.util.ArrayList;

public class JsonRoot {
    public String code;
    public Result result;
    public class Result{
        public ArrayList<Tracks> tracks;
    }
    public class Tracks{
        public String name;
        public String id;
        public Album album;
    }
    public class Album{
        public String picUrl;
    }
}
