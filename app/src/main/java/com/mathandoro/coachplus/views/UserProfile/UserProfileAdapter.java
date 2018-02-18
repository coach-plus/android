package com.mathandoro.coachplus.views.UserProfile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.views.UserProfile.ViewHolders.SectionHeaderViewHolder;
import com.mathandoro.coachplus.views.UserProfile.ViewHolders.UserInfoViewHolder;
import com.mathandoro.coachplus.views.UserProfile.ViewHolders.UserMembershipViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 18.02.18.
 */

public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Membership> memberships;
    private ReducedUser user;

    final int USER_INFO = 0;
    final int MEMBERSHIPS_HEADER = 1;
    final int MEMBERSHIP_ITEM = 2;


    public UserProfileAdapter(UserProfileActivity userProfileActivity) {
        memberships = new ArrayList<>();
    }

    public void setMemberships(List<Membership> memberships){
        this.memberships = memberships;
        this.notifyDataSetChanged();
    }

    public void setUser(ReducedUser user){
        this.user = user;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return USER_INFO;
        }
        else if(position == 1){
            return MEMBERSHIPS_HEADER;
        }
        return MEMBERSHIP_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case USER_INFO:
                view = layoutInflater.inflate(R.layout.user_profile_user_info, parent, false);
                return new UserInfoViewHolder(view);
            case MEMBERSHIPS_HEADER:
                view = layoutInflater.inflate(R.layout.list_section_heading, parent, false);
                return new SectionHeaderViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.user_profile_membership_item, parent, false);
                return new UserMembershipViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case USER_INFO:
                UserInfoViewHolder userInfoViewHolder = (UserInfoViewHolder) holder;
                userInfoViewHolder.bind(user);
                break;
            case MEMBERSHIPS_HEADER:
                SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
                sectionHeaderViewHolder.bind();
                break;
            case MEMBERSHIP_ITEM:
                Membership membership = this.getMembership(position);
                UserMembershipViewHolder membershipViewHolder = (UserMembershipViewHolder) holder;
                membershipViewHolder.bind(membership);
                break;
        }
    }

    protected Membership getMembership(int position){
        return this.memberships.get(position - 2);
    }

    @Override
    public int getItemCount() {
        return memberships.size() + 2;
    }
}