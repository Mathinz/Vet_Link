package com.vet.link.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.support.v7.app.AlertDialog;

import com.vet.link.R;
import com.vet.link.Services.Constant;
import com.vet.link.activity.LoginActivity;
import com.vet.link.pojo.ServerResponse;
import com.vet.link.rest.ApiClient;
import com.vet.link.rest.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.makeText;


/**

 */
public class ReportFragment extends Fragment {

    String keyW;
    String desC;
    private Button btn_submit_review;
    private EditText keywords;
    private EditText description;
    public ImageView img_place_pic1, img_place_pic2;


    String TAG = "MyTest";
    final int REQUEST_PERMISSION_KEY = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static final int GALLERY_1 = 1, CAMERA_1 = 2, GALLERY_2 = 11, CAMERA_2 = 22;

    private int count = 0;

    private String[] imageFilePath = new String[2];

    private String[] imageName = {"", ""};

    private final String IMAGE_DIRECTORY = "/app";
    ProgressDialog loading;

    public ReportFragment() {
        // Required empty public constructor
    }

    private void showPictureDialog(final int num) {

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Photo Gallery", "Camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (num == 1) {
                            Log.d(TAG, "onClick: GALLERY_1");
                            choosePhotoFromGallery(GALLERY_1);
                        } else {
                            Log.d(TAG, "onClick: GALLERY_2");
                            choosePhotoFromGallery(GALLERY_2);
                        }

                        break;
                    case 1:
                        if (num == 1) {
                            Log.d(TAG, "onClick: CAMERA_1");
                            takePhotoFromCamera(0, CAMERA_1);
                        } else {
                            Log.d(TAG, "onClick: CAMERA_2");
                            takePhotoFromCamera(1, CAMERA_2);
                        }

                        break;
                }
            }
        });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, requestCode);
    }

    private File createImageFile(int index) throws IOException {
        String imageFileName = getActivity().getIntent().getStringExtra("userid") + System.currentTimeMillis();
        File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath[index] = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: imageFilePath: " + imageFilePath[index]);
        return image;
    }

    private void takePhotoFromCamera(int index, int requestCode) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(index);

            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.vet.link.provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, requestCode);
            }
        }
    }

    public String saveImage(int index, Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, getActivity().getIntent().getStringExtra("userid") + System.currentTimeMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getContext(), new String[]{f.getPath()}, new String[]{"image/jpg"}, null);
            fo.close();
            imageFilePath[index] = f.getAbsolutePath();
            Log.d(TAG, "saveImage: imageFilePath: "+imageFilePath[index]);
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn_submit_review = (Button) getView().findViewById(R.id.btn_submit_review);
        img_place_pic1 = (ImageView) getView().findViewById(R.id.img_place_pic1);
        img_place_pic2 = (ImageView) getView().findViewById(R.id.img_place_pic2);
        keywords = getView().findViewById(R.id.edt_keywords);
        description = getView().findViewById(R.id.edt_review);

        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LoginActivity.latitude == 0.0 && LoginActivity.longitude == 0.0) {
                    LoginActivity.getLocation(getContext());
                }
                if (keywords.getText().toString().isEmpty()) {
                    makeText(getContext(), "Please enter keywords.", Toast.LENGTH_SHORT).show();
                } else if (description.getText().toString().isEmpty()) {
                    makeText(getContext(), "Please enter description.", Toast.LENGTH_SHORT).show();
                } else if (imageFilePath[0] == null) {
                    makeText(getContext(), "Please enter image 1.", Toast.LENGTH_SHORT).show();
                } else if (imageFilePath[1] == null) {
                    makeText(getContext(), "Please enter image 2.", Toast.LENGTH_SHORT).show();
                } else {
                    report();
                    Log.d(TAG, "onClick: image1: " + imageFilePath[0] + " image2: " + imageFilePath[1]);
                    Log.d(TAG, "onClick: image1 name: " + imageName[0] + " image2 name: " + imageName[1]);
                }
            }
        });

        img_place_pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.hasPermissions(getContext(), PERMISSIONS)) {
                    showPictureDialog(1);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
                }
            }
        });

        img_place_pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.hasPermissions(getContext(), PERMISSIONS)) {
                    showPictureDialog(2);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_1 && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: CAMERA_1");

            imageFilePath[0] = compressImage(imageFilePath[0]);
            Bitmap reducedSizeBitmap = getBitmap(compressImage(imageFilePath[0]));

            if (reducedSizeBitmap != null) {

                img_place_pic1.setImageBitmap(reducedSizeBitmap);

            } else {
                Log.d(TAG, "onActivityResult: Error while reducedSizeBitmap");
            }

        } else if (requestCode == CAMERA_2 && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: CAMERA_2");
            imageFilePath[1] = compressImage(imageFilePath[1]);
            Bitmap reducedSizeBitmap = getBitmap(compressImage(imageFilePath[1]));

            if (reducedSizeBitmap != null) {

                img_place_pic2.setImageBitmap(reducedSizeBitmap);


            } else {
                Log.d(TAG, "onActivityResult: Error while reducedSizeBitmap");
            }
        } else if (requestCode == GALLERY_1 && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: GALLERY_1");

            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                String path = saveImage(0, BitmapFactory.decodeFile(imgDecodableString));
                img_place_pic1.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            }
        } else if (requestCode == GALLERY_2 && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: GALLERY_2");
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                String path = saveImage(1, BitmapFactory.decodeFile(imgDecodableString));
                img_place_pic2.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {

                for (int i = 0; i < grantResults.length; i++) {
                    Log.d(TAG, "onRequestPermissionsResult: Perm " + i + (grantResults[i] == PackageManager.PERMISSION_GRANTED));
                }

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Granted");

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Not granted");
                }

            }
        }
    }

    private Bitmap getBitmap(String path) {

        Log.d(TAG, "getBitmap: " + path);
        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getActivity().getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getActivity().getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
//                Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

//            Log.d(TAG, "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
            return b;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        }
    }

    private void report() {

        keyW = keywords.getText().toString();
        desC = description.getText().toString();

        loading = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
        loading.setCancelable(false);

        File file1 = new File(imageFilePath[0]);
        File file2 = new File(imageFilePath[1]);

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file1);
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("*/*"), file2);

        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file1", file1.getName(), requestBody1);
        MultipartBody.Part fileToUpload2 = MultipartBody.Part.createFormData("file2", file2.getName(), requestBody2);

        imageName[0] = file1.getName();
        imageName[1] = file2.getName();

        RequestBody filename1 = RequestBody.create(MediaType.parse("text/plain"), file1.getName());
        RequestBody filename2 = RequestBody.create(MediaType.parse("text/plain"), file2.getName());

        Log.d(TAG, "report: imageName1: "+ imageName[0]);
        Log.d(TAG, "report: imageName2: "+ imageName[1]);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ServerResponse> call = apiService.uploadImage(fileToUpload1, filename1, fileToUpload2, filename2);
        Log.d(TAG, "uploadImage: " + call.request().url());
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.d(TAG, "uploadImage: onResponse: isSuccessful: " + response.isSuccessful());
                Log.d(TAG, "uploadImage: onResponse: code: " + response.code());
                ServerResponse serverResponse = response.body();
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Log.d(TAG, "uploadImage: onResponse: getMessage: " + serverResponse.getMessage());
                        if (serverResponse.getMessage().equals("Image Successfully Uploaded")) {

                            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                            Call<ResponseBody> uploadPojoCall = apiService.uploadData(
                                    getActivity().getIntent().getStringExtra("userid"),
                                    String.valueOf(getActivity().getIntent().getDoubleExtra("latitude", 0)),
                                    String.valueOf(getActivity().getIntent().getDoubleExtra("longitude", 0)),
                                    keyW,
                                    desC,
                                    imageName[0],
                                    imageName[1]
                            );
                            Log.d(TAG, "onResponse: imageName[0]: "+imageName[0]+" imageName[1]: "+imageName[1]);
                            Log.d(TAG, "uploadData: report: " + uploadPojoCall.request().url());
                            uploadPojoCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Log.d(TAG, "uploadData: onResponse: isSuccessful: " + response.isSuccessful());
                                    Log.d(TAG, "uploadData: onResponse: code: " + response.code());
                                    if (response.isSuccessful()) {
                                        keywords.setText(null);
                                        description.setText(null);
                                        img_place_pic1.setImageResource(R.drawable.add_photo);
                                        img_place_pic2.setImageResource(R.drawable.add_photo);
                                        imageFilePath = new String[2];
                                        imageName[0] = "";
                                        imageName[1] = "";
                                        loading.dismiss();
                                        Toast.makeText(getContext(), "Report Successfully Uploaded.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    loading.dismiss();
                                    Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "uploadData: onFailure: " + t);
                                }

                            });
                        } else {
                            loading.dismiss();
                            Toast.makeText(getContext(), "Report did not Upload.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    assert serverResponse != null;
                    Log.d(TAG, serverResponse.toString());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "uploadImage: onFailure: " + t);
            }
        });
    }

    public void refreshGallery(Context context, String filePath) {
        // ScanFile so it will be appeared on Gallery
        MediaScannerConnection.scanFile(context,
                new String[]{filePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        Log.d(TAG, "compressImage: "+filename);
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + getActivity().getIntent().getStringExtra("userid") + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}
