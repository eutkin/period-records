package zone.bi.biplan.ignite.period.records

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput

class PeriodRecords : Externalizable {

    lateinit var contents: MutableList<PeriodRecord>

    override fun writeExternal(out: ObjectOutput) {
        out.writeInt(contents.size)
        for (row in this.contents) {
            row.writeExternal(out)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PeriodRecords

        if (contents != other.contents) return false

        return true
    }

    override fun hashCode(): Int {
        return contents.hashCode()
    }

    override fun toString(): String {
        return "PeriodRecords$contents)"
    }


}