package com.example.batrakov.notificationmanagertask;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Main application activity. Represent main screen and utility views which control notifications.
 * Control Notifications and AsyncTask that represent work shown in first Notification.
 * Allow to start {@link SecondNotificationActivity} that represents list of second Notification messages.
 * Download image from resources and save it to external storage for next manipulation by third Notification.
 */
public class MainActivity extends AppCompatActivity {

    private Button mFirstButton;
    private Button mSecondButton;
    private Button mThirdButton;
    private CheckBox mFirstCheckBox;
    private CheckBox mSecondCheckBox;
    private EditText mEditText;
    private NotificationCompat.Builder mFirstNotificationBuilder;
    private File mImage;
    private NotificationManager mNotificationManager;
    private ArrayList<String> mSecondNotificationContent;
    private FirstNotificationTask mFirstNotificationTask;
    private NotificationCompat.InboxStyle mInboxStyle;
    private NotificationCompat.MessagingStyle mMessagingStyle;
    private int mSecondNotificationMessagesCounter;

    /**
     * Flag for transfer ArrayList of Strings between activities.
     */
    public static final String SECOND_NOTIFICATION_CONTENT = "second content";

    private static final String CANCEL_TASK = "cancel_task";
    private static final int FIRST_NOTIFICATION_ID = 0;
    private static final int SECOND_NOTIFICATION_ID = 1;
    private static final int THIRD_NOTIFICATION_ID = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private static final int COMPRESS_CONST = 100;
    private static final int SPAN_FROM = 0;
    private static final int SPAN_TO = 8;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);


        mImage = new File(Environment.getExternalStorageDirectory().toString(), "testNotificationImage.jpg");
        if (!mImage.exists()) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.kek);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(mImage);
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_CONST, fileOutputStream);
            } catch (IOException aE) {
                aE.printStackTrace();
            }
        }

        mInboxStyle = new NotificationCompat.InboxStyle();
        mMessagingStyle = new NotificationCompat.MessagingStyle("Tosya");
        mSecondNotificationMessagesCounter = 0;
        mSecondNotificationContent = new ArrayList<>();


        mFirstButton = (Button) findViewById(R.id.first_button);
        mSecondButton = (Button) findViewById(R.id.second_button);
        mThirdButton = (Button) findViewById(R.id.third_button);
        mFirstCheckBox = (CheckBox) findViewById(R.id.first_check_box);
        mSecondCheckBox = (CheckBox) findViewById(R.id.second_check_box);
        mEditText = (EditText) findViewById(R.id.edit_text);

        changeFirstNotificationAndButton();
        changeSecondNotificationAndButton();
        changeThirdNotificationAndButton();

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence aCharSequence, int aStart, int aCount, int aAfter) {
            }

            @Override
            public void onTextChanged(CharSequence aCharSequence, int aStart, int aBefore, int aCount) {
                if (aCharSequence.toString().isEmpty()) {
                    mSecondButton.setEnabled(false);
                } else {
                    mSecondButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable aEditable) {
            }
        });

        Intent listenerServiceIntent = new Intent(this, NotificationListenerService.class);
        startService(listenerServiceIntent);
    }

    @Override
    protected void onNewIntent(Intent aIntent) {
        if (aIntent.getAction() != null) {
            switch (aIntent.getAction()) {
                case CANCEL_TASK:
                    mFirstNotificationTask.cancel(false);
                    mNotificationManager.cancel(FIRST_NOTIFICATION_ID);
                    break;
                default:
                    break;
            }
        }
        super.onNewIntent(aIntent);
    }

    /**
     * Build and update first depending on first CheckButton state.
     * Set actions for buttons and launch it by NotificationManager.
     */
    private void buildFirstNotification() {

        Intent mainActivityIntent = new Intent(this, MainActivity.class);

        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0,
                mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent cancelTaskIntent = new Intent(this, MainActivity.class);
        cancelTaskIntent.setAction(CANCEL_TASK);

        PendingIntent cancelTaskPendingIntent = PendingIntent.getActivity(this, 0,
                cancelTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (mFirstCheckBox.isChecked()) {
            mFirstNotificationBuilder = new NotificationCompat.Builder(this);
            mFirstNotificationBuilder.setSmallIcon(R.drawable.number_one)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setOngoing(true)
                    .setContentIntent(mainActivityPendingIntent)
                    .addAction(0, getResources().getString(R.string.cancel), cancelTaskPendingIntent);
        } else {
            mFirstNotificationBuilder = new NotificationCompat.Builder(this);
            mFirstNotificationBuilder.setSmallIcon(R.drawable.number_one)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOngoing(true)
                    .setContentIntent(mainActivityPendingIntent)
                    .addAction(0, getResources().getString(R.string.cancel), cancelTaskPendingIntent);
        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(FIRST_NOTIFICATION_ID, mFirstNotificationBuilder.build());
    }

    /**
     * Change first Notification and Button depending on AsyncTask state.
     */
    private void changeFirstNotificationAndButton() {

        if (mFirstNotificationTask != null && mFirstNotificationTask.isInProgress()) {
            mFirstNotificationTask.setListener(this);
            buildFirstNotification();
            mFirstCheckBox.setEnabled(false);
            mFirstButton.setText(getResources().getString(R.string.cancel));

            mFirstButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View aView) {
                    mFirstNotificationTask.cancel(false);
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(FIRST_NOTIFICATION_ID);
                }
            });
        } else {
            mFirstCheckBox.setEnabled(true);
            mFirstButton.setText(getResources().getString(R.string.start));
            mFirstNotificationTask = new FirstNotificationTask(this);

            mFirstButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View aView) {
                    mFirstNotificationTask.execute();
                }
            });
        }
    }

    /**
     * AsyncTask for imitating work that represents in first Notification.
     * Update progress bar in first Notification.
     */
    private static class FirstNotificationTask extends AsyncTask<Void, Void, Void> {
        private boolean mInProgress;
        private WeakReference<MainActivity> mReference;
        private int mCounter;

        private static final int WORK_DURATION = 10;
        private static final int WORK_DELAY = 1000;


        /**
         * Constructor.
         *
         * @param aMainActivity reference to current Activity.
         */
        FirstNotificationTask(MainActivity aMainActivity) {
            mReference = new WeakReference<>(aMainActivity);
        }

        @Override
        protected void onCancelled() {
            mReference.get().mNotificationManager.cancel(FIRST_NOTIFICATION_ID);
            mReference.get().changeFirstNotificationAndButton();
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            mInProgress = true;
            mReference.get().changeFirstNotificationAndButton();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mInProgress = false;
            mReference.get().mNotificationManager.cancel(FIRST_NOTIFICATION_ID);
            mReference.get().changeFirstNotificationAndButton();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... aParams) {
            mCounter = 1;
            while (mCounter < WORK_DURATION) {
                if (isCancelled()) {
                    mInProgress = false;
                    break;
                }
                publishProgress();
                mCounter++;
                try {
                    Thread.sleep(WORK_DELAY);
                } catch (InterruptedException aE) {
                    aE.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... aValues) {
            mReference.get().mFirstNotificationBuilder
                    .setContentTitle(mReference.get().getResources().getString(R.string.task_in_progress))
                    .setContentText(mReference.get().getResources().getString(R.string.remaining_time)
                            + String.valueOf(WORK_DURATION - getCounter()) + " sec")
                    .setSubText(String.valueOf(getCounter() * WORK_DURATION) + "%: done")
                    .setProgress(WORK_DURATION, getCounter(), false);

            mReference.get().mNotificationManager.notify(FIRST_NOTIFICATION_ID,
                    mReference.get().mFirstNotificationBuilder.build());
        }

        /**
         * Check if work in progress.
         *
         * @return work state.
         */
        boolean isInProgress() {
            return mInProgress;
        }

        /**
         * Get progress counter.
         *
         * @return progress counter.
         */
        int getCounter() {
            return mCounter;
        }

        /**
         * Set new reference to Activity if previous was destroyed.
         *
         * @param aMainActivity current Activity reference.
         */
        private void setListener(MainActivity aMainActivity) {
            mReference = new WeakReference<>(aMainActivity);
        }
    }

    /**
     * Change second Button state depending on second CheckButton state.
     */
    private void changeSecondNotificationAndButton() {
        if (mEditText.getText().toString().isEmpty()) {
            mSecondButton.setEnabled(false);
        } else {
            mSecondButton.setEnabled(true);
        }

        mSecondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                buildSecondNotification();
            }
        });
    }

    /**
     * Build second Notification depending on second CheckButton state.
     * Set current time to messages, group it in InboxStyle Notification
     * and add into ArrayList of Strings.
     * Launch it by NotificationManager.
     */
    private void buildSecondNotification() {

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss", Locale.ENGLISH);
        String targetString = sdf.format(currentTime.getTime()) + " " + mEditText.getText();

        mSecondNotificationContent.add(targetString);

        Intent secondNotificationActivityIntent = new Intent(this, SecondNotificationActivity.class);
        secondNotificationActivityIntent.putExtra(SECOND_NOTIFICATION_CONTENT, mSecondNotificationContent);

        PendingIntent secondNotificationActivityPendingIntent = PendingIntent.getActivity(this, 0,
                secondNotificationActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder secondNotificationBuilder = new NotificationCompat.Builder(this);
        if (mSecondCheckBox.isChecked()) {
            secondNotificationBuilder.setContentIntent(secondNotificationActivityPendingIntent)
                    .setContentTitle(getString(R.string.last_message))
                    .setSmallIcon(R.drawable.number_two)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setNumber(++mSecondNotificationMessagesCounter)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setDefaults(Notification.DEFAULT_ALL);
        } else {
            secondNotificationBuilder.setContentIntent(secondNotificationActivityPendingIntent)
                    .setContentTitle(getString(R.string.last_message))
                    .setSmallIcon(R.drawable.number_two)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setNumber(++mSecondNotificationMessagesCounter);
        }
        mMessagingStyle.setConversationTitle(getString(R.string.messages));
        mMessagingStyle.addMessage(mEditText.getText(), 0, sdf.format(currentTime.getTime()));
        secondNotificationBuilder.setStyle(mMessagingStyle);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(SECOND_NOTIFICATION_ID, secondNotificationBuilder.build());

        mEditText.setText("");
    }

    /**
     * Set third button onClickListener.
     */
    private void changeThirdNotificationAndButton() {
        mThirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                buildThirdNotification();
            }
        });
    }

    /**
     * Build third Notification.
     * Create Bitmap image and include it in BigPictureStyle Notification.
     * Create Intent for sending included image through another applications
     * which provides access for ACTION_SEND and image type Intent data.
     */
    private void buildThirdNotification() {
        Intent sharePictureIntent = new Intent();
        sharePictureIntent.setAction(Intent.ACTION_SEND);
        sharePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Bitmap bitmap = BitmapFactory.decodeFile(mImage.getAbsolutePath());

        Uri bitmapUri = FileProvider.getUriForFile(this,
                "com.example.batrakov.notificationmanagertask.fileprovider", mImage);

        sharePictureIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        sharePictureIntent.setType("image/jpeg");

        PendingIntent sharePicturePendingIntent = PendingIntent.getActivity(this, 0,
                sharePictureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent closeIntent = new Intent();

        PendingIntent closePendingIntent = PendingIntent.getActivity(this, 0,
                closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder thirdNotificationBuilder = new NotificationCompat.Builder(this);
        thirdNotificationBuilder.setContentTitle("Image")
                .setContentText(getString(R.string.it_can_be_expanded))
                .setSmallIcon(R.drawable.number_three)
                .setLargeIcon(bitmap)
                .addAction(0, getString(R.string.share), sharePicturePendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(closePendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle(getString(R.string.incoming_message))
                        .setSummaryText(getString(R.string.it_can_be_squeezed))
                        .bigLargeIcon(null)
                        .bigPicture(bitmap));

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(THIRD_NOTIFICATION_ID, thirdNotificationBuilder.build());
    }
}
