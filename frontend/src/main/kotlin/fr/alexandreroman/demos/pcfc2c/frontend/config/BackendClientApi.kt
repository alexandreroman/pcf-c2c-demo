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

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Backend client service API interface.
 */
interface BackendClientApi {
    // Retrofit is used here to simplify API service definition,
    // but you are free to use any REST client library.

    @GET("ring?")
    fun ring(@Query("visitor") visitor: String): Call<RingResponse>

    /**
     * Data class holding response after calling "ring" method.
     */
    data class RingResponse(val message: String)
}
