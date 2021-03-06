package com.example.jack.phonelistener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jack.phonelistener.bean.Calllog;
import com.example.jack.phonelistener.dao.CalllogDao;
import com.example.jack.phonelistener.dao.CalllogDaoImpl;
import com.example.jack.phonelistener.helper.Macro;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private CalllogDao calllogDao;
    private ListView lv_files;
    private List<Calllog> calllogList;
    private RecordingAdapter adapter;
    private ProgressBar pb;
    private Button btn_play;
    private Button btn_stop;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private TextView tv_calllog_info;
    private Button btn_rewind;
    private Button btn_fast;
    private SimpleDateFormat simpleDateFormat;
    private boolean isGoingToPlay = false;
    private static Calllog newCalllog;

    private PhoneService.PhoneBinder mBinder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (PhoneService.PhoneBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        calllogList = new ArrayList<>();
//        calllogList.add(new Calllog("123", 123, 123, "file1"));
//        calllogList.add(new Calllog("1231", 123, 123, "file2"));
//
//        Log.d("phonelistener_tag", "calllogList.contains(): " + calllogList.contains(new Calllog("123", 123, 123, "file1")));

//        Calllog c1 = new Calllog("123", 123, 123, "file1");
//        Calllog c2 = new Calllog("123", 123, 123, "file1");
//        Log.d("phonelistener_tag", "c1.hashCode(): " + c1.hashCode() + ", c1.toString(): " + c1.toString());
//        Log.d("phonelistener_tag", "c2.hashCode(): " + c2.hashCode() + ", c2.toString(): " + c2.toString());


        Intent service = new Intent(MainActivity.this, PhoneService.class);
        bindService(service, conn, BIND_AUTO_CREATE);

        calllogDao = new CalllogDaoImpl(this);
//        calllogDao.insert(new Calllog("15716017670", System.currentTimeMillis(), 10000, "1499083327595.3gp"));

        final File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Recording");
        if (!root.exists()) {
            root.mkdir();
        }
        calllogList = calllogDao.query();

        lv_files = (ListView) findViewById(R.id.lv_files);
        pb = (ProgressBar) findViewById(R.id.pb);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_rewind = (Button) findViewById(R.id.btn_rewind);
        btn_fast = (Button) findViewById(R.id.btn_fast);
        tv_calllog_info = (TextView) findViewById(R.id.tv_calllog_info);

        btn_play.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_rewind.setOnClickListener(this);
        btn_fast.setOnClickListener(this);

        btn_play.setEnabled(false);
        btn_stop.setEnabled(false);
        btn_fast.setEnabled(false);
        btn_rewind.setEnabled(false);

        handler = new Handler();

        adapter = new RecordingAdapter(this, calllogList);
        lv_files.setAdapter(adapter);

        simpleDateFormat = new SimpleDateFormat("mm:ss");

        lv_files.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除通话记录");
                builder.setMessage("确定删除这条记录吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calllog calllog = calllogList.get(position);
                        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Recording", calllog.getFile());
                        calllogDao.delete(calllog.getId());
                        calllogList.remove(position);
                        adapter.notifyDataSetChanged();
                        if (file.delete()) {
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "取消删除", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
                return true;
            }
        });

        lv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();
