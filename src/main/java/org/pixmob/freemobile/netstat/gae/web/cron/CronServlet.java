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
package org.pixmob.freemobile.netstat.gae.web.cron;

import static com.google.appengine.api.taskqueue.RetryOptions.Builder.withTaskAgeLimitSeconds;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

/**
 * 3 This servlet is responsible for starting tasks in background.
 * @author Pixmob
 */
public class CronServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Logger logger = Logger.getLogger(CronServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        final String reqPath = req.getRequestURI();
        final String cronPath = "/cron/";
        if (!reqPath.startsWith(cronPath) || reqPath.equals(cronPath)) {
            logger.warning("Failed to handle cron request: " + reqPath);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final String taskPath = reqPath.substring(cronPath.length());
        final String fullTaskPath = "/task/" + taskPath;
        logger.info("Starting task: " + fullTaskPath);

        final Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl(fullTaskPath).retryOptions(withTaskAgeLimitSeconds(60 * 10)));

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
