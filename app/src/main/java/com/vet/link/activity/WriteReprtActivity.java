package com.vet.link.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vet.link.R;
import com.vet.link.Services.Utility;
import com.vet.link.Services.VolleyCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.vet.link.activity.MainActivity.context;

public class WriteReprtActivity extends AppCompatActivity {

    private Button btn_submit_review;
    private Button btn_attach_pic;
    private static final int SELECT_PHOTO = 0111;
    public ImageView img_place_pic1, img_place_pic2;
    private int count = 0;
    private int serverResponseCode;



    private Uri[] imageToUploadUri = new Uri[2] ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_reprt);

        btn_submit_review = (Button) findViewById(R.id.btn_submit_review);
        btn_attach_pic = (Button) findViewById(R.id.btn_attach_pic);
        img_place_pic1 = (ImageView) findViewById(R.id.img_place_pic1);
        img_place_pic2 = (ImageView) findViewById(R.id.img_place_pic2);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {

                                                     Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                                     intent.putExtra("fname",getIntent().getStringExtra("fname"));
                                                     intent.putExtra("email",getIntent().getStringExtra("email"));
                                                     intent.putExtra("phone",getIntent().getStringExtra("phone"));
                                                     startActivity(intent);
                                                     finish();


                                                 }
                                             }
        );


        Typeface myTypeface3 = Typeface.createFromAsset(context.getAssets(), "fonts/extra_bold.ttf");

        btn_submit_review.setTypeface(myTypeface3);
        btn_attach_pic.setTypeface(myTypeface3);


        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UploadFileAsync().execute("");


            }
        });

        btn_attach_pic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                if (true) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), getIntent().getStringExtra("fname")+System.currentTimeMillis()+".jpg");
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    imageToUploadUri[count] = Uri.fromFile(f);
                    startActivityForResult(cameraIntent, SELECT_PHOTO);
                } else {
                    //Toast.makeText(getApplicationContext(), "You can add maximum 3 attachments only!!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getExtras().get("data");




            if(imageToUploadUri[count] != null){
                Uri selectedImage = imageToUploadUri[count];
                getContentResolver().notifyChange(selectedImage, null);
                Bitmap reducedSizeBitmap = getBitmap(imageToUploadUri[count].getPath());




            if (reducedSizeBitmap != null && count < 2) {
                if (count == 0) {

                    findViewById(R.id.lin_place_pic).setVisibility(View.VISIBLE);
                    img_place_pic1.setVisibility(View.VISIBLE);
                    img_place_pic1.setImageBitmap(reducedSizeBitmap);
                    Log.d("Image path", imageToUploadUri[count].getPath());



                }

                if (count == 1) {
                    img_place_pic2.setVisibility(View.VISIBLE);
                    img_place_pic2.setImageBitmap(reducedSizeBitmap);
                    Log.d("Image path", imageToUploadUri[count].getPath());
                    btn_attach_pic.setClickable(false);
                }
                count++;
            }

            else{
                Toast.makeText(this,"Error while capturing Image",Toast.LENGTH_LONG).show();
            }

            }else{
                Toast.makeText(this,"Error while capturing Image",Toast.LENGTH_LONG).show();
            }






        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

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
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
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
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

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

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void onBackPressed() { }


    private void servercallToWriteReview(String user_id, String place_id, String str_review, String rating, String is_wheelchair, String is_service_dog, String date, String time, String place_name, String lat, String lng) throws Exception {

        String json_request = "{\"data\":[{\"user_id\":\"" + user_id + "\",\"place_id\":\"" + place_id + "\",\"review\":\"" + str_review + "\",\"rating\":\"" + rating + "\",\"is_wheelchair\":\"" + is_wheelchair + "\",\"is_service_dog\":\"" + is_service_dog + "\",\"date\":\"" + date + "\",\"time\":\"" + time + "\",\"place_name\":\"" + place_name + "\",\"lat\":\"" + lat + "\",\"long\":\"" + lng + "\"}]}";

        sendDataToServerForSocialLogin(json_request);
        Log.e(">", json_request);

    }

    private void sendDataToServerForSocialLogin(String json_request) throws Exception {

        String url = "http://vetlink1.pvamu.edu/mobileapp/appAP_registration2.php";
        try {
            JSONObject jsonObjectForrequest = new JSONObject(json_request);

            Utility.callProgressDialog(WriteReprtActivity.this);
            VolleyCommon.callVolley(null, WriteReprtActivity.this, url, jsonObjectForrequest, "getJsonResponseOfaddReview", Utility.dialog);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    



    private class UploadFileAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            for(Uri u:imageToUploadUri) {

                try {
                    String sourceFileUri = u.getPath();

                    HttpURLConnection conn = null;
                    DataOutputStream dos = null;
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 1 * 1024 * 1024;
                    File sourceFile = new File(sourceFileUri);

                    if (sourceFile.isFile()) {

                        try {
                            String upLoadServerUri = "http://10.0.0.174/vetlink/appAP_uploadImage.php";

                            // open a URL connection to the Servlet
                            FileInputStream fileInputStream = new FileInputStream(
                                    sourceFile);
                            URL url = new URL(upLoadServerUri);

//                            Uri.Builder builder = new Uri.Builder()
//                                    .appendQueryParameter("Filename", sourceFileUri);
//
//                            String query = builder.build().getEncodedQuery();

                            // Open a HTTP connection to the URL
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true); // Allow Inputs
                            conn.setDoOutput(true); // Allow Outputs
                            conn.setUseCaches(false); // Don't use a Cached Copy
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("ENCTYPE",
                                    "multipart/form-data");
                            conn.setRequestProperty("Content-Type",
                                    "multipart/form-data;boundary=" + boundary);
                            conn.setRequestProperty("file", sourceFileUri);


                            // Open connection for sending data
//                            String PostParam = "Filename=android";


                            dos = new DataOutputStream(conn.getOutputStream());

                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                    + sourceFileUri + "\"" + lineEnd);


                            dos.writeBytes(lineEnd);

//                            dos.writeBytes(PostParam);

                            // create a buffer of maximum size
                            bytesAvailable = fileInputStream.available();

                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            // read file and write it into form...
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            while (bytesRead > 0) {

                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math
                                        .min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0,
                                        bufferSize);

                            }

                            // send multipart form data necesssary after file
                            // data...
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens
                                    + lineEnd);

                            // Responses from the server (code and message)
                            serverResponseCode = conn.getResponseCode();
                            InputStream is = conn.getInputStream();
                            String serverResponseMessage = conn
                                    .getResponseMessage();

                            Log.d("resp", serverResponseMessage);

                            if (serverResponseCode == 200) {

                                // messageText.setText(msg);
                                //Toast.makeText(context, "File Upload Complete.", Toast.LENGTH_SHORT).show();

                                // recursiveDelete(mDirectory1);

                                InputStream responseStream = new
                                        BufferedInputStream(is);
                                BufferedReader responseStreamReader =
                                        new BufferedReader(new InputStreamReader(responseStream));
                                String line = "";
                                StringBuilder stringBuilder = new StringBuilder();
                                while ((line = responseStreamReader.readLine()) != null) {
                                    stringBuilder.append(line).append("\n");
                                }
                                responseStreamReader.close();
                                String response = stringBuilder.toString();
                                //result = reader.readLine();
                                Log.d("Rseted", response);

                            }

                            // close the streams //
                            fileInputStream.close();
                            dos.flush();
                            dos.close();

                        } catch (Exception e) {

                            // dialog.dismiss();
                            e.printStackTrace();

                        }
                        // dialog.dismiss();

                    } // End else block


                } catch (Exception ex) {
                    // dialog.dismiss();

                    ex.printStackTrace();
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

//            Intent in = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(in);
//            finish();

//            Toast.makeText(context, serverResponseCode, Toast.LENGTH_SHORT).show();
            Log.d("reslt", result);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    
    
}
