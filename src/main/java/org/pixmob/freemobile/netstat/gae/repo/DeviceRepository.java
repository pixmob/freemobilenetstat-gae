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
package org.pixmob.freemobile.netstat.gae.repo;

import java.util.Iterator;
import java.util.logging.Logger;

import com.googlecode.objectify.AsyncObjectify;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

/**
 * {@link Device} repository.
 * @author Pixmob
 */
public class DeviceRepository extends DAOBase {
    private final Logger logger = Logger.getLogger(DeviceRepository.class.getName());

    /**
     * Create an user device in the datastore.
     * @throws DeviceException
     *             if the device id is already used
     */
    public Device create(String deviceId, String brand, String model) throws DeviceException {
        if (deviceId == null) {
            throw new IllegalArgumentException("Device identifier is required");
        }

        final Objectify ofy = ObjectifyService.begin();
        if (ofy.find(new Key<Device>(Device.class, deviceId)) != null) {
            throw new DeviceException("Cannot create device: device identifier conflict", deviceId);
        }

        final Device ud = new Device();
        ud.id = deviceId;
        ud.brand = brand;
        ud.model = model;

        ofy.put(ud);

        logger.info("Device created: " + deviceId);

        return ud;
    }

    public void delete(String deviceId) {
        if (deviceId != null) {
            // Get all statistics records for this device.
            final Objectify ofy = ObjectifyService.begin();
            final Iterable<DeviceStat> deviceStats = ofy.query(DeviceStat.class).ancestor(
                    new Key<Device>(Device.class, deviceId));

            // Delete records related to this device.
            final AsyncObjectify aofy = ofy.async();
            aofy.delete(deviceStats);
            aofy.delete(Device.class, deviceId);

            logger.info("Device deleted: " + deviceId);
        }
    }

    public Device get(String deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("Device identifier is required");
        }

        final Objectify ofy = ObjectifyService.begin();
        return ofy.find(Device.class, deviceId);
    }

    public Iterator<Device> getAll() {
        final Objectify ofy = ObjectifyService.begin();
        return ofy.query(Device.class).iterator();
    }
}
