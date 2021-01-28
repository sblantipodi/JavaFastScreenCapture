/*
  Configuration.java

  Firefly Luciferin, very fast Java Screen Capture software designed
  for Glow Worm Luciferin firmware.

  Copyright (C) 2021  Davide Perini

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.dpsoftware.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dpsoftware.LEDCoordinate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Configuration used in the FireflyLuciferin.yaml file, supports deep copy
 * All defaults can be manually overridden in the yaml file
 */
@NoArgsConstructor
@Getter
@Setter
public class Configuration implements Cloneable {

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    // Number of CPU Threads to use, this app is heavy multithreaded,
    // high cpu cores equals to higher framerate but big CPU usage
    // 4 Threads are enough for 24FPS on an Intel i7 5930K@4.2GHz
    // 3 thread is enough for 30FPS with GPU Hardware Acceleration and uses nearly no CPU
    private int numberOfCPUThreads;

    // WinAPI and DDUPL enables GPU Hardware Acceleration, CPU uses CPU brute force only,
    // DDUPL (Desktop Duplication API) is recommended in Win8/Win10
    public enum CaptureMethod {
        CPU,
        WinAPI,
        DDUPL,
        XIMAGESRC,
        AVFVIDEOSRC
    }

    // Windows Desktop Duplication API
    private String captureMethod;

    // Serial port to use, use AUTO for automatic port search
    // NOTE: for multi display this contain the deviceName of the MQTT device where to stream
    private String serialPort;

    // Arduino/Microcontroller config
    private String baudRate = Constants.DEFAULT_BAUD_RATE;

    // Default led matrix to use
    private String defaultLedMatrix;

    // Numbers of LEDs
    int topLed;
    int leftLed;
    int rightLed;
    int bottomLeftLed;
    int bottomRightLed;
    int bottomRowLed;

    // LED strip orientation
    String orientation;

    // used for Serial connection timeout
    private int timeout = 2000;

    // Screen resolution
    private int screenResX;
    private int screenResY;

    // OS Scaling factor example: 150%
    private int osScaling;

    // Gamma correction of 2.2 is recommended for LEDs like WS2812B or similar
    private double gamma;

    // MQTT Config params
    private String mqttServer = "";
    private String mqttTopic = "";
    private String mqttUsername = "";
    private String mqttPwd = "";
    private boolean mqttEnable = false;
    private boolean mqttStream = false;
    private boolean checkForUpdates = true;
    // Misc Tab
    private boolean autoStartCapture = false;
    private boolean eyeCare = false;
    private boolean toggleLed = false;
    private String desiredFramerate = "30";
    private String colorChooser = Constants.DEFAULT_COLOR_CHOOSER;
    private int brightness = 100;
    private int ledStartOffset = 0;
    private boolean splitBottomRow = true;
    private boolean startWithSystem = false;
    private int multiMonitor = 1;
    private int monitorNumber = 1;
    private boolean syncCheck = true;

    // LED Matrix Map
    private Map<String, LinkedHashMap<Integer, LEDCoordinate>> ledMatrix;
    private boolean extendedLog = false;

    /**
     * Constructor
     * @param fullScreenLedMatrix config matrix for LED strip
     * @param letterboxLedMatrix letterbox config matrix for LED strip
     */
    public Configuration(LinkedHashMap<Integer, LEDCoordinate> fullScreenLedMatrix, LinkedHashMap<Integer, LEDCoordinate> letterboxLedMatrix) {

        this.ledMatrix = new HashMap<>();
        ledMatrix.put(Constants.FULLSCREEN, fullScreenLedMatrix);
        ledMatrix.put(Constants.LETTERBOX, letterboxLedMatrix);

    }

    /**
     * Get the LED Matrix in use from the available list
     * @param ledMatrixInUse config matrix for LED strip
     * @return return led matrix in use
     */
    public LinkedHashMap<Integer, LEDCoordinate> getLedMatrixInUse(String ledMatrixInUse) {

        return ledMatrix.get(ledMatrixInUse);

    }

}