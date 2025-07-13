package io.github.drednote.telegram.datasource.kryo;

import java.io.IOException;

/**
 * Generic interface for serializing and deserializing objects using Kryo.
 * <p>
 * Implementations of this interface are expected to provide Kryo-based binary serialization for a specific type
 * {@code T}.
 * </p>
 *
 * <p>Serialization errors during encoding are propagated as {@link IOException}.</p>
 *
 * @param <T> the type of object to serialize and deserialize
 * @author Ivan Galushko
 */
public interface KryoSerializationService<T> {

    /**
     * Serializes the given object into a byte array using Kryo.
     *
     * @param context the object to serialize
     * @return the serialized byte array
     * @throws IOException if serialization fails
     */
    byte[] serialize(T context) throws IOException;

    /**
     * Deserializes the given byte array into an object of type {@code T} using Kryo.
     *
     * @param bytes the byte array to deserialize
     * @return the deserialized object
     */
    T deserialize(byte[] bytes);
}
