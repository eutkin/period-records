package zone.bi.biplan.ignite.period.records

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.text.Charsets.UTF_8

internal class PeriodRecordsTest {

    @Test
    fun test() {
        val recordContents = mutableListOf<PeriodRecord>()
        for (i in 0 until 5000) {
            val record = PeriodRecord.of(
                UUID.randomUUID().toString(),
                OffsetDateTime.now(),
                mapOf("test" to run {
                    val bytes = ByteArray(ThreadLocalRandom.current().nextInt(1, 100))
                    ThreadLocalRandom.current().nextBytes(bytes)
                    String(bytes, UTF_8)
                }
                )
            )
            recordContents.add(record)
        }
        val records = PeriodRecords()
        records.contents = recordContents

        val body = ByteArrayOutputStream()
        var start = System.currentTimeMillis()
        ObjectOutputStream(body).use { records.writeExternal(it) }
        println("Serialization: ${System.currentTimeMillis() - start} ms")
        val records0 = PeriodRecords()

        println("Value size ${body.toByteArray().size / 1024} kb")

        start = System.currentTimeMillis()
        ObjectInputStream(ByteArrayInputStream(body.toByteArray())).use { records0.readExternal(it) }
        println("Deserialization: ${System.currentTimeMillis() - start} ms")

        assertEquals(records, records0)
    }
}