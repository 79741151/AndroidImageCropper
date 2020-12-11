package com.theartofdev.edmodo.cropper.test;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.test.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
  private static final String SAVE_PIC_PATH=Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() :"/mnt/sdcard";//保存到SD卡
  private static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/FileResult/";//保存的确切位置
  public static final String PAT = "yyyy-MM-dd HH-mm-ss";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  /** Start pick image activity with chooser. */
  public void onSelectImageClick(View view) {
    CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    // handle result of CropImageActivity
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK) {
        final Uri resultUri = result.getUri();

        ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(resultUri);
        File file = uri2File(resultUri);
        try {
          Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));

//          try {
//           // bitmap = BitmapUtil.getBitmapFormUri(this,resultUri);
//
//          } catch (IOException e) {
//            e.printStackTrace();
//          }
          bitmap = BitmapUtil.decodeUri(this,resultUri,800,600);

          SimpleDateFormat sdf = new SimpleDateFormat(PAT);
          Date date = new Date();
          String dateTime = sdf.format(date);
          try {
            saveFile(bitmap, dateTime + ".jpg");
          } catch (IOException e) {
            e.printStackTrace();
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        Toast.makeText(
                this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
            .show();
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
      }
    }
  }


  private File uri2File(Uri uri) {
    String img_path;
    String[] proj = {MediaStore.Images.Media.DATA};
    Cursor actualimagecursor = managedQuery(uri, proj, null,
            null, null);
    if (actualimagecursor == null) {
      img_path = uri.getPath();
    } else {
      int actual_image_column_index = actualimagecursor
              .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      actualimagecursor.moveToFirst();
      img_path = actualimagecursor
              .getString(actual_image_column_index);
    }
    File file = new File(img_path);
    return file;
  }

  public static void saveFile(Bitmap bm, String fileName) throws IOException {
    String subForder = SAVE_REAL_PATH ;
    File foder = new File(subForder);
    if (!foder.exists()) {
      foder.mkdirs();
    }
    File myCaptureFile = new File(subForder, fileName);
    if (!myCaptureFile.exists()) {
      myCaptureFile.createNewFile();
    }
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
    bos.flush();
    bos.close();
  }
}
