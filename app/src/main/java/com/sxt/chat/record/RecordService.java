package com.sxt.chat.record;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.utils.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sxt on 2019/2/28.
 */
public class RecordService extends Service {

    private final String TAG = this.getClass().getName();
    public static final int NOTIFY_ID = 10086;
    private MediaRecorder mRecorder;
    private File recordFile;
    CharSequence name = "Record";
    String description = "居家工单,正在录音中...";
    public static final String RECORD_FLAG = "RECORD_FLAG";
    private int recordID;
    private Timer timer;
    private int duration_record = 70 * 60 * 1000;
    private int duration_timer = duration_record + 5 * 1000;
    private RecordTask recordTask;
    private boolean isStarting = false;
    private PhoneReceiver phoneReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "录音服务创建");
        if (phoneReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_CALL);
            intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            registerReceiver(phoneReceiver, intentFilter);
        }
    }

    public class PhoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {//去电

            } else {//来电(存在以下三种情况)
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    switch (telephonyManager.getCallState()) {
                        case TelephonyManager.CALL_STATE_IDLE://挂断
                            if (!isStarting) {
                                startRecord();//重新开启录音
                            }
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK://接听
                            break;
                        case TelephonyManager.CALL_STATE_RINGING://响铃
                            stopRecordBeForced();//强制停止录音
                            break;
                    }
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            recordID = intent.getIntExtra(RECORD_FLAG, 0);//获取本次录音的ID
        }
        Notification.Builder builder;
        Intent in = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, in, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(String.valueOf(NOTIFY_ID), name, importance);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            mChannel.setDescription("录音渠道");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new Notification.Builder(this, String.valueOf(NOTIFY_ID));
        } else {
            builder = new Notification.Builder(this);
        }
        startForeground(NOTIFY_ID, builder.setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .build());

        startRecord();

        return START_STICKY;
    }

    /**
     * 准备录音
     */
    private void startRecord() {
        initRecord();
        startRecording();
        if (timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mRecorder != null) {
                    stopRecord();//定时到了就停止录音服务
                    //TODO 处理音频的任务 应该放在上传文件的时候,这里只是方便调试
                    if (recordTask != null) recordTask.cancel(true);
                    recordTask = new RecordTask().setRecordCallBackListener(new RecordTask.RecordCallBackListener() {
                        @Override
                        public void onSuccessed(String filePath) {
                            Log.e(TAG, "onSuccessed : " + filePath);
                        }

                        @Override
                        public void onFailed(String error) {
                            Log.e(TAG, "onFailed : " + error);
                        }
                    });
                    recordTask.execute(Prefs.getInstance(App.getCtx()).getRecordFolder(), String.valueOf(recordID));
                }
            }
        }, duration_timer);
        Log.e(TAG, "录音开始...");
    }

    /**
     * 初始化录音设置
     */
    private void initRecord() {
        if (mRecorder == null) {
            File recordFolder = new File(Prefs.getInstance(App.getCtx()).getRecordFolder());
            if (!recordFolder.exists()) recordFolder.mkdirs();
            //每次的录音 唯一标识为 :  工单id + 时间戳 , 方便上传的时候拼接为一个录音文件
            String record_ID_CARD = String.format("%s-%s", recordID, System.currentTimeMillis());
            recordFile = new File(recordFolder + File.separator + record_ID_CARD + ".mp4");
            mRecorder = new MediaRecorder();
            if (recordTask != null) recordTask.cancel(true);

            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //录音文件保存的格式，这里保存为 mp4
            mRecorder.setOutputFile(recordFile.getPath()); // 设置录音文件的保存路径
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioChannels(1);
            //设置录音文件的清晰度
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
            mRecorder.setMaxDuration(duration_record);
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i1) {
                    stopService();
                    Log.e(TAG, "录制出错,结束录音");
                }
            });
        }
    }

    /**
     * 开启录音
     */
    private void startRecording() {
        initRecord();
        try {
            mRecorder.prepare();
            mRecorder.start();
            isStarting = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 被迫停止录音服务
     */
    private void stopRecordBeForced() {
        if (timer != null) {
            timer.cancel();
        }
        stopRecording();
    }

    /**
     * 正常停止录音服务
     */
    private void stopRecord() {
        stopRecording();
        stopService();
    }

    /**
     * 停止录音
     */
    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            isStarting = false;
            Log.e(TAG, "录音结束");
        }
    }

    @Override
    public void onDestroy() {
        stopRecord();
        if (timer != null) timer.cancel();
        if (phoneReceiver != null) {
            unregisterReceiver(phoneReceiver);
        }
        super.onDestroy();
    }

    private void stopService() {
        stopForeground(true);
        stopSelf();
    }
}
