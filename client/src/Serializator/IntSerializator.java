package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

public class IntSerializator implements IBaseSerializator<Integer> {
    private final HashSet<String> names;
    private final byte id = (byte)5;
    public IntSerializator(){
        names = new HashSet<>();
        names.add("int");
        names.add("java.lang.Integer");
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
    public ByteArrayOutputStream Serialize(Integer value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        try {
            byteStream.write(ByteBuffer.allocate(4).putInt(value).array());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return byteStream;
    }

    @Override
    public Tuple<Integer, Integer> Deserialize(byte[] raw, int position) {
        int value = ByteBuffer.wrap(Arrays.copyOfRange(raw, position + 1, position + 5)).getInt();
        return new Tuple<>(value, position + 5);
    }
}

