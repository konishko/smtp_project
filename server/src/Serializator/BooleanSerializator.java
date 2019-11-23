package Serializator;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;

public class BooleanSerializator implements IBaseSerializator<Boolean> {
    private final HashSet<String> names;
    private final byte id = (byte)0;

    public BooleanSerializator() {
        names = new HashSet<>();
        names.add("boolean");
        names.add("java.lang.Boolean");
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
    public ByteArrayOutputStream Serialize(Boolean value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        if (value) byteStream.write((byte)1);
        else byteStream.write((byte)0);
        return byteStream;
    }

    @Override
    public Tuple<Boolean, Integer> Deserialize(byte[] raw, int position) {
        boolean value;
        value = raw[position + 1] == 1;
        return new Tuple<>(value, position + 2);
    }
}
