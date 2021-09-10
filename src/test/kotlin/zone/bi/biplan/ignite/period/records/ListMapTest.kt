package zone.bi.biplan.ignite.period.records

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.text.Charsets.UTF_8

internal class ListMapTest {

    @Test
    fun test() {
        val records = mutableListOf<Map<String, Any?>>()
        for (i in 0 until 5000) {
            val record = mapOf(
                "id" to UUID.randomUUID().toString(),
                "timestamp" to OffsetDateTime.now(),
                "value" to mapOf("test" to run {
                    val bytes = ByteArray(ThreadLocalRandom.current().nextInt(1, 100))
                    ThreadLocalRandom.current().nextBytes(bytes)
                    String(bytes, UTF_8)
                })
            )
            records.add(record)
        }

        val body = ByteArrayOutputStream()
        var start = System.currentTimeMillis()
        ObjectOutputStream(body).use { it.writeObject(records) }
        println("Serialization: ${System.currentTimeMillis() - start} ms")


        println("Value size ${body.toByteArray().size / 1024} kb")

        start = System.currentTimeMillis()
        val records0 =
            ObjectInputStream(ByteArrayInputStream(body.toByteArray())).use { it.readObject() } as ArrayList<Map<String, Any?>>
        println("Deserialization: ${System.currentTimeMillis() - start} ms")

        assertEquals(records, records0)
    }
}