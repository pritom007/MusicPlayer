package com.example.pritom.voicrtest;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Administrator on 5/2/2018.
 */

public class GesturePerformListener implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gestureLibrary = null;
    private String message;


    public GesturePerformListener(GestureLibrary gestureLibrary) {
        this.gestureLibrary = gestureLibrary;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    /* When GestureOverlayView widget capture a user gesture it will run the code in this method.
       The first parameter is the GestureOverlayView object, the second parameter store user gesture information.*/
    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {

        // Recognize the gesture and return prediction list.
        ArrayList<Prediction> predictionList = gestureLibrary.recognize(gesture);

        int size = predictionList.size();

        if(size > 0)
        {
            StringBuffer messageBuffer = new StringBuffer();

            // Get the first prediction.
            Prediction firstPrediction = predictionList.get(0);

            /* Higher score higher gesture match. */
            if(firstPrediction.score > 5)
            {
                String action = firstPrediction.name;

                messageBuffer.append("Your gesture match " + action);
            }else
            {
                messageBuffer.append("Your gesture do not match any predefined gestures.");
            }

            // Display related messages.
            Toast.makeText(gestureOverlayView.getContext(),messageBuffer.toString(),Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.putExtra("message",messageBuffer.toString());

        }
    }


}