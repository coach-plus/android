package com.mathandoro.coachplus.helpers;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
