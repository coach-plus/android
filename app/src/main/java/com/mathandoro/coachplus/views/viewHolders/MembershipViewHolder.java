package com.mathandoro.coachplus.views.viewHolders;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Team;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Member;

public class MembershipViewHolder extends RecyclerView.ViewHolder {

    public TextView teamNameTextView;
    public TextView teamMembers;
    public ImageView teamImageView;
    public ImageView privateTeamImageView;
    public ImageView privateTeamImageBackgroundView;
    public View containerView;
    public TextView rightIconView;

    public interface MembershipViewHolderListener {
        void navigateToMembership(Membership membership);
        void joinTeam(Team team);
        void leaveTeam(Team team);
    }

    private MembershipViewHolderListener listener;

    public MembershipViewHolder(View view) {
        super(view);
        containerView = view;
        teamNameTextView = view.findViewById(R.id.team_item_team_name);
        teamImageView = view.findViewById(R.id.team_item_team_icon);
        privateTeamImageView = view.findViewById(R.id.team_item_private_icon);
        privateTeamImageBackgroundView = view.findViewById(R.id.team_item_private_icon_background);
        rightIconView = view.findViewById(R.id.team_item_right_icon);

        teamMembers = view.findViewById(R.id.team_item_members);
    }

    public void bind(Membership membership, boolean showActions, boolean isMyUser, MembershipViewHolderListener listener){
        this.listener = listener;
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
        if(showActions){
            if(isMyUser){
                rightIconView.setVisibility(View.VISIBLE);
                rightIconView.setTextColor(Color.RED);
                rightIconView.setText(R.string.fa_sign_out_alt);
                rightIconView.setOnClickListener((View view) -> listener.leaveTeam(membership.getTeam()));
                containerView.setOnClickListener((View view) -> listener.navigateToMembership(membership));
            }
            else if(membership.isJoined()){
                rightIconView.setVisibility(View.INVISIBLE);
                containerView.setOnClickListener((View view) -> listener.navigateToMembership(membership));

            }
            else{
                rightIconView.setVisibility(View.VISIBLE);
                rightIconView.setTextColor(Color.GREEN);
                rightIconView.setText(R.string.fa_sign_in_alt);
                rightIconView.setOnClickListener((View view) -> listener.joinTeam(membership.getTeam()));
            }
        }
    }
}



