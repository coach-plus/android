package com.mathandoro.coachplus.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Membership;
import com.squareup.picasso.Picasso;

public class MembershipViewHolder extends RecyclerView.ViewHolder {

    public TextView teamNameTextView;
    public TextView teamMembers;
    public ImageView teamImageView;
    public ImageView privateTeamImageView;
    public ImageView privateTeamImageBackgroundView;
    public View containerView;

    public MembershipViewHolder(View view) {
        super(view);
        containerView = view;
        teamNameTextView = view.findViewById(R.id.team_item_team_name);
        teamImageView = view.findViewById(R.id.team_item_team_icon);
        privateTeamImageView = view.findViewById(R.id.team_item_private_icon);
        privateTeamImageBackgroundView = view.findViewById(R.id.team_item_private_icon_background);

        teamMembers = view.findViewById(R.id.team_item_members);
    }

    public void bind(Membership membership){
        teamNameTextView.setText(membership.getTeam().getName());
        if(membership.getTeam().isPublic()){
            privateTeamImageView.setVisibility(View.INVISIBLE);
            privateTeamImageBackgroundView.setVisibility(View.INVISIBLE);
        }
        else {
            privateTeamImageView.setVisibility(View.VISIBLE);
            privateTeamImageBackgroundView.setVisibility(View.VISIBLE);
        }
        String numMembersSuffix = membership.getTeam().getMemberCount() == 1 ? itemView.getResources().getString(R.string.member) : itemView.getResources().getString(R.string.members);
        teamMembers.setText(membership.getTeam().getMemberCount() + " " + numMembersSuffix);
        if(membership.getTeam().getImage() != null){
            String imageUrl = BuildConfig.BASE_URL + "/uploads/" + membership.getTeam().getImage();
            Picasso.with(teamImageView.getContext())
                    .load(imageUrl)
                    .resize(Settings.TEAM_ICON_SIZE, Settings.TEAM_ICON_SIZE)
                    .placeholder(R.drawable.circle)
                    .transform(new CircleTransform())
                    .into(teamImageView);
        }
        else {
            teamImageView.setImageResource(R.drawable.circle);
        }
    }
}



