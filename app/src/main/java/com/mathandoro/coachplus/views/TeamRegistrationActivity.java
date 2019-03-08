package com.mathandoro.coachplus.views;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.layout.ImagePickerView;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.theartofdev.edmodo.cropper.CropImage;

public class TeamRegistrationActivity extends AppCompatActivity
        implements ToolbarFragment.ToolbarFragmentListener, ImagePickerView.ImagePickerListener {

    protected Settings settings;
    protected ImagePickerView imagePickerView;
    protected ToolbarFragment toolbarFragment;
    private DataLayer dataLayer;

    public static final String RETURN_PARAM_MEMBERSHIP = "membership";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_team_activity);
        settings = new Settings(this);

        dataLayer = new DataLayer(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();


        FloatingActionButton registerTeamButton = findViewById(R.id.create_team_create_button);
        imagePickerView = findViewById(R.id.teamImageView);
        imagePickerView.setEditable(true);
        imagePickerView.setListener(this);

        final EditText teamNameEditText = findViewById(R.id.teamNameEditText);
        final Switch registerTeamPublicToggleButton = findViewById(R.id.registerTeamPublicToggleButton);
        final TextView registerTeamVisibilityDescription = findViewById(R.id.registerTeamVisibilityDescription);

        registerTeamPublicToggleButton.setOnCheckedChangeListener((compoundButton,isPublic) -> {
            if(isPublic){
                registerTeamVisibilityDescription.setText(R.string.team_description_public);
            }
            else{
                registerTeamVisibilityDescription.setText(R.string.team_description_private);
            }
        });

        registerTeamButton.setOnClickListener((View v) ->
                registerTeam(teamNameEditText.getText().toString(), registerTeamPublicToggleButton.isChecked(), imagePickerView.getSelectedImageBase64())
        );
    }

    void registerTeam(String teamName, boolean isPublic, String teamImageBase64){
        dataLayer.registerTeam(teamName, isPublic, teamImageBase64).subscribe(
                membership -> {
                    success(membership);
                },
                error -> {
                    fail();
                });
    }

    @Override
    public void onPickImage() {
        this.pickImage();
    }

    private void pickImage(){
        ImagePickerView.startImagePickerIntent(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            imagePickerView.onActivityResult(resultCode, data).subscribe();
        }
    }

    void success(Membership membership){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RETURN_PARAM_MEMBERSHIP, membership);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    void fail(){
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() {
    }

}
