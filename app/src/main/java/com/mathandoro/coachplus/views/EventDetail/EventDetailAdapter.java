package com.mathandoro.coachplus.views.EventDetail;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Role;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.helpers.RecycleViewStack;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.News;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.EventDetail.ViewHolders.AttendanceHeadingViewHolder;
import com.mathandoro.coachplus.views.EventDetail.ViewHolders.EventDetailHeaderViewHolder;
import com.mathandoro.coachplus.views.EventDetail.ViewHolders.NewsItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.SectionHeaderViewHolder;
import com.mathandoro.coachplus.views.viewHolders.StaticViewHolder;
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
    private List<News> news;
    private Event event;
    private DataLayer dataLayer;
    private MyReducedUser myUser;
    private TeamMember myUsersMembership;

    final int EVENT_DETAIL_HEADER = 0;
    final int ATTENDANCE_HEADING = 1;
    final int ATTENDANCE_ITEM_ACTIVE = 2;
    final int ATTENDANCE_ITEM_PASSIVE = 3;
    final int ATTENDANCE_ITEM = 4;
    final int NEWS_HEADER = 5;
    final int NEWS_ITEM = 6;
    final int FOOTER = 7;

    private RecycleViewStack viewStack;


    public enum ItemState {
        READ_DID_ATTEND_STATE,
        READ_WILL_ATTEND_STATE,
        SET_WILL_ATTEND_STATE,
        SET_DID_ATTEND_STATE
    }


    public EventDetailAdapter(EventDetailActivity mainActivity, Event event) {
        this.initViewStack();
        this.participationItems = new ArrayList<>();
        this.news = new ArrayList<>();
        this.dataLayer = DataLayer.getInstance(mainActivity);
        this.event = event;
        this.mainActivity = mainActivity;
        this.loadMyUser();
    }

    private void initViewStack(){
        this.viewStack = new RecycleViewStack();
        this.viewStack.addSection(EVENT_DETAIL_HEADER, 1);
        this.viewStack.addSection(NEWS_HEADER, 0);
        this.viewStack.addSection(NEWS_ITEM, 0);
        this.viewStack.addSection(ATTENDANCE_HEADING, 1);
        this.viewStack.addSection(ATTENDANCE_ITEM, 0);
        this.viewStack.addSection(FOOTER, 1);
    }

    public void setEvent(Event event) {
        this.event = event;
        this.notifyDataSetChanged();
    }

    public void setNews(List<News> news){
        this.news = news;
        if(news.size() > 0){
            this.viewStack.updateSection(NEWS_HEADER, 1);
        }
        else{
            this.viewStack.updateSection(NEWS_HEADER, 0);
        }
        this.viewStack.updateSection(NEWS_ITEM, news.size());
        this.notifyDataSetChanged();
    }

    public void setParticipationItems(List<ParticipationItem> items){
        this.participationItems = items;
        this.viewStack.updateSection(ATTENDANCE_ITEM, items.size());
        for (ParticipationItem item : items) {
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

    private void onMyUserChanged(MyReducedUser myUser){
        this.myUser = myUser;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int sectionId = this.viewStack.getSectionIdAt(position);
        if(sectionId == ATTENDANCE_ITEM){
            ItemState itemState = getItemState(position);
            if(itemState == ItemState.SET_WILL_ATTEND_STATE){
                return ATTENDANCE_ITEM_ACTIVE;
            }
            return ATTENDANCE_ITEM_PASSIVE;
        }
        return sectionId;

    }

    private ItemState getItemState(int position){
        ParticipationItem participationItem = getParticipationItem(position);
        if(event.getStart().before(new Date()) ){
            if(myUsersMembership.getRole().equals(Role.COACH)){
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
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case EVENT_DETAIL_HEADER:
                view = layoutInflater.inflate(R.layout.event_detail_header, parent, false);
                viewHolder = new EventDetailHeaderViewHolder(view);
                break;
            case NEWS_HEADER:
                view = layoutInflater.inflate(R.layout.list_section_heading, parent, false);
                viewHolder = new SectionHeaderViewHolder(view);
                break;
            case NEWS_ITEM:
                view = layoutInflater.inflate(R.layout.event_detail_news_item, parent, false);
                viewHolder = new NewsItemViewHolder(view);
                break;
            case ATTENDANCE_HEADING:
                view = layoutInflater.inflate(R.layout.event_detail_attendance_header, parent, false);
                viewHolder = new AttendanceHeadingViewHolder(view);
                break;
            case ATTENDANCE_ITEM_ACTIVE:
            case ATTENDANCE_ITEM_PASSIVE:
                view = layoutInflater.inflate(R.layout.member_item, parent, false);
                if(viewType == ATTENDANCE_ITEM_ACTIVE){
                    View.inflate(view.getContext(), R.layout.member_item_buttons, view.findViewById(R.id.member_item_right_container));
                }
                else{
                    View.inflate(view.getContext(), R.layout.member_item_attendence_status, view.findViewById(R.id.member_item_right_container));
                }
                viewHolder = new TeamMemberViewHolder(view);
                break;
            case FOOTER:
                view = layoutInflater.inflate(R.layout.footer, parent, false);
                viewHolder = new StaticViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case EVENT_DETAIL_HEADER:
                EventDetailHeaderViewHolder eventDetailViewHolder = (EventDetailHeaderViewHolder) holder;
               eventDetailViewHolder.bind(event);
                break;
            case NEWS_HEADER:
                SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
                sectionHeaderViewHolder.bind(mainActivity.getString(R.string.news_title));
                break;
            case NEWS_ITEM:
                News news = getNewsItem(position);
                NewsItemViewHolder newsItemViewHolder = (NewsItemViewHolder)holder;
                newsItemViewHolder.bind(news);
                if(myUsersMembership.getRole().equals(Role.COACH)){
                    newsItemViewHolder.makeDeleteable(() -> {
                        mainActivity.showNewsDeletionConfirmation(news);
                    });
                }
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

    private ParticipationItem getParticipationItem(int position){
        return this.participationItems.get(this.viewStack.positionInSection(ATTENDANCE_ITEM, position));
    }

    private News getNewsItem(int position){
        return this.news.get(this.viewStack.positionInSection(NEWS_ITEM, position));
    }

    @Override
    public int getItemCount() {
        return this.viewStack.size();
    }
}

