import kotlin.reflect.KProperty

class NotNullDelegate<T>(
    private val value: T?,
    private val message: (() -> String)? = null,
) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T = value ?: throw IllegalStateException(message?.invoke() ?: "${property.name} not initialized")
}

fun <T> notNull(
    value: T?,
    message: (() -> String)? = null,
) = NotNullDelegate(value, message)
