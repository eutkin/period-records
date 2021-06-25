package zone.bi.biplan.ignite.period.records

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class PeriodRecords : Externalizable {

    lateinit var contents: MutableList<PeriodRecord>

    override fun writeExternal(out: ObjectOutput) {
        out.writeInt(contents.size)
        for (row in this.contents) {
            val record = PeriodRecord()
            record.writeExternal(out)
        }
    }

    override fun readExternal(input: ObjectInput) {
        val contents = mutableListOf<PeriodRecord>()
        val size = input.readInt()
        for (i in 0 until size) {
            val record = PeriodRecord()
            record.readExternal(input)
            contents.add(record)
        }
        this.contents = contents
    }

}