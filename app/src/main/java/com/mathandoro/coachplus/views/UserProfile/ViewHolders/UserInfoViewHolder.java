package com.mathandoro.coachplus.views.UserProfile.ViewHolders;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.views.layout.ImagePickerView;

import io.reactivex.Observable;

/**
 * Created by dominik on 18.02.18.
 */

public class UserInfoViewHolder extends RecyclerView.ViewHolder {
    ImageView teamImage;
    TextView usernameText;
    TextView emailText;

    ImagePickerView imagePickerView;

    public UserInfoViewHolder(View view, ImagePickerView.ImagePickerListener listener) {
        super(view);
        teamImage = view.findViewById(R.id.image_picker_user_image);
        usernameText = view.findViewById(R.id.user_profile_username);
        emailText = view.findViewById(R.id.user_profile_email);
        imagePickerView = view.findViewById(R.id.user_profile_image);

        imagePickerView.setListener(listener);
    }

    public void bind(ReducedUser user, boolean isMyUser){
        usernameText.setText(user.getFirstname() + " " + user.getLastname());
        String imageUrl = BuildConfig.BASE_URL + "/uploads/" + user.getImage();

        imagePickerView.setImage(imageUrl, R.drawable.ic_user_black);
        imagePickerView.setEditable(isMyUser);

        if(isMyUser){
            emailText.setText(((MyReducedUser) user).getEmail());
        }
        else{
            emailText.setVisibility(View.GONE);
        }
    }

    public Observable<Bitmap> handleImagePickerActivityResult(int resultCode, Intent data) {
        return imagePickerView.onActivityResult(resultCode, data);
    }

    public String getImageBase64() {
        return imagePickerView.getSelectedImageBase64();
    }
}