package com.example.batrakov.notificationmanagertask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by batrakov on 18.10.17.
 */

public class SecondNotificationActivity extends AppCompatActivity {

    ArrayList<String> mContent;
    private RecyclerView mListView;
    private ListAdapter mListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.second_notification_activity);
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            mContent = getIntent().getStringArrayListExtra(MainActivity.SECOND_NOTIFICATION_CONTENT);
            if (mContent != null) {
                mListView = (RecyclerView) findViewById(R.id.list);
                mListAdapter = new ListAdapter(mContent);
                mListView.setLayoutManager(new LinearLayoutManager(this));
                mListView.setAdapter(mListAdapter);
                mListAdapter.replaceData(mContent);
            }
        }
    }


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
            mField.setText(aStr);
        }
    }

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
