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

internal class PeriodRecordTest {

    @Test
    fun test() {
        val record = PeriodRecord.of(
            UUID.randomUUID().toString(),
            OffsetDateTime.now(),
            mapOf("test" to BigDecimal.valueOf(1000))
        )

        val body = ByteArrayOutputStream()
        ObjectOutputStream(body).use { record.writeExternal(it) }

        val record0 = PeriodRecord()

        ObjectInputStream(ByteArrayInputStream(body.toByteArray())).use { record0.readExternal(it) }

        assertEquals(record, record0)
    }
}