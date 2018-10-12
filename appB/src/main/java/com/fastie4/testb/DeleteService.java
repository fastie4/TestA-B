package com.fastie4.testb;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.fastie4.common.db.HistoryContract;

public class DeleteService extends Service {
    public static final String DELETE_ID = "id_to_delete";
    private static final int DELETING_DELAY = 15000;

    public DeleteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final long id = intent.getLongExtra(DELETE_ID, -1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = getContentResolver().delete(HistoryContract.HistoryEntry.buildHistoryUriWithId(id),
                        null, null);
                if (count > 0) {
                    Toast.makeText(DeleteService.this, R.string.message_link_deleted,
                            Toast.LENGTH_SHORT).show();
                }
                stopSelf(startId);
            }
        }, DELETING_DELAY);
        // Redeliver intent when app will be terminate
        return START_REDELIVER_INTENT;
    }
}