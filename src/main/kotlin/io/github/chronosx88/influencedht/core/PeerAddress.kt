package io.github.chronosx88.influencedht.core

import io.gitub.chronosx88.influencedht.core.Number160
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * A PeerAddress in the Kademlia network - Contains basic node network information.
 *
 * @author Joshua Kissoon
 */
class PeerAddress {

    /**
     * @return The NodeId object of this node
     */
    var nodeId: Number160? = null
        private set
    private var inetAddress: InetAddress? = null
    private var port: Int = 0
    private val strRep: String

    /**
     * Creates a SocketAddress for this node
     *
     * @return
     */
    val socketAddress: InetSocketAddress
        get() = InetSocketAddress(this.inetAddress, this.port)

    constructor(nid: Number160, ip: InetAddress, port: Int) {
        this.nodeId = nid
        this.inetAddress = ip
        this.port = port
        this.strRep = this.nodeId!!.toString()
    }

    /**
     * Load the PeerAddress's data from a DataInput stream
     *
     * @param inputStream Stream which contains this PeerAddress
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    constructor(inputStream: DataInputStream) {
        this.fromStream(inputStream)
        this.strRep = this.nodeId!!.toString()
    }

    /**
     * Set the InetAddress of this node
     *
     * @param addr The new InetAddress of this node
     */
    fun setInetAddress(addr: InetAddress) {
        this.inetAddress = addr
    }

    @Throws(IOException::class)
    fun toStream(out: DataOutputStream) {
        /* Add the NodeId to the stream */
        out.write(this.nodeId!!.toByteArray())

        /* Add the PeerAddress's IP address to the stream */
        val a = inetAddress!!.address
        if (a.size != 4) {
            throw IllegalArgumentException("Expected InetAddress of 4 bytes, got " + a.size)
        }
        out.write(a)

        /* Add the port to the stream */
        out.writeInt(port)
    }

    @Throws(IOException::class)
    fun fromStream(inputStream: DataInputStream) {
        /* Load the NodeId */
        val nodeIDBinary = ByteArray(Number160.BYTE_ARRAY_SIZE)
        inputStream.readFully(nodeIDBinary)
        this.nodeId = Number160(nodeIDBinary)
        /* Load the IP Address */
        val ip = ByteArray(4)
        inputStream.readFully(ip)
        this.inetAddress = InetAddress.getByAddress(ip)

        /* Read inputStream the port */
        this.port = inputStream.readInt()
    }

    override fun equals(o: Any?): Boolean {
        if (o is PeerAddress) {
            val n = o as PeerAddress?
            return if (n === this) {
                true
            } else this.nodeId!!.equals(n!!.nodeId)
        }
        return false
    }

    override fun hashCode(): Int {
        return this.nodeId!!.hashCode()
    }

    override fun toString(): String {
        return this.nodeId!!.toString()
    }
}
