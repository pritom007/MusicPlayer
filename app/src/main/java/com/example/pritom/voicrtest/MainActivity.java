package com.example.pritom.voicrtest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    private EditText metTextHint;
    private ListView mlvTextMatches;
    private Spinner msTextMatches;
    private Button mbtSpeak;

    private GestureOverlayView gestureOverlayView = null;
    private String message;
    private GestureLibrary gestureLibrary = null;

    private TextToSpeech myTTS;

    Sensor sensor;
    SensorManager sensorManager;
    boolean isLight = true;
    Button playBtn,nextBtn,previousBtn;
    SeekBar positionBar, volumbar;
    TextView elapsedTimeLabel, remainingTimeLabel, textView;
    MediaPlayer mp;
    int pauseCurrentPosition;
    int totalTime;
    //

    int songIndex = 0;
    String[] song_urls = {"https://od.lk/s/NDFfOTkzMjUxOF8/Baarish.mp3",
            "https://od.lk/s/NDFfOTkzMjUyMV8/Blind_Blake.mp3",
            "https://od.lk/s/NDFfOTkzMjUxNF8/dilbar.mp3",
            "https://od.lk/s/NDFfOTkzMjUyNV8/Here_Comes_The_Rain.mp3",
            "https://od.lk/s/NDFfOTkzMjUxNV8/Ijazat.mp3",
            "https://od.lk/s/NDFfOTkzMjUyM18/Kee_Rain.mp3",
            "https://od.lk/s/NDFfOTkzMjUyMF8/Nobody.mp3",
            "https://od.lk/s/NDFfOTkzMjUxOV8/Pal.mp3",
            "https://od.lk/s/NDFfOTkzMjUxN18/Chahunga.mp3",
            "https://od.lk/s/NDFfOTkzMjUyNF8/Rain.mp3",
            "https://od.lk/s/NDFfOTkzMjUyMl8/Sole_Mio.mp3",
            "https://od.lk/s/NDFfOTkzMjUxNl8/Tera_Zikr.mp3"};

    //Shake listen
    private ShakeListener mShaker;
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //mbtSpeak = (Button) findViewById(R.id.btSpeak);
        //checkVoiceRecognition();

        Context context = getApplicationContext();

        init(context);
        final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        gestureOverlayView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
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

                    // Display a snackbar with related messages.
                    Toast.makeText(gestureOverlayView.getContext(),messageBuffer.toString(),Toast.LENGTH_LONG).show();
                    message = messageBuffer.toString();
                    myAction(message);
                }
            }
        });
        textView = (TextView) findViewById(R.id.textView);
        playBtn = (Button) findViewById(R.id.playBtn);
        nextBtn =(Button) findViewById(R.id.nextBtn);
        previousBtn = (Button) findViewById(R.id.previousBtn);
        elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);

        //Media Player
       try {
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(getApplicationContext(),Uri.parse(song_urls[songIndex]));
            mp.prepare();
            totalTime = mp.getDuration();
        } catch (Exception e){
            Log.e("Music Player ", "onCreate: ",e);
        }

        //Position Bar
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mp.seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Volume Bar
        volumbar = (SeekBar) findViewById(R.id.volumeBar);
        volumbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumeNum = progress/100f;
                mp.setVolume(volumeNum, volumeNum);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Thread (Update PositionBar and TimeLabel)

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null){
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (Exception e){

                    }
                }
            }
        }).start();
        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
            @SuppressLint("MissingPermission")
            public void onShake()
            {
                vibe.vibrate(100);
                showToastMessage("shooken "+mShaker.getShakeCount() );
                myVoice("Don't shake me");

            }
        });


    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int currentPosition = msg.what;
            // Update positionBar
            positionBar.setProgress(currentPosition);

            //Update Labels
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime =  createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText("- "+remainingTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time/1000/60;
        int sec = time/ 1000%60;
        timeLabel = min +":";
        if(sec < 10) timeLabel+="0";
        timeLabel += sec;
        return timeLabel;

    }
    public void playBtnClick(View view)  {
        if(mp==null){
            try {
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(getApplicationContext(), Uri.parse(song_urls[songIndex]));
                mp.prepare();
                totalTime = mp.getDuration();
                mp.start();
                playBtn.setBackgroundResource(R.drawable.pause);
            }
            catch (Exception e){
                showToastMessage(e.getMessage());
            }
        }
        else if(!mp.isPlaying()){
            mp.seekTo(pauseCurrentPosition);
            mp.start();
            playBtn.setBackgroundResource(R.drawable.pause);
        }
        else if(mp.isPlaying() && mp!=null){
            mp.pause();
            pauseCurrentPosition = mp.getCurrentPosition();
            playBtn.setBackgroundResource(R.drawable.play1);
        }
        else {
            mp.start();
            playBtn.setBackgroundResource(R.drawable.pause);
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void initTextToSpeech() {

        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onInit(int i) {
                if(myTTS.getEngines().size()==0){
                    Toast.makeText(MainActivity.this, "There is no TTS engine in your device", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    myTTS.setLanguage(Locale.US);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void myVoice(String s) {
        if (Build.VERSION.SDK_INT >=21){
            myTTS.speak(s, TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else {
            myTTS.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }
    }
    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            mbtSpeak.setText("Voice recognizer not present");
            Toast.makeText(this, "Voice recognizer not present",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());

        // Given an hint to the recognizer about what the user is going to say
        //There are two form of language model available
        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //Start the Voice recognizer activity for the result.
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            //If Voice recognition is successful then it returns RESULT_OK
            if(resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                   processResult(textMatchList.get(0).toString());

                }
                //Result code for various error.
            }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client Error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network Error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @TargetApi(Build.VERSION_CODES.DONUT)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void processResult(String command) {
        command = command.toLowerCase();
        Toast.makeText(MainActivity.this, "In process", Toast.LENGTH_SHORT).show();
        if(command.indexOf("play") != -1){
            showToastMessage("playing");
            myVoice("playing");
            playBtnClick(playBtn);
        }
        else if (command.indexOf("stop") != -1){
            playBtnClick(playBtn);
        }
        else if (command.indexOf("what") != -1){
            if (command.indexOf("time") != -1){
                Date date = new Date();
                String time = DateUtils.formatDateTime(this,date.getTime(),DateUtils.FORMAT_SHOW_TIME);
                showToastMessage("The time is: "+time);
                myVoice("The time is: "+time);
            }
            else if (command.indexOf("name") != -1){
                myVoice("my name is VMusic");
                showToastMessage("my name is VMusic");
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void myAction(String message) {
        if(message.contains("yes")) {
            myTTS.speak("ok playing",TextToSpeech.QUEUE_FLUSH,null);
            showToastMessage("playing song");
            playBtnClick(playBtn);
        }
        else if(message.contains("right")){
            showToastMessage("playing next song");
            nextOrPreviousSong("next");
        }
        else if(message.contains("left")) {
            showToastMessage("playing previous song");
            nextOrPreviousSong("previous");
        }
        else
            showToastMessage("Gesture not match");
        Log.i("Gesture message", "myAction: "+message);
        
    }

    /* Initialise class or instance variables. */
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void init(Context context) {
        initTextToSpeech();
        if(gestureLibrary == null)
        {
            // Load custom gestures from gesture.txt file.
            gestureLibrary = GestureLibraries.fromRawResource(context, R.raw.gesture);

            if(!gestureLibrary.load())
            {
                Toast.makeText(MainActivity.this,"Custom gesture file load failed.",Toast.LENGTH_LONG).show();

                finish();
            }
        }

        if(gestureOverlayView == null)
        {
            gestureOverlayView = (GestureOverlayView)findViewById(R.id.gesture_overlay_view);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null){
            isLight = true;
            myVoice("Your phone doesn't have proximity");
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (sensorEvent.values[0] > 0) {
                isLight= true;
            }
            else {
                isLight = false;
                //mp.pause();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void nextSong(View view){
        nextOrPreviousSong("next");
    }
    public void previousSong(View view){
        nextOrPreviousSong("previous");
    }
    protected void nextOrPreviousSong(String orientation){
        mp.stop();
        mp.reset();
        playBtn.setBackgroundResource(R.drawable.play1);
        if (orientation.contains("next")){
            songIndex = (songIndex+1)%song_urls.length;
            try {
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(getApplicationContext(), Uri.parse(song_urls[songIndex]));
                mp.prepare();
                totalTime = mp.getDuration();
                mp.start();
                playBtn.setBackgroundResource(R.drawable.pause);
            }
            catch (Exception e){
                showToastMessage(e.getMessage());
            }

        }
        else if(orientation.contains("previous")){
            songIndex = (songIndex-1+song_urls.length)%song_urls.length;
            try {
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(getApplicationContext(), Uri.parse(song_urls[songIndex]));
                mp.prepare();
                totalTime = mp.getDuration();
                mp.start();
                playBtn.setBackgroundResource(R.drawable.pause);
            }
            catch (Exception e){
                showToastMessage(e.getMessage());
            }
        }

    }
    /**
     * Helper method to show the toast message
     **/
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
}