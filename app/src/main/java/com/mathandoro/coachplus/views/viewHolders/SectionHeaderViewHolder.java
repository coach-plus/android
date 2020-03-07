package com.mathandoro.coachplus.views.viewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mathandoro.coachplus.R;

/**
 * Created by dominik on 18.02.18.
 */

public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView sectionHeaderText;

    public SectionHeaderViewHolder(View view) {
        super(view);
        sectionHeaderText = view.findViewById(R.id.event_detail_attendance_sub_header_text);
    }

    public void bind(String sectionName){
        this.sectionHeaderText.setText(sectionName);
    }
}