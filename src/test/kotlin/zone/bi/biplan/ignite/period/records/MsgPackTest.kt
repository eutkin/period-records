package zone.bi.biplan.ignite.period.records

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.msgpack.core.MessagePack
import org.msgpack.core.MessageUnpacker
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.text.Charsets.UTF_8

internal class MsgPackTest {

    @Test
    fun test() {
        val records = mutableListOf<Map<String, Any?>>()
        for (i in 0 until 5000) {
            val record = mapOf(
                "id" to UUID.randomUUID(),
                "timestamp" to OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(ThreadLocalRandom.current().nextLong(0, 180)),
                "value" to run {
                    val bytes = ByteArray(ThreadLocalRandom.current().nextInt(1, 100))
                    ThreadLocalRandom.current().nextBytes(bytes)
                    String(bytes, UTF_8)
                })
            records.add(record)
        }

        val body = ByteArrayOutputStream()
        var start = System.currentTimeMillis()

        MessagePack.newDefaultPacker(body).use { packer ->
            packer
                .packArrayHeader(records.size)
            records.forEach { map ->
                map.forEach { (key, value) ->
                    when (key) {
                        "id" -> {
                            val value0 = value as UUID
                            packer.packLong(value0.mostSignificantBits)
                            packer.packLong(value0.leastSignificantBits)
                        }
                        "timestamp" -> {
                            packer.packTimestamp((value as OffsetDateTime).toInstant())
                        }
                        else -> {
                            packer.packString(value as String)
                        }
                    }
                }
            }
        }
        println("Serialization: ${System.currentTimeMillis() - start} ms")


        println("Value size ${body.toByteArray().size / 1024} kb")

        start = System.currentTimeMillis()
        val records0 = MessagePack.newDefaultUnpacker(body.toByteArray()).use { unpacker ->
            val records0 = mutableListOf<Map<String, Any?>>()
            for (i in 0 until unpacker.unpackArrayHeader()) {
                records0.add(mapOf(
                    "id" to UUID(unpacker.unpackLong(), unpacker.unpackLong()),
                    "timestamp" to OffsetDateTime.ofInstant(unpacker.unpackTimestamp(), ZoneOffset.UTC),
                    "value" to unpacker.unpackString()
                ))
            }
            records0
        }

        println("Deserialization: ${System.currentTimeMillis() - start} ms")

        assertEquals(records, records0)
    }
}