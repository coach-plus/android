package com.mathandoro.coachplus.helpers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by dominik on 18.02.18.
 */

public class PreloadLayoutManager extends LinearLayoutManager {
    public PreloadLayoutManager(Context context) {
        super(context);
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return 0;
    }
}
