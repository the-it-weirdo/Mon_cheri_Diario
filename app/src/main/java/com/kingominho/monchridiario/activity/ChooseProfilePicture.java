package com.kingominho.monchridiario.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.manager.AccountManager;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ChooseProfilePicture extends AppCompatActivity implements AccountManager.AccountManagerTaskListener {

    private static final String TAG = "ChooseProfilePicture:";

    static final String UPLOAD_PATH = "profilePictures";

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage, mButtonUpload, mSkipImageButton;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_profile_picture);

        mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);
        mButtonUpload = (Button) findViewById(R.id.button_upload);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mSkipImageButton = (Button) findViewById(R.id.button_skip_image);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Intent intent = new Intent(ChooseProfilePicture.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openFileChooser();
                getImageFromUser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    uploadFile();
                } else {
                    Toast.makeText(getApplicationContext(), "Connect to internet" +
                            " and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSkipImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageUri = Uri.EMPTY;
                OnSuccessfulProfilePictureSet();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void getImageFromUser() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .start(this);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            updateUI(false);

            //compressing file
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "uploadFile: Image URI not found.", e);
            }
            if (bmp != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                byte[] data = baos.toByteArray();
                //Log.d(TAG, "uploadFile: data byte:" + data);
                AccountManager accountManager = AccountManager.getInstance();
                accountManager.setAccountManagerTaskListener(this);
                accountManager.setProfilePicture(data);
            }
        } else {
            Toast.makeText(this, "No file selected!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            /*mImageUri = data.getData();
            Picasso.with(this)
                    .load(mImageUri)
                    .transform(new CropCircleTransformation())
                    .into(mImageView);*/
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                Log.d(TAG, "onActivityResult: Uri: " + mImageUri.toString());
                Picasso.with(this)
                        .load(mImageUri)
                        .transform(new CropCircleTransformation())
                        .into(mImageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "File is not a picture. Please choose a picture.",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onActivityResult: ", error);
            }
        }
    }

    @Override
    public void OnFailure(Exception e) {
        updateUI(true);
        Log.e(TAG, "OnFailure: Exception occurred. ", e);
        Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OnSuccess(String message) {
        updateUI(true);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnComplete(Task task) {
        updateUI(true);
    }

    @Override
    public void OnTaskNotSuccessful(Task task, int task_id) {
        updateUI(true);
    }

    @Override
    public void OnTaskSuccessful(Task task, int task_id) {
        if (task_id == AccountManager.TASK_ID_CHANGE_PROFILE_PICTURE) {
            OnSuccessfulProfilePictureSet();
            mButtonUpload.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Profile Picture set successful!", Toast.LENGTH_SHORT).show();
        }
    }

    void OnSuccessfulProfilePictureSet() {
        Intent intent = new Intent(ChooseProfilePicture.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChooseProfilePicture.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(boolean value) {
        if (value) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mSkipImageButton.setEnabled(value);
        mButtonUpload.setEnabled(value);
        mButtonChooseImage.setEnabled(value);
    }
}
