/*
 * Copyright (c) 2019 Pivotal Software, Inc.
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

import fr.alexandreroman.demos.pcfc2c.frontend.BackendClientService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.vavr.control.Try
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import retrofit2.Response

/**
 * [BackendClientService] implementation using a Retrofit generated service instance,
 * backed by a Resilience4j circuit breaker.
 */
@Component
@Primary
class BackendClientServiceImpl(
        private val api: BackendClientApi,
        @Qualifier("fallback") private val fallback: BackendClientService,
        private val circuitBreaker: CircuitBreaker) : BackendClientService {
    override fun ring(visitor: String): String {
        // Use the API service instance generated by Retrofit to send
        // a REST call to the backend.
        // If there is no backend instance running, a circuit breaker using Resilience4j
        // will return a default value.

        val supplier = CircuitBreaker.decorateSupplier(circuitBreaker) {
            api.ring(visitor)
        }
        return Try.ofSupplier(supplier)
                .map {
                    // Extract response body.
                    it.execute().bodyNotNull().message
                }.getOrElseGet {
                    // If there is an error while connecting to the backend,
                    // a default value is returned.
                    fallback.ring(visitor)
                }
    }
}

/**
 * Fallback [BackendClientService] implementation,
 * used when no backend instance is available.
 */
@Component
@Qualifier("fallback")
class BackendClientServiceFallback : BackendClientService {
    override fun ring(visitor: String) = "No backend service available"
}

/**
 * Get response body.
 * @throws IllegalStateException if no body is available in the response
 */
private fun <T> Response<T>.bodyNotNull() =
        this.body() ?: throw IllegalStateException("No body set in response")
