package com.mathandoro.coachplus.views;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.Navigation;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.UserProfile.ConfirmationBottomSheet;
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
    public static final String INTENT_PARAM_TEAM = "team";

    RadioGroup radioGroup;
    RadioButton registerTeamPublicToggleButton;
    RadioButton registerTeamPrivateToggleButton;
    TextInputEditText teamNameEditText;

    Button deleteTeamButton;
    Button saveTeamButton;

    boolean editMode = false;
    Team editableTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_team_activity);

        settings = new Settings(this);
        dataLayer = new DataLayer(this);

        if(getIntent().getExtras() != null){
            editableTeam = getIntent().getExtras().getParcelable(INTENT_PARAM_TEAM);
            editMode = editableTeam != null;
        }

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();


        imagePickerView = findViewById(R.id.team_editor_image_view);
        imagePickerView.setEditable(true);
        imagePickerView.setListener(this);

        teamNameEditText = findViewById(R.id.team_editor_text_field);

        radioGroup = findViewById(R.id.team_editor_visibility);
        registerTeamPublicToggleButton = findViewById(R.id.team_editor_public_radio_button);
        registerTeamPrivateToggleButton = findViewById(R.id.team_editor_private_radio_button);

        deleteTeamButton = findViewById(R.id.team_editor_delete_button);
        saveTeamButton = findViewById(R.id.team_editor_save_button);

        final TextView registerTeamVisibilityDescription = findViewById(R.id.registerTeamVisibilityDescription);

        radioGroup.setOnCheckedChangeListener((radioGroup1, selectedId) -> {
            if(selectedId == registerTeamPublicToggleButton.getId()){
                registerTeamVisibilityDescription.setText(R.string.team_description_public);
            }
            else{
                registerTeamVisibilityDescription.setText(R.string.team_description_private);
            }
        });

        if(editMode){
            loadExistingTeam();
            saveTeamButton.setText(R.string.register_team_update_button_text);
        }
        else {
            deleteTeamButton.setVisibility(View.INVISIBLE);
            toolbarFragment.setTitle(getString(R.string.new_team_title));
        }

        saveTeamButton.setOnClickListener((View v) -> {
            boolean isPublic = radioGroup.getCheckedRadioButtonId() == registerTeamPublicToggleButton.getId() ? true : false;
            if(editMode){
                updateTeam(editableTeam.get_id(), teamNameEditText.getText().toString(), isPublic, imagePickerView.getSelectedImageBase64());
            }
            else {
                createTeam(teamNameEditText.getText().toString(), isPublic, imagePickerView.getSelectedImageBase64());
            }
        });
    }

    private void loadExistingTeam(){
        teamNameEditText.setText(editableTeam.getName());
        toolbarFragment.setTitle("Edit " + editableTeam.getName());
        String teamImageUrl = BuildConfig.BASE_URL + "/uploads/" + editableTeam.getImage();
        imagePickerView.setImage(teamImageUrl, R.drawable.ic_tshirt_solid);

        radioGroup.check(editableTeam.isPublic() ? registerTeamPublicToggleButton.getId() : registerTeamPrivateToggleButton.getId());

        deleteTeamButton.setOnClickListener(view -> {
            final ConfirmationBottomSheet bottomSheet = ConfirmationBottomSheet.show(getSupportFragmentManager(),
                    getString(R.string.delete_team_confirmation, editableTeam.getName()), true);
            bottomSheet.setListener(new ConfirmationBottomSheet.IComfirmationBottomSheetListener() {
                @Override
                public void onConfirm() {
                    dataLayer.deleteTeam(editableTeam.get_id()).subscribe(data -> {
                        bottomSheet.dismiss();
                        Navigation.navigateToMembership(TeamRegistrationActivity.this, null);
                    }, error -> {
                        bottomSheet.dismiss();
                    });
                }

                @Override
                public void onDecline() {
                    bottomSheet.dismiss();
                }
            });
        });
    }

    private void createTeam(String teamName, boolean isPublic, String teamImageBase64){
        dataLayer.registerTeam(teamName, isPublic, teamImageBase64).subscribe(
                membership -> success(membership),
                error -> fail());
    }

    private void updateTeam(String teamId, String teamName, boolean isPublic, String selectedImageBase64){
        dataLayer.updateTeam(teamId, teamName, isPublic, selectedImageBase64).subscribe(data -> {
            success();
        }, error -> {});
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
        if(membership != null){
            returnIntent.putExtra(RETURN_PARAM_MEMBERSHIP, membership);
        }
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    void success(){
        this.success(null);
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
