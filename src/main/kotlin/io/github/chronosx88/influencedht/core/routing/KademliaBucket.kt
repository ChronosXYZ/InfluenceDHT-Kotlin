package io.github.chronosx88.influencedht.core.routing

import io.github.chronosx88.influencedht.core.IKadConfiguration
import io.github.chronosx88.influencedht.core.PeerAddress
import java.util.*

/**
 * A bucket in the Kademlia routing table
 *
 * @property depth How deep is this bucket in the Routing Table
 * @property config Kademlia configuration
 *
 * @author Joshua Kissoon
 */
class KademliaBucket(@get:Synchronized val depth: Int, private val config: IKadConfiguration) {

    /* Contacts stored in this routing table */
    private val contacts: TreeSet<PeerContact> = TreeSet()

    /* A set of last seen contacts that can replace any current contact that is unresponsive */
    private val replacementCache: TreeSet<PeerContact> = TreeSet()

    @Synchronized
    fun insert(c: PeerContact) {
        if (this.contacts.contains(c)) {
            /**
             * If the contact is already in the bucket, lets update that we've seen it
             * We need to remove and re-add the contact to get the Sorted Set to update sort order
             */
            val tmp = this.removeFromContacts(c.getPeerAddress())
            tmp.setSeenNow()
            tmp.resetStaleCount()
            this.contacts.add(tmp)
        } else {
            /* If the bucket is filled, so put the contacts in the replacement cache */
            if (contacts.size >= this.config.k) {
                /* If the cache is empty, we check if any contacts are stale and replace the stalest one */
                var stalest: PeerContact? = null
                for (tmp in this.contacts) {
                    if (tmp.staleCount() >= this.config.stale) {
                        /* Contact is stale */
                        if (stalest == null) {
                            stalest = tmp
                        } else if (tmp.staleCount() > stalest.staleCount()) {
                            stalest = tmp
                        }
                    }
                }

                /* If we have a stale contact, remove it and add the new contact to the bucket */
                if (stalest != null) {
                    this.contacts.remove(stalest)
                    this.contacts.add(c)
                } else {
                    /* No stale contact, lets insert this into replacement cache */
                    this.insertIntoReplacementCache(c)
                }
            } else {
                this.contacts.add(c)
            }
        }
    }

    @Synchronized
    fun insert(n: PeerAddress) {
        this.insert(PeerContact(n))
    }

    @Synchronized
    fun containsContact(c: PeerContact): Boolean {
        return this.contacts.contains(c)
    }

    @Synchronized
    fun containsNode(n: PeerAddress): Boolean {
        return this.containsContact(PeerContact(n))
    }

    @Synchronized
    fun removeContact(c: PeerContact): Boolean {
        /* If the contact does not exist, then we failed to remove it */
        if (!this.contacts.contains(c)) {
            return false
        }

        /* Contact exist, lets remove it only if our replacement cache has a replacement */
        if (!this.replacementCache.isEmpty()) {
            /* Replace the contact with one from the replacement cache */
            this.contacts.remove(c)
            val replacement = this.replacementCache.first()
            this.contacts.add(replacement)
            this.replacementCache.remove(replacement)
        } else {
            /* There is no replacement, just increment the contact's stale count */
            this.getFromContacts(c.getPeerAddress()).incrementStaleCount()
        }

        return true
    }

    @Synchronized
    private fun getFromContacts(n: PeerAddress): PeerContact {
        for (c in this.contacts) {
            if (c.getPeerAddress().equals(n)) {
                return c
            }
        }

        /* This contact does not exist */
        throw NoSuchElementException("The contact does not exist in the contacts list.")
    }

    @Synchronized
    private fun removeFromContacts(n: PeerAddress): PeerContact {
        for (c in this.contacts) {
            if (c.getPeerAddress().equals(n)) {
                this.contacts.remove(c)
                return c
            }
        }

        /* We got here means this element does not exist */
        throw NoSuchElementException("Node does not exist in the replacement cache. ")
    }

    @Synchronized
    fun removeNode(n: PeerAddress): Boolean {
        return this.removeContact(PeerContact(n))
    }

    @Synchronized
    fun numContacts(): Int {
        return this.contacts.size
    }

    @Synchronized
    fun getContacts(): List<PeerContact> {
        val ret = ArrayList<PeerContact>()

        /* If we have no contacts, return the blank arraylist */
        if (this.contacts.isEmpty()) {
            return ret
        }

        /* We have contacts, lets copy put them into the arraylist and return */
        for (c in this.contacts) {
            ret.add(c)
        }

        return ret
    }

    /**
     * When the bucket is filled, we keep extra contacts in the replacement cache.
     */
    @Synchronized
    private fun insertIntoReplacementCache(c: PeerContact) {
        /* Just return if this contact is already in our replacement cache */
        if (this.replacementCache.contains(c)) {
            // If the contact is already in the bucket, lets update that we've seen it
            // We need to remove and re-add the contact to get the Sorted Set to update sort order
            val tmp = this.removeFromReplacementCache(c.getPeerAddress())
            tmp.setSeenNow()
            this.replacementCache.add(tmp)
        } else if (this.replacementCache.size > this.config.k) {
            /* if our cache is filled, we remove the least recently seen contact */
            this.replacementCache.remove(this.replacementCache.last())
            this.replacementCache.add(c)
        } else {
            this.replacementCache.add(c)
        }
    }

    @Synchronized
    private fun removeFromReplacementCache(n: PeerAddress): PeerContact {
        for (c in this.replacementCache) {
            if (c.getPeerAddress().equals(n)) {
                this.replacementCache.remove(c)
                return c
            }
        }

        /* We got here means this element does not exist */
        throw NoSuchElementException("Node does not exist in the replacement cache. ")
    }

    @Synchronized
    override fun toString(): String {
        val sb = StringBuilder("Bucket at depth: ")
        sb.append(this.depth)
        sb.append("\n Nodes: \n")
        for (n in this.contacts) {
            sb.append("Node: ")
            sb.append(n.getPeerAddress().nodeId.toString())
            sb.append(" (stale: ")
            sb.append(n.staleCount())
            sb.append(")")
            sb.append("\n")
        }

        return sb.toString()
    }
}
