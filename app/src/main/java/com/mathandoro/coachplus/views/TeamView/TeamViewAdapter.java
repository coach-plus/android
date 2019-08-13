package com.mathandoro.coachplus.views.TeamView;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.helpers.RecycleViewStack;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.persistence.AppState;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.TeamView.viewHolders.SeeAllEventsViewHolder;
import com.mathandoro.coachplus.views.viewHolders.EventItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.StaticViewHolder;
import com.mathandoro.coachplus.views.viewHolders.TeamImageItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.TeamMemberViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by dominik on 02.04.17.
 */

public class TeamViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final TeamViewActivity mainActivity;
    private final TeamViewFragment teamFeedFragment;
    private List<TeamMember> members;
    private List<Event> visibleEvents;
    private MyReducedUser myUser;
    private Membership myUsersMembership;
    private DataLayer dataLayer;
    private RecycleViewStack viewStack;

    final int UPCOMING_EVENTS_HEADER = 0;
    final int UPCOMING_EVENTS_ITEM = 1;
    final int MEMBERS_HEADER = 2;
    final int MEMBERS_ITEM = 3;
    final int MEMBER_ITEM_ADMIN_MODE = 4; // special case
    final int TEAM_IMAGE_ITEM = 5;
    final int NO_UPCOMING_EVENTS = 6;
    final int SHOW_ALL_EVENTS = 7;
    final int FOOTER = 8;

    private static final int MAX_VISIBLE_EVENTS = 3;


    public TeamViewAdapter(TeamViewActivity mainActivity, TeamViewFragment teamFeedFragment, Membership myUsersMembership) {
        this.members = new ArrayList<>();
        this.myUsersMembership = myUsersMembership;
        this.visibleEvents = new ArrayList<>();
        this.teamFeedFragment = teamFeedFragment;
        this.mainActivity = mainActivity;
        this.dataLayer = new DataLayer(mainActivity);
        this.loadMyUser();

        viewStack = new RecycleViewStack();
        viewStack.addSection(TEAM_IMAGE_ITEM, 1);
        viewStack.addSection(UPCOMING_EVENTS_HEADER, 1);
        viewStack.addSection(UPCOMING_EVENTS_ITEM, 0);
        viewStack.addSection(NO_UPCOMING_EVENTS, 0);
        viewStack.addSection(SHOW_ALL_EVENTS, 1);
        viewStack.addSection(MEMBERS_HEADER, 1);
        viewStack.addSection(MEMBERS_ITEM, 0);
        viewStack.addSection(FOOTER, 1);

        subscribeData();
    }

    private void subscribeData(){
        AppState.instance().members$.subscribe(teamMembers -> {
            this.members = teamMembers;
            this.viewStack.updateSection(MEMBERS_ITEM, members.size());
            this.notifyDataSetChanged();
        });

        AppState.instance().events$.subscribe(events -> {
            this.visibleEvents = this.filterVisibleEvents(events);
            if(this.visibleEvents.size() == 0){
                this.viewStack.updateSection(NO_UPCOMING_EVENTS, 1);
                this.viewStack.updateSection(UPCOMING_EVENTS_ITEM, 0);
            }
            else{
                this.viewStack.updateSection(NO_UPCOMING_EVENTS, 0);
                this.viewStack.updateSection(UPCOMING_EVENTS_ITEM, this.visibleEvents.size());
            }
            this.notifyDataSetChanged();
        });
    }

    private void loadMyUser(){
        Observable<MyUserResponse> myUserV2 = this.dataLayer.getMyUserV2(true);
        myUserV2.subscribe((response) -> this.onMyUserChanged(response.user));
    }

    private void onMyUserChanged(MyReducedUser myUser){
        this.myUser = myUser;
        this.notifyDataSetChanged();
    }
    
    @Override
    public int getItemViewType(int position) {
        int viewType = viewStack.getSectionIdAt(position);
        if(viewType == MEMBERS_ITEM && myUsersMembership != null && myUsersMembership.isCoach()){
            return MEMBER_ITEM_ADMIN_MODE;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view;
        switch(viewType){
            case TEAM_IMAGE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_team_image_item, parent, false);
                return new TeamImageItemViewHolder(view);
            case UPCOMING_EVENTS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_upcoming_events_header, parent, false);
                return new StaticViewHolder(view);
            case UPCOMING_EVENTS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
                return new EventItemViewHolder(view);
            case MEMBERS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_members_header, parent, false);
                return new StaticViewHolder(view);
            case NO_UPCOMING_EVENTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_no_event_item, parent, false);
                return new StaticViewHolder(view);
            case SHOW_ALL_EVENTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_see_all_events_item, parent, false);
                return new SeeAllEventsViewHolder(view);
            case MEMBERS_ITEM:
            case MEMBER_ITEM_ADMIN_MODE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
                View rightView = null;
                if(myUsersMembership.isCoach()){
                    rightView = View.inflate(view.getContext(), R.layout.member_item_actions_indicator, view.findViewById(R.id.member_item_right_container));
                }
                return new TeamMemberViewHolder(view, rightView);
            case FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                return new StaticViewHolder(view);
        }
        throw new Error("unknown viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TEAM_IMAGE_ITEM:
                String image = myUsersMembership.getTeam().getImage(); // this.teamFeedFragment.getCurrentMembership().getTeam().getImage();
                TeamImageItemViewHolder teamImageItemViewHolder = (TeamImageItemViewHolder) holder;
                if(image != null){
                    teamImageItemViewHolder.teamImage.setScaleX(1f);
                    teamImageItemViewHolder.teamImage.setScaleY(1f);
                    teamImageItemViewHolder.teamImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    String imageUrl = BuildConfig.BASE_URL + "/uploads/" + image;
                     Picasso.with(teamImageItemViewHolder.itemView.getContext())
                            .load(imageUrl)
                            .placeholder(teamImageItemViewHolder.teamImage.getDrawable())
                            .resize(Settings.TEAM_ICON_LARGE, Settings.TEAM_ICON_LARGE)
                             .into(teamImageItemViewHolder.teamImage);
                }
                else{
                    teamImageItemViewHolder.teamImage.setImageResource(R.drawable.ic_tshirt_solid);
                    teamImageItemViewHolder.teamImage.setScaleX(.8f);
                    teamImageItemViewHolder.teamImage.setScaleY(.8f);
                    teamImageItemViewHolder.teamImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                }
                break;

            case SHOW_ALL_EVENTS:
                SeeAllEventsViewHolder seeAllEventsViewHolder = (SeeAllEventsViewHolder) holder;
                seeAllEventsViewHolder.bind(() -> teamFeedFragment.navigateToAllEvents());

                break;

            case UPCOMING_EVENTS_ITEM:
                EventItemViewHolder eventItemViewHolder = (EventItemViewHolder)holder;
                Event event = getEvent(position);
                eventItemViewHolder.bind(event);
                holder.itemView.setOnClickListener((View view) ->
                    teamFeedFragment.navigateToEvent(event)
                );
                break;
            case MEMBERS_ITEM:
            case MEMBER_ITEM_ADMIN_MODE:
                TeamMemberViewHolder memberViewHolder = (TeamMemberViewHolder)holder;
                final TeamMember teamMember = getMember(position);
                memberViewHolder.bindTeamViewMode(teamMember, myUser,
                        () -> mainActivity.showBottomSheet(teamMember), myUsersMembership.isCoach());
                memberViewHolder.itemView.setOnClickListener((View view) ->
                    teamFeedFragment.navigateToUserProfile(teamMember.getUser()));
                break;
        }
    }

    protected Event getEvent(int position){
        return this.visibleEvents.get(viewStack.positionInSection(UPCOMING_EVENTS_ITEM, position));
    }

    protected TeamMember getMember(int position){
        return this.members.get(viewStack.positionInSection(MEMBERS_ITEM, position));
    }

    private List<Event> filterVisibleEvents(List<Event> events){
        Collections.sort(events, (eventA, eventB) -> eventA.getStart().compareTo(eventB.getStart()));
        List<Event> upcomingEvents = new ArrayList();
        for(Event event: events){
            if(event.getEnd().after(new Date())){
                upcomingEvents.add(event);
                if(upcomingEvents.size() == MAX_VISIBLE_EVENTS){
                    break;
                }
            }
        }
        return upcomingEvents;
    }

    @Override
    public int getItemCount() {
        return viewStack.size();
    }

    public void setMembership(Membership updatedMembership) {
        this.myUsersMembership = updatedMembership;
        notifyDataSetChanged();
    }
}