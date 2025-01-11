package de.chasenet.foxhole.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class SafeSerializer<T>(
    private val serializer: KSerializer<T>,
) : KSerializer<T?> {
    override val descriptor = serializer.descriptor

    // safe because @Serializable skips null fields
    override fun serialize(
        encoder: Encoder,
        value: T?,
    ) = encoder.encodeSerializableValue(serializer, value!!)

    override fun deserialize(decoder: Decoder): T? =
        try {
            decoder.decodeSerializableValue(serializer)
        } catch (_: Exception) {
            null
        }
}
