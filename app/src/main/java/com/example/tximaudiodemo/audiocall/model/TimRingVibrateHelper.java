package com.example.tximaudiodemo.audiocall.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import com.example.tximaudiodemo.MyApplication;
import com.example.tximaudiodemo.R;

import java.io.IOException;

import androidx.annotation.RequiresApi;

import static android.content.Context.AUDIO_SERVICE;

/**
 * @author leary
 * 响铃震动帮助类
 */
public class TimRingVibrateHelper {
    private static final String TAG = TimRingVibrateHelper.class.getSimpleName();
    /**
     * =============响铃 震动相关
     */
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    private static TimRingVibrateHelper instance;

    public static TimRingVibrateHelper getInstance() {
        if (instance == null) {
            synchronized (TimRingVibrateHelper.class) {
                if (instance == null) {
                    instance = new TimRingVibrateHelper();
                }
            }
        }
        return instance;
    }

    private TimRingVibrateHelper() {
        //铃声相关
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(mp -> {
            if (mp != null) {
                mp.setLooping(true);
                mp.start();
            }
        });
    }

    /**
     * ==============响铃、震动相关方法========================
     */
    public void initLocalCallRinging() {
        try {
            AssetFileDescriptor assetFileDescriptor = MyApplication.getInstance().getResources().openRawResourceFd(R.raw.phone);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            assetFileDescriptor.close();
            // 设置 MediaPlayer 播放的声音用途
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .build();
                mMediaPlayer.setAudioAttributes(attributes);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
            mMediaPlayer.prepareAsync();
            final AudioManager am = (AudioManager) MyApplication.getInstance().getSystemService(AUDIO_SERVICE);
            if (am != null) {
                am.setSpeakerphoneOn(false);
                // 设置此值可在拨打时控制响铃音量
                am.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // 设置拨打时响铃音量默认值

                am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 8, AudioManager.STREAM_VOICE_CALL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断系统响铃正东相关设置
     * 1、系统静音 不震动 就两个都不设置
     * 2、静音震动
     * 3、只响铃不震动
     * 4、响铃且震动
     */
    public void initRemoteCallRinging() {
        int ringerMode = getRingerMode(MyApplication.getInstance());
        if (ringerMode != AudioManager.RINGER_MODE_SILENT) {
            if (ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
                startVibrator();
            } else {
                if (isVibrateWhenRinging()) {
                    startVibrator();
                }
                startRing();
            }
        }
    }

    private int getRingerMode(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        return audio.getRingerMode();
    }

    /**
     * 开始响铃
     */
    private void startRing() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        try {
            mMediaPlayer.setDataSource(MyApplication.getInstance(), uri);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Ringtone not found : " + uri);
            try {
                uri = RingtoneManager.getValidRingtoneUri(MyApplication.getInstance());
                mMediaPlayer.setDataSource(MyApplication.getInstance(), uri);
                mMediaPlayer.prepareAsync();
            } catch (Exception e1) {
                e1.printStackTrace();
                Log.e(TAG, "Ringtone not found: " + uri);
            }
        }
    }

    /**
     * 开始震动
     */
    private void startVibrator() {
        if (mVibrator == null) {
            mVibrator = (Vibrator) MyApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            mVibrator.cancel();
        }
        mVibrator.vibrate(new long[]{500, 1000}, 0);
    }

    /**
     * 判断系统是否设置了 响铃时振动
     */
    private boolean isVibrateWhenRinging() {
        ContentResolver resolver = MyApplication.getInstance().getApplicationContext().getContentResolver();
        if (Build.MANUFACTURER.equals("Xiaomi")) {
            return Settings.System.getInt(resolver, "vibrate_in_normal", 0) == 1;
        } else if (Build.MANUFACTURER.equals("smartisan")) {
            return Settings.Global.getInt(resolver, "telephony_vibration_enabled", 0) == 1;
        } else {
            return Settings.System.getInt(resolver, "vibrate_when_ringing", 0) == 1;
        }
    }

    /**
     * 停止震动和响铃
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void stopRing() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
        if (mVibrator != null) {
            mVibrator.cancel();
        }
        if (MyApplication.getInstance() != null) {
            //通话时控制音量
            AudioManager audioManager = (AudioManager) MyApplication.getInstance().getApplicationContext().getSystemService(AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    /**
     * 释放资源
     */
    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (instance != null) {
            instance = null;
        }
        // 退出此页面后应设置成正常模式，否则按下音量键无法更改其他音频类型的音量
        if (MyApplication.getInstance() != null) {
            AudioManager am = (AudioManager) MyApplication.getInstance().getApplicationContext().getSystemService(AUDIO_SERVICE);
            if (am != null) {
                am.setMode(AudioManager.MODE_NORMAL);
            }
        }
    }
}

