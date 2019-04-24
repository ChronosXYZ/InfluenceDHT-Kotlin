package io.github.chronosx88.influencedht.core.routing

import io.github.chronosx88.influencedht.core.IKadConfiguration
import io.github.chronosx88.influencedht.core.KeyComparator
import io.github.chronosx88.influencedht.core.PeerAddress
import io.gitub.chronosx88.influencedht.core.Number160
import java.util.*

/**
 * Implementation of a Kademlia routing table
 *
 * @author Joshua Kissoon
 */
class KademliaRoutingTable(
    private val localNode: PeerAddress// The current node
    , @field:Transient private var config: IKadConfiguration
) {
    /**
     * @return Bucket[] The buckets in this Kad Instance
     */
    /**
     * Set the KadBuckets of this routing table, mainly used when retrieving saved state
     *
     * @param buckets
     */
    @Transient
    lateinit var buckets: Array<KademliaBucket>

    /**
     * @return List A List of all Nodes in this KademliaRoutingTable
     */
    val allNodes: List<PeerAddress>
        @Synchronized get() {
            val nodes = ArrayList<PeerAddress>()

            for (b in this.buckets) {
                for (c in b.getContacts()) {
                    nodes.add(c.getPeerAddress())
                }
            }

            return nodes
        }

    /**
     * @return List A List of all Nodes in this KademliaRoutingTable
     */
    val allContacts: List<PeerContact>
        get() {
            val contacts = ArrayList<PeerContact>()

            for (b in this.buckets) {
                contacts.addAll(b.getContacts())
            }

            return contacts
        }

    init {

        /* Initialize all of the buckets to a specific depth */
        this.initialize()

        /* Insert the local node */
        this.insert(localNode)
    }

    /**
     * Initialize the KademliaRoutingTable to it's default state
     */
    fun initialize() {
        this.buckets = emptyArray()
        for (i in 0 until Number160.BITS) {
            buckets[i] = KademliaBucket(i, this.config)
        }
    }

    fun setConfiguration(config: IKadConfiguration) {
        this.config = config
    }

    /**
     * Adds a contact to the routing table based on how far it is from the LocalNode.
     *
     * @param c The contact to add
     */
    @Synchronized
    fun insert(c: PeerContact) {
        this.buckets[this.getBucketId(c.getPeerAddress().nodeId)].insert(c)
    }

    /**
     * Adds a node to the routing table based on how far it is from the LocalNode.
     *
     * @param n The node to add
     */
    @Synchronized
    fun insert(n: PeerAddress) {
        this.buckets[this.getBucketId(n.nodeId)].insert(n)
    }

    /**
     * Compute the bucket ID in which a given node should be placed; the bucketId is computed based on how far the node is away from the Local PeerAddress.
     *
     * @param nid The NodeId for which we want to find which bucket it belong to
     *
     * @return Integer The bucket ID in which the given node should be placed.
     */
    fun getBucketId(nid: Number160?): Int {
        val bId = this.localNode.nodeId!!.getDistance(nid) - 1

        /* If we are trying to insert a node into it's own routing table, then the bucket ID will be -1, so let's just keep it in bucket 0 */
        return if (bId < 0) 0 else bId
    }

    /**
     * Find the closest set of contacts to a given NodeId
     *
     * @param target           The NodeId to find contacts close to
     * @param numNodesRequired The number of contacts to find
     *
     * @return List A List of contacts closest to target
     */
    @Synchronized
    fun findClosest(target: Number160, numNodesRequired: Int): List<PeerAddress> {
        val sortedSet = TreeSet(KeyComparator(target))
        sortedSet.addAll(this.allNodes)

        val closest = ArrayList<PeerAddress>(numNodesRequired)

        /* Now we have the sorted set, lets get the top numRequired */
        var count = 0
        for (n in sortedSet) {
            closest.add(n)
            if (++count == numNodesRequired) {
                break
            }
        }
        return closest
    }

    /**
     * Method used by operations to notify the routing table of any contacts that have been unresponsive.
     *
     * @param contacts The set of unresponsive contacts
     */
    fun setUnresponsiveContacts(contacts: List<PeerAddress>) {
        if (contacts.isEmpty()) {
            return
        }
        for (n in contacts) {
            this.setUnresponsiveContact(n)
        }
    }

    /**
     * Method used by operations to notify the routing table of any contacts that have been unresponsive.
     *
     * @param n
     */
    @Synchronized
    fun setUnresponsiveContact(n: PeerAddress) {
        val bucketId = this.getBucketId(n.nodeId)

        /* Remove the contact from the bucket */
        this.buckets[bucketId].removeNode(n)
    }

    @Synchronized
    override fun toString(): String {
        val sb = StringBuilder("\n ***************** \n")
        var totalContacts = 0
        for (b in this.buckets) {
            if (b.numContacts() > 0) {
                totalContacts += b.numContacts()
                sb.append("# nodes in Bucket with depth ")
                sb.append(b.depth)
                sb.append(": ")
                sb.append(b.numContacts())
                sb.append("\n")
                sb.append(b.toString())
                sb.append("\n")
            }
        }

        sb.append("\nTotal Contacts: ")
        sb.append(totalContacts)
        sb.append("\n\n")

        sb.append(" ******************** ")

        return sb.toString()
    }
}
