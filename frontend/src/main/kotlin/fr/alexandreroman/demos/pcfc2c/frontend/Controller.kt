/*
 * Copyright (c) 2018 Pivotal Software, Inc.
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

package fr.alexandreroman.demos.pcfc2c.frontend

import fr.alexandreroman.demos.pcfc2c.frontend.config.BackendProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.GetMapping
import java.io.PrintWriter

@Controller
class Controller(
        private val backendClientService: BackendClientService,
        private val backendProps: BackendProperties,
        private val instanceInfo: InstanceInfo) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/")
    fun index(out: PrintWriter) {
        val stopWatch = StopWatch()
        stopWatch.start()
        try {
            doIndex(out)
        } finally {
            stopWatch.stop()
            out.println("Time spent: ${stopWatch.totalTimeMillis} ms")
        }
    }

    private fun doIndex(out: PrintWriter) {
        out.println("Welcome to PCF Container-to-container Demo")
        out.println("Frontend instance: $instanceInfo")
        out.println("Connecting to backend: ${backendProps.host}:${backendProps.port}")

        // See BackendConfig for implementation details.
        val resp = backendClientService.ring("$instanceInfo")
        val formattedMessage = resp.replace("\n", "\n  ")
        out.println("Received message from backend:\n  $formattedMessage")

        logger.info("Sending message received from backend: $resp")
    }
}
