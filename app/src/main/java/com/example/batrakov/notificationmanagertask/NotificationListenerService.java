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
public class NotificationListenerService extends IntentService {

    private static final int CHECK_DELAY = 400;
    private static final int REFERENCE_NOTIFICATION_ID = 4;
    /**
     * Constructor for defining service in manifest.
     */
    public NotificationListenerService() {
        super("service");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param aName Used to name the worker thread, important only for debugging.
     */
    public NotificationListenerService(String aName) {
        super(aName);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent aIntent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
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
                } else if (manager.getActiveNotifications().length > 1){
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
