package com.mikael.mkAPI.kotlin.api.redis

class RedisConnectionData(

    /**
     * Data para criar um Cliente Redis (Jedis) usando a RedisAPI.
     *
     * @see RedisAPI
     */

    var isEnabled: Boolean = false,
    var usePass: Boolean = false,
    var pass: String = "password",
    var port: Int = 6379,
    var host: String = "localhost"
)