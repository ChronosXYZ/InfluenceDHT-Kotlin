package io.github.chronosx88.influencedht.core

import io.gitub.chronosx88.influencedht.core.Number160
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and

object Utils {
    const val IPV4_BYTES = 4
    const val IPV6_BYTES = 16
    const val BYTE_BITS = 8
    const val MASK_0F = 0xf            // 00000000 00000000 00000000 00001111
    const val MASK_80 = 0x80            // 00000000 00000000 00000000 10000000
    const val MASK_FF = 0xff            // 00000000 00000000 00000000 11111111
    const val BYTE_BYTE_SIZE = 1        //  8 bits
    const val SHORT_BYTE_SIZE = 2    // 16 bits
    const val INTEGER_BYTE_SIZE = 4    // 32 bits
    const val LONG_BYTE_SIZE = 8        // 64 bits
    val EMPTY_BYTE_ARRAY = ByteArray(0)

    @JvmStatic
    fun makeSHAHash(strInput: String): Number160 {
        val buffer = strInput.toByteArray()
        return makeSHAHash(buffer)
    }

    @JvmStatic
    fun makeSHAHash(buffer: ByteBuffer): Number160 {
        try {
            val md = MessageDigest.getInstance("SHA-1")
            md.update(buffer)
            val digest = md.digest()
            return Number160(digest)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return Number160()
        }

    }

    @JvmStatic
    fun makeSHAHash(buffer: ByteArray): Number160 {
        return makeSHAHash(ByteBuffer.wrap(buffer))
    }

    /**
     * Converts a byte array to a Inet4Address.
     *
     * @param src
     * the byte array
     * @param offset
     * where to start in the byte array
     * @return The Inet4Address
     *
     * @exception IndexOutOfBoundsException
     * if copying would cause access of data outside array bounds for `src`.
     * @exception NullPointerException
     * if either `src` is `null`.
     */
    @JvmStatic
    fun inet4FromBytes(src: ByteArray, offset: Int): InetAddress {
        // IPv4 is 32 bit
        val tmp2 = ByteArray(Utils.IPV4_BYTES)
        System.arraycopy(src, offset, tmp2, 0, Utils.IPV4_BYTES)
        try {
            return Inet4Address.getByAddress(tmp2)
        } catch (e: UnknownHostException) {
            /*
                 * This really shouldn't happen in practice since all our byte sequences have the right length. However
                 * {@link InetAddress#getByAddress} is documented as potentially throwing this
                 * "if IP address is of illegal length".
                 */
            throw IllegalArgumentException(
                String.format(
                    "Host address '%s' is not a valid IPv4 address.", Arrays.toString(tmp2)
                ), e
            )
        }

    }

    /**
     * Converts a byte array to a Inet6Address.
     *
     * @param me
     * me the byte array
     * @param offset
     * where to start in the byte array
     * @return The Inet6Address
     *
     * @exception IndexOutOfBoundsException
     * if copying would cause access of data outside array bounds for `src`.
     * @exception NullPointerException
     * if either `src` is `null`.
     */
    @JvmStatic
    fun inet6FromBytes(me: ByteArray, offset: Int): InetAddress {
        // IPv6 is 128 bit
        val tmp2 = ByteArray(Utils.IPV6_BYTES)
        System.arraycopy(me, offset, tmp2, 0, Utils.IPV6_BYTES)
        try {
            return Inet6Address.getByAddress(tmp2)
        } catch (e: UnknownHostException) {
            /*
                 * This really shouldn't happen in practice since all our byte sequences have the right length. However
                 * {@link InetAddress#getByAddress} is documented as potentially throwing this
                 * "if IP address is of illegal length".
                 */
            throw IllegalArgumentException(
                String.format(
                    "Host address '%s' is not a valid IPv4 address.", Arrays.toString(tmp2)
                ), e
            )
        }

    }

    /**
     * Convert a byte to a bit set. BitSet.valueOf(new byte[] {b}) is only available in 1.7, so we need to do this on
     * our own.
     *
     * @param b
     * The byte to be converted
     * @return The resulting bit set
     */
    @JvmStatic
    fun createBitSet(b: Byte): BitSet {
        val bitSet = BitSet(8)
        for (i in 0 until Utils.BYTE_BITS) {
            val value = b and (1 shl i).toByte()
            bitSet.set(i, value.toInt() != 0)
        }
        return bitSet
    }
}