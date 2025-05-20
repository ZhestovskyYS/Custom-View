package otus.homework.customview

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class NormalizedViewStateUpdater<T: Any>(
    initialValue: T,
    val normalize: (T) -> T,
    val onUpdate: (T) -> Unit,
) : ObservableProperty<T>(initialValue) {


    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        super.setValue(thisRef, property, normalize(value))
    }

    override fun beforeChange(
        property: KProperty<*>,
        oldValue: T,
        newValue: T
    ): Boolean {
        val isUpdateAllowed = oldValue != newValue
        if (isUpdateAllowed) onUpdate(newValue)
        return isUpdateAllowed
    }
}