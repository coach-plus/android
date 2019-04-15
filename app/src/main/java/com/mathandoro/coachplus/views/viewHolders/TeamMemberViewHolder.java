package com.mathandoro.coachplus.views.viewHolders;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.views.EventDetail.EventDetailAdapter;
import com.mathandoro.coachplus.views.EventDetail.ParticipationItem;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class TeamMemberViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    AppCompatImageView icon;
    ImageView whistle;
    ImageView whistleBackground;
    ImageButton attend;
    ImageButton dontAttend;
    TextView attendenceStatus;
    View rightPlacehoder;
    View rightInflatedView;

    public final int MAX_NAME_LENGTH = 14;
    Context context;
    int colorRed;
    int colorGreen;
    int colorBlue;
    int colorOrange;

    public interface ITeamMemberItemListener {
        void onOptionsClick();
    }

    private ITeamMemberItemListener listener;

    public TeamMemberViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.member_item_name);
        icon = view.findViewById(R.id.member_item_icon);
        whistle = view.findViewById(R.id.member_item_whistle);
        whistleBackground = view.findViewById(R.id.member_item_whistle_background);
        attend = view.findViewById(R.id.member_item_attend);
        dontAttend = view.findViewById(R.id.member_item_dont_attend);
        attendenceStatus = view.findViewById(R.id.member_item_attendence_status);
        rightPlacehoder = view.findViewById(R.id.member_item_right_container);

        context = this.itemView.getContext();

        colorRed = ResourcesCompat.getColor(context.getResources(), R.color.colorRed, null);
        colorGreen = ResourcesCompat.getColor(context.getResources(), R.color.colorGreen, null);
        colorBlue = ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null);
        colorOrange = ResourcesCompat.getColor(context.getResources(), R.color.colorOrange, null);
    }

    public TeamMemberViewHolder(View view, View rightInflatedView)  {
        this(view);
        this.rightInflatedView = rightInflatedView;
    }

    public void bindReadMode(MyReducedUser myUser, Event event, EventDetailAdapter.ItemState itemState,
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


        if(event.getStart().before(new Date())){
            int text = R.string.fa_question_circle;
            if(item.getParticipation() == null){
                text = R.string.fa_question_circle;
                attendenceStatus.setTextColor(colorBlue);
            }
            else if(item.getParticipation().DidAttend() != null){
                text = item.getParticipation().DidAttend() ? R.string.fa_check_circle : R.string.fa_times_circle;
            }
            else if(item.getParticipation().WillAttend() != null) {
                text = item.getParticipation().WillAttend() ?  R.string.fa_check_circle : R.string.fa_times_circle;
            }
            if(isBadState(item.getParticipation())){
                text = R.string.fa_exclamation_circle;
                attendenceStatus.setTextColor(colorRed);
            }
            else if(item.getParticipation() != null &&
                    item.getParticipation().WillAttend() != null && item.getParticipation().WillAttend() == false
                    ){
                attendenceStatus.setTextColor(colorOrange);
            }
            else if(item.getParticipation() != null &&
                    ((item.getParticipation().WillAttend() != null && item.getParticipation().WillAttend()) ||
                    item.getParticipation().DidAttend() != null && item.getParticipation().DidAttend())) {
                attendenceStatus.setTextColor(colorGreen);
            }
            attendenceStatus.setText(text);
        }
        else{
            int color = colorBlue;
            int text = R.string.fa_question_circle;
            if(item.getParticipation() == null){
                text = R.string.fa_question_circle;
            }
            else {
             text = item.getParticipation().WillAttend() ? R.string.fa_check_circle : R.string.fa_times_circle;
             color = item.getParticipation().WillAttend() ? colorGreen : colorOrange;
            }
            attendenceStatus.setText(text);
            attendenceStatus.setTextColor(color);
        }
    }

    private boolean isMyUser(TeamMember teamMember, MyReducedUser myUser){
        return myUser != null && myUser.get_id().equals(teamMember.getUser().get_id());
    }

    public void bindTeamViewMode(TeamMember teamMember, MyReducedUser myUser, ITeamMemberItemListener listener, boolean isCoach){
        this.listener = listener;
        this.bindGeneralInformation(teamMember, myUser);
        if(isCoach && this.isMyUser(teamMember, myUser)){
            this.rightInflatedView.setVisibility(View.GONE);
        }
        else if(isCoach){
            this.rightInflatedView.setVisibility(View.VISIBLE);
            this.rightPlacehoder.setOnClickListener((View view) -> listener.onOptionsClick());
        }
    }

    private void bindGeneralInformation(TeamMember teamMember, MyReducedUser myUser){
        ReducedUser user = teamMember.getUser();
        String username = user.getFirstname() + " " + user.getLastname();
        if(username.length() > MAX_NAME_LENGTH){
            username = user.getFirstname() + " " + user.getLastname().substring(0,1) +".";
        }
        if(this.isMyUser(teamMember, myUser)){
            username += " (" + itemView.getContext().getString(R.string.you) + ")";
        }
        name.setText(username);

        if(teamMember.getRole().equals(Role.COACH)){
            whistle.setVisibility(View.VISIBLE);
            whistleBackground.setVisibility(View.VISIBLE);
        }
        else{
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
                    .placeholder(R.drawable.ic_user_black)
                    .into(icon);
        }
        else{
            icon.setImageResource(R.drawable.ic_user_black);
        }
    }

    public void bindSetMode(ParticipationItem item, MyReducedUser myUser, IAttendenceStateChange attendenceChange){
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
        return participationItem != null &&

                (((participationItem.DidAttend() != null  // wanted to attend but didn't attend
                        && participationItem.WillAttend() != null
                        && participationItem.WillAttend() == true
                        && participationItem.DidAttend() == false))
                ||
                ( participationItem.WillAttend() == null && // didn't say anything and didn't attend
                        participationItem.DidAttend() != null &&
                        participationItem.DidAttend() == false));
    }
}