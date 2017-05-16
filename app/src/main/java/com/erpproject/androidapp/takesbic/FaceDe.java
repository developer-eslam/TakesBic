package com.erpproject.androidapp.takesbic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by Eslam on 5/9/2017.
 */

public class FaceDe extends FaceDetector {


    /**
     * Creates a FaceDetector, configured with the size of the images to
     * be analysed and the maximum number of faces that can be detected.
     * These parameters cannot be changed once the object is constructed.
     * Note that the width of the image must be even.
     *
     * @param width    the width of the image
     * @param height   the height of the image
     * @param maxFaces the maximum number of faces to identify
     */

    MainActivity mainActivity;
    private  static int maxFaces ;
    public FaceDe(int width, int height, int maxFaces) {
        super(width, height, maxFaces);
        this.maxFaces=maxFaces;

    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public int findFaces(Bitmap bitmap, Face[] faces) {

        if(null!=bitmap && faces!=null){
            int widthface = bitmap.getWidth();
            int heightface = bitmap.getHeight();
            FaceDetector detector = new FaceDetector(widthface, heightface,FaceDe.maxFaces);
            faces = new Face[FaceDe.maxFaces];
            Bitmap bitmap565 = Bitmap.createBitmap(widthface, heightface, Bitmap.Config.RGB_565);
            Paint ditherPaint = new Paint();
            Paint drawPaint = new Paint();

            ditherPaint.setDither(true);
            drawPaint.setColor(Color.RED);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeWidth(2);

            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap565);
            canvas.drawBitmap(bitmap, 0, 0, ditherPaint);

            int facesFound = detector.findFaces(bitmap565, faces);
            PointF midPoint = new PointF();
            float eyeDistance = 0.0f;
            float confidence = 0.0f;

            Log.i("FaceDetector", "Number of faces found: " + facesFound);

            if(facesFound > 0)
            {
                for(int index=0; index<facesFound; ++index){
                    faces[index].getMidPoint(midPoint);
                    eyeDistance = faces[index].eyesDistance();
                    confidence = faces[index].confidence();

                    Log.i("FaceDetector",
                            "Confidence: " + confidence +
                                    ", Eye distance: " + eyeDistance +
                                    ", Mid Point: (" + midPoint.x + ", " + midPoint.y + ")");

                    canvas.drawRect((int)midPoint.x - eyeDistance ,
                            (int)midPoint.y - eyeDistance ,
                            (int)midPoint.x + eyeDistance,
                            (int)midPoint.y + eyeDistance, drawPaint);
                }
            }

            String filepath = Environment.getExternalStorageDirectory() + "/facedetect" + System.currentTimeMillis() + ".jpg";

            try {
                FileOutputStream fos = new FileOutputStream(filepath);

                bitmap565.compress(Bitmap.CompressFormat.JPEG, 80, fos);

                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(mainActivity!=null) {
                ImageView imageView = (ImageView) mainActivity.findViewById(R.id.imageview);

                imageView.setImageBitmap(bitmap565);
            }
        }


        return super.findFaces(bitmap, faces);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
