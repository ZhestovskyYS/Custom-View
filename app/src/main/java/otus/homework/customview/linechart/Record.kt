package otus.homework.customview.linechart

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Record(
    val name: String,
    val data: List<RecordData>,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.createTypedArrayList(RecordData.CREATOR)!!,
    )

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<out Record?> {
            return arrayOfNulls(size)
        }
    }
}

data class RecordData(
    val date: Date,
    val moneySpent: Double,
): Parcelable {
    constructor(parcel: Parcel) : this(
        Date(parcel.readLong()),
        parcel.readDouble(),
    )

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(date.time)
        parcel.writeDouble(moneySpent)
    }

    companion object CREATOR : Parcelable.Creator<RecordData> {
        override fun createFromParcel(source: Parcel): RecordData {
            return RecordData(source)
        }

        override fun newArray(size: Int): Array<out RecordData?> {
            return arrayOfNulls(size)
        }
    }
}
