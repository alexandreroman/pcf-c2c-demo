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

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.retrofit.CircuitBreakerCallAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Duration

/**
 * Backend configuration.
 */
@Configuration
class BackendConfig {
    // Use Retrofit to build an API service instance.
    // Here we are targeting the backend using its Cloud Foundry application name:
    // PCF (using BOSH-DNS) will resolve this host name to a running application address.

    // Using BOSH-DNS through PCF brings us these features "for free" (no external library):
    // - no need for a service registry (such as Eureka, Consul, Zookeeper):
    //   BOSH-DNS takes care of locating running applications, using health checks;
    // - distribute REST calls across instances: when a host name is being resolved,
    //   BOSH-DNS chooses a running application name, making sure this instance is healthy.

    @Bean
    fun backendClientApi(retrofit: Retrofit) = retrofit.create(BackendClientApi::class.java)

    @Bean
    fun retrofit(backendProps: BackendProperties, circuitBreaker: CircuitBreaker) =
    // Reuse a single Retrofit instance for all generated API services.
            Retrofit.Builder()
                    .baseUrl("http://${backendProps.host}:${backendProps.port}")
                    .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
                    .addCallAdapterFactory(CircuitBreakerCallAdapter.of(circuitBreaker))
                    .build()

    @Bean
    fun circuitBreaker(): CircuitBreaker {
        val config = CircuitBreakerConfig.custom()
                .ringBufferSizeInClosedState(2)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .recordFailure { true }
                .build()
        return CircuitBreaker.of("backend", config)
    }
}
