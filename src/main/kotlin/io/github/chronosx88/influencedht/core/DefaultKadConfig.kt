package io.github.chronosx88.influencedht.core

class DefaultKadConfig: IKadConfiguration {
    private val RESTORE_INTERVAL = (60 * 1000).toLong() // in milliseconds
    private val RESPONSE_TIMEOUT: Long = 2000
    private val OPERATION_TIMEOUT: Long = 2000
    private val CONCURRENCY = 10
    private val K = 5
    private val RCSIZE = 3
    private val STALE = 1
    private val IS_TESTING = false

    override val isTesting: Boolean
        get() = IS_TESTING
    override val restoreInterval: Long
        get() = RESTORE_INTERVAL
    override val responseTimeout: Long
        get() = RESPONSE_TIMEOUT
    override val operationTimeout: Long
        get() = OPERATION_TIMEOUT
    override val maxConcurrentMessagesTransiting: Int
        get() = CONCURRENCY
    override val k: Int
        get() = K
    override val replacementCacheSize: Int
        get() = RCSIZE
    override val stale: Int
        get() = STALE
}