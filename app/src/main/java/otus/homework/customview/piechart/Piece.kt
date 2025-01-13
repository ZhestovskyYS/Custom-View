package otus.homework.customview.piechart

import android.os.Parcel
import android.os.Parcelable

data class Piece(
    val name: String,
    val weight: Float,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readFloat()
    )

    init {
        require(weight >= 0.0)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeFloat(weight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Piece> {
        override fun createFromParcel(parcel: Parcel): Piece {
            return Piece(parcel)
        }

        override fun newArray(size: Int): Array<Piece?> {
            return arrayOfNulls(size)
        }
    }
}