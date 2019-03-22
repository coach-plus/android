package com.mathandoro.coachplus.views.UserProfile;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.viewHolders.MembershipViewHolder;
import com.mathandoro.coachplus.views.viewHolders.SectionHeaderViewHolder;
import com.mathandoro.coachplus.views.UserProfile.ViewHolders.UserInfoViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 18.02.18.
 */

public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Membership> memberships;
    private ReducedUser user;
    private boolean isMyUser = false;

    final int USER_INFO = 0;
    final int MEMBERSHIPS_HEADER = 1;
    final int MEMBERSHIP_ITEM = 2;

    private UserInfoViewHolder userInfoViewHolder;
    private UserProfileActivity userProfileActivity;
    private DataLayer dataLayer;


    public UserProfileAdapter(UserProfileActivity userProfileActivity, DataLayer dataLayer) {
        memberships = new ArrayList<>();
        this.userProfileActivity = userProfileActivity;
        this.dataLayer = dataLayer;
    }

    public void setMemberships(List<Membership> memberships){
        this.memberships = memberships;
        this.notifyDataSetChanged();
    }

    public void setUser(ReducedUser user, boolean isMyUser){
        this.user = user;
        this.isMyUser = isMyUser;
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
                userInfoViewHolder = new UserInfoViewHolder(view, userProfileActivity);
                return userInfoViewHolder;
            case MEMBERSHIPS_HEADER:
                view = layoutInflater.inflate(R.layout.list_section_heading, parent, false);
                return new SectionHeaderViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.team_item , parent, false);
                return new MembershipViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case USER_INFO:
                UserInfoViewHolder userInfoViewHolder = (UserInfoViewHolder) holder;
                userInfoViewHolder.bind(user, isMyUser);
                break;
            case MEMBERSHIPS_HEADER:
                SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
                sectionHeaderViewHolder.bind("MEMBERSHIPS");
                break;
            case MEMBERSHIP_ITEM:
                Membership membership = this.getMembership(position);
                MembershipViewHolder membershipViewHolder = (MembershipViewHolder) holder;
                membershipViewHolder.bind(membership, true, isMyUser, false, userProfileActivity);
                break;
        }
    }

    protected Membership getMembership(int position){
        return this.memberships.get(position - 2);
    }

    @Override
    public int getItemCount() {
        if(user == null){
            return 0;
        }
        return memberships.size() + 2;
    }

    public void handleImagePickerActivityResult(int resultCode, Intent data) {
        userInfoViewHolder.handleImagePickerActivityResult(resultCode, data)
                .subscribe((Bitmap bitmap) -> changeUserProfileImage());
    }

    public void changeUserProfileImage(){
        dataLayer.uploadUserImage(userInfoViewHolder.getImageBase64()).subscribe(myUserResponse -> {
            Toast.makeText(userProfileActivity, "upload done", Toast.LENGTH_LONG);
        });
    }
}