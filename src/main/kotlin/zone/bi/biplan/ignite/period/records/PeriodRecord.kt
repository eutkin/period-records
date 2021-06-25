package zone.bi.biplan.ignite.period.records

import java.time.OffsetDateTime

class PeriodRecord {

    lateinit var id: String
    lateinit var timestamp: OffsetDateTime
    lateinit var value: Any

    companion object {

        fun of(id: String, timestamp: OffsetDateTime, value: Any): PeriodRecord {
            val r = PeriodRecord()
            r.id = id
            r.timestamp = timestamp
            r.value = value
            return r
        }
    }

    override fun toString(): String {
        return "HistoryRecord(id='$id', timestamp=$timestamp, value=$value)"
    }

    operator fun component1() : String = this.id

    operator fun component2() : OffsetDateTime = this.timestamp

    operator fun component3() : Any = this.value
}