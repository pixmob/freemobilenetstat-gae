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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

/**
 * {@link DeviceStat} repository.
 * @author Pixmob
 */
public class DeviceStatRepository extends DAOBase {
    private final Logger logger = Logger.getLogger(DeviceStatRepository.class.getName());

    public DeviceStat update(String deviceId, long date, long timeOnOrange, long timeOnFreeMobile)
            throws DeviceNotFoundException {
        if (deviceId == null) {
            throw new IllegalArgumentException("Device identifier is required");
        }

        final Objectify ofy = ObjectifyService.begin();
        final Device ud = ofy.find(Device.class, deviceId);
        if (ud == null) {
            throw new DeviceNotFoundException(deviceId);
        }

        DeviceStat ds = ofy.query(DeviceStat.class).ancestor(ud).filter("date", date).get();
        if (ds == null) {
            ds = new DeviceStat();
            ds.device = new Key<Device>(Device.class, deviceId);
            ds.date = date;

            logger.info("Creating statistics for device " + deviceId);
        } else {
            logger.info("Updating statistics for device " + deviceId);
        }

        ds.timeOnOrange = timeOnOrange;
        ds.timeOnFreeMobile = timeOnFreeMobile;
        ofy.put(ds);

        return ds;
    }

    public Iterator<DeviceStat> getAll(long fromDate, String deviceId) throws DeviceNotFoundException {
        final Iterable<DeviceStat> deviceStats;
        final Objectify ofy = ObjectifyService.begin();
        Device device = null;
        if (deviceId != null) {
            device = ofy.find(new Key<Device>(Device.class, deviceId));
            if (device == null) {
                throw new DeviceNotFoundException(deviceId);
            }
            deviceStats = ofy.query(DeviceStat.class).ancestor(device).filter("date >=", fromDate);
        } else {
            deviceStats = ofy.query(DeviceStat.class).filter("date >=", fromDate);
        }

        return deviceStats.iterator();
    }
}
