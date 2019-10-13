package Serializator;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;

public interface IBaseSerializator<T> {
    HashSet<String> getNames();
    byte getId();
    ByteArrayOutputStream Serialize(T value, ByteArrayOutputStream byteStream);
    Tuple<T, Integer> Deserialize(byte[] raw, int position);
}
