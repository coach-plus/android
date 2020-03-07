package com.mathandoro.coachplus.views.EventDetail;

import android.app.Dialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;

import com.mathandoro.coachplus.R;

public class EventDetailBottomSheet extends BottomSheetDialogFragment {

    IEventDetailBottomSheetEvent listener;

    public interface IEventDetailBottomSheetEvent {
        void onSetDidAttend();
        void onSetDidNotAttend();
    }

    public void setListener(IEventDetailBottomSheetEvent listener){
        this.listener = listener;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.event_detail_bottom_sheet, null);
        dialog.setContentView(contentView);

        Button didAttendButton = dialog.findViewById(R.id.changeDidAttendButton);
        Button didNotAttendButton = dialog.findViewById(R.id.changeDidNotAttendButton);

        didAttendButton.setOnClickListener(view -> listener.onSetDidAttend());
        didNotAttendButton.setOnClickListener(v -> listener.onSetDidNotAttend());
    }
}