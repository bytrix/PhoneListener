package com.example.jack.phonelistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.jack.phonelistener.bean.Calllog;
import com.example.jack.phonelistener.dao.CalllogDao;
import com.example.jack.phonelistener.dao.CalllogDaoImpl;

import java.io.File;

public class PhoneReceiver extends BroadcastReceiver {

    private CalllogDao calllogDao;
//    private String[] files;
//    private List<Calllog> calllogList;
//    boolean singleCallState;
//    private static String mLastState;
    private long lastCreateTime;

    public PhoneReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

//        Toast.makeText(context, "OnReceive...", Toast.LENGTH_SHORT).show();
        Log.d("phonelistener_tag", "onReceive");
//        singleCallState = false;
        String mState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//        Log.d("phonelistener_tag", "state: " + mState + ", lastState: " + mLastState);
//        if (mState.equals(mLastState)) {
//            return;
//        }
//        if (true) {
            calllogDao = new CalllogDaoImpl(context);

            final File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Recording");
            if (!root.exists()) {
                root.mkdir();
            }
//        files = root.list();
//        calllogList = calllogDao.query();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            private long startTime;
            private long endTime;
            private File file;
            private MediaRecorder mediaRecorder;
            private String incomingNumber;
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
//                if (incomingNumber == null) {
//                    return;
//                }
                lastCreateTime = System.currentTimeMillis();
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
//                        singleCallState = true;
                        Log.d("phonelistener_tag", "RINGING");
                        this.incomingNumber = incomingNumber;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
//                        long current = System.currentTimeMillis();
                        startTime = System.currentTimeMillis();
                        if (this.incomingNumber == null) {
                            return;
                        }
                        Log.i("phonelistener_tag", "current: " + System.currentTimeMillis());
                        Log.i("phonelistener_tag", "lastCreateTime: " + lastCreateTime);
//                        if (current-lastCreateTime < 1000 && current!=lastCreateTime) {
//                            return;
//                        }
                        Log.d("phonelistener_tag", "OFFHOOK, incomingNumber: " + this.incomingNumber);
//                        if (singleCallState) {
                            Toast.makeText(context, "正在录音。。。", Toast.LENGTH_SHORT).show();
                            String filename = System.currentTimeMillis() + ".3gp";
                            file = new File(root.getAbsoluteFile(), filename);
                            try {
                                mediaRecorder = new MediaRecorder();
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                mediaRecorder.setOutputFile(file.getAbsolutePath());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d("phonelistener_tag", "IDLE");
//                        singleCallState = true;
                        if (mediaRecorder != null) {
                            try {
                                mediaRecorder.stop();
                                mediaRecorder.release();
                                mediaRecorder = null;
                                endTime = System.currentTimeMillis();
                                Calllog calllog = new Calllog(this.incomingNumber, startTime, endTime-startTime, file.getName());
                                Log.i("phonelistener_tag", "begin to insert...");
                                long rowId = calllogDao.insert(calllog);
                                Log.i("phonelistener_tag", "insert over, rowId: " + rowId);
                                Toast.makeText(context, "录音结束，文件保存至：" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                            files = root.list();
//                            lv_files.deferNotifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


//        }
    }
}
