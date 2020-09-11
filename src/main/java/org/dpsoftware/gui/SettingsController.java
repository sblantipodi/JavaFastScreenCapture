/*
  SettingsController.java

  Firefly Luciferin, very fast Java Screen Capture software designed
  for Glow Worm Luciferin firmware.

  Copyright (C) 2020  Davide Perini

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
package org.dpsoftware.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.dpsoftware.FireflyLuciferin;
import org.dpsoftware.LEDCoordinate;
import org.dpsoftware.StorageManager;
import org.dpsoftware.config.Configuration;
import org.dpsoftware.config.Constants;
import org.dpsoftware.gui.elements.GlowWormDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @FXML private TextField screenWidth;
    @FXML private TextField screenHeight;
    @FXML private ComboBox<String> scaling;
    @FXML private ComboBox<String> gamma;
    @FXML private ComboBox<Configuration.WindowsCaptureMethod> captureMethod;
    @FXML private ComboBox<Configuration.LinuxCaptureMethod> linuxCaptureMethod;
    @FXML private TextField numberOfThreads;
    @FXML private Button saveLedButton;
    @FXML private Button playButton;
    @FXML private Button saveMQTTButton;
    @FXML private Button saveMiscButton;
    @FXML private Button saveSettingsButton;
    @FXML private Button saveDeviceButton;
    @FXML private Button showTestImageButton;
    @FXML private ComboBox<String> serialPort;
    @FXML private ComboBox<String> aspectRatio;
    @FXML private TextField mqttHost;
    @FXML private TextField mqttPort;
    @FXML private TextField mqttTopic;
    @FXML private TextField mqttUser;
    @FXML private PasswordField mqttPwd;
    @FXML private CheckBox mqttEnable;
    @FXML private CheckBox mqttStream;
    @FXML private CheckBox checkForUpdates;
    @FXML private TextField topLed;
    @FXML private TextField leftLed;
    @FXML private TextField rightLed;
    @FXML private TextField bottomLeftLed;
    @FXML private TextField bottomRightLed;
    @FXML private ComboBox<String> orientation;
    @FXML private Label producerLabel;
    @FXML private Label consumerLabel;
    @FXML private Label version;
    @FXML private final StringProperty producerValue = new SimpleStringProperty("");
    @FXML private final StringProperty consumerValue = new SimpleStringProperty("");
    @FXML private TableView<GlowWormDevice> deviceTable;
    @FXML private TableColumn<GlowWormDevice, String> deviceNameColumn;
    @FXML private TableColumn<GlowWormDevice, String> deviceIPColumn;
    @FXML private TableColumn<GlowWormDevice, String> deviceVersionColumn;
    @FXML private Label versionLabel;
    public static ObservableList<GlowWormDevice> deviceTableData = FXCollections.observableArrayList();
    @FXML private CheckBox autoStart;
    @FXML private CheckBox eyeCare;
    @FXML private ComboBox<String> framerate;
    @FXML private ColorPicker colorPicker;
    @FXML private ToggleButton toggleLed;
    @FXML private Slider brightness;
    Image controlImage;
    ImageView imageView;


    /**
     * Initialize controller with system's specs
     */
    @FXML
    protected void initialize() {

        Platform.setImplicitExit(false);

        scaling.getItems().addAll("100%", "125%", "150%", "175%", "200%", "225%", "250%", "300%", "350%");
        gamma.getItems().addAll("1.0", "1.8", "2.0", "2.2", "2.4", "4", "5", "6", "8", "10");
        serialPort.getItems().add(Constants.SERIAL_PORT_AUTO);
        if (com.sun.jna.Platform.isWindows()) {
            for (int i=0; i<=256; i++) {
                serialPort.getItems().add(Constants.SERIAL_PORT_COM + i);
            }
            captureMethod.getItems().addAll(Configuration.WindowsCaptureMethod.DDUPL, Configuration.WindowsCaptureMethod.WinAPI, Configuration.WindowsCaptureMethod.CPU);
        } else {
            if (FireflyLuciferin.communicationError) {
                controlImage = new Image(this.getClass().getResource(Constants.IMAGE_CONTROL_GREY).toString(), true);
            } else if (FireflyLuciferin.RUNNING) {
                controlImage = new Image(this.getClass().getResource(Constants.IMAGE_CONTROL_PLAY).toString(), true);
            } else {
                controlImage = new Image(this.getClass().getResource(Constants.IMAGE_CONTROL_LOGO).toString(), true);
            }
            imageView = new ImageView(controlImage);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            playButton.setGraphic(imageView);
            for (int i=0; i<=256; i++) {
                serialPort.getItems().add(Constants.SERIAL_PORT_TTY + i);
            }
            linuxCaptureMethod.getItems().addAll(Configuration.LinuxCaptureMethod.XIMAGESRC);
        }
        orientation.getItems().addAll(Constants.CLOCKWISE, Constants.ANTICLOCKWISE);
        aspectRatio.getItems().addAll(Constants.FULLSCREEN, Constants.LETTERBOX);
        framerate.getItems().addAll("10 FPS", "20 FPS", "30 FPS", "40 FPS", "50 FPS", "60 FPS", Constants.UNLOCKED);
        StorageManager sm = new StorageManager();
        Configuration currentConfig = sm.readConfig();
        showTestImageButton.setVisible(currentConfig != null);
        setSaveButtonText(currentConfig);
        // Init default values
        initDefaultValues(currentConfig);
        // Init tooltips
        setTooltips(currentConfig);
        // Force numeric fields
        setNumericTextField();
        runLater();
        // Device table
        deviceNameColumn.setCellValueFactory(cellData -> cellData.getValue().deviceNameProperty());
        deviceIPColumn.setCellValueFactory(cellData -> cellData.getValue().deviceIPProperty());
        deviceVersionColumn.setCellValueFactory(cellData -> cellData.getValue().deviceVersionProperty());
        deviceTable.setItems(getDeviceTableData());
        initListeners(currentConfig);

    }

    /**
     * Run Later after GUI Init
     */
    private void runLater() {

        if (com.sun.jna.Platform.isWindows()) {
            Platform.runLater(() -> orientation.requestFocus());
        } else {
            producerLabel.textProperty().bind(producerValueProperty());
            consumerLabel.textProperty().bind(consumerValueProperty());
            version.setText(Constants.BY_DAVIDE.replaceAll(Constants.VERSION, Constants.FIREFLY_LUCIFERIN_VERSION));
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    setProducerValue(Constants.PRODUCING + FireflyLuciferin.FPS_PRODUCER + " " + Constants.FPS);
                    setConsumerValue(Constants.CONSUMING + FireflyLuciferin.FPS_CONSUMER + " " + Constants.FPS);
                }
            }.start();
        }

    }

    /**
     * Init all the settings listener
     * @param currentConfig stored config
     */
    private void initListeners(Configuration currentConfig) {

        // Toggle LED button listener
        toggleLed.setOnAction(e -> {
            if ((toggleLed.isSelected())) {
                toggleLed.setText(Constants.TURN_LED_OFF);
                turnOnLEDs(currentConfig, true);
            } else {
                toggleLed.setText(Constants.TURN_LED_ON);
                turnOffLEDs(currentConfig);
            }
        });
        // Color picker listener
        EventHandler<ActionEvent> event = e -> turnOnLEDs(currentConfig, true);
        colorPicker.setOnAction(event);
        // Gamma can be changed on the fly
        gamma.valueProperty().addListener((ov, t, t1) -> FireflyLuciferin.config.setGamma(Double.parseDouble(t1)));
        brightness.valueProperty().addListener((ov, old_val, new_val) -> turnOnLEDs(currentConfig, false));

    }

    /**
     * Init Save Button Text
     * @param currentConfig stored config
     */
    private void setSaveButtonText(Configuration currentConfig) {

        if (currentConfig == null) {
            saveLedButton.setText(Constants.SAVE);
            saveSettingsButton.setText(Constants.SAVE);
            saveMQTTButton.setText(Constants.SAVE);
            saveMiscButton.setText(Constants.SAVE);
            saveDeviceButton.setText(Constants.SAVE);
            if (com.sun.jna.Platform.isWindows()) {
                saveLedButton.setPrefWidth(95);
                saveSettingsButton.setPrefWidth(95);
                saveMQTTButton.setPrefWidth(95);
                saveMiscButton.setPrefWidth(95);
                saveDeviceButton.setPrefWidth(95);
            } else {
                saveLedButton.setPrefWidth(125);
                saveSettingsButton.setPrefWidth(125);
                saveMQTTButton.setPrefWidth(125);
                saveMiscButton.setPrefWidth(125);
                saveDeviceButton.setPrefWidth(125);
            }
        } else {
            saveLedButton.setText(Constants.SAVE_AND_CLOSE);
            saveSettingsButton.setText(Constants.SAVE_AND_CLOSE);
            saveMQTTButton.setText(Constants.SAVE_AND_CLOSE);
            saveMiscButton.setText(Constants.SAVE_AND_CLOSE);
            saveDeviceButton.setText(Constants.SAVE_AND_CLOSE);
        }

    }

    /**
     * Init form values
     */
    void initDefaultValues(Configuration currentConfig) {

        versionLabel.setText(Constants.FIREFLY_LUCIFERIN + " (v" + Constants.FIREFLY_LUCIFERIN_VERSION + ")");
        brightness.setMin(0);
        brightness.setMax(100);
        brightness.setMajorTickUnit(10);
        brightness.setMinorTickCount(5);
        brightness.setShowTickMarks(true);
        brightness.setBlockIncrement(10);
        brightness.setShowTickLabels(true);

        if (currentConfig == null) {
            // Get OS scaling using JNA
            GraphicsConfiguration screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            AffineTransform screenInfo = screen.getDefaultTransform();
            double scaleX = screenInfo.getScaleX();
            double scaleY = screenInfo.getScaleY();
            // Get screen resolution
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenWidth.setText(String.valueOf((int) (screenSize.width * scaleX)));
            screenHeight.setText(String.valueOf((int) (screenSize.height * scaleY)));
            scaling.setValue(((int) (screenInfo.getScaleX() * 100)) + Constants.PERCENT);
            if (com.sun.jna.Platform.isWindows()) {
                captureMethod.setValue(Configuration.WindowsCaptureMethod.DDUPL);
            } else {
                linuxCaptureMethod.setValue(Configuration.LinuxCaptureMethod.XIMAGESRC);
            }
            gamma.setValue(Constants.GAMMA_DEFAULT);
            serialPort.setValue(Constants.SERIAL_PORT_AUTO);
            numberOfThreads.setText("1");
            aspectRatio.setValue(Constants.FULLSCREEN);
            framerate.setValue("30 FPS");
            mqttHost.setText(Constants.DEFAULT_MQTT_HOST);
            mqttPort.setText(Constants.DEFAULT_MQTT_PORT);
            mqttTopic.setText(Constants.DEFAULT_MQTT_TOPIC);
            orientation.setValue(Constants.CLOCKWISE);
            topLed.setText("33");
            leftLed.setText("18");
            rightLed.setText("18");
            bottomLeftLed.setText("13");
            bottomRightLed.setText("13");
            checkForUpdates.setSelected(true);
            toggleLed.setSelected(false);
            brightness.setValue(255);
        } else {
            initValuesFromSettingsFile(currentConfig);
        }
        deviceTable.setPlaceholder(new Label("No devices found"));

    }

    /**
     * Init form values by reading existing config file
     * @param currentConfig existing
     */
    private void initValuesFromSettingsFile(Configuration currentConfig) {

        screenWidth.setText(String.valueOf(currentConfig.getScreenResX()));
        screenHeight.setText(String.valueOf(currentConfig.getScreenResY()));
        scaling.setValue(currentConfig.getOsScaling() + Constants.PERCENT);
        if (com.sun.jna.Platform.isWindows()) {
            captureMethod.setValue(Configuration.WindowsCaptureMethod.valueOf(currentConfig.getCaptureMethod()));
        } else {
            linuxCaptureMethod.setValue(Configuration.LinuxCaptureMethod.valueOf(currentConfig.getCaptureMethod()));
        }
        gamma.setValue(String.valueOf(currentConfig.getGamma()));
        serialPort.setValue(currentConfig.getSerialPort());
        numberOfThreads.setText(String.valueOf(currentConfig.getNumberOfCPUThreads()));
        aspectRatio.setValue(currentConfig.getDefaultLedMatrix());
        framerate.setValue(currentConfig.getDesiredFramerate() + ((currentConfig.getDesiredFramerate().equals(Constants.UNLOCKED)) ? "" : " FPS"));
        mqttHost.setText(currentConfig.getMqttServer().substring(0, currentConfig.getMqttServer().lastIndexOf(":")));
        mqttPort.setText(currentConfig.getMqttServer().substring(currentConfig.getMqttServer().lastIndexOf(":") + 1));
        mqttTopic.setText(currentConfig.getMqttTopic());
        mqttUser.setText(currentConfig.getMqttUsername());
        mqttPwd.setText(currentConfig.getMqttPwd());
        mqttEnable.setSelected(currentConfig.isMqttEnable());
        autoStart.setSelected(currentConfig.isAutoStartCapture());
        eyeCare.setSelected(currentConfig.isEyeCare());
        mqttStream.setSelected(currentConfig.isMqttStream());
        checkForUpdates.setSelected(currentConfig.isCheckForUpdates());
        orientation.setValue(currentConfig.getOrientation());
        topLed.setText(String.valueOf(currentConfig.getTopLed()));
        leftLed.setText(String.valueOf(currentConfig.getLeftLed()));
        rightLed.setText(String.valueOf(currentConfig.getRightLed()));
        bottomLeftLed.setText(String.valueOf(currentConfig.getBottomLeftLed()));
        bottomRightLed.setText(String.valueOf(currentConfig.getBottomRightLed()));
        String[] color = (FireflyLuciferin.config.getColorChooser().equals(Constants.DEFAULT_COLOR_CHOOSER)) ?
                currentConfig.getColorChooser().split(",") : FireflyLuciferin.config.getColorChooser().split(",");
        colorPicker.setValue(Color.rgb(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2]), Double.parseDouble(color[3])/255));
        if ((FireflyLuciferin.config.isToggleLed())) {
            toggleLed.setText(Constants.TURN_LED_OFF);
        } else {
            toggleLed.setText(Constants.TURN_LED_ON);
        }
        toggleLed.setSelected(FireflyLuciferin.config.isToggleLed());
        brightness.setValue(currentConfig.getBrightness());

    }

    /**
     * Save button event
     * @param e event
     */
    @FXML
    public void save(InputEvent e) {

        // No config found, init with a default config
        LEDCoordinate ledCoordinate = new LEDCoordinate();
        LinkedHashMap<Integer, LEDCoordinate> ledFullScreenMatrix = ledCoordinate.initFullScreenLedMatrix(Integer.parseInt(screenWidth.getText()),
                Integer.parseInt(screenHeight.getText()), Integer.parseInt(bottomRightLed.getText()), Integer.parseInt(rightLed.getText()),
                Integer.parseInt(topLed.getText()), Integer.parseInt(leftLed.getText()), Integer.parseInt(bottomLeftLed.getText()));
        LinkedHashMap<Integer, LEDCoordinate> ledLetterboxMatrix = ledCoordinate.initLetterboxLedMatrix(Integer.parseInt(screenWidth.getText()),
                Integer.parseInt(screenHeight.getText()), Integer.parseInt(bottomRightLed.getText()), Integer.parseInt(rightLed.getText()),
                Integer.parseInt(topLed.getText()), Integer.parseInt(leftLed.getText()), Integer.parseInt(bottomLeftLed.getText()));

        Configuration config = new Configuration(ledFullScreenMatrix,ledLetterboxMatrix);
        config.setNumberOfCPUThreads(Integer.parseInt(numberOfThreads.getText()));
        if (com.sun.jna.Platform.isWindows()) {
            switch (captureMethod.getValue()) {
                case DDUPL -> config.setCaptureMethod(Configuration.WindowsCaptureMethod.DDUPL.name());
                case WinAPI -> config.setCaptureMethod(Configuration.WindowsCaptureMethod.WinAPI.name());
                case CPU -> config.setCaptureMethod(Configuration.WindowsCaptureMethod.CPU.name());
            }
        } else {
            if (linuxCaptureMethod.getValue() == Configuration.LinuxCaptureMethod.XIMAGESRC) {
                config.setCaptureMethod(Configuration.LinuxCaptureMethod.XIMAGESRC.name());
            }
        }
        config.setSerialPort(serialPort.getValue());
        config.setScreenResX(Integer.parseInt(screenWidth.getText()));
        config.setScreenResY(Integer.parseInt(screenHeight.getText()));
        config.setOsScaling(Integer.parseInt((scaling.getValue()).replace(Constants.PERCENT,"")));
        config.setGamma(Double.parseDouble(gamma.getValue()));
        config.setSerialPort(serialPort.getValue());
        config.setDefaultLedMatrix(aspectRatio.getValue());
        config.setDesiredFramerate(framerate.getValue().equals(Constants.UNLOCKED) ? framerate.getValue() : framerate.getValue().substring(0,2));
        config.setMqttServer(mqttHost.getText() + ":" + mqttPort.getText());
        config.setMqttTopic(mqttTopic.getText());
        config.setMqttUsername(mqttUser.getText());
        config.setMqttPwd(mqttPwd.getText());
        config.setMqttEnable(mqttEnable.isSelected());
        config.setEyeCare(eyeCare.isSelected());
        config.setAutoStartCapture(autoStart.isSelected());
        config.setMqttStream(mqttStream.isSelected());
        config.setCheckForUpdates(checkForUpdates.isSelected());
        config.setTopLed(Integer.parseInt(topLed.getText()));
        config.setLeftLed(Integer.parseInt(leftLed.getText()));
        config.setRightLed(Integer.parseInt(rightLed.getText()));
        config.setBottomLeftLed(Integer.parseInt(bottomLeftLed.getText()));
        config.setBottomRightLed(Integer.parseInt(bottomRightLed.getText()));
        config.setOrientation(orientation.getValue());
        config.setToggleLed(toggleLed.isSelected());
        config.setColorChooser((int)(colorPicker.getValue().getRed()*255) + "," + (int)(colorPicker.getValue().getGreen()*255) + ","
                + (int)(colorPicker.getValue().getBlue()*255) + "," + (int)(colorPicker.getValue().getOpacity()*255));
        config.setBrightness((int)(brightness.getValue()/100 *255));

        try {
            StorageManager sm = new StorageManager();
            sm.writeConfig(config);
            boolean firstStartup = FireflyLuciferin.config == null;
            FireflyLuciferin.config = config;
            if (!firstStartup) {
                exit();
            } else {
                cancel(e);
            }
        } catch (IOException ioException) {
            logger.error("Can't write config file.");
        }

    }

    /**
     * Save and Exit button event
     */
    @FXML
    public void exit() {

        if (FireflyLuciferin.guiManager != null) {
            FireflyLuciferin.guiManager.stopCapturingThreads();
        }
        System.exit(0);

    }

    /**
     * Cancel button event
     */
    @FXML
    public void cancel(InputEvent e) {

        final Node source = (Node) e.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();

    }

    /**
     * Show a canvas containing a test image for the LED Matrix in use
     * @param e event
     */
    @FXML
    public void showTestImage(InputEvent e) {

        StorageManager sm = new StorageManager();
        Configuration currentConfig = sm.readConfig();

        final Node source = (Node) e.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        Group root = new Group();
        Scene s;
        if (com.sun.jna.Platform.isWindows()) {
            s = new Scene(root, 330, 400, Color.BLACK);
        } else {
            s = new Scene(root, currentConfig.getScreenResX(), currentConfig.getScreenResY(), Color.BLACK);
        }
        int scaleRatio = currentConfig.getOsScaling();
        Canvas canvas = new Canvas((scaleResolution(currentConfig.getScreenResX(), scaleRatio)),
                (scaleResolution(currentConfig.getScreenResY(), scaleRatio)));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);

        // Hide canvas on key pressed
        canvas.setOnKeyPressed(t -> {
            stage.setFullScreen(false);
            stage.hide();
            FireflyLuciferin.guiManager.showSettingsDialog();
        });

        drawTestShapes(gc, currentConfig);

        Text fireflyLuciferin = new Text(Constants.FIREFLY_LUCIFERIN);
        fireflyLuciferin.setFill(Color.CHOCOLATE);
        fireflyLuciferin.setStyle("-fx-font-weight: bold");
        fireflyLuciferin.setFont(Font.font(java.awt.Font.MONOSPACED, 60));
        Effect glow = new Glow(1.0);
        fireflyLuciferin.setEffect(glow);
        final int textPositionX = (int) ((scaleResolution(currentConfig.getScreenResX(),scaleRatio)/2) - (fireflyLuciferin.getLayoutBounds().getWidth()/2));
        fireflyLuciferin.setX(textPositionX);
        fireflyLuciferin.setY(scaleResolution((currentConfig.getScreenResY()/2), scaleRatio));
        root.getChildren().add(fireflyLuciferin);

        root.getChildren().add(canvas);
        stage.setScene(s);
        stage.show();
        stage.setFullScreen(true);

    }

    /**
     * Open browser to the GitHub project page
     * @param link GitHub
     */
    @FXML
    public void onMouseClickedGitHubLink(ActionEvent link) {

        FireflyLuciferin.guiManager.surfToGitHub();

    }

    /**
     * Start and stop capturing
     * @param e InputEvent
     */
    @FXML
    public void onMouseClickedPlay(InputEvent e) {

        controlImage = new Image(this.getClass().getResource(Constants.IMAGE_CONTROL_GREY).toString(), true);
        if (!FireflyLuciferin.communicationError) {
            if (FireflyLuciferin.RUNNING) {
                controlImage = new Image(this.getClass().getResource(Constants.IMAGE_CONTROL_LOGO).toString(), true);
            } else {
                controlImage = new Image(this.getClass().getResource(Constants.IMAGE_CONTROL_PLAY).toString(), true);
            }
            imageView = new ImageView(controlImage);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            playButton.setGraphic(imageView);
            if (FireflyLuciferin.RUNNING) {
                FireflyLuciferin.guiManager.stopCapturingThreads();
            } else {
                FireflyLuciferin.guiManager.startCapturingThreads();
            }
        }

    }

    /**
     * Display a canvas, useful to test LED matrix
     * @param gc graphics canvas
     * @param conf stored config
     */
    private void drawTestShapes(GraphicsContext gc, Configuration conf) {

        LinkedHashMap<Integer, LEDCoordinate> ledMatrix = conf.getLedMatrixInUse(conf.getDefaultLedMatrix());

        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(10);
        gc.stroke();

        int scaleRatio = conf.getOsScaling();
        AtomicInteger ledDistance = new AtomicInteger();
        ledMatrix.forEach((key, coordinate) -> {

            int colorToUse = key;
            if (key > 3) {
                while (colorToUse > 3) {
                    colorToUse -= 3;
                }
            }
            switch (colorToUse) {
                case 1 -> gc.setFill(Color.RED);
                case 2 -> gc.setFill(Color.GREEN);
                default -> gc.setFill(Color.BLUE);
            }

            String ledNum;
            if (Constants.CLOCKWISE.equals(conf.getOrientation())) {
                ledNum = "#" + ((conf.getBottomRightLed()+conf.getRightLed()+conf.getTopLed()+conf.getLeftLed()+conf.getBottomLeftLed()) - (key-1));
            } else {
                ledNum = "#" + key;
            }
            int twelveX = scaleResolution(conf.getScreenResX(), scaleRatio) / 12;

            if (key <= conf.getBottomRightLed()) { // Bottom right
                if (ledDistance.get() == 0) {
                    ledDistance.set(scaleResolution(ledMatrix.get(key + 1).getX(), scaleRatio) - scaleResolution(coordinate.getX(), scaleRatio));
                }
                gc.fillRect(scaleResolution(coordinate.getX(), scaleRatio)+10, scaleResolution(coordinate.getY(), scaleRatio),
                        ledDistance.get() - 10, scaleResolution(coordinate.getY(), scaleRatio));
                gc.setFill(Color.WHITE);
                gc.fillText(ledNum, scaleResolution(coordinate.getX(), scaleRatio) + 12, scaleResolution(coordinate.getY(), scaleRatio) + 15);
            } else if (key <= conf.getBottomRightLed() + conf.getRightLed()) { // Right
                if (key == conf.getBottomRightLed() + 1) {
                    ledDistance.set(scaleResolution(coordinate.getY(), scaleRatio) - scaleResolution(ledMatrix.get(key + 1).getY(), scaleRatio));
                }
                gc.fillRect(scaleResolution(conf.getScreenResX(), scaleRatio) - twelveX, scaleResolution(coordinate.getY(), scaleRatio),
                        twelveX, ledDistance.get() - 10);
                gc.setFill(Color.WHITE);
                gc.fillText(ledNum, scaleResolution(conf.getScreenResX(), scaleRatio) - (twelveX) + 2, scaleResolution(coordinate.getY(), scaleRatio) + 15);
            } else if (key > (conf.getBottomRightLed() + conf.getRightLed()) && key <= (conf.getBottomRightLed() + conf.getRightLed() + conf.getTopLed())) { // Top
                if (key == (conf.getBottomRightLed() + conf.getRightLed()) + 1) {
                    ledDistance.set(scaleResolution(coordinate.getX(), scaleRatio) - scaleResolution(ledMatrix.get(key + 1).getX(), scaleRatio));
                }
                gc.fillRect(scaleResolution(coordinate.getX(), scaleRatio), 0,
                        ledDistance.get() - 10, scaleResolution(coordinate.getY() + 20, scaleRatio));
                gc.setFill(Color.WHITE);
                gc.fillText(ledNum, scaleResolution(coordinate.getX(), scaleRatio) + 2, 15);
            } else if (key > (conf.getBottomRightLed() + conf.getRightLed() + conf.getTopLed()) && key <= (conf.getBottomRightLed() + conf.getRightLed() + conf.getTopLed() + conf.getLeftLed())) { // Left
                if (key == (conf.getBottomRightLed() + conf.getRightLed() + conf.getTopLed()) + 1) {
                    ledDistance.set(scaleResolution(ledMatrix.get(key + 1).getY(), scaleRatio) - scaleResolution(coordinate.getY(), scaleRatio));
                }
                gc.fillRect(0, scaleResolution(coordinate.getY(), scaleRatio),
                        twelveX, ledDistance.get() - 10);
                gc.setFill(Color.WHITE);
                gc.fillText(ledNum, 0, scaleResolution(coordinate.getY(), scaleRatio) + 15);
            } else { // bottom left
                if (key == (conf.getBottomRightLed() + conf.getRightLed() + conf.getTopLed() + conf.getLeftLed()) + 1) {
                    ledDistance.set(scaleResolution(ledMatrix.get(key + 1).getX(), scaleRatio) - scaleResolution(coordinate.getX(), scaleRatio));
                }
                gc.fillRect(scaleResolution(coordinate.getX(), scaleRatio), scaleResolution(coordinate.getY(), scaleRatio),
                        ledDistance.get() - 10, scaleResolution(coordinate.getY(), scaleRatio));
                gc.setFill(Color.WHITE);
                gc.fillText(ledNum, scaleResolution(coordinate.getX(), scaleRatio) + 2, scaleResolution(coordinate.getY(), scaleRatio) + 15);
            }

            Image image = new Image(getClass().getResource(Constants.IMAGE_CONTROL_LOGO).toString());
            gc.drawImage(image, scaleResolution((conf.getScreenResX()/2), scaleRatio)-64,scaleResolution((conf.getScreenResY()/3), scaleRatio) );

        });

    }

    int scaleResolution(int numberToScale, int scaleRatio) {
        return (numberToScale*100)/scaleRatio;
    }

    /**
     * Turn ON LEDs
     * @param currentConfig stored config
     * @param setBrightness brightness level
     */
    void turnOnLEDs(Configuration currentConfig, boolean setBrightness) {

        if (setBrightness) {
            brightness.setValue((int)(colorPicker.getValue().getOpacity()*100));
        } else {
            colorPicker.setValue(Color.rgb((int)(colorPicker.getValue().getRed() * 255), (int)(colorPicker.getValue().getGreen() * 255),
                    (int)(colorPicker.getValue().getBlue() * 255), (brightness.getValue()/100)));
        }
        if (toggleLed.isSelected()) {
            if (currentConfig != null && currentConfig.isMqttEnable()) {
                FireflyLuciferin.guiManager.mqttManager.publishToTopic(FireflyLuciferin.config.getMqttTopic(), Constants.STATE_ON_SOLID_COLOR
                        .replace(Constants.RED_COLOR, String.valueOf((int)(colorPicker.getValue().getRed() * 255)))
                        .replace(Constants.GREEN_COLOR, String.valueOf((int)(colorPicker.getValue().getGreen() * 255)))
                        .replace(Constants.BLU_COLOR, String.valueOf((int)(colorPicker.getValue().getBlue() * 255)))
                        .replace(Constants.BRIGHTNESS, String.valueOf((int)((brightness.getValue() / 100) * 255))));
            }
        }

    }

    /**
     * Turn ON LEDs
     * @param currentConfig stored config
     */
    void turnOffLEDs(Configuration currentConfig) {

        if (currentConfig != null && currentConfig.isMqttEnable()) {
            FireflyLuciferin.guiManager.mqttManager.publishToTopic(FireflyLuciferin.config.getMqttTopic(), Constants.STATE_OFF_SOLID);
        }

    }

    /**
     * Force TextField to be numeric
     * @param textField numeric fields
     */
    void addTextFieldListener(TextField textField) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) return;
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        });

    }

    /**
     * Set form tooltips
     */
    void setTooltips(Configuration currentConfig) {

        topLed.setTooltip(createTooltip(Constants.TOOLTIP_TOPLED));
        leftLed.setTooltip(createTooltip(Constants.TOOLTIP_LEFTLED));
        rightLed.setTooltip(createTooltip(Constants.TOOLTIP_RIGHTLED));
        bottomLeftLed.setTooltip(createTooltip(Constants.TOOLTIP_BOTTOMLEFTLED));
        bottomRightLed.setTooltip(createTooltip(Constants.TOOLTIP_BOTTOMRIGHTLED));
        orientation.setTooltip(createTooltip(Constants.TOOLTIP_ORIENTATION));
        screenWidth.setTooltip(createTooltip(Constants.TOOLTIP_SCREENWIDTH));
        screenHeight.setTooltip(createTooltip(Constants.TOOLTIP_SCREENHEIGHT));
        scaling.setTooltip(createTooltip(Constants.TOOLTIP_SCALING));
        gamma.setTooltip(createTooltip(Constants.TOOLTIP_GAMMA));
        if (com.sun.jna.Platform.isWindows()) {
            captureMethod.setTooltip(createTooltip(Constants.TOOLTIP_CAPTUREMETHOD));
        } else {
            linuxCaptureMethod.setTooltip(createTooltip(Constants.TOOLTIP_LINUXCAPTUREMETHOD));
        }
        numberOfThreads.setTooltip(createTooltip(Constants.TOOLTIP_NUMBEROFTHREADS));
        serialPort.setTooltip(createTooltip(Constants.TOOLTIP_SERIALPORT));
        aspectRatio.setTooltip(createTooltip(Constants.TOOLTIP_ASPECTRATIO));
        framerate.setTooltip(createTooltip(Constants.TOOLTIP_ASPECTRATIO));

        mqttHost.setTooltip(createTooltip(Constants.TOOLTIP_MQTTHOST));
        mqttPort.setTooltip(createTooltip(Constants.TOOLTIP_MQTTPORT));
        mqttTopic.setTooltip(createTooltip(Constants.TOOLTIP_MQTTTOPIC));
        mqttUser.setTooltip(createTooltip(Constants.TOOLTIP_MQTTUSER));
        mqttPwd.setTooltip(createTooltip(Constants.TOOLTIP_MQTTPWD));
        mqttEnable.setTooltip(createTooltip(Constants.TOOLTIP_MQTTENABLE));
        eyeCare.setTooltip(createTooltip(Constants.TOOLTIP_MQTTENABLE));
        autoStart.setTooltip(createTooltip(Constants.TOOLTIP_MQTTENABLE));
        mqttStream.setTooltip(createTooltip(Constants.TOOLTIP_MQTTSTREAM));
        checkForUpdates.setTooltip(createTooltip(Constants.TOOLTIP_CHECK_UPDATES));
        brightness.setTooltip(createTooltip(Constants.TOOLTIP_CHECK_UPDATES));

        if (currentConfig == null) {
            if (!com.sun.jna.Platform.isWindows()) {
                playButton.setTooltip(createTooltip(Constants.TOOLTIP_PLAYBUTTON_NULL, 50, 6000));
            }
            saveLedButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVELEDBUTTON_NULL));
            saveMQTTButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVEMQTTBUTTON_NULL));
            saveMiscButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVEMQTTBUTTON_NULL));
            saveSettingsButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVESETTINGSBUTTON_NULL));
            saveDeviceButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVEDEVICEBUTTON_NULL));
        } else {
            if (!com.sun.jna.Platform.isWindows()) {
                playButton.setTooltip(createTooltip(Constants.TOOLTIP_PLAYBUTTON, 200, 6000));
            }
            saveLedButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVELEDBUTTON,200, 6000));
            saveMQTTButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVEMQTTBUTTON,200, 6000));
            saveMiscButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVEMQTTBUTTON,200, 6000));
            saveSettingsButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVESETTINGSBUTTON,200, 6000));
            saveDeviceButton.setTooltip(createTooltip(Constants.TOOLTIP_SAVEDEVICEBUTTON,200, 6000));
            showTestImageButton.setTooltip(createTooltip(Constants.TOOLTIP_SHOWTESTIMAGEBUTTON,200, 6000));
        }

    }

    /**
     * Set tooltip properties
     * @param text tooltip string
     */
    public Tooltip createTooltip(String text) {

        Tooltip tooltip;
        tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(500));
        tooltip.setHideDelay(Duration.millis(6000));
        return tooltip;

    }

    /**
     * Set tooltip properties width delays
     * @param text tooltip string
     * @param showDelay delay used to show the tooltip
     * @param hideDelay delay used to hide the tooltip
     */
    public Tooltip createTooltip(String text, int showDelay, int hideDelay) {

        Tooltip tooltip;
        tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(showDelay));
        tooltip.setHideDelay(Duration.millis(hideDelay));
        return tooltip;

    }

    /**
     * Lock TextField in a numeric state
     */
    void setNumericTextField() {

        addTextFieldListener(screenWidth);
        addTextFieldListener(screenHeight);
        addTextFieldListener(numberOfThreads);
        addTextFieldListener(mqttPort);
        addTextFieldListener(topLed);
        addTextFieldListener(leftLed);
        addTextFieldListener(rightLed);
        addTextFieldListener(bottomLeftLed);
        addTextFieldListener(bottomRightLed);

    }

    /**
     * Return the observable devices list
     * @return devices list
     */
    public ObservableList<GlowWormDevice> getDeviceTableData() {
        return deviceTableData;
    }

    public StringProperty producerValueProperty() {
        return producerValue;
    }

    public void setProducerValue(String producerValue) {
        this.producerValue.set(producerValue);
    }

    public StringProperty consumerValueProperty() {
        return consumerValue;
    }

    public void setConsumerValue(String consumerValue) {
        this.consumerValue.set(consumerValue);
    }

}
