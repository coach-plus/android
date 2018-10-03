package com.mathandoro.coachplus.views.EventDetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.Const;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.viewHolders.TeamMemberViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by dominik on 21.04.17.
 */

public class EventDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final EventDetailActivity mainActivity;
    private List<ParticipationItem> participationItems;
    private Event event;
    private DataLayer dataLayer;
    private JWTUser myUser;
    private TeamMember myUsersMembership;

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
            heading = view.findViewById(R.id.list_section_heading_text);
        }

        public void setHeading(String heading){
            this.heading.setText(heading);
        }
    }


    public EventDetailAdapter(EventDetailActivity mainActivity, Event event) {
        this.participationItems = new ArrayList<>();
        this.dataLayer = DataLayer.getInstance(mainActivity);
        this.event = event;
        this.mainActivity = mainActivity;
        this.loadMyUser();
    }

    public void setEvent(Event event) {
        this.event = event;
        this.notifyDataSetChanged();
    }

    public void setParticipationItems(List<ParticipationItem> items){
        this.participationItems = items;
        for (ParticipationItem item : items
             ) {
            if(item.getTeamMember().getUser().get_id().equals(myUser.get_id())){
                myUsersMembership = item.getTeamMember();
                break;
            }
        }
        this.notifyDataSetChanged();
    }

    private void loadMyUser(){
        Observable<MyUserResponse> myUserV2 = this.dataLayer.getMyUserV2(true);
        myUserV2.subscribe((response) -> this.onMyUserChanged(response.user));
    }

    private void onMyUserChanged(JWTUser myUser){
        this.myUser = myUser;
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_detail_header, parent, false);
                viewHolder = new EventDetailHeaderViewHolder(view);
                break;
            case ATTENDANCE_HEADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_section_heading, parent, false);
                viewHolder = new AttendanceHeadingViewHolder(view);
                break;
            case ATTENDANCE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
                viewHolder = new TeamMemberViewHolder(view);
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
                TeamMemberViewHolder attendanceViewHolder = (TeamMemberViewHolder)holder;
                TeamMember teamMember = getParticipationItem(position).getTeamMember();
                attendanceViewHolder.bindParticipationMode(getParticipationItem(position), myUser, event, () -> {
                    if(eventHasStarted(event)){
                        if(isMyUserCoach()){
                            mainActivity.onUpdateDidAttend(teamMember.getUser().get_id(), true);
                        }
                    }
                    else if(teamMemberIsMyUser(teamMember)){
                        mainActivity.onUpdateWillAttend(teamMember.getUser().get_id(), true);
                    }
                }, () -> {
                    if(eventHasStarted(event)){
                        if(isMyUserCoach()){
                            mainActivity.onUpdateDidAttend(teamMember.getUser().get_id(), false);
                        }
                    }
                    else if(this.teamMemberIsMyUser(teamMember)){
                        mainActivity.onUpdateWillAttend(teamMember.getUser().get_id(), false);
                    }
                });
                break;
        }
    }

    protected boolean teamMemberIsMyUser(TeamMember teamMember){
        return teamMember.getUser().get_id().equals(myUser.get_id());
    }

    protected boolean isMyUserCoach(){
        return this.myUsersMembership.getRole().equals(Const.Role.Coach.toString());
    }

    protected boolean eventHasStarted(Event event){
        return event.getStart().before(new Date());
    }


    protected ParticipationItem getParticipationItem(int position){
        return this.participationItems.get(position - 2);
    }

    @Override
    public int getItemCount() {
        return this.participationItems.size() +  2;
    }


}

