package com.example.batrakov.notificationmanagertask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Service that monitor amount of application Notifications and create reference Notification
 * if there are not any other application Notification.
 * Created by batrakov on 19.10.17.
 */
public class NotificationListenerService extends Service {

    private static final int CHECK_DELAY = 400;
    private static final int REFERENCE_NOTIFICATION_ID = 4;

    /**
     * Flag means that service is already running.
     */
    private boolean mAlive = false;

    /**
     * Constructor for defining service in manifest.
     */
    public NotificationListenerService() {
        super();
    }

    @Override
    public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
        if (!mAlive) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startReceiveNotifications();
                }
            }).start();
        }
        return super.onStartCommand(aIntent, aFlags, aStartId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent aIntent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent aRootIntent) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        stopSelf();
        super.onTaskRemoved(aRootIntent);
    }

    /**
     * Start receiving amount of application notifications and build reference Notification if amount is 0.
     */
    protected void startReceiveNotifications() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mAlive = true;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent mainActivityReferenceIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent mainActivityReferencePendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, mainActivityReferenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mainActivityReferenceNotificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext());
            mainActivityReferenceNotificationBuilder.setAutoCancel(true)
                    .setContentIntent(mainActivityReferencePendingIntent)
                    .setContentTitle(getString(R.string.app_is_alive))
                    .setContentText(getString(R.string.tap_to_open))
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.icon);

            while (true) {
                try {
                    Thread.sleep(CHECK_DELAY);
                } catch (InterruptedException aE) {
                    aE.printStackTrace();
                }
                if (manager.getActiveNotifications().length == 0) {
                    manager.notify(REFERENCE_NOTIFICATION_ID, mainActivityReferenceNotificationBuilder.build());
                } else if (manager.getActiveNotifications().length > 1) {
                    for (int i = 0; i < manager.getActiveNotifications().length; i++) {
                        if (manager.getActiveNotifications()[i].getId() == REFERENCE_NOTIFICATION_ID) {
                            manager.cancel(REFERENCE_NOTIFICATION_ID);
                        }
                    }
                }
            }
        }
    }
}
