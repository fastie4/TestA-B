package com.fastie4.testb;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fastie4.common.Common;
import com.fastie4.common.LinkStatus;
import com.fastie4.common.db.HistoryContract;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String SAVE_STATE_BITMAP = "save_state_bitmap";
    private static final String SAVE_STATE_MESSAGE = "save_state_message";
    private static final String SAVE_STATE_TIMER = "save_state_timer";
    private static final int TIME_TO_FINISH = 10000;
    private ImageView mImage;
    private ProgressBar mProgressBar;
    private TextView mMessage;
    private boolean fromTest;
    private long mId;
    private Bitmap mBitmap;
    private CountDownTimer mTimer;
    private long mMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        mImage = findViewById(R.id.image);
        mProgressBar = findViewById(R.id.progress);
        mMessage = findViewById(R.id.message);

        mId = getIntent().getLongExtra(Common.EXTRA_ID, -1);

        if (savedInstanceState == null) {
            String link = getIntent().getStringExtra(Common.EXTRA_LINK);
            String action = getIntent().getAction();
            if (action != null && isRight(action)) {
                if (fromTest) {
                    saveLink(link, System.currentTimeMillis());
                }
                if (link == null || link.isEmpty()) {
                    showMessage(getString(R.string.message_not_correct_link));
                    return;
                }
                // Show image without caching
                Picasso.get()
                        .load(link)
                        .networkPolicy(NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(mTarget);
                return;
            }
            showNoActionAndClose(TIME_TO_FINISH);
        } else {
            restoreSavedState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mBitmap != null) {
            outState.putParcelable(SAVE_STATE_BITMAP, mBitmap);
        }
        if (mMessage.getVisibility() == View.VISIBLE) {
            outState.putString(SAVE_STATE_MESSAGE, mMessage.getText().toString());
        }
        if (mTimer != null) {
            mTimer.cancel();
            mMillis -= System.currentTimeMillis();
            outState.putLong(SAVE_STATE_TIMER, mMillis);
        }
        super.onSaveInstanceState(outState);
    }

    private void restoreSavedState(Bundle savedInstanceState) {
        mBitmap = savedInstanceState.getParcelable(SAVE_STATE_BITMAP);
        if (mBitmap != null) {
            mImage.setImageBitmap(mBitmap);
        }
        String message = savedInstanceState.getString(SAVE_STATE_MESSAGE);
        if (message != null) {
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(message);
        }
        mMillis = savedInstanceState.getLong(SAVE_STATE_TIMER);
        if (mMillis > 0) {
            showNoActionAndClose((int) mMillis);
        }
    }

    private void showMessage(String message) {
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText(message);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private final Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mProgressBar.setVisibility(View.GONE);
            mBitmap = bitmap;
            mImage.setImageBitmap(bitmap);
            updateLinkStatus(LinkStatus.LOADED);
            if (!fromTest) {
                int status = getIntent().getIntExtra(Common.EXTRA_STATUS, -1);
                if (status == LinkStatus.LOADED) {
                    saveFile(bitmap);
                    Intent intent = new Intent(MainActivity.this, DeleteService.class);
                    intent.putExtra(DeleteService.DELETE_ID, mId);
                    startService(intent);
                }
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            mProgressBar.setVisibility(View.GONE);
            updateLinkStatus(LinkStatus.ERROR);
            showMessage(getString(R.string.message_error));
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    };

    private boolean isRight(String action) {
        fromTest = action.equals(Common.ACTION_OPEN_FROM_TEST);
        return fromTest || action.equals(Common.ACTION_OPEN_FROM_HISTORY);
    }

    private void saveFile(Bitmap bitmap) {
        String link = getIntent().getStringExtra(Common.EXTRA_LINK);
        List<String> paths = Uri.parse(link).getPathSegments();
        File dir = new File(Environment.getExternalStorageDirectory() + "/BIGDIG/test/B");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Gets last path segment - filename
        File dest = new File(dir, paths.get(paths.size() - 1));
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveLink(String link, long time) {
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryEntry.COLUMN_LINK, link);
        values.put(HistoryContract.HistoryEntry.COLUMN_STATUS, LinkStatus.UNKNOWN);
        values.put(HistoryContract.HistoryEntry.COLUMN_TIME, time);
        Uri uri = getContentResolver().insert(HistoryContract.HistoryEntry.CONTENT_URI, values);
        mId = ContentUris.parseId(uri);
    }

    private void updateLinkStatus(int newStatus) {
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryEntry.COLUMN_STATUS, newStatus);
        getContentResolver().update(HistoryContract.HistoryEntry.buildHistoryUriWithId(mId), values,
                null, null);
    }

    private void showNoActionAndClose(int time) {
        mTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                showMessage(getString(R.string.message_no_action, l / 1000));
            }

            @Override
            public void onFinish() {
                mTimer = null;
                finish();
            }
        };
        mTimer.start();
        mMillis = System.currentTimeMillis() + time;
    }
}