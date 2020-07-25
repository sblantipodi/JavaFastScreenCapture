# Java Fast Screen Capture for PC Ambilight
Fast Screen Capture software for my [PC Ambilight](https://github.com/sblantipodi/pc_ambilight).  
_Written in Java for Arduino._
<img align="right" width="100" height="100" src="https://github.com/sblantipodi/JavaFastScreenCapture/blob/master/data/img/java_fast_screen_capture_logo.png">


[![Java CI with Maven](https://github.com/sblantipodi/JavaFastScreenCapture/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/sblantipodi/JavaFastScreenCapture/actions)
[![GitHub version](https://img.shields.io/github/v/release/sblantipodi/JavaFastScreenCapture.svg)](https://github.com/sblantipodi/JavaFastScreenCapture/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/sblantipodi/JavaFastScreenCapture/graphs/commit-activity)
[![DPsoftware](https://img.shields.io/static/v1?label=DP&message=Software&color=orange)](https://www.dpsoftware.org)


If you like **Fast Screen Capture**, give it a star, or fork it and contribute!

[![GitHub stars](https://img.shields.io/github/stars/sblantipodi/JavaFastScreenCapture.svg?style=social&label=Star)](https://github.com/sblantipodi/JavaFastScreenCapture/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/sblantipodi/JavaFastScreenCapture.svg?style=social&label=Fork)](https://github.com/sblantipodi/JavaFastScreenCapture/network)

## How To
You can build the software from the source or if you prefer you can **download the installer from [here](https://github.com/sblantipodi/JavaFastScreenCapture/releases)**.  
`Fast Screen Capture` uses `Java 14` to create the native installer, this means that you don't have to install Java or other libraries separately.
  
This software can run on any Desktop PC using Windows. Linux and MacOS support will be added later. 
To get the full `ambilight` experience you need a microcontroller connected to the PC (ex. Arduino UNO, ESP8266, ESP32, Teensy, ecc.) running my [PC Ambilight](https://github.com/sblantipodi/pc_ambilight) software.

## PC Ambilight + Java Fast Screen Capture (click to watch it on YouTube)
[![IMAGE ALT TEXT HERE](https://github.com/sblantipodi/pc_ambilight/blob/master/data/img/pc_ambilight.png)](https://www.youtube.com/watch?v=68pnR5HMCTU)  

## Configuration
As soon as you start the software it creates a `FastScreenCapture.yaml` file in your documents folder, you can configure it manually or via user interface.
`If you don't know how to configure it, just use the default settings`. 

![IMAGE ALT TEXT HERE](https://github.com/sblantipodi/JavaFastScreenCapture/blob/OptionDialog-AutoLedMatrix/data/img/settings_screen.png)

```yaml
---
numberOfCPUThreads: 3     // more threads more performance but more CPU usage
captureMethod: "DDUPL"    // WinAPI and DDUPL enables GPU Hardware Acceleration, CPU uses CPU brute force only
serialPort: "AUTO"        // use "AUTO" to autodetect Serial Port, "COM7" for COM7 
dataRate: 500000          // faster data rate helps when using more LEDs or higher framerate
timeout: 2000             // timeout in serial port detection
screenResX: 3840          // screen resolution width
screenResY: 2160          // screen resolution height
osScaling: 150            // OS scaling feature
gamma: 2.2                // gamma correction for the LED strip
mqttServer: "OPTIONAL"    // MQTT Server protocol://host:port (E.g. "tcp://192.168.1.3:1883")
mqttTopic: "OPTIONAL"     // MQTT Server Topic used to start/stop screen capture on the microcontroller
mqttUsername: "OPTIONAL"  // MQTT Server username
mqttPwd: "OPTIONAL"       // MQTT Server pwd
ledMatrix:                // Auto generated LED Matrix
  Letterbox:
    1:
      x: 2596
      y: 1590
    2:
      x: 2694
      y: 1590
  ...
```

## Why it's fast? What is the achievable framerate?
Fast Screen Capture is written in Java using AWT's Robot class, Robots is the only way to screen capture using Java (without exotic libs).  
With that thing you can almost never get above 5FPS (in 4K) because as you can see in the OpenJDK implementation, `robot.createScreenCapture()` is synchronized and the native calls it uses are pretty slow.  
Fast enough for screenshots but too slow for screen capture. If one Robot can capture at about 5FPS, what about 2 Robots in a `multi threaded producer/consumer` environment?  
What about adding `GPU Hardware Acceleration` to the mix?

## CPU load with 6 threads
With 6 threads and an i7 5930K @ 4.2GHz I can capture at 25FPS in 4K (no GPU), 12 threads gives me +30FPS.   
If you want, you can increase threads numbers variable and get even higher framerate.  

Note: performance does not increase linearly, find the sweet spot for your taste and your environment.  
`Maximum framerate` is generally achieved by setting thread number at a value greater than your CPU cores, if you  
have a 8 cores CPU, best framerate is achieved with 16 threads.  
  
![CPU LOAD](https://github.com/sblantipodi/JavaFastScreenCapture/blob/master/data/img/smashing_threads.jpg)

If you are using a slow microcontroller, capturing at a very high framerate does not help. If you right click tray icon and then click `FPS`,
you can see the output as shown in the image below. In that output you can see how fast the software is captruing the screen (producing)
and how fast your microcontroller is able to process (consume) this data.  

<p align="center">
  <img width="700" src="https://raw.githubusercontent.com/sblantipodi/JavaFastScreenCapture/master/data/img/framerate_counter_javafx_menu.jpg">
</p>

Increase `dataRate` accordingly to your microcontroller's serial speed, 115200 is generally more than enough for 30FPS and 100 LEDs. Producers framerate should not exceed the consuming one, all data that is not consumed in time, is lost.

## GPU Hardware Acceleration using Java Native Access (for Windows only) 
Screen capturing is pretty slow and very CPU intensive in Windows systems (Linux is much more efficient here),
for this reason I wrapped the Windows GDI32 C class using [Java Native Access](https://github.com/java-native-access/jna) to access Windows hardware acceleration.  

This API capture and deliver captured frames in GPU memory. It's fast but not enough for my tastes because it adds 
a little bit of lag to the mouse and is a CPU hog.  

If you are running Windows 8 or Windows 10 you can use `Desktop Duplication API (DDUPL)`, it's the fastest implementation yet, no lag, 
no stutter, very small usage of resources. DDUPL is accessed via [JNA](https://github.com/java-native-access/jna) using the [GStreamer bindings for Java](https://gstreamer.freedesktop.org/bindings/java.html).  

## TODO
- Add Linux/MacOS support, don't use on Linux or MacOS yet. 

## Credits
- Davide Perini

## Thanks To 
|  Thanks              |  For                           |
|----------------------|--------------------------------|
|<a href="https://www.jetbrains.com/"><img width="200" src="https://raw.githubusercontent.com/sblantipodi/arduino_bootstrapper/master/data/img/jetbrains.png"></a>| For the <a href="https://www.jetbrains.com/idea">IntelliJ IDEA</a> licenses.|
