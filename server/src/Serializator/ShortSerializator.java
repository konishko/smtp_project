package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

public class ShortSerializator implements IBaseSerializator<Short> {
    private final HashSet<String> names;
    private final byte id = (byte)7;
    public ShortSerializator(){
        names = new HashSet<>();
        names.add("short");
        names.add("java.lang.Short");
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
    public ByteArrayOutputStream Serialize(Short value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        try {
            byteStream.write(ByteBuffer.allocate(2).putShort(value).array());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return byteStream;
    }

    @Override
    public Tuple<Short, Integer> Deserialize(byte[] raw, int position) {
        short value = ByteBuffer.wrap(Arrays.copyOfRange(raw, position + 1, position + 3)).getShort();
        return new Tuple<>(value, position + 3);
    }
}
