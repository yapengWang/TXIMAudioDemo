package com.example.tximaudiodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.example.tximaudiodemo.audiocall.CallService;
import com.example.tximaudiodemo.audiocall.TRTCAudioCallActivity;
import com.example.tximaudiodemo.audiocall.TXContents;
import com.example.tximaudiodemo.audiocall.model.ProfileManager;
import com.example.tximaudiodemo.audiocall.model.UserModel;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    Button btCall;
    boolean isLogin = false;

    String sign = TXContents.sign;
    String loginUserId = TXContents.userId;
    String loginImg = TXContents.userImg;
    String callUserId = TXContents.userId2;
    String callImg = TXContents.userImg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        login();
    }

    private void login() {
        V2TIMSDKConfig v2TIMSDKConfig = new V2TIMSDKConfig();
        v2TIMSDKConfig.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_ERROR);
        V2TIMManager.getInstance().initSDK(this, TXContents.APP_ID, v2TIMSDKConfig, new V2TIMSDKListener() {
            @Override
            public void onConnectSuccess() {
                super.onConnectSuccess();
                Log.e(TAG, "已经成功连接到腾讯云服务器");
            }

            @Override
            public void onConnectFailed(int code, String error) {
                super.onConnectFailed(code, error);
                Log.e(TAG, " 连接到腾讯云服务器失败 code->" + code + "  error->" + error);
            }
        });
        V2TIMManager.getInstance().login(loginUserId, sign, new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "登陆失败 code->" + i + "   msg->" + s);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "登陆成功");
                CallService.start(MainActivity.this);
                isLogin = true;
                //保存个人信息
                ProfileManager.getInstance().login(loginUserId, loginImg, loginUserId, sign, new ProfileManager.ActionCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed(int code, String msg) {

                    }
                });
            }
        });
    }

    private void initView() {
        btCall = (Button) findViewById(R.id.bt_call);
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin) {
                    ToastUtils.showShort("未登录");
                    return;
                }
                List<UserModel> list = new ArrayList<>();
                UserModel userModel = new UserModel();
                userModel = new UserModel();
                userModel.phone = "";
                userModel.userAvatar = callImg;
                userModel.userId = callUserId;
                userModel.userName = callUserId;
                list.add(userModel);
                TRTCAudioCallActivity.startCallSomeone(MainActivity.this, list);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallService.stop(this);
    }
}