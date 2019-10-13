package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

public class FloatSerializator implements IBaseSerializator<Float> {
    private final HashSet<String> names;
    private final byte id = (byte)4;
    public FloatSerializator(){
        names = new HashSet<>();
        names.add("float");
        names.add("java.lang.Float");
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
    public ByteArrayOutputStream Serialize(Float value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        try {
            byteStream.write(ByteBuffer.allocate(4).putFloat(value).array());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return byteStream;
    }

    @Override
    public Tuple<Float, Integer> Deserialize(byte[] raw, int position) {
        float value = ByteBuffer.wrap(Arrays.copyOfRange(raw,position + 1, position + 5)).getFloat();
        return new Tuple<>(value, position + 5);
    }
}
