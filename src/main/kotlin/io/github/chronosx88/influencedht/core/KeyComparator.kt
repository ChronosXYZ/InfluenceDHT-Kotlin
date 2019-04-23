package io.github.chronosx88.influencedht.core

import io.gitub.chronosx88.influencedht.core.Number160
import java.util.*

/**
 * A Comparator to compare 2 keys to a given key
 *
 * @author Joshua Kissoon
 */
class KeyComparator(private val key: Number160): Comparator<PeerAddress> {

    /**
     * Compare two objects which must both be of type `PeerAddress`
     * and determine which is closest to the identifier specified in the
     * constructor.
     *
     * @param n1 Node 1 to compare distance from the key
     * @param n2 Node 2 to compare distance from the key
     */
    override fun compare(n1: PeerAddress, n2: PeerAddress): Int {
        var b1 = n1.nodeId
        var b2 = n2.nodeId

        b1 = b1!!.xor(key)
        b2 = b2!!.xor(key)

        return b1.compareTo(b2)
    }
}
