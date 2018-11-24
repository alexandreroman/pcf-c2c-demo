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

package fr.alexandreroman.demos.pcfc2c.backend

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong
import javax.validation.constraints.NotEmpty

@RestController
class Controller(private val instanceInfo: InstanceInfo) {
    private val counter: AtomicLong = AtomicLong()
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/ring")
    fun ringDoorbell(@NotEmpty @RequestParam("visitor") visitor: String): DoorbellResponse {
        logger.info("Someone is at the door: ${visitor}")
        val c = counter.incrementAndGet()
        return DoorbellResponse("$instanceInfo says:\nThank you for coming, ${visitor}!\nVisitor count: $c")
    }

    data class DoorbellResponse(val message: String)
}
