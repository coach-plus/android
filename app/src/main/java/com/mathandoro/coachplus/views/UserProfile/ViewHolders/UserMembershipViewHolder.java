package com.mathandoro.coachplus.views.UserProfile.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.R;

/**
 * Created by dominik on 18.02.18.
 */

public class UserMembershipViewHolder extends RecyclerView.ViewHolder {
    TextView teamNameText;
    ImageView teamImage;

    public UserMembershipViewHolder(View view) {
        super(view);
        teamNameText = (TextView)view.findViewById(R.id.user_profile_membership_item_team_name); // wrong
        teamImage = (ImageView) view.findViewById(R.id.user_profile_membership_item_team_icon);
    }

    public void bind(){
        // TODO
    }
}