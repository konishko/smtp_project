package Serializator;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;

public class ByteSerializator implements IBaseSerializator<Byte> {
    private final HashSet<String> names;
    private final byte id = (byte)1;
    public ByteSerializator(){
        names = new HashSet<>();
        names.add("byte");
        names.add("java.lang.Byte");
    }
    @Override
    public HashSet<String> getNames() {
        return names;
    }

    @Override
    public byte getId() {
        return id;
    }

    @Override
    public ByteArrayOutputStream Serialize(Byte value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        byteStream.write(value);
        return byteStream;
    }

    @Override
    public Tuple<Byte, Integer> Deserialize(byte[] raw, int position) {
        return new Tuple<>(raw[position + 1], position + 2);
    }
}
