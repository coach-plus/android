package com.mathandoro.coachplus.views.EventDetail.ViewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.views.EventDetail.ParticipationItem;

import java.util.Date;
import java.util.List;

public class AttendanceHeadingViewHolder extends RecyclerView.ViewHolder {

    View itemContainer;
    TextView numCommitmentsTextView;
    TextView numRejectionsTextView;
    TextView numUnknownTextView;

    public AttendanceHeadingViewHolder(View view) {
        super(view);
        this.itemContainer = view;
        numCommitmentsTextView = view.findViewById(R.id.event_detail_num_commitments);
        numRejectionsTextView = view.findViewById(R.id.event_detail_num_rejections);
        numUnknownTextView = view.findViewById(R.id.event_detail_num_unknown);
    }

    public void bind(List<ParticipationItem> items, Event event){
        int numCommitments=0, numRejections=0, numUnknwon=0;
        for (ParticipationItem item : items) {
            if (item.getParticipation() != null) {
                Boolean attendStatus = item.getParticipation().WillAttend();
                if(event.getStart().before(new Date())){
                    if(item.getParticipation().DidAttend()!=null){
                        attendStatus = item.getParticipation().DidAttend();
                    }
                    else if(item.getParticipation().WillAttend()!=null){
                        attendStatus = item.getParticipation().WillAttend();
                    }
                    else{
                        attendStatus = false;
                    }
                }
                if(attendStatus != null){
                    if(attendStatus){
                        numCommitments++;
                    }
                    else{
                        numRejections++;
                    }
                }
                else {
                    numUnknwon++;
                }
            }
            else if(event.getStart().before(new Date())){
                numRejections++;
            }
            else{
                numUnknwon++;
            }
        }
        numCommitmentsTextView.setText(""+numCommitments);
        numRejectionsTextView.setText(""+numRejections);
        numUnknownTextView.setText(""+numUnknwon);
    }
}