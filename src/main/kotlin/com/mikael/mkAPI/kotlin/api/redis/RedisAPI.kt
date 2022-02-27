package com.mikael.mkAPI.kotlin.api.redis

import redis.clients.jedis.Connection
import redis.clients.jedis.Jedis

object RedisAPI {

    /**
     * RedisAPI v1.1 (Using Jedis v4.1.1)
     *
     * Remember that in the mkAPI config you can enable this API automatically by setting Redis' 'isEnabled' to true.
     * This API is not yet supported to be activated and used in different plugins at the same time with different Clients.
     * You should always create a single Redis Client and use it in all your plugins.
     *
     * @author Mikael
     * @see RedisConnectionData
     */

    lateinit var managerData: RedisConnectionData
    var client: Jedis? = null
    var clientConnection: Connection? = null

    /**
     * Creates a new redis client (Jedis) using the data provided by RedisConnectionData.
     * After creation sets the 'client' variable to the new created client.
     *
     * @param connectionData A RedisConnectionData to create the Redis Client.
     * @return A redis client (Jedis).
     * @throws IllegalStateException if the 'isEnabled' of the given RedisConnectionData is false.
     * @see connectClient
     */
    fun createClient(connectionData: RedisConnectionData): Jedis {
        if (!connectionData.isEnabled) error("RedisConnectionData isEnabled must not be false")
        val jedis = Jedis("http://${connectionData.host}:${connectionData.port}/")
        if (connectionData.usePass) {
            jedis.auth(connectionData.pass)
        }
        client = jedis
        return client!!
    }

    /**
     * Connects the client (Jedis) if it is not already connected.
     * If the client is already connected, it will just return the existing connection.
     *
     * @return An existing Jedis connection.
     * @throws IllegalStateException if the RedisAPI client is null.
     * @see createClient
     */
    fun connectClient(force: Boolean = false): Connection {
        if (client == null) error("Cannot get redis client (Jedis)")
        if (!force) {
            if (clientConnection != null) {
                return clientConnection!!
            }
        }
        client!!.connect()
        clientConnection = client!!.connection
        return clientConnection!!
    }

    /**
     * Closes the existing connection to the redis server, and then sets the variable 'clientConnection' and 'client' to null.
     */
    fun finishConnection() {
        clientConnection?.close()
        clientConnection = null
        client = null
    }

    /**
     * Checks if the redis client (Jedis) is created and connected.
     *
     * @return True if the redis client is created and connected. Otherwise, false.
     */
    fun isInitialized(): Boolean {
        if (client == null || clientConnection == null) return false
        return true
    }

    /**
     * Sends a ping to Redis server.
     *
     * @return True if the ping is answered. Otherwise, false.
     * @throws IllegalStateException if the Redis client or the connection is null.
     */
    fun testPing(): Boolean {
        if (!isInitialized()) error("Cannot send ping to a null redis server")
        return try {
            client!!.ping()
            true
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * Send a 'event' to Redis server.
     *
     * @param channelName the redis channel name to send the event.
     * @param message message that will be sent with the event.
     * @throws IllegalStateException if the Redis client or the connection is null.
     */
    fun sendEvent(channelName: String, message: String) {
        if (!isInitialized()) error("Cannot send a event message to a null redis server")
        client!!.publish(channelName, message)
    }

}