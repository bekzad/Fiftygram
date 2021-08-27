package edu.harvard.cs50.fiftygram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileDescriptor;
import java.io.IOException;

import edu.harvard.cs50.fiftygram.databinding.ActivityMainBinding;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.MaskTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SwirlFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    ActivityMainBinding binding;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

    }

    // The result of the dialog box for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Getting a photo from photo library
    public void choosePhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                Uri uri = data.getData();
                parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                binding.imageView.setImageBitmap(image);
            }
            catch (IOException e) {
                Log.e("Bekzad", "Image not found", e);
            }
            finally {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        Log.e("Bekzad", "Problem with closing filedescriptor", e);
                    }
                }
            }
        }
    } // onActivityResult method

    public void savePhoto(View view) {
        if (binding.imageView.getDrawable() == null) {
            return;
        }
        BitmapDrawable drawable = (BitmapDrawable) binding.imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Fiftygram", "Image");
        Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
    }

    private void apply (Transformation<Bitmap> filter) {
        Glide.with(this)
                .load(image)
                .apply(RequestOptions.bitmapTransform(filter))
                .into(binding.imageView);
    }

    public void applySepia(View view) {
        apply(new SepiaFilterTransformation());
    }

    public void applyToon(View view) {
        apply(new ToonFilterTransformation());
    }

    public void applySketch(View view) {
        apply(new SketchFilterTransformation());
    }

    public void applyCropCircle(View view) {
        apply(new CropCircleWithBorderTransformation());
    }

    public void applySwirl(View view) {
        apply(new SwirlFilterTransformation());
    }
}