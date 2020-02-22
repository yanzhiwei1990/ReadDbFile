package www.opendiylib.readdbfile;

import android.app.Activity;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import www.opendiylib.readdbfile.Sqlite.SqliteManager;
import www.opendiylib.readdbfile.Sqlite.TvData;


public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static int MSG_CHANNEL_ADD= 1;
    private final static int MSG_CHANNEL_REMOVE = 2;
    private final static int MSG_PROGRAM_ADD = 3;
    private final static int MSG_PROGRAM_REMOVE = 4;
    private final static int MSG_OVER= 5;

    private Handler mHandler = null;
    private SqliteManager mSqliteManager = null;
    private TvData mTvData = null;
    private Button mChannelAdd;
    private Button mChannelRemove;
    private Button mProgramAdd;
    private Button mProgramRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MainActivityHandler(this);
        mSqliteManager = new SqliteManager();
        mSqliteManager.openDb(SqliteManager.DB_PATH/*this.getDatabasePath("tv.db").getAbsolutePath()*/);
        mTvData = new TvData(this);

        mChannelAdd = (Button)findViewById(R.id.channel_add);
        mChannelAdd.setOnClickListener(mOnClickListener);
        //mChannelAdd.setOnLongClickListener(mOnLongClickListener);
        mChannelRemove = (Button)findViewById(R.id.channel_remove);
        mChannelRemove.setOnClickListener(mOnClickListener);
        //mChannelRemove.setOnLongClickListener(mOnLongClickListener);

        mProgramAdd = (Button)findViewById(R.id.program_add);
        mProgramAdd.setOnClickListener(mOnClickListener);
        //mProgramAdd.setOnLongClickListener(mOnLongClickListener);
        mProgramRemove = (Button)findViewById(R.id.program_remove);
        mProgramRemove.setOnClickListener(mOnClickListener);
        //mProgramRemove.setOnLongClickListener(mOnLongClickListener);
    }

    private View.OnClickListener mOnClickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick " + v);
            switch (v.getId()) {
                case R.id.channel_add:
                    mHandler.sendEmptyMessage(MSG_CHANNEL_ADD);
                    break;
                case R.id.channel_remove:
                    mHandler.sendEmptyMessage(MSG_CHANNEL_REMOVE);
                    break;
                case R.id.program_add:
                    mHandler.sendEmptyMessage(MSG_PROGRAM_ADD);
                    break;
                case R.id.program_remove:
                    mHandler.sendEmptyMessage(MSG_PROGRAM_REMOVE);
                    break;
            }
            mHandler.sendEmptyMessage(MSG_OVER);
        }
    };

    /*private View.OnLongClickListener mOnLongClickListener =  new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "onLongClick " + v);
            return true;
        }
    };*/

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown " + event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp " + event);
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyLongPress " + event);
        return super.onKeyLongPress(keyCode, event);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSqliteManager.closeDb();
    }

    private static class MainActivityHandler extends WeakHandler<MainActivity> {

        private MainActivityHandler(MainActivity mainActivity) {
            super(mainActivity);
        }

        @Override
        protected void handleMessage(Message msg, MainActivity referent) {
            switch (msg.what) {
                case MSG_CHANNEL_ADD:
                    referent.dealTvChannelsUpdate();
                    break;
                case MSG_CHANNEL_REMOVE:
                    referent.deleteTvChannels();
                    break;
                case MSG_PROGRAM_ADD:
                    referent.dealTvProgramslUpdate();
                    break;
                case MSG_PROGRAM_REMOVE:
                    referent.deleteTvPrograms();
                    break;
                case MSG_OVER:
                    referent.dealOver();
                    referent.showMessage("done!");
                    break;
                default:
                    break;
            }
        }
    }

    private void dealTvChannelsUpdate() {
        Log.d(TAG, "dealTvChannelsUpdate start");
        Cursor cursor = mSqliteManager.queryData(TvData.PATH_CHANNEL, TvData.CHANNEL_BASE_COLUMNS, null, null);
        TvData.TvChannel tvChannel = mTvData.getTvChannel();
        List<TvData.TvChannel> channels = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                channels.add(tvChannel.fromCursor(cursor));
            }
        }
        if (channels != null && channels.size() > 0) {
            mTvData.syncTvChannels(channels);
        }
        Log.d(TAG, "dealTvChannelsUpdate end " + channels.size());
    }

    private void deleteTvChannels() {
        if (mTvData != null) {
            mTvData.deleteTvChannels();
        }
    }

    private void dealTvProgramslUpdate() {
        Log.d(TAG, "dealTvProgramslUpdate start");
        Cursor cursor = mSqliteManager.queryData(TvData.PATH_PROGRAM, TvData.PROGRAM_BASE_COLUMNS, null, null);
        TvData.TvProgram tvProgram = mTvData.getTvProgram();
        List<TvData.TvProgram> programs = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                programs.add(tvProgram.fromCursor(cursor));
            }
        }
        if (programs != null && programs.size() > 0) {
            mTvData.syncTvPrograms(programs);
        }
        Log.d(TAG, "dealTvProgramslUpdate end " + programs.size());
    }

    private void deleteTvPrograms() {
        if (mTvData != null) {
            mTvData.deleteTvPrograms();
        }
    }

    private void dealOver() {
        Log.d(TAG, "dealOver");
    }

    private void showMessage(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }
    /*
    *logcat -s MainActivity TvData SqliteManager AndroidRuntime
     */
}
