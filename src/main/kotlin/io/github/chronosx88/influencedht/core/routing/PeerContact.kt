package io.github.chronosx88.influencedht.core.routing

import io.github.chronosx88.influencedht.core.PeerAddress

/**
 * Keeps information about contacts of the Node; Contacts are stored in the Buckets in the Routing Table.
 *
 * Contacts are used instead of nodes because more information is needed than just the node information.
 * - Information such as
 * -- Last seen time
 *
 * @author Joshua Kissoon
 * @since 20140425
 * @updated 20140426
 */
class PeerContact(peerAddress: PeerAddress): Comparable<PeerContact> {

    private val n: PeerAddress = peerAddress
    private var lastSeen: Long = 0

    /**
     * Stale as described by Kademlia paper page 64
     * When a contact fails to respond, if the replacement cache is empty and there is no replacement for the contact,
     * just mark it as stale.
     *
     * Now when a new contact is added, if the contact is stale, it is removed.
     */
    private var staleCount: Int = 0

    /**
     * Create a contact object
     *
     * @param n The node associated with this contact
     */
    init {
        this.lastSeen = System.currentTimeMillis() / 1000L
    }

    fun getPeerAddress(): PeerAddress {
        return this.n
    }

    /**
     * When a Node sees a contact a gain, the Node will want to update that it's seen recently,
     * this method updates the last seen timestamp for this contact.
     */
    fun setSeenNow() {
        this.lastSeen = System.currentTimeMillis() / 1000L
    }

    /**
     * When last was this contact seen?
     *
     * @return long The last time this contact was seen.
     */
    fun lastSeen(): Long {
        return this.lastSeen
    }

    override fun equals(c: Any?): Boolean {
        return (c as? PeerContact)?.getPeerAddress()?.equals(this.getPeerAddress()) ?: false

    }

    /**
     * Increments the amount of times this count has failed to respond to a request.
     */
    fun incrementStaleCount() {
        staleCount++
    }

    /**
     * @return Integer Stale count
     */
    fun staleCount(): Int {
        return this.staleCount
    }

    /**
     * Reset the stale count of the contact if it's recently seen
     */
    fun resetStaleCount() {
        this.staleCount = 0
    }

    override fun compareTo(o: PeerContact): Int {
        if (this.getPeerAddress().equals(o.getPeerAddress())) {
            return 0
        }

        return if (this.lastSeen() > o.lastSeen()) 1 else -1
    }

    override fun hashCode(): Int {
        return this.getPeerAddress().hashCode()
    }
}