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
package org.pixmob.freemobile.netstat.gae;

import org.pixmob.freemobile.netstat.gae.repo.RepositoryModule;
import org.pixmob.freemobile.netstat.gae.web.WebModule;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Guice application configuration.
 * @author Pixmob
 */
public class AppConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new RepositoryModule(), new WebModule(), new AppEngineModule());
    }

    private static class AppEngineModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(MemcacheService.class).toInstance(MemcacheServiceFactory.getMemcacheService());
        }
    }
}
