package com.mathandoro.coachplus.views.UserProfile.ViewHolders;

import android.support.v7.widget.RecyclerView;
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
        sectionHeaderText = (TextView)view.findViewById(R.id.event_detail_attendance_header_text);
    }

    public void bind(){
        this.sectionHeaderText.setText("MEMBERSHIPS");
    }
}