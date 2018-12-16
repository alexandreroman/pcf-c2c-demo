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

package fr.alexandreroman.demos.pcfc2c.frontend.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.alexandreroman.demos.pcfc2c.frontend.InstanceInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Factory component creating [InstanceInfo] instances.
 */
@Configuration
class InstanceInfoConfig {
    @Value("\${spring.application.name}")
    private lateinit var springApplicationName: String

    @Bean
    fun instanceInfo(objectMapper: ObjectMapper): InstanceInfo {
        val instanceIndex: Int = Integer.parseInt(System.getenv("CF_INSTANCE_INDEX") ?: "-1")
        val applicationName: String
        val vcapApplicationJson = System.getenv("VCAP_APPLICATION")
        if (vcapApplicationJson == null) {
            applicationName = springApplicationName
        } else {
            val vcapApplication = objectMapper.readValue<VcapApplication>(vcapApplicationJson)
            applicationName = vcapApplication.applicationName ?: springApplicationName
        }
        return InstanceInfo(applicationName, instanceIndex)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class VcapApplication(
            @JsonProperty("application_name")
            val applicationName: String?)
}
