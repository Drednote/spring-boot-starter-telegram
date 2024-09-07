package io.github.drednote.telegram.datasource.kryo;

import java.io.IOException;

public interface KryoSerializationService<T> {

    byte[] serialize(T context) throws IOException;

    T deserialize(byte[] bytes);
}
