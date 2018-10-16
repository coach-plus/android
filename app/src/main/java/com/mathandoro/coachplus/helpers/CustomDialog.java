package com.mathandoro.coachplus.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class CustomDialog {

    Dialog dialog;
    public CustomDialog(Context context, int layoutId) {
        dialog = new Dialog(context);
        dialog.setContentView(layoutId);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    public void show(){
        dialog.show();
    }

    public void hide(){
        dialog.hide();
    }

    public View findViewById(int id){
        return dialog.findViewById(id);
    }
}
