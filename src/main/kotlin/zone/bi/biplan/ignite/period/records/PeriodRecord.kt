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

class PeriodRecord : Externalizable {

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

    override fun writeExternal(out: ObjectOutput) {
        out.writeInt(id.length)
        id.toByteArray().forEach { v -> out.writeByte(v.toInt()) }

        out.writeLong(timestamp.toEpochSecond())
        val type = value.javaClass.simpleName
        out.writeInt(type.length)
        out.writeBytes(type)
        when (value) {
            is Int -> out.writeInt(value as Int)
            is Long -> out.writeLong(value as Long)
            is String -> {
                val value = value as String
                out.writeInt(value.length)
                out.write(value.toByteArray())
            }
            is BigDecimal -> {
                val value = value as BigDecimal
                val scale: Int = value.scale()
                val precision: Int = value.precision()
                val bytes: ByteArray = value.unscaledValue().toByteArray()
                out.writeInt(scale)
                out.writeInt(precision)
                out.writeInt(bytes.size)
                out.write(bytes)
            }
        }
    }

    override fun readExternal(input: ObjectInput) {
        val idSize = input.readInt()
        val rawId = ByteArray(idSize)
        (0 until idSize).forEach { j -> rawId[j] = input.readByte() }
        val id = String(rawId)

        val timestampAsSeconds = input.readLong()
        val timestamp = OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestampAsSeconds), ZoneOffset.UTC)

        val valueTypeSize = input.readInt()
        val rawType = ByteArray(valueTypeSize)
        (0 until valueTypeSize).forEach { j -> rawType[j] = input.readByte() }
        val value = when (val type = String(rawType)) {
            "Int" -> input.readInt()
            "Long" -> input.readLong()
            "String" -> {
                val valueSize = input.readInt()
                val raw = ByteArray(valueSize)
                String(raw)
            }
            "BigDecimal" -> {
                val scale = input.readInt()
                val precision = input.readInt()
                val valueSize = input.readInt()
                val raw = ByteArray(valueSize)
                input.read(raw)
                val number = BigInteger(raw, 0, valueSize)
                BigDecimal(number, scale, MathContext(precision))
            }
            else -> throw IllegalStateException("Unknown type $type")
        }
        this.id = id
        this.timestamp = timestamp
        this.value = value
    }

    operator fun component1() : String = this.id

    operator fun component2() : OffsetDateTime = this.timestamp

    operator fun component3() : Any = this.value
}