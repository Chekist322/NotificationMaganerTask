package com.example.batrakov.notificationmanagertask;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * Service that monitor amount of application Notifications and create reference Notification
 * if there are not any other application Notification.
 * Created by batrakov on 19.10.17.
 */

public class ServiceForForthNotification extends IntentService {

    private int mFlagIfNotificationExist = 0;
    private static final int CHECK_DELAY = 400;
    private static final int REFERENCE_NOTIFICATION_ID = 4;
    /**
     * Constructor for defining service in manifest.
     */
    public ServiceForForthNotification() {
        super("service");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param aName Used to name the worker thread, important only for debugging.
     */
    public ServiceForForthNotification(String aName) {
        super(aName);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent aIntent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent mainActivityReferenceIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent mainActivityReferencePendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, mainActivityReferenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            while (true) {
                try {
                    Thread.sleep(CHECK_DELAY);
                } catch (InterruptedException aE) {
                    aE.printStackTrace();
                }
                if (manager.getActiveNotifications().length == mFlagIfNotificationExist) {
                    NotificationCompat.Builder mainActivityReferenceNotification =
                            new NotificationCompat.Builder(getApplicationContext());
                    mainActivityReferenceNotification.setAutoCancel(true)
                            .setContentIntent(mainActivityReferencePendingIntent)
                            .setContentTitle("Application is alive!")
                            .setContentText("Tap to open...")
                            .setCategory(NotificationCompat.CATEGORY_SERVICE)
                            .setSmallIcon(R.drawable.icon);
                    manager.notify(REFERENCE_NOTIFICATION_ID, mainActivityReferenceNotification.build());
                    mFlagIfNotificationExist = 1;
                } else {
                    manager.cancel(REFERENCE_NOTIFICATION_ID);
                    mFlagIfNotificationExist = 0;
                }
            }
        }
    }
}
