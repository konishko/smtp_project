package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

public class StringSerializator implements IBaseSerializator<String> {
    private final HashSet<String> names;
    private final byte id = (byte)8;

    public StringSerializator() {
        names = new HashSet<>();
        names.add("java.lang.String");
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
    public ByteArrayOutputStream Serialize(String value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        try {
            byteStream.write(ByteBuffer.allocate(4).putInt(bytes.length).array());
            byteStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return byteStream;
    }

    @Override
    public Tuple<String, Integer> Deserialize(byte[] raw, int position) {
        int length = ByteBuffer.wrap(Arrays.copyOfRange(raw, position + 1, position + 5)).getInt();
        position += 5;
        String value = new String(Arrays.copyOfRange(raw, position, position + length), StandardCharsets.UTF_8);
        return new Tuple<>(value, position + length);
    }
}
