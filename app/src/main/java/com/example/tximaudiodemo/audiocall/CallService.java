package com.example.tximaudiodemo.audiocall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ServiceUtils;
import com.example.tximaudiodemo.R;
import com.example.tximaudiodemo.audiocall.model.ITRTCAudioCall;
import com.example.tximaudiodemo.audiocall.model.TRTCAudioCallImpl;
import com.example.tximaudiodemo.audiocall.model.TRTCAudioCallListener;
import com.example.tximaudiodemo.audiocall.model.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;

public class CallService extends Service {
    public static final String TAG = CallService.class.getName();
    private static final int NOTIFICATION_ID = 1001;

    private ITRTCAudioCall mITRTCAudioCall;
    private TRTCAudioCallListener mTRTCAudioCallListener = new TRTCAudioCallListener() {
        // <editor-fold  desc="音频监听代码">
        @Override
        public void onError(int code, String msg) {
        }

        @Override
        public void onInvited(String sponsor, final List<String> userIdList, boolean isFromGroup, int callType) {
            Log.e(TAG, "sponsor" + sponsor);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserModel model = new UserModel();
                    model.userAvatar = TextUtils.equals(TXContents.userId,sponsor)?TXContents.userImg:TXContents.userImg2;
                    model.userName = sponsor;
                    model.phone = "";
                    model.userId = sponsor;
                    TRTCAudioCallActivity.startBeingCall(CallService.this, model, new ArrayList<UserModel>());
                }
            }).start();
        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {
        }

        @Override
        public void onUserEnter(String userId) {
        }

        @Override
        public void onUserLeave(String userId) {
        }

        @Override
        public void onReject(String userId) {
        }

        @Override
        public void onNoResp(String userId) {
        }

        @Override
        public void onLineBusy(String userId) {
        }

        @Override
        public void onCallingCancel() {
        }

        @Override
        public void onCallingTimeout() {
        }

        @Override
        public void onCallEnd() {
        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {
        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
        }
        // </editor-fold>
    };

    public static void start(Context context) {
        if (ServiceUtils.isServiceRunning(CallService.class)) {
            return;
        }
        Log.e(TAG, "开启语音通知");
        Intent starter = new Intent(context, CallService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(starter);
        } else {
            context.startService(starter);
        }
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, CallService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取服务通知
        Notification notification = createForegroundNotification();
        //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
        startForeground(NOTIFICATION_ID, notification);
        mITRTCAudioCall = TRTCAudioCallImpl.sharedInstance(this);
        mITRTCAudioCall.init();
        mITRTCAudioCall.addListener(mTRTCAudioCallListener);

        mITRTCAudioCall.login(TXContents.APP_ID, TXContents.userId, TXContents.sign
                , new ITRTCAudioCall.ActionCallBack() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.e(TAG, "语音登录失败 code: " + code + " msg-> " + msg);
                    }

                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "语音登录成功");
                    }
                });
    }

    private Notification createForegroundNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 唯一的通知通道的id.
        String notificationChannelId = "notification_channel_id_01";

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "TRTC Foreground Service Notification";
            //通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("Channel description");
            //震动
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
        //通知小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //通知标题
        builder.setContentTitle(getString(R.string.app_name));
        //通知内容
        builder.setContentText("正在运行中");
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis());

        //创建通知并返回
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mITRTCAudioCall != null) {
            mITRTCAudioCall.removeListener(mTRTCAudioCallListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
