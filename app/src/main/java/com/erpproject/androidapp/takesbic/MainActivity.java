package com.erpproject.androidapp.takesbic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;




public class MainActivity extends AppCompatActivity {


 //   private static final int RQS_LOADIMAGE = 100;
    private static final int TAKE_PICTURE_CODE = 100;

    private Button  btnDetFace, btntake;
    private ImageView imgView;
    private Bitmap myBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 15) {
            // Call some material design APIs here
            btnDetFace = (Button) findViewById(R.id.btnDetectFace);
            imgView = (ImageView) findViewById(R.id.imgview);
            btntake = (Button) findViewById(R.id.btntakepic);
            btntake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openCamera();

                }
            });


            btnDetFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myBitmap == null) {
                        Toast.makeText(MainActivity.this,
                                "myBitmap == null",
                                Toast.LENGTH_LONG).show();
                    } else {
                        detectFace();
                        Toast.makeText(MainActivity.this,
                                "Done",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            // Implement this feature without material design
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (TAKE_PICTURE_CODE == requestCode) {
            processCameraImage(data);
        }
    }
    /*
    reference:
    https://search-codelabs.appspot.com/codelabs/face-detection
     */
    private void detectFace() {

        //Create a Paint object for drawing with
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(3);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        //Create a Canvas object for drawing on
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Detect the Faces
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).build();

        //!!!
        //Cannot resolve method setTrackingEnabled(boolean)
        //skip for now
        //faceDetector.setTrackingEnabled(false);

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        //Draw Rectangles on the Faces
        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 1, 1, myRectPaint);
        }
        imgView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

    }


    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, TAKE_PICTURE_CODE);
    }

    //
    private void processCameraImage(Intent intent) {




        myBitmap = (Bitmap) intent.getExtras().get("data");

        imgView.setImageBitmap(myBitmap);
    }
}

