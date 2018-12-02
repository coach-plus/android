package com.mathandoro.coachplus.views.viewHolders;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Role;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.views.EventDetail.EventDetailAdapter;
import com.mathandoro.coachplus.views.EventDetail.ParticipationItem;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class TeamMemberViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView icon;
    ImageView whistle;
    ImageView whistleBackground;
    ImageButton attend;
    ImageButton dontAttend;
    TextView attendenceStatus;
    public final int MAX_NAME_LENGTH = 14;
    Context context;
    int colorRed;
    int colorGreen;
    int colorBlue;

    public TeamMemberViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.member_item_name);
        icon = view.findViewById(R.id.member_item_icon);
        whistle = view.findViewById(R.id.member_item_whistle);
        whistleBackground = view.findViewById(R.id.member_item_whistle_background);
        attend = view.findViewById(R.id.member_item_attend);
        dontAttend = view.findViewById(R.id.member_item_dont_attend);
        attendenceStatus = view.findViewById(R.id.member_item_attendence_status);

        context = this.itemView.getContext();

        colorRed = ResourcesCompat.getColor(context.getResources(), R.color.colorRed, null);
        colorGreen = ResourcesCompat.getColor(context.getResources(), R.color.colorGreen, null);
        colorBlue = ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null);
    }

    public void bindReadMode(JWTUser myUser, Event event, EventDetailAdapter.ItemState itemState,
                             ParticipationItem item, IAttendenceStateChange didAttendChange){
        this.bindGeneralInformation(item.getTeamMember(), myUser);

        if(itemState == EventDetailAdapter.ItemState.SET_DID_ATTEND_STATE){
            // register click on item for state change
            // fire event: IAttendenceStateChange attendenceChange
            //boolean newState = item.getParticipation().DidAttend() == null ? item.getParticipation().WillAttend() : item.
            boolean newState = true;
            if(item.getParticipation() != null){
                if(item.getParticipation().DidAttend() != null){
                    newState = !item.getParticipation().DidAttend();
                }
                else if(item.getParticipation().WillAttend() != null){
                    newState = !item.getParticipation().WillAttend();
                }
            }
            boolean finalNewState = newState;
            itemView.setOnClickListener(view -> {
                didAttendChange.onChange(finalNewState);
            });
        }

        GradientDrawable bgShape = (GradientDrawable)attendenceStatus.getBackground();

        if(event.getStart().before(new Date())){
            String text = "";
            if(item.getParticipation() == null){
                text = "no response";
            }
            else if(item.getParticipation().DidAttend() != null){
                text = item.getParticipation().DidAttend() ? "did attend" : "didn't attend";
            }
            else if(item.getParticipation().WillAttend() != null) {
                text = item.getParticipation().WillAttend() ? "did attend" : "didn't attend";
            }
            if(isBadState(item.getParticipation())){
                text = "unexcused absence";
                bgShape.setStroke(3, colorRed);
                attendenceStatus.setTextColor(colorRed);
            }
            else{
                bgShape.setStroke(3, colorGreen);
                attendenceStatus.setTextColor(colorGreen);
            }
            attendenceStatus.setText(text);
        }
        else{
            attendenceStatus.setTextColor(colorBlue);
            bgShape.setStroke(3, colorBlue);
            String text;
            if(item.getParticipation() == null){
                text = "unknown";
            }
            else {
             text = item.getParticipation().WillAttend() ? "will attend" : "won't attend";
            }
            attendenceStatus.setText(text);
        }
    }

    private boolean isMyUser(TeamMember teamMember, JWTUser myUser){
        return myUser != null && myUser.get_id().equals(teamMember.getUser().get_id());
    }

    public void bindGeneralInformation(TeamMember teamMember, JWTUser myUser){
        ReducedUser user = teamMember.getUser();
        String username = user.getFirstname() + " " + user.getLastname();
        if(username.length() > MAX_NAME_LENGTH){
            username = user.getFirstname() + " " + user.getLastname().substring(0,1) +".";
        }
        if(this.isMyUser(teamMember, myUser)){
            username += " (" + itemView.getContext().getString(R.string.you) + ")";
        }
        name.setText(username);

        if(!teamMember.getRole().equals(Role.COACH)){
            whistle.setVisibility(View.GONE);
            whistleBackground.setVisibility(View.GONE);
        }

        String userImage = teamMember.getUser().getImage();
        if(userImage != null){
            String imageUrl = BuildConfig.BASE_URL + "/uploads/" + userImage;
            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .resize(Settings.USER_ICON_SIZE, Settings.USER_ICON_SIZE)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.circle)
                    .into(icon);
        }
        else{
            icon.setImageResource(R.drawable.circle);
        }
    }

    public void bindSetMode(ParticipationItem item, JWTUser myUser, IAttendenceStateChange attendenceChange){
        this.bindGeneralInformation(item.getTeamMember(), myUser);

        attend.setOnClickListener(view -> {
            attendenceChange.onChange(true);
        });
        dontAttend.setOnClickListener(view -> {
            attendenceChange.onChange(false);
        });

        if(item.getParticipation() != null){
            if(item.getParticipation().WillAttend() != null && item.getParticipation().WillAttend()){
                attend.setColorFilter(colorGreen);
                dontAttend.setColorFilter(null);
            }
            else if(item.getParticipation().WillAttend() != null && !item.getParticipation().WillAttend()){
                dontAttend.setColorFilter(colorRed);
                attend.setColorFilter(null);
            }
        }
    }

    private boolean isBadState(Participation participationItem){
        return participationItem == null
                ||
                (participationItem.WillAttend() == null
                        && (participationItem.DidAttend() == null
                        || (participationItem.DidAttend() != null && !participationItem.DidAttend()))
                )
                ||
                (participationItem.DidAttend() != null
                        && participationItem.WillAttend() != null
                        && participationItem.WillAttend() == true
                        && participationItem.DidAttend() == false);
    }
}