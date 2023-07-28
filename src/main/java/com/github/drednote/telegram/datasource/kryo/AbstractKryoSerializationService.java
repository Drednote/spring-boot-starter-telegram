package com.github.drednote.telegram.datasource.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.github.drednote.telegram.utils.Assert;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractKryoSerializationService<T> {

  protected final KryoPool pool;

  protected AbstractKryoSerializationService() {
    KryoFactory factory = () -> {
      Kryo kryo = new Kryo();
      // kryo is really getting trouble checking things if class loaders
      // don't match. for now, just use the below trick before we try
      // to go fully on beans and get a bean class loader.
      kryo.setClassLoader(getDefaultClassLoader());
      configureKryoInstance(kryo);
      return kryo;
    };
    this.pool = new KryoPool.Builder(factory).softReferences().build();
  }

  public byte[] serialize(T context) throws IOException {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      encode(context, bos);
      return bos.toByteArray();
    }
  }

  public T deserialize(byte[] bytes) {
    Assert.notNull(bytes, "bytes");
    try (Input input = new Input(bytes)) {
      return decode(input);
    }
  }

  /**
   * Subclasses implement this method to encode with Kryo.
   *
   * @param kryo   the Kryo instance
   * @param object the object to encode
   * @param output the Kryo Output instance
   */
  protected abstract void doEncode(Kryo kryo, T object, Output output);

  /**
   * Subclasses implement this method to decode with Kryo.
   *
   * @param kryo  the Kryo instance
   * @param input the Kryo Input instance
   * @return the decoded object
   */
  protected abstract T doDecode(Kryo kryo, Input input);

  /**
   * Subclasses implement this to configure the kryo instance. This is invoked on each new Kryo
   * instance when it is created.
   *
   * @param kryo the kryo instance
   */
  protected abstract void configureKryoInstance(Kryo kryo);

  private void encode(final T object, OutputStream outputStream) {
    Assert.notNullC(object, "cannot encode a null object");
    Assert.notNull(outputStream, "outputSteam");
    final Output output = (outputStream instanceof Output o
        ? o
        : new Output(outputStream));
    this.pool.run((KryoCallback<Void>) kryo -> {
      doEncode(kryo, object, output);
      return null;
    });
    output.close();
  }

  private T decode(InputStream inputStream) {
    Assert.notNull(inputStream, "inputStream");
    try (Input input = (inputStream instanceof Input i
        ? i
        : new Input(inputStream))) {
      return this.pool.run(kryo -> doDecode(kryo, input));
    }
  }

  private static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Exception ex) {
      // Cannot access thread context ClassLoader - falling back...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = AbstractKryoSerializationService.class.getClassLoader();
      if (cl == null) {
        // getClassLoader() returning null indicates the bootstrap ClassLoader
        try {
          cl = ClassLoader.getSystemClassLoader();
        } catch (Exception ex) {
          // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
        }
      }
    }
    return cl;
  }
}
