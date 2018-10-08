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
    final int ATTENDANCE_ITEM_ACTIVE = 2;
    final int ATTENDANCE_ITEM_PASSIVE = 3;


    public enum ItemState {
        READ_DID_ATTEND_STATE,
        READ_WILL_ATTEND_STATE,
        SET_WILL_ATTEND_STATE,
        SET_DID_ATTEND_STATE
    }

    class EventDetailHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView location;
        TextView description;


        public EventDetailHeaderViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.event_detail_attendance_header_text);
            location = view.findViewById(R.id.event_detail_location);
            time = view.findViewById(R.id.event_detail_time);
            description = view.findViewById(R.id.event_detail_description);
        }
    }

    class AttendanceHeadingViewHolder extends RecyclerView.ViewHolder {

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
        ItemState itemState = getItemState(position);

        if(itemState == ItemState.SET_WILL_ATTEND_STATE){
            return ATTENDANCE_ITEM_ACTIVE;
        }
        return ATTENDANCE_ITEM_PASSIVE;
    }

    private ItemState getItemState(int position){
        ParticipationItem participationItem = getParticipationItem(position);
        if(event.getStart().before(new Date()) ){
            if(myUsersMembership.getRole().equals(Const.Role.Coach.toString())){
                return ItemState.SET_DID_ATTEND_STATE;
            }
            return ItemState.READ_DID_ATTEND_STATE;
        }
        else{
            if(participationItem.getTeamMember() == myUsersMembership){
                return ItemState.SET_WILL_ATTEND_STATE;
            }
            return ItemState.READ_WILL_ATTEND_STATE;
        }
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_detail_attendance_header, parent, false);
                viewHolder = new AttendanceHeadingViewHolder(view);
                break;
            case ATTENDANCE_ITEM_ACTIVE:
            case ATTENDANCE_ITEM_PASSIVE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
                if(viewType == ATTENDANCE_ITEM_ACTIVE){
                    View.inflate(view.getContext(), R.layout.member_item_buttons, view.findViewById(R.id.member_item_right_container));
                }
                else{
                    View.inflate(view.getContext(), R.layout.member_item_attendence_status, view.findViewById(R.id.member_item_right_container));
                }
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

            case ATTENDANCE_HEADING: {
                AttendanceHeadingViewHolder eventItemViewHolder = (AttendanceHeadingViewHolder) holder;
                eventItemViewHolder.bind(participationItems, event);
                break;
            }

            case ATTENDANCE_ITEM_ACTIVE: {
                TeamMemberViewHolder attendanceViewHolder = (TeamMemberViewHolder) holder;
                TeamMember teamMember = getParticipationItem(position).getTeamMember();
                attendanceViewHolder.bindSetMode(getParticipationItem(position), myUser, (willAttend) -> {
                    mainActivity.onUpdateWillAttend(teamMember.getUser().get_id(), willAttend);
                });
                break;
            }

            case ATTENDANCE_ITEM_PASSIVE: {
                TeamMemberViewHolder attendancePassiveViewHolder = (TeamMemberViewHolder) holder;
                ParticipationItem item = getParticipationItem(position);
                attendancePassiveViewHolder.bindReadMode(myUser, event, getItemState(position), item, (didAttend) -> {
                    mainActivity.showBottomSheet(item.teamMember.getUser().get_id(), didAttend);
                });
                break;
            }
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

