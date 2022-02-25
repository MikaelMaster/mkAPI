package com.mikael.mkAPI.kotlin.api.redis

import redis.clients.jedis.Connection
import redis.clients.jedis.Jedis

object RedisAPI {

    /**
     * RedisAPI v1.0b (Using Jedis v4.1.1)
     *
     * Lembre-se que na config do mkAPI você pode ativar esta API automaticamente marcando o 'isEnabled' do Redis como true.
     * Esta API ainda não tem suporte a ser ativada e utilizada em plugins diferentes ao mesmo tempo com Clientes diferentes.
     * Você sempre deve criar um único Cliente Redis e utilizar ele em todos os seus plugins.
     *
     * @author Mikael
     * @see RedisConnectionData
     */

    lateinit var managerData: RedisConnectionData
    var client: Jedis? = null
    var clientConnection: Connection? = null

    /**
     * Cria um novo cliente redis (Jedis) utilizando os dados fornecidos pelo RedisConnectionData.
     * Após a criação define a variável 'client' para o novo cliente criado.
     *
     * @param connectionData Uma RedisConnectionData para criar o Cliente Redis.
     * @return Um cliente redis (Jedis).
     * @throws IllegalStateException se o 'isEnabled' da RedisConnectionData fornecida for false.
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
     * Conecta o cliente (Jedis) caso ele ainda não esteja conectado.
     * Caso o cliente já esteja conectado, ele apenas retornará a conexão existente.
     *
     * @return Uma conexão Jedis existente.
     * @throws IllegalStateException se o cliente do RedisAPI for nulo.
     * @see createClient
     */
    fun connectClient(): Connection {
        if (client == null) error("Cannot get redis client (Jedis)")
        if (clientConnection != null) {
            return clientConnection!!
        }
        client!!.connect()
        clientConnection = client!!.connection
        return clientConnection!!
    }

    /**
     * Fecha a conexão existente com o servidor redis, e depois marca a variável 'clientConnection' e 'client' como null.
     */
    fun finishConnection() {
        clientConnection?.close()
        clientConnection = null
        client = null
    }

    /**
     * Verifica se o cliente redis (Jedis) está criado e conectado.
     *
     * @return True se o cliente redis etiver criado e conectado. Senão, false.
     */
    fun isInitialized(): Boolean {
        if (client == null || clientConnection == null) return false
        return true
    }

    /**
     * Envia um ping para o servidor redis.
     *
     * @return True se o ping for respondido pelo servidor. Senão, false.
     * @throws IllegalStateException se o cliente redis ou a conexão for null.
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
     * Envia um evento para o servidor redis.
     * Para registrar um listener no servidor redis você deve fazer isto manualmente.
     *
     * @param channelName o nome do canal do redis para enviar o evento.
     * @param message mensagem que vai ser enviada junto ao evento.
     * @throws IllegalStateException se o cliente redis ou a conexão for null.
     */
    fun sendEvent(channelName: String, message: String) {
        if (!isInitialized()) error("Cannot send a event message to a null redis server")
        client!!.publish(channelName, message)
    }

}