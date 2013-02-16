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

import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Head;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
/**
 * REST service for checking server availability.
 * @author Pixmob
 */
public class InfoService {
    private static final Set< String> KNOWN_USER_AGENTS = new HashSet< String>(2);
    static {
        KNOWN_USER_AGENTS.add("FreeMobileNetstat/21");
        KNOWN_USER_AGENTS.add("FreeMobileNetstat/22");
    }

    @Head
    public Reply< ?> checkAvailability(Request req) {
        if (!isClientCompatible(req)) {
            return Reply.saying().notFound();
        }
        return Reply.saying().ok();
    }

    @Get
    public Reply< ?> getAvailability(Request req) {
        if (!isClientCompatible(req)) {
            return Reply.saying().notFound();
        }
        return Reply.with("Server is up and running!").ok();
    }

    private boolean isClientCompatible(Request req) {
        final Collection< String> userAgents = req.headers().get("User-Agent");
        for (final String userAgent : userAgents) {
            for (final String u : KNOWN_USER_AGENTS) {
                if (userAgent.contains(u)) {
                    return true;
                }
            }
        }
        return false;
    }
}
