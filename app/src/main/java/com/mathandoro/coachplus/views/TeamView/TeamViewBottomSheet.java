package com.mathandoro.coachplus.views.TeamView;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;

import com.mathandoro.coachplus.R;

public class TeamViewBottomSheet extends BottomSheetDialogFragment {

    ITeamViewBottomSheetEvent listener;

    public interface ITeamViewBottomSheetEvent {
        void onKickUser();
        void onMakeCoach();
    }

    public void setListener(ITeamViewBottomSheetEvent listener) {
        this.listener = listener;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.team_view_bottom_sheet, null);
        dialog.setContentView(contentView);

        Button button = dialog.findViewById(R.id.team_view_bottom_sheet_make_coach);
        button.setOnClickListener(view -> {
            listener.onMakeCoach();
        });

        Button kickButton = dialog.findViewById(R.id.team_view_bottom_sheet_kick_button);
        kickButton.setOnClickListener(view -> {
            listener.onKickUser();
        });
    }
}