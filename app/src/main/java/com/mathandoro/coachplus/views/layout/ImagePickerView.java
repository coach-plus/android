package com.mathandoro.coachplus.views.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import static android.app.Activity.RESULT_OK;

public class ImagePickerView extends ConstraintLayout {

    private AppCompatImageView imageView;
    private View editBackgroundView;
    private ImageView editIconView;
    private boolean editable = false;
    private static int IMAGE_SIZE = 512;
    private final int IMAGE_QUALITY = 95;
    private ImagePickerListener listener;

    protected boolean imageSelected = false;
    private Bitmap bitmap;

    private ObservableEmitter<Bitmap> bitmapObservableEmitter;

    Observable<Bitmap> bitmapObservable = Observable.create(emitter -> {
        bitmapObservableEmitter = emitter;
    });

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ImagePickerView.this.bitmap = bitmap;
            bitmapObservableEmitter.onNext(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public ImagePickerView(Context context) {
        this(context,null);
    }

    public ImagePickerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImagePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setListener(ImagePickerListener listener){
        this.listener = listener;
    }

    public void setImage(String imageUrl){
        imageView.setPadding(0,0,0,0);
        imageView.setColorFilter(null   );
        Picasso.with(this.imageView.getContext())
                .load(imageUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.circle)
                .into(this.imageView);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        if(editable) {
            editIconView.setVisibility(View.VISIBLE);
            editBackgroundView.setVisibility(View.VISIBLE);
        }
        else {
            editIconView.setVisibility(View.GONE);
            editBackgroundView.setVisibility(View.GONE);
        }
    }


    public static void startImagePickerIntent(Activity activity){
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setRequestedSize(IMAGE_SIZE, IMAGE_SIZE)
            .setAspectRatio(1,1)
            .start(activity);
    }

    public Observable<Bitmap> onActivityResult(int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            imageSelected = true;
            Uri resultUri = result.getUri();

            imageView.setPadding(0,0,0,0);

            RequestCreator requestCreator = Picasso.with(imageView.getContext()).load(resultUri);

            requestCreator.into(target);
            requestCreator.transform(new CircleTransform()).into(imageView);

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
        }
        return bitmapObservable;
    }

    public String getSelectedImageBase64(){
        if(bitmap == null || !imageSelected){
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bos);
        byte[] bb = bos.toByteArray();
        String imageData = Base64.encodeToString(bb, Base64.DEFAULT);
        String base64ImagePrefix = "data:image/jpeg;base64,";
        return base64ImagePrefix + imageData;
    }

    public boolean isImageSelected(){
        return this.imageSelected;
    }

    public interface ImagePickerListener {
        void onPickImage();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.image_picker_view, this, false);
        inflate(getContext(),R.layout.image_picker_view,this);

        imageView = findViewById(R.id.image_picker_user_image);
        editIconView = findViewById(R.id.image_picker_edit_icon);
        editBackgroundView = findViewById(R.id.image_picker_edit_profile_picture);

        imageView.setOnClickListener((View view) -> {
            if(editable) {
                if(listener != null){
                    listener.onPickImage();
                }
            }
        });
    }
}

