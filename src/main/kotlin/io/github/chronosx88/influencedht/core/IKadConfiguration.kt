package io.github.chronosx88.influencedht.core

/**
 * Interface that defines a IKadConfiguration object
 *
 * @author Joshua Kissoon
 */
interface IKadConfiguration {

    /**
     * @return Whether we're in a testing or production system.
     */
    val isTesting: Boolean

    /**
     * @return Interval in milliseconds between execution of RestoreOperations.
     */
    val restoreInterval: Long

    /**
     * If no reply received from a node in this period (in milliseconds)
     * consider the node unresponsive.
     *
     * @return The time it takes to consider a node unresponsive
     */
    val responseTimeout: Long

    /**
     * @return Maximum number of milliseconds for performing an operation.
     */
    val operationTimeout: Long

    /**
     * @return Maximum number of concurrent messages in transit.
     */
    val maxConcurrentMessagesTransiting: Int

    /**
     * @return K-Value used throughout Kademlia
     */
    val k: Int

    /**
     * @return Size of replacement cache.
     */
    val replacementCacheSize: Int

    /**
     * @return # of times a node can be marked as stale before it is actually removed.
     */
    val stale: Int
}