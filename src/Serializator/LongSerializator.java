package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

public class LongSerializator implements IBaseSerializator<Long> {
    private final HashSet<String> names;
    private final byte id = (byte)6;
    public LongSerializator(){
        names = new HashSet<>();
        names.add("long");
        names.add("java.lang.Long");
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
    public ByteArrayOutputStream Serialize(Long value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        try {
            byteStream.write(ByteBuffer.allocate(8).putLong(value).array());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return byteStream;
    }

    @Override
    public Tuple<Long, Integer> Deserialize(byte[] raw, int position) {
        long value = ByteBuffer.wrap(Arrays.copyOfRange(raw, position + 1, position + 9)).getLong();
        return new Tuple<>(value, position + 9);
    }
}
