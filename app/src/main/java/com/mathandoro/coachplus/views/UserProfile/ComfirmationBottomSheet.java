package com.mathandoro.coachplus.views.UserProfile;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Role;


public class ComfirmationBottomSheet extends BottomSheetDialogFragment {

    private IComfirmationBottomSheetListener listener;
    private String confirmationText;
    private boolean danger;


    public ComfirmationBottomSheet(){
    }

    public static ComfirmationBottomSheet newInstance(String confirmationText, boolean danger) {
        ComfirmationBottomSheet fragment = new ComfirmationBottomSheet();
        fragment.init(confirmationText, danger);
        return fragment;
    }

    public void init(String confirmationText, boolean danger){
        this.danger = danger;
        this.confirmationText = confirmationText;
    }

    public interface IComfirmationBottomSheetListener {
        void onConfirm();
        void onDecline();
    }

    public void setListener(IComfirmationBottomSheetListener listener) {
        this.listener = listener;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.confirmation_bottom_sheet, null);
        dialog.setContentView(contentView);

        TextView confirmationTextView = dialog.findViewById(R.id.confirmation_bottom_sheet_confirmation_text);
        confirmationTextView.setText(confirmationText);

        MaterialButton confirmButton = dialog.findViewById(R.id.confirmation_bottom_sheet_confirm_button);
        MaterialButton declineButton = dialog.findViewById(R.id.confirmation_bottom_sheet_decline_button);

        if(danger){
            confirmButton.setTextColor(getResources().getColor(R.color.danger));
            confirmButton.setStrokeColorResource(R.color.danger);
        }

        confirmButton.setOnClickListener((View view) -> listener.onConfirm());
        declineButton.setOnClickListener((View view) -> listener.onDecline());
    }
}
