package com.mathandoro.coachplus.views.EventDetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.TeamMember;
import com.squareup.picasso.Picasso;

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
            name = view.findViewById(R.id.list_section_heading_text);
            location = view.findViewById(R.id.event_detail_location);
            time = view.findViewById(R.id.event_detail_time);
            description = view.findViewById(R.id.event_detail_description);
        }
    }

    class AttendanceHeadingViewHolder extends RecyclerView.ViewHolder {

        View itemContainer;
        TextView heading;

        public AttendanceHeadingViewHolder(View view) {
            super(view);
            this.itemContainer = view;
        }

        public void setHeading(String heading){
            this.heading.setText(heading);
        }
    }

    class AttendanceItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView you;
        Button attendButton;
        View itemContainer;
        ImageView profilePicture;

        public AttendanceItemViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.eventDetailUsername);
            you = view.findViewById(R.id.eventDetailYou);
            profilePicture = view.findViewById(R.id.eventDetailUserImage);

            itemContainer = view;
        }

        public void bindMember(TeamMember teamMember){
            String userImage = teamMember.getUser().getImage();
            name.setText(teamMember.getUser().getFirstname() + " " + teamMember.getUser().getLastname());
            // todo is user ?
            you.setVisibility(View.INVISIBLE);
            if(userImage != null){
                String imageUrl = BuildConfig.BASE_URL + "/uploads/" + userImage;
                Picasso.with(profilePicture.getContext())
                        .load(imageUrl)
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.circle)
                        .into(profilePicture);
            }
            else {
                profilePicture.setImageResource(R.drawable.circle);
            }
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_section_heading, parent, false);
                viewHolder = new AttendanceHeadingViewHolder(view);
                break;
            case ATTENDANCE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_detail_attendence, parent, false);
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
                if(event.getLocation() != null){
                    eventDetailViewHolder.location.setText(event.getLocation().getName());
                    // todo location google maps link!
                }
                if(event.getStart() != null){
                    eventDetailViewHolder.time.setText(event.getStart().toString());
                }
                eventDetailViewHolder.description.setText(event.getDescription());
                break;

            case ATTENDANCE_HEADING:
                AttendanceHeadingViewHolder eventItemViewHolder = (AttendanceHeadingViewHolder)holder;
                eventItemViewHolder.setHeading("Attendance");
                break;

            case ATTENDANCE_ITEM:
                AttendanceItemViewHolder attendanceViewHolder = (AttendanceItemViewHolder)holder;
                TeamMember teamMember = getMember(position);
                attendanceViewHolder.bindMember(teamMember);
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