package com.ecg.complimenter.data;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.ecg.complimenter.R;

import java.util.ArrayList;

/**
 * Created by Sam on 10/28/2014.
 */
public class ComplimentDataPagerAdapter extends ArrayAdapter<ComplimentData> {
    private ComplimentDataProvider mProvider;
    private int pageSize = 10;

    public ComplimentDataPagerAdapter(Context context, Integer pageSize){
        super(context, R.layout.activity_ecg, ComplimentDataProvider.getInstance(context).getRange(0, pageSize));
        this.mProvider = ComplimentDataProvider.getInstance(context);
        this.pageSize = pageSize;
    }

    public int getDataCount(){
        return this.mProvider.getCount();
    }

    @Override
    public ComplimentData getItem(int position) {
        int localCount = this.getCount();
        if(position >= localCount) {
            while(localCount < position) {
                ArrayList<ComplimentData> moreData = this.mProvider.getRange(localCount, pageSize);
                if(moreData.isEmpty()){ break; }
                for (int i = 0; i < moreData.size(); i++) {
                    this.insert(moreData.get(i), localCount + i);
                }
                localCount = this.getCount();
            }
        }
        if(position < this.getCount() && position >= 0) {
            return super.getItem(position);
        }
        else {
            return null;
        }
    }
}
