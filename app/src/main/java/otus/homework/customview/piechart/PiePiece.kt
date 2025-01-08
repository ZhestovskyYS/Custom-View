package otus.homework.customview.piechart

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt

data class PiePiece(
    val name: String,
    @ColorInt val color: Int,
    val angle: Float,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(color)
        parcel.writeFloat(angle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PiePiece> {
        override fun createFromParcel(parcel: Parcel): PiePiece {
            return PiePiece(parcel)
        }

        override fun newArray(size: Int): Array<PiePiece?> {
            return arrayOfNulls(size)
        }
    }
}