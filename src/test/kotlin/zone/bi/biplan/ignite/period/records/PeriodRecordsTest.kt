package zone.bi.biplan.ignite.period.records

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom

internal class PeriodRecordsTest {

    @Test
    fun test() {
        val recordContents = mutableListOf<PeriodRecord>()
        for (i in 0 until 999) {
            val record = PeriodRecord.of(
                UUID.randomUUID().toString(),
                OffsetDateTime.now(),
                mapOf("test" to BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(1000, 100000)))
            )
            recordContents.add(record)
        }
        val records = PeriodRecords()
        records.contents = recordContents

        val body = ByteArrayOutputStream()
        ObjectOutputStream(body).use { records.writeExternal(it) }

        val records0 = PeriodRecords()

        ObjectInputStream(ByteArrayInputStream(body.toByteArray())).use { records0.readExternal(it) }

        assertEquals(records, records0)
    }
}