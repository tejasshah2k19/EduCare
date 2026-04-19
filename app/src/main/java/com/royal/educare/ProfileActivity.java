package com.royal.educare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.royal.educare.data.AppDatabase;
import com.royal.educare.data.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail;
    private ImageView ivProfilePic;
    private FrameLayout flAvatarContainer;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword, btnLogout;

    private int currentUserId = -1;
    private User currentUser;

    // Key for storing photo path in SharedPreferences
    private static final String KEY_PHOTO_PATH = "profile_photo_path_";

    // Photo picker launcher (no runtime permission needed for system photo picker on API 30+)
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    saveImageToInternalStorage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        flAvatarContainer = findViewById(R.id.flAvatarContainer);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        if (currentUserId == -1) {
            finish();
            return;
        }

        // Load saved photo (if any)
        loadSavedPhoto();

        // Load user profile from DB
        loadUserProfile();

        // Tap avatar to pick a new photo
        flAvatarContainer.setOnClickListener(v ->
                pickImageLauncher.launch("image/*"));

        btnChangePassword.setOnClickListener(v -> handleChangePassword());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadSavedPhoto() {
        SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
        String photoPath = prefs.getString(KEY_PHOTO_PATH + currentUserId, null);
        if (photoPath != null) {
            File file = new File(photoPath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    ivProfilePic.setImageBitmap(getRoundedBitmap(bitmap));
                }
            }
        }
    }

    private void saveImageToInternalStorage(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Open input stream from selected image URI
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream == null) return;

                // Decode to bitmap and scale it down for storage efficiency
                Bitmap original = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                if (original == null) return;

                // Scale to 200x200 for storage
                Bitmap scaled = Bitmap.createScaledBitmap(original, 200, 200, true);

                // Save to internal files dir
                File photoFile = new File(getFilesDir(), "profile_pic_" + currentUserId + ".jpg");
                OutputStream outputStream = new FileOutputStream(photoFile);
                scaled.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.flush();
                outputStream.close();

                // Save the path to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
                prefs.edit().putString(KEY_PHOTO_PATH + currentUserId, photoFile.getAbsolutePath()).apply();

                // Rounded bitmap for display
                Bitmap roundedBitmap = getRoundedBitmap(scaled);

                runOnUiThread(() -> {
                    ivProfilePic.setImageBitmap(roundedBitmap);
                    Toast.makeText(ProfileActivity.this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
                });

            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ProfileActivity.this, "Failed to save photo.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Creates a circular/rounded Bitmap for clean display in the round ImageView.
     */
    private Bitmap getRoundedBitmap(Bitmap src) {
        int size = Math.min(src.getWidth(), src.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(output);
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, (size - src.getWidth()) / 2f, (size - src.getHeight()) / 2f, paint);
        return output;
    }

    private void loadUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            currentUser = db.userDao().getUserById(currentUserId);

            if (currentUser != null) {
                runOnUiThread(() -> {
                    tvProfileName.setText(currentUser.firstName + " " + currentUser.lastName);
                    tvProfileEmail.setText(currentUser.email);
                });
            }
        });
    }

    private void handleChangePassword() {
        String currentPass = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Please fill all password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) return;

        if (!currentPass.equals(currentUser.password)) {
            Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            currentUser.password = newPass;
            db.userDao().updateUser(currentUser);

            runOnUiThread(() -> {
                Toast.makeText(ProfileActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                etCurrentPassword.setText("");
                etNewPassword.setText("");
                etConfirmPassword.setText("");
            });
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("EduCarePrefs", MODE_PRIVATE);
        prefs.edit().remove("userId").apply();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
