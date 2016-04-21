package com.example.jinchen.diarytest;

/**
 * Created by JinChen on 2016/4/3.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
