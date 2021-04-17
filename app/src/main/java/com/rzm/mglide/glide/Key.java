package com.rzm.mglide.glide;

import com.rzm.mglide.glide.util.Tool;

public class Key {

    private String key;

    public Key(String path) { // https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg
        this.key = Tool.getSHA256StrJava(path);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
