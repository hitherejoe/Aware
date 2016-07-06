Bourbon
=======

<p align="center">
    <img src="images/icon.png" alt="Aware Icon"/>
</p>

Aware is a simple playground for the new Google Awareness API, nothing fancy!

#What you can try out
---------------------

<p align="center">
    <img src="images/main.png" alt="Main Screen"/>
</p>

Snapshot API
------------

- Detecting the device headphone jack status
- Detecting the Weather properties of the location of the current device (temperature, humidity, feels like temperature, dew point and conditions)
- Detect Places nearby the user with the likeliness that they are in that place
- Detect what activity the user may currently be engaging in (in a vehicle, running, walking etc)
- Detect the users current location, along with accuracy / altitude etc
- Detect nearby registered beacons


Fence API
---------

- Listen for changes of the device headphone jack status
- Listen for changes in the users current activity (for example  when they're running, when they're started to run, they're beginning to stop running, when they stop running)
- Listen for changes in the users location fence (for example when they're at home, when they're entering home, when they're leaving home and when they've left home)
- Listen for a specific time frame to occur on a specific day or daily basis (for example trigger at event between 12:00 and 13:00 every day or just on fridays / wednesdays)
- Listen for when the user is in a beacon zone or when they have left a beacon zone

Requirements
------------

 - [Android SDK](http://developer.android.com/sdk/index.html).
 - Android [5.0 (API 21) ](http://developer.android.com/tools/revisions/platforms.html#5.0).
 - Android SDK Tools
 - Android SDK Build tools 23.0.2
 - Android Support Repository
 - Android Support libraries

Building
--------

To build, install and run a debug version, run this from the root of the project:

    ./gradlew app:assembleDebug