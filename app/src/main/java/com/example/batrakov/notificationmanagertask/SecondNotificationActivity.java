package com.example.batrakov.notificationmanagertask;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Second activity that provide list view for messages which income to second Notification;
 * Created by batrakov on 18.10.17.
 */

public class SecondNotificationActivity extends AppCompatActivity {

    private static final int SPAN_FROM = 0;
    private static final int SPAN_TO = 8;

    @Override
    protected void onCreate(@Nullable Bundle aSavedInstanceState) {
        setContentView(R.layout.second_notification_activity);
        super.onCreate(aSavedInstanceState);

        if (getIntent() != null) {
            ArrayList<String> content = getIntent().getStringArrayListExtra(MainActivity.SECOND_NOTIFICATION_CONTENT);
            if (content != null) {
                RecyclerView listView = (RecyclerView) findViewById(R.id.list);
                ListAdapter listAdapter = new ListAdapter(content);
                listView.setLayoutManager(new LinearLayoutManager(this));
                listView.setAdapter(listAdapter);
                listAdapter.replaceData(content);
            }
        }
    }


    /**
     * Holder for RecyclerView. Contain single list element.
     */
    private final class ListHolder extends RecyclerView.ViewHolder {

        private TextView mField;

        /**
         * Constructor.
         * @param aItemView item view
         */
        private ListHolder(View aItemView) {
            super(aItemView);
            mField = itemView.findViewById(R.id.item);
        }

        /**
         * View filling.
         * @param aStr string from list
         */
        void bindView(String aStr) {
            Spannable spannable = new SpannableString(aStr);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), SPAN_FROM, SPAN_TO, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mField.setText(spannable);
        }
    }

    /**
     * Adapter for recycler view. Allow to fill and update list.
     */
    private class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        private ArrayList<String> mList;

        /**
         * Constructor.
         * @param aList target list for fill.
         */
        ListAdapter(ArrayList<String> aList) {
            mList = aList;
        }

        /**
         * List updating.
         * @param aList new target list.
         */
        void replaceData(ArrayList<String> aList) {
            mList = aList;
            notifyDataSetChanged();
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup aParent, int aViewType) {
            View rowView = LayoutInflater.from(aParent.getContext()).inflate(R.layout.list_item, aParent, false);
            return new ListHolder(rowView);
        }

        @Override
        public void onBindViewHolder(ListHolder aHolder, int aPosition) {
            String str = mList.get(aPosition);
            aHolder.bindView(str);
        }

        @Override
        public long getItemId(int aIndex) {
            return aIndex;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

}
