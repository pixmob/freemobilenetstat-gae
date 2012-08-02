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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.pixmob.freemobile.netstat.gae.repo.DeviceNotFoundException;
import org.pixmob.freemobile.netstat.gae.repo.DeviceStat;
import org.pixmob.freemobile.netstat.gae.repo.DeviceStatRepository;

import com.google.inject.Inject;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

@Service
/**
 * EST service handling chart resources.
 * @author Pixmob
 */
public class NetworkUsageChart {
    private final Logger logger = Logger.getLogger(NetworkUsageChart.class.getName());
    private final DeviceStatRepository dsr;

    @Inject
    NetworkUsageChart(final DeviceStatRepository dsr) {
        this.dsr = dsr;
    }

    @Get
    public Reply<?> networkUsage(Request req) {
        logger.info("Compute values for network usage chart");
        final NetworkUsage nu = computeNetworkUsage();

        // Add cache headers to the response.
        final int cacheDuration = 60 * 60 * 2; // in seconds
        final Map<String, String> headers = new HashMap<String, String>(3);
        headers.put("Cache-Control", "public, max-age=" + cacheDuration);
        headers.put("Pragma", "Public");
        headers.put("Age", "0");

        return Reply.with(nu).as(Json.class).headers(headers);
    }

    private NetworkUsage computeNetworkUsage() {
        final long fromDate = System.currentTimeMillis() - 86400 * 1000 * 7;
        final Iterator<DeviceStat> i;
        try {
            i = dsr.getAll(fromDate, null);
        } catch (DeviceNotFoundException e) {
            throw new RuntimeException("Unexpected error", e);
        }

        final NetworkUsage nu = new NetworkUsage();
        while (i.hasNext()) {
            final DeviceStat ds = i.next();
            final long total = ds.timeOnOrange + ds.timeOnFreeMobile;
            if (total < 3600 * 1000 * 3) {
                // Skip device statistics if there is not enough data.
                continue;
            }
            nu.orange += ds.timeOnOrange;
            nu.freeMobile += ds.timeOnFreeMobile;
        }

        return nu;
    }

    /**
     * JSON data class for network usage.
     * @author Pixmob
     */
    public static class NetworkUsage {
        public int orange;
        public int freeMobile;

        public NetworkUsage() {
        }

        @Override
        public String toString() {
            return "orange=" + orange + ", freeMobile=" + freeMobile;
        }
    }
}
