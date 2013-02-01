/*
 * Copyright (C) 2012 Pixmob (http://github.com/pixmob)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pixmob.freemobile.netstat.gae.web.v1;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.pixmob.freemobile.netstat.gae.repo.DeviceException;
import org.pixmob.freemobile.netstat.gae.repo.DeviceRepository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Delete;
import com.google.sitebricks.http.Put;

@Service
/**
 * REST service handling device resources.
 * @author Pixmob
 */
public class DeviceService {
    private final Logger logger = Logger.getLogger(DeviceService.class.getName());
    private final DeviceRepository dr;

    @Inject
    DeviceService(final DeviceRepository dr) {
        this.dr = dr;
    }

    @Put
    public Reply<?> register(Request req, @Named("id") String deviceId) {
        final DeviceReg devReg = req.read(DeviceReg.class).as(Json.class);
        devReg.brand = StringUtils.trimToNull(devReg.brand);
        devReg.model = StringUtils.trimToNull(devReg.model);

        logger.fine("Trying to register device: " + devReg);

        try {
            dr.create(deviceId, devReg.brand, devReg.model);
        } catch (DeviceException e) {
            logger.log(Level.WARNING, "Failed to register device " + deviceId, e);
            return Reply.with(e.getMessage()).status(HttpServletResponse.SC_CONFLICT);
        }

        logger.info("Device registered");

        return Reply.saying().status(HttpServletResponse.SC_CREATED);
    }

    @Delete
    public Reply<?> unregister(@Named("id") String deviceId) {
        logger.fine("Trying to unregister device");
        dr.delete(deviceId);
        logger.info("Device unregistered");

        return Reply.saying().ok();
    }

    private static class DeviceReg {
        public String brand;
        public String model;

        @Override
        public String toString() {
            return "brand=" + brand + ", model=" + model;
        }
    }
}
