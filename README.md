# MusicPlayer

  Now a days sensors are very common in smartphones and these sensors can be used in the apps to get better user experience. “Music Player” is one of the apps that we use in our daily life. In the modern smartphone there is at least one “music player” app is pre-installed. Apart from that there are lots of “music player” apps and software available in the apps market. Since the beginning of smartphone era the “music player” app have not changed or improved much and the user experience for this have not improved as well. In this mobile application we want to show how User Experience (UX) of a “music player” can be improved just by using the built in sensors of the smart phones. For this experiment we used accelerometer sensor, light sensor/ proximity sensor, customized gestures, and voice assistance.

# Main Features

  <b>- Voice Assistance:</b> Generally in a music player users don’t get the voice assistance app. There are some apps that provides song recognition feature but very few of them has voice assistance in them. So in our app we integrated voice assistance feature.            This assistance is not artificially intelligent but it can perform some simple tasks. For example: user can ask it to play current song, play next song, play previous song, stop/pause the current song, also can tell user the current time.
Text-to-Speech: In accordance with the voice assistance we have integrated text-to-speech function. When user gives any voice command via the voice assistance, the text-to-speech feature will speak accordingly. This feature is not so user friendly for a music player but still can be polished so that users get better experience.
  - Gesture: In this app we have used custom made gesture features. Generally android library provides “tapping”, “double tapping”, “fling” etc. as gesture. But if user wants to do other gestures then it’s not possible to do perform with the traditional gesture library. In this project we backtracked a third party app named “gesture builder” [2]. With the help of this app we custom made three gestures: 
    1) Tick (✓): plays or pauses the current song.
    2) Left to Right (->): plays previous song.
    3) Right to Left (<-): plays next song.
  
    Actually we can create any gestures and assign that to a task. So after creating the gesture from the described 3rd party app we  extracted the raw file from the app by debugging the app. And after that we integrated the gesture in our music player. The gesture extracting process will be described briefly in the upcoming part.
Accelerometer Sensor: We included shaking feature in our app. If the user shake or vibrates the app then it playlist shuffles. We used accelerometer sensor to detect the vibration. The relevant code can be found in “shakeListener.java” file. Inside the “onSensorChanged” method we sensor values of accelerometer sensor.    

  <b>- Light / Proximity Sensor:</b> Before playing the song our player check if there is light around. If there is no light the player won’t start. Some music players use proximity sensor to detect if any part of user body is near to the device, in that case music player stops. For example: if user place the phone near his ear then the music player will stop or if user flip the phone downwards then music will pause. While testing the features both sensors were giving unstable results. While using the virtual sensors in the emulator sensors worked fine but in an actual device the result was not stable. But still we included this feature and hopefully in future we will be able to develop this feature furthermore.

  <b>- Stream from Internet:</b> For our player we saved our songs in a cloud service. So after starting the app it starts to stream the song. The original plan was to use the local directory to save the music. But in that case the app size would have be bigger. So taking in consideration of that fact we saved the music/songs in the 3rd party cloud. So every time we play a new song it will take some to load. Therefore the music player take longer time to load any new song which is obviously not efficient.

# Device Requirements

  To run the app we at least need android <b>Ice Cream Sandwich OS</b>. We also need <b>light sensor, accelerometer sensor, supported gesture library</b>. We also need voice recognition engine for the text-to-speech function. Several permissions are also necessary. Such as: internet permission, read-write the local storage, wake lock permission etc. 

# Future Developments

  There are bunch of developments can be done in the future. For example the gestures, currently the gestures are hard coded that means users can’t create their own gesture and assign the feature. But in the future it is very possible to introduce this feature. From a user point of view this will be a huge improvement. Currently no music player provides such feature. If we have enough music in our library we can also introduce AI based music preference. Currently lack of data we could not introduce this feature. Since the temperature sensor also doesn’t work well we discarded our original plan of including that. Instead in future we can use 3rd party weather API and create playlist according to the temperature. We also have a plan to use the “accelerometer sensor” and detect the “tilt” of phone and use this behavior to increase or decrease the volume. We have already tested this feature but since it did not give good result we discarded this in our current version.