//                Log.d("phonelistener_tag", "position: " + position + ", " + "selectItem: " + adapter.getSelectItem());
                Message msg = Message.obtain();
                msg.what = Macro.MEDIAPLAYER_STATE_PLAY;
                handler.sendMessage(msg);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    handler.removeCallbacks(runnable);
                }

                mediaPlayer = new MediaPlayer();
                try {
                    Log.d("phonelistener_tag", "dataSource: " + root+File.separator+ calllogList.get(position).getFile());
                    mediaPlayer.setDataSource(root+File.separator+ calllogList.get(position).getFile());
//                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory()+File.separator+"Ghost Town-Adam Lambert.mp3");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
//                    updateProgress();

                    TextView viewById = (TextView) parent.findViewById(R.id.tv_phone);
                    viewById.setTextColor(Color.BLUE);
                    TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
                    tv_phone.setTextColor(Color.RED);

                    runnable = new Runnable() {
                        private int progress;
                        int timestamps;

                        @Override
                        public void run() {

                            if (!mediaPlayer.isPlaying() && progress==99) {
                                isGoingToPlay = true;
                                progress = 0;
                                timestamps = 0;
                                btn_play.setBackgroundResource(R.drawable.selector_play);
                                btn_stop.setEnabled(false);
                                btn_rewind.setEnabled(false);
                                btn_fast.setEnabled(false);
                                tv_calllog_info.setText(simpleDateFormat.format(new Date(timestamps)) + " 播放完毕：" + calllogList.get(position).getFile());
                                handler.removeCallbacks(this);
                            }

                            if (mediaPlayer.isPlaying()) {
                                progress = (int) ((mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration()) * 100);
                                timestamps = mediaPlayer.getCurrentPosition();
                                tv_calllog_info.setText(simpleDateFormat.format(new Date(timestamps)) + " 正在播放：" + calllogList.get(position).getFile());
                                btn_play.setBackgroundResource(R.drawable.selector_pause);
                                btn_play.setEnabled(true);
                                btn_stop.setEnabled(true);
                                btn_fast.setEnabled(true);
                                btn_rewind.setEnabled(true);
                                btn_fast.setEnabled(true);
                            }

                            handler = new Handler() {

                                private int currentPosition;

                                @Override
                                public void handleMessage(Message msg) {
                                    switch (msg.what) {
                                        case Macro.MEDIAPLAYER_STATE_PAUSE:
            //                                Toast.makeText(MainActivity.this, "pause", Toast.LENGTH_SHORT).show();
            //                                btn_play.setEnabled(true);
            //                                btn_pause.setEnabled(false);
            //                                isGoingToPlay = false;
            //                                btn_stop.setEnabled(false);
                                            mediaPlayer.pause();
                                            tv_calllog_info.setText(simpleDateFormat.format(timestamps) + " 暂停播放：" + calllogList.get(position).getFile());
                                            btn_play.setEnabled(true);
                                            btn_stop.setEnabled(true);
                                            btn_rewind.setEnabled(false);
                                            btn_fast.setEnabled(false);
                                            btn_play.setBackgroundResource(R.drawable.selector_play);
                                            handler.removeCallbacks(runnable);
                                            break;
                                        case Macro.MEDIAPLAYER_STATE_STOP:
                                            progress = 0;
                                            timestamps = 0;
                                            btn_stop.setEnabled(false);
                                            btn_play.setEnabled(true);
                                            btn_rewind.setEnabled(false);
                                            btn_fast.setEnabled(false);
                                            pb.setProgress(progress);
                                            mediaPlayer.seekTo(progress);
                                            mediaPlayer.pause();
                                            adapter.notifyDataSetInvalidated();
                                            tv_calllog_info.setText(simpleDateFormat.format(timestamps) + " 播放完毕：" + calllogList.get(position).getFile());
                                            btn_play.setBackgroundResource(R.drawable.selector_play);
                                            handler.removeCallbacks(runnable);
                                            break;
                                        case Macro.MEDIAPLAYER_STATE_REWIND:
                                            currentPosition = mediaPlayer.getCurrentPosition();
                                            mediaPlayer.seekTo(currentPosition -5000);
                                            break;
                                        case Macro.MEDIAPLAYER_STATE_FAST:
                                            currentPosition = mediaPlayer.getCurrentPosition();
                                            mediaPlayer.seekTo(currentPosition +5000);
                                            break;
                                    }
                                }
                            };

                            pb.setProgress(progress);
                            handler.postDelayed(this, 1);
                        }
                    };
                    handler.post(runnable);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void updateProgress() {
//                runnable = new Runnable() {
//
//                    private int progress;
//
//                    @Override
//                    public void run() {
//                        if (mediaPlayer.isGoingToPlay()) {
//                            handler.postDelayed(this, 1);
//                            progress = (int) ((mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration()) * 100);
//                        }
//                        if (progress==99 && !mediaPlayer.isGoingToPlay()) {
//                            mediaPlayer.stop();
//                            mediaPlayer.release();
//                            mediaPlayer = null;
//                            progress = 0;
//                            handler.removeCallbacks(this);
//                        }
//                        if (handler.hasMessages(Macro.MEDIAPLAYER_STATE_PAUSE)) {
////                                Toast.makeText(MainActivity.this, "pause", Toast.LENGTH_SHORT).show();
//                            btn_play.setEnabled(true);
//                            btn_pause.setEnabled(false);
//                            btn_stop.setEnabled(false);
//                            mediaPlayer.pause();
//                            handler.removeCallbacks(this);
//                        }
//                        if (handler.hasMessages(Macro.MEDIAPLAYER_STATE_STOP)) {
////                                Toast.makeText(MainActivity.this, "stop", Toast.LENGTH_SHORT).show();
//                            btn_stop.setEnabled(false);
//                            btn_pause.setEnabled(false);
//                            btn_play.setEnabled(true);
//                            progress = 0;
//                            mediaPlayer.seekTo(progress);
//                            mediaPlayer.pause();
//                            handler.removeCallbacks(this);
//                        }
//                        if (handler.hasMessages(Macro.MEDIAPLAYER_STATE_BACK)) {
//                            int currentPosition = mediaPlayer.getCurrentPosition();
//                            mediaPlayer.seekTo(currentPosition-5000);
//                        }
//                        if (handler.hasMessages(Macro.MEDIAPLAYER_STATE_FORWARD)) {
//                            int currentPosition = mediaPlayer.getCurrentPosition();
//                            mediaPlayer.seekTo(currentPosition+5000);
//                        }
//                        pb.setProgress(progress);
//                    }
//                };
//                handler.post(runnable);
            }

        });

    }

    @Override
    protected void onStart() {
        Log.d("phonelistener_tag", "---------- onStart ----------");
        super.onStart();
        if (mBinder == null) {
            return;
        }
        boolean isOffhook = mBinder.getService().isOffhook();
        long rowId = mBinder.getService().getId();
        Log.d("phonelistener_tag", "onStart, isOffhook: " + isOffhook);
        Log.d("phonelistener_tag", "onStart, rowId: " + rowId);
        if (isOffhook && rowId!=0) {
//        if (mBinder != null) {
//            Log.d("phonelistener_tag", "mBinder.getService().isOffhook(): " + mBinder.getService().isOffhook());
            newCalllog = calllogDao.query(rowId);
            Log.d("phonelistener_tag", "mBinder.getService().getId(): " + rowId);
            Log.d("phonelistener_tag", "newCalllog: " + newCalllog);
//            Log.d("phonelistener_tag", "\t--- before adding newCalllog ---");
//            for (int i = 0; i < calllogList.size(); i++) {
//                Log.d("phonelistener_tag", "hashCode: " + calllogList.get(i).hashCode() + ", toString: " + calllogList.get(i).toString());
//            }
//            Log.d("phonelistener_tag", "\t--- end before adding newCalllog ---");
            if (!calllogList.contains(newCalllog)) {
                calllogList.add(newCalllog);
            }
//            Log.d("phonelistener_tag", "\t--- after adding newCalllog ---");
//            for (int i = 0; i < calllogList.size(); i++) {
//                Log.d("phonelistener_tag", "hashCode: " + calllogList.get(i).hashCode() + ", toString: " + calllogList.get(i).toString());
//            }
//            Log.d("phonelistener_tag", "\t--- end after adding newCalllog ---");
            adapter.notifyDataSetChanged();
//            Log.d("phonelistener_tag", "mBinder.getService().getId(): " + mBinder.getService().getId());
//            Log.d("phonelistener_tag", "mBinder.doSomething(): " + mBinder.doSomething());
//            Log.d("phonelistener_tag", "newCalllog: " + newCalllog);
//            Log.d("phonelistener_tag", "calllogList.size(): " + calllogList.size());
        }
        Log.d("phonelistener_tag", "---------- onStart ----------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (isGoingToPlay) {
                    mediaPlayer.start();
                    handler.postDelayed(runnable, 1);
                    isGoingToPlay = false;
                } else {
                    handler.sendEmptyMessage(Macro.MEDIAPLAYER_STATE_PAUSE);
                    isGoingToPlay = true;
                }
                break;
            case R.id.btn_stop:
                isGoingToPlay = true;
                handler.sendEmptyMessage(Macro.MEDIAPLAYER_STATE_STOP);
                break;
            case R.id.btn_rewind:
                handler.sendEmptyMessage(Macro.MEDIAPLAYER_STATE_REWIND);
                break;
            case R.id.btn_fast:
                handler.sendEmptyMessage(Macro.MEDIAPLAYER_STATE_FAST);
                break;
        }
//        Log.d("phonelistener_tag", "isGoingToPlay: " + isGoingToPlay);
    }
}
