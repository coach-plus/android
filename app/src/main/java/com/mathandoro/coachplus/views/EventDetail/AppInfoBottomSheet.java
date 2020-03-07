package com.mathandoro.coachplus.views.EventDetail;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mathandoro.coachplus.R;


public class AppInfoBottomSheet extends BottomSheetDialogFragment {

    IAppInfoBottomSheetListener listener;

    public interface IAppInfoBottomSheetListener {
        void onShowThirdPartyLicenses();
        void onShowImpressum();
        void onShowDataPrivacyStatement();
        void onShowTermsOfUse();
    }

    public AppInfoBottomSheet(IAppInfoBottomSheetListener listener){
        this.listener = listener;
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.app_info_bottom_sheet, null);
        dialog.setContentView(contentView);
        String versionName = "unknown version";
        int versionCode = -1;

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView appVersionTextView = dialog.findViewById(R.id.appVersionTextView);
        appVersionTextView.setText("Coach+ " + versionName + " (" + versionCode + ")");

        Button thirdPartyLicensesButton = dialog.findViewById(R.id.thirdPartyLicenses);
        thirdPartyLicensesButton.setOnClickListener(view -> {
            listener.onShowThirdPartyLicenses();
        });

        Button impressumButton = dialog.findViewById(R.id.impressumButton);
        impressumButton.setOnClickListener(view -> {
            listener.onShowImpressum();
        });

        Button dataPrivacyButton = dialog.findViewById(R.id.dataPrivacyButton);
        dataPrivacyButton.setOnClickListener(view -> {
            listener.onShowDataPrivacyStatement();
        });

        Button termsOfUseButton = dialog.findViewById(R.id.termsOfUseButton);
        termsOfUseButton.setOnClickListener(view -> {
            listener.onShowTermsOfUse();
        });
    }
}