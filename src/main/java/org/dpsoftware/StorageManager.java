/*
  StorageManager.java

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
package org.dpsoftware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.dpsoftware.config.Configuration;
import org.dpsoftware.config.Constants;

import java.io.File;
import java.io.IOException;


/**
 * Write and read yaml configuration file
 */
@Slf4j
public class StorageManager {

    private final ObjectMapper mapper;
    private String path;

    /**
     * Constructor
     */
    public StorageManager() {

        // Initialize yaml file writer
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Create FireflyLuciferin in the Documents folder
        path = System.getProperty(Constants.HOME_PATH) + File.separator + Constants.DOCUMENTS_FOLDER;
        path += File.separator + Constants.LUCIFERIN_FOLDER;
        File customDir = new File(path);

        if (customDir.exists()) {
            log.info(customDir + " " + Constants.ALREADY_EXIST);
        } else if (customDir.mkdirs()) {
            log.info(customDir + " " + Constants.WAS_CREATED);
        } else {
            log.info(customDir + " " + Constants.WAS_NOT_CREATED);
        }

    }

    /**
     * Write params inside the configuration file
     * @param config file
     * @oaram filename where to write the config
     * @throws IOException can't write to file
     */
    public void writeConfig(Configuration config, String filename) throws IOException {

        Configuration currentConfig = readConfig(filename);
        if (currentConfig != null) {
            File file = new File(path + File.separator + filename);
            if (file.delete()) {
                log.info(Constants.CLEANING_OLD_CONFIG);
            } else{
                log.info(Constants.FAILED_TO_CLEAN_CONFIG);
            }
        }
        mapper.writeValue(new File(path + File.separator + filename), config);

    }

    /**
     * Load configuration file
     * @param filename file to read
     * @return config file
     */
    public Configuration readConfig(String filename) {

        Configuration config = null;
        try {
            config = mapper.readValue(new File(path + File.separator + filename), Configuration.class);
            log.info(Constants.CONFIG_OK);
        } catch (IOException e) {
            log.error(Constants.ERROR_READING_CONFIG);
        }
        return config;

    }

}