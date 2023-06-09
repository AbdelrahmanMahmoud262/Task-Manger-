package com.androprogramming.taskmanger.LogIn;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.androprogramming.taskmanger.Activities.MainActivity;
import com.androprogramming.taskmanger.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LogIn extends AppCompatActivity {


    EditText editTextName;

    FrameLayout frameLayout;
    RoundedImageView imageProfile;
    private String encodedImage;

    Button buttonLogIn;


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences  =getSharedPreferences("main",MODE_PRIVATE);
        if (!sharedPreferences.getString("name","").isEmpty()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageProfile.setImageBitmap(bitmap);
                    encodedImage = encodeImage(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        imageProfile = findViewById(R.id.imageProfile);
        editTextName = findViewById(R.id.editTextFirstName);
        frameLayout = findViewById(R.id.frameLayout);
        buttonLogIn  = findViewById(R.id.button);

        setLogIn();
    }

    private void setLogIn() {

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });


        buttonLogIn.setOnClickListener(view -> {
            if (editTextName.getText().toString().isEmpty()){
                editTextName.setError("Name required");
                editTextName.requestFocus();
                return;
            }

            if (editTextName.getText().toString().length() < 3){
                editTextName.setError("Name Must be more than 3 characters");
                editTextName.requestFocus();
                return;
            }

            if (encodedImage == null){
                Toast.makeText(this, "Must pick an image", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("main",MODE_PRIVATE);
            sharedPreferences.edit().putString("name",editTextName.getText().toString())
                    .putString("image",encodedImage).apply();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}