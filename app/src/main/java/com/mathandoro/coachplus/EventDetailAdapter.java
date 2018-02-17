package com.mathandoro.coachplus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.TeamMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 21.04.17.
 */

public class EventDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final EventDetailActivity mainActivity;
    private List<TeamMember> members;
    private Event event;

    final int EVENT_DETAIL_HEADER = 0;
    final int ATTENDANCE_HEADING = 1;
    final int ATTENDANCE_ITEM = 2;

    class EventDetailHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView location;
        TextView description;


        public EventDetailHeaderViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.event_detail_event_name);
            location = (TextView)view.findViewById(R.id.event_detail_location);
            time = (TextView)view.findViewById(R.id.event_detail_time);
            description = (TextView)view.findViewById(R.id.event_detail_description);
        }
    }

    class AttendanceHeadingViewHolder extends RecyclerView.ViewHolder {

        View itemContainer;

        public AttendanceHeadingViewHolder(View view) {
            super(view);
            this.itemContainer = view;
        }
    }

    class AttendanceItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView role;
        View itemContainer;

        public AttendanceItemViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.event_detail_attendance_item_name);
            role = (TextView)view.findViewById(R.id.event_detail_attendance_item_role);
            itemContainer = view;
        }
    }


    public EventDetailAdapter(EventDetailActivity mainActivity, Event event) {
        this.members = new ArrayList<>();
        this.event = event;
        this.mainActivity = mainActivity;
    }

    public void setEvent(Event event) {
        this.event = event;
        this.notifyDataSetChanged();
    }

    public void setMembers(List<TeamMember> members){
        this.members = members;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return EVENT_DETAIL_HEADER;
        }
        else if(position == 1){
            return ATTENDANCE_HEADING;
        }
        return ATTENDANCE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        switch(viewType){
            case EVENT_DETAIL_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_event_detail_header, parent, false);
                viewHolder = new EventDetailHeaderViewHolder(view);
                break;
            case ATTENDANCE_HEADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_event_detail_attendance_header, parent, false);
                viewHolder = new AttendanceHeadingViewHolder(view);
                break;
            case ATTENDANCE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_event_detail_attendance_item, parent, false);
                viewHolder = new AttendanceItemViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case EVENT_DETAIL_HEADER:
                EventDetailHeaderViewHolder eventDetailViewHolder = (EventDetailHeaderViewHolder) holder;
                eventDetailViewHolder.name.setText(event.getName());
                // todo location ! eventDetailViewHolder.location.setText(event.);
                if(event.getStart() != null){
                    eventDetailViewHolder.time.setText(event.getStart().toString());
                }
                eventDetailViewHolder.description.setText(event.getDescription());
                break;

            case ATTENDANCE_HEADING:
                AttendanceHeadingViewHolder eventItemViewHolder = (AttendanceHeadingViewHolder)holder;
                break;

            case ATTENDANCE_ITEM:
                AttendanceItemViewHolder attendanceViewHolder = (AttendanceItemViewHolder)holder;
                TeamMember teamMember = getMember(position);
                attendanceViewHolder.name.setText(teamMember.getUser().getFirstname() + teamMember.getUser().getLastname());
                attendanceViewHolder.role.setText(teamMember.getRole());
                break;
        }
    }

    protected TeamMember getMember(int position){
        return this.members.get(position - 2);
    }

    @Override
    public int getItemCount() {
        return members.size() +  2;
    }
}