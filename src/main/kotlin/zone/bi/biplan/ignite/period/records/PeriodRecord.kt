package zone.bi.biplan.ignite.period.records

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.SECONDS
import java.util.concurrent.TimeUnit

class PeriodRecord : Externalizable {

    lateinit var id: String
    lateinit var timestamp: OffsetDateTime
    lateinit var values: Map<String, Any>

    companion object {

        fun of(id: String, timestamp: OffsetDateTime, value: Map<String, Any>): PeriodRecord {
            val r = PeriodRecord()
            r.id = id
            r.timestamp = timestamp.truncatedTo(SECONDS)
            r.values = value
            return r
        }
    }

    override fun toString(): String {
        return "HistoryRecord(id='$id', timestamp=$timestamp, value=$values)"
    }

    override fun writeExternal(out: ObjectOutput) {
        out.writeInt(this.id.length)
        this.id.toByteArray().forEach { v -> out.writeByte(v.toInt()) }

        val offsetId = this.timestamp.offset.id
        out.writeInt(offsetId.length)
        offsetId.toByteArray().forEach { v -> out.writeByte(v.toInt()) }
        out.writeLong(this.timestamp.toEpochSecond())

        out.writeInt(this.values.size)
        for ((field, value) in values) {
            out.writeInt(field.length)
            field.toByteArray().forEach { v -> out.writeByte(v.toInt()) }
            val type = value.javaClass.simpleName
            out.writeInt(type.length)
            out.writeBytes(type)
            when (value) {
                is Int -> out.writeInt(value)
                is Long -> out.writeLong(value)
                is String -> {
                    out.writeInt(value.length)
                    out.write(value.toByteArray())
                }
                is BigDecimal -> {
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
    }

    override fun readExternal(input: ObjectInput) {
        val idSize = input.readInt()
        val rawId = ByteArray(idSize)
        (0 until idSize).forEach { j -> rawId[j] = input.readByte() }
        val id = String(rawId)

        val offsetIdSize = input.readInt()
        val rawOffsetId = ByteArray(offsetIdSize)
        (0 until offsetIdSize).forEach { j -> rawOffsetId[j] = input.readByte() }
        val timestampAsSeconds = input.readLong()
        val timestamp = OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestampAsSeconds), ZoneId.of(String(rawOffsetId)))

        val valuesSize = input.readInt()
        val values = (0 until valuesSize).associate {
            val fieldSize = input.readInt()
            val rawField = ByteArray(fieldSize)
            (0 until fieldSize).forEach { j -> rawField[j] = input.readByte() }
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
            String(rawField) to value
        }


        this.id = id
        this.timestamp = timestamp
        this.values = values
    }

    operator fun component1(): String = this.id

    operator fun component2(): OffsetDateTime = this.timestamp

    operator fun component3(): Any = this.values
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PeriodRecord

        if (id != other.id) return false
        if (timestamp != other.timestamp) return false
        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }


}