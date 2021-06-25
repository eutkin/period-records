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

internal class PeriodRecordsTest {

    @Test
    fun test() {
        val record = PeriodRecord.of(
            UUID.randomUUID().toString(),
            OffsetDateTime.now(),
            mapOf("test" to BigDecimal.valueOf(1000))
        )
        val records = PeriodRecords()
        records.contents = mutableListOf(record)

        val body = ByteArrayOutputStream()
        ObjectOutputStream(body).use { records.writeExternal(it) }

        val records0 = PeriodRecords()

        ObjectInputStream(ByteArrayInputStream(body.toByteArray())).use { records0.readExternal(it) }

        assertEquals(records, records0)
    }
}