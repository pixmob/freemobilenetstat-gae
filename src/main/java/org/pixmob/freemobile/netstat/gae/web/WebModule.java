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
package org.pixmob.freemobile.netstat.gae.web;

import org.pixmob.freemobile.netstat.gae.web.task.UpdateChartsTask;
import org.pixmob.freemobile.netstat.gae.web.v1.DailyDeviceStatService;
import org.pixmob.freemobile.netstat.gae.web.v1.DeviceService;
import org.pixmob.freemobile.netstat.gae.web.v1.InfoService;
import org.pixmob.freemobile.netstat.gae.web.v1.NetworkUsageChart;

import com.google.inject.servlet.ServletModule;
import com.google.sitebricks.SitebricksModule;
import com.googlecode.objectify.cache.AsyncCacheFilter;

/**
 * Guice web configuration.
 * @author Pixmob
 */
public class WebModule extends ServletModule {
    @Override
    protected void configureServlets() {
        // An AsyncCacheFilter is required in order to use async datastore
        // queries with Objectify.
        final AsyncCacheFilter asyncCacheFilter = new AsyncCacheFilter();
        filter("/task/*").through(asyncCacheFilter);
        filter("/1/*").through(asyncCacheFilter);

        install(new SitebricksModule() {
            @Override
            protected void configureSitebricks() {
                at("/task/update-charts").serve(UpdateChartsTask.class);
                at("/1").serve(InfoService.class);
                at("/1/device/:id").serve(DeviceService.class);
                at("/1/device/:id/daily/:date").serve(DailyDeviceStatService.class);
                at("/1/chart/network-usage").serve(NetworkUsageChart.class);
            }
        });
    }
}
