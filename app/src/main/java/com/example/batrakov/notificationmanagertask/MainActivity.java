package com.example.batrakov.notificationmanagertask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button mFirstButton;
    Button mSecondButton;
    Button mThirdButton;
    CheckBox mFirstCheckBox;
    CheckBox mSecondCheckBox;
    EditText mEditText;
    NotificationCompat.Builder mFirstNotificationBuilder;
    NotificationCompat.Builder mSecondNotificationBuilder;
    NotificationCompat.Builder mThirdNotificationBuilder;
    NotificationManager mNotificationManager;
    ArrayList<String> mSecondNotificationContent;
    static FirstNotificationTask mFirstNotificationTask;
    NotificationCompat.InboxStyle mInboxStyle;
    static int mSecondNotificationMessagesCounter;

    private static final String CANCEL_TASK = "cancel_task";
    private static final String START_TASK = "start_task";
    public static final String SECOND_NOTIFICATION_CONTENT = "second content";
    private static final String SECOND_NOTIFICATION_CONTENT_INBOX = "second content inbox";
    private static final int FIRST_NOTIFICATION_ID = 0;
    private static final int SECOND_NOTIFICATION_ID = 1;
    private static final int THIRD_NOTIFICATION_ID = 2;

    private static final String TASK_IS_NOT_ACTIVE_FIRST_NOTIFICATION = "new";
    private static final String TASK_IS_ACTIVE_FIRST_NOTIFICATION = "update";

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case CANCEL_TASK:
                    mFirstNotificationTask.cancel(false);
                    mNotificationManager.cancel(FIRST_NOTIFICATION_ID);
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInboxStyle = new NotificationCompat.InboxStyle();
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    mSecondButton.setEnabled(false);
                } else {
                    mSecondButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void buildFirstNotification() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent cancelTaskIntent = new Intent(this, MainActivity.class);
        cancelTaskIntent.setAction(CANCEL_TASK);
        cancelTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent cancelTaskPendingIntent = PendingIntent.getActivity(this, 0, cancelTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (mFirstCheckBox.isChecked()) {
            mFirstNotificationBuilder = new NotificationCompat.Builder(this);
            mFirstNotificationBuilder.setSmallIcon(R.drawable.number_one)
                    .setContentTitle("first notification title")
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setContentIntent(mainActivityPendingIntent)
                    .addAction(0, getResources().getString(R.string.cancel), cancelTaskPendingIntent)
                    .setContentText("first notification content text.").build();
            mNotificationManager.notify(0, mFirstNotificationBuilder.build());
        } else {
            mFirstNotificationBuilder = new NotificationCompat.Builder(this);
            mFirstNotificationBuilder.setSmallIcon(R.drawable.number_one)
                    .setContentTitle("first notification title")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setContentIntent(mainActivityPendingIntent)
                    .addAction(0, getResources().getString(R.string.cancel), cancelTaskPendingIntent)
                    .setContentText("first notification content text.").build();
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(FIRST_NOTIFICATION_ID, mFirstNotificationBuilder.build());
        }
    }

    private void changeFirstNotificationAndButton() {
        if (mFirstNotificationTask != null && mFirstNotificationTask.isInProgress()) {
            mFirstCheckBox.setVisibility(View.INVISIBLE);
            mFirstButton.setText(getResources().getString(R.string.cancel));
            mFirstButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFirstNotificationTask.cancel(false);
                    mNotificationManager.cancel(FIRST_NOTIFICATION_ID);
                }
            });
        } else {
            mFirstCheckBox.setVisibility(View.VISIBLE);
            mFirstButton.setText(getResources().getString(R.string.start));
            mFirstNotificationTask = new FirstNotificationTask(this);
            mFirstButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFirstNotificationTask.execute();
                    buildFirstNotification();
                }
            });
        }
    }

    private void changeSecondNotificationAndButton() {
        if (mEditText.getText().toString().isEmpty()) {
            mSecondButton.setEnabled(false);
        } else {
            mSecondButton.setEnabled(true);
        }

        mSecondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildSecondNotification();
            }
        });
    }

    private void buildSecondNotification() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        String targetString = sdf.format(currentTime.getTime()) + ": " + mEditText.getText();

        mSecondNotificationContent.add(targetString);

        Intent secondNotificationActivityIntent = new Intent(this, SecondNotificationActivity.class);
        secondNotificationActivityIntent.putExtra(SECOND_NOTIFICATION_CONTENT, mSecondNotificationContent);
        PendingIntent secondNotificationActivityPendingIntent = PendingIntent.getActivity(this, 0, secondNotificationActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mSecondNotificationBuilder = new NotificationCompat.Builder(this);
        if (mSecondCheckBox.isChecked()) {
            mSecondNotificationBuilder.setContentIntent(secondNotificationActivityPendingIntent)
                    .setContentTitle("Last message")
                    .setContentText(targetString)
                    .setSmallIcon(R.drawable.number_two)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setNumber(++mSecondNotificationMessagesCounter)
                    .setDefaults(Notification.DEFAULT_ALL);
            mInboxStyle.setSummaryText("more below spoiler...");
            mInboxStyle.addLine(targetString);
            if (mSecondNotificationMessagesCounter > 1) {
                mSecondNotificationBuilder.setStyle(mInboxStyle);
            }
        } else {
            mSecondNotificationBuilder.setContentIntent(secondNotificationActivityPendingIntent)
                    .setContentTitle("Last message")
                    .setContentText(targetString)
                    .setSmallIcon(R.drawable.number_two)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setNumber(++mSecondNotificationMessagesCounter);
            mInboxStyle.setSummaryText("more below spoiler...");
            mInboxStyle.addLine(targetString);
            if (mSecondNotificationMessagesCounter > 1) {
                mSecondNotificationBuilder.setStyle(mInboxStyle);
            }

        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(SECOND_NOTIFICATION_ID, mSecondNotificationBuilder.build());
    }


    private void changeThirdNotificationAndButton() {
        mThirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildThirdNotification();
            }
        });
    }

    private void buildThirdNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.raw.kek);
        mThirdNotificationBuilder = new NotificationCompat.Builder(this);
        mThirdNotificationBuilder.setContentTitle("Third notification")
                .setContentText("image")
                .setSmallIcon(R.drawable.number_three)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap));
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(THIRD_NOTIFICATION_ID, mThirdNotificationBuilder.build());

    }


    private static class FirstNotificationTask extends AsyncTask<Void, Void, Void> {
        private boolean mInProgress;
        private WeakReference<MainActivity> mReference;
        int mCounter;

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
        protected Void doInBackground(Void... params) {
            mCounter = 1;
            while (mCounter < 10) {
                if (isCancelled()) {
                    mInProgress = false;
                    break;
                }
                publishProgress();
                mCounter++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException aE) {
                    aE.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mReference.get().mFirstNotificationBuilder.setProgress(10, getCounter(), false);
            mReference.get().mFirstNotificationBuilder.setContentText("Remaining time: " + String.valueOf(10 - getCounter()));
            mReference.get().mNotificationManager.notify(FIRST_NOTIFICATION_ID, mReference.get().mFirstNotificationBuilder.build());
        }

        boolean isInProgress() {
            return mInProgress;
        }

        int getCounter() {
            return mCounter;
        }

        private void setlistener(MainActivity aMainActivity) {
            mReference = new WeakReference<MainActivity>(aMainActivity);
        }
    }
}
