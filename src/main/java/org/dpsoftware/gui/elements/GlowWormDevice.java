/*
  GlowWormDevice.java

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
package org.dpsoftware.gui.elements;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *  A class that map a device running Glow Worm Luciferin firmware
 */
public class GlowWormDevice {

    private final SimpleStringProperty deviceName = new SimpleStringProperty("");
    private final SimpleStringProperty deviceIP = new SimpleStringProperty("");
    private final SimpleStringProperty deviceVersion = new SimpleStringProperty("");
    private final SimpleStringProperty deviceBoard = new SimpleStringProperty("");
    private final SimpleStringProperty mac = new SimpleStringProperty("");
    private final SimpleStringProperty gpio = new SimpleStringProperty("");
    private final SimpleStringProperty numberOfLEDSconnected = new SimpleStringProperty("");
    private final SimpleStringProperty lastSeen = new SimpleStringProperty("");
    private final SimpleStringProperty firmwareType = new SimpleStringProperty("");

    public GlowWormDevice() {
        this("", "", "", "", "", "", "", "", "");
    }

    public GlowWormDevice(String deviceName, String deviceIP, String deviceVersion, String deviceBoard,
                          String mac, String gpio, String numberOfLEDSconnected, String lastSeen, String firmwareType) {
        setDeviceName(deviceName);
        setDeviceIP(deviceIP);
        setDeviceVersion(deviceVersion);
        setDeviceBoard(deviceBoard);
        setMac(mac);
        setGpio(gpio);
        setNumberOfLEDSconnected(numberOfLEDSconnected);
        setLastSeen(lastSeen);
        setFirmwareType(firmwareType);
    }

    public String getDeviceName() {
        return deviceName.get();
    }

    public void setDeviceName(String deviceNameStr) {
        deviceName.set(deviceNameStr);
    }

    public StringProperty deviceNameProperty() {
        return deviceName;
    }

    public String getDeviceIP() {
        return deviceIP.get();
    }

    public void setDeviceIP(String deviceIPStr) {
        deviceIP.set(deviceIPStr);
    }

    public StringProperty deviceIPProperty() {
        return deviceIP;
    }

    public String getDeviceVersion() {
        return deviceVersion.get();
    }

    public void setDeviceVersion(String deviceVersionStr) {
        deviceVersion.set(deviceVersionStr);
    }

    public StringProperty deviceVersionProperty() {
        return deviceVersion;
    }

    public String getDeviceBoard() {
        return deviceBoard.get();
    }

    public void setDeviceBoard(String deviceBoardStr) {
        deviceBoard.set(deviceBoardStr);
    }

    public StringProperty deviceBoardProperty() {
        return deviceBoard;
    }

    public String getMac() {
        return mac.get();
    }

    public void setMac(String macStr) {
        mac.set(macStr);
    }

    public StringProperty macProperty() {
        return mac;
    }

    public String getGpio() {
        return gpio.get();
    }

    public void setGpio(String gpioStr) {
        gpio.set(gpioStr);
    }

    public StringProperty gpioProperty() {
        return gpio;
    }

    public String getNumberOfLEDSconnected() {
        return numberOfLEDSconnected.get();
    }

    public void setNumberOfLEDSconnected(String numberOfLEDSconnectedStr) {
        numberOfLEDSconnected.set(numberOfLEDSconnectedStr);
    }

    public StringProperty numberOfLEDSconnectedProperty() {
        return numberOfLEDSconnected;
    }

    public String getLastSeen() {
        return lastSeen.get();
    }

    public void setLastSeen(String lastSeenStr) {
        lastSeen.set(lastSeenStr);
    }

    public StringProperty lastSeenProperty() {
        return lastSeen;
    }

    public String getFirmwareType() {
        return firmwareType.get();
    }

    public void setFirmwareType(String firmwareTypeStr) {
        firmwareType.set(firmwareTypeStr);
    }

    public StringProperty firmwareTypeProperty() {
        return firmwareType;
    }

}
