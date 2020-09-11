package com.tedmob.moph.tracer.notifications

import android.os.Parcel
import android.os.Parcelable

data class NotificationData(
    val title: String?,
    val message: String?,
    val image: String?,
    val imageHeight: Double?,
    val imageWidth: Double?
) : Parcelable, ObjectContainingImage {

    override fun image(): String? = image

    override fun imageHeight(): Double? = imageHeight

    override fun imageWidth(): Double? = imageWidth

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as Double?,
        parcel.readValue(Double::class.java.classLoader) as Double?
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(image)
        parcel.writeValue(imageHeight)
        parcel.writeValue(imageWidth)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<NotificationData> {
        override fun createFromParcel(parcel: Parcel): NotificationData = NotificationData(parcel)
        override fun newArray(size: Int): Array<NotificationData?> = arrayOfNulls(size)
    }
}