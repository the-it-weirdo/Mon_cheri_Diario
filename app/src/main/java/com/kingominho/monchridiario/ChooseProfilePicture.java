package com.kingominho.monchridiario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ChooseProfilePicture extends AppCompatActivity implements AccountManager.AccountManagerTaskListener {

    private static final String TAG = "ChooseProfilePicture:";

    static final String UPLOAD_PATH = "profilePictures";

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage, mButtonUpload;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_profile_picture);

        mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);
        mButtonUpload = (Button) findViewById(R.id.button_upload);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Intent intent = new Intent(ChooseProfilePicture.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        mStorageReference = FirebaseStorage.getInstance().getReference(ChooseProfilePicture.UPLOAD_PATH);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(ChooseProfilePicture.UPLOAD_PATH);

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ChooseProfilePicture.this, "Upload in Progress!!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }*/
                uploadFile();
            }
        });
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
            AccountManager accountManager = AccountManager.getInstance();
            accountManager.setAccountManagerTaskListener(this);
            accountManager.setProfilePicture(mImageUri);
        } else {
            Toast.makeText(this, "No file selected!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
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
    public void OnTaskNotSuccessful(Task task) {
        updateUI(true);
    }

    @Override
    public void OnSuccessfulProfilePictureSet() {
        mButtonUpload.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Profile Picture set successful!", Toast.LENGTH_SHORT).show();

        //Navigation.findNavController(getCurrentFocus()).navigate(R.id.nav_home);


        Intent intent = new Intent(ChooseProfilePicture.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChooseProfilePicture.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(boolean value) {
        if (value) {
            mButtonUpload.setEnabled(true);
            mButtonChooseImage.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mButtonUpload.setEnabled(false);
            mButtonChooseImage.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
