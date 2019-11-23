package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

public class DoubleSerializator implements IBaseSerializator<Double> {
    private final HashSet<String> names;
    private final byte id = (byte)3;
    public DoubleSerializator(){
        names = new HashSet<>();
        names.add("double");
        names.add("java.lang.Double");
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
    public ByteArrayOutputStream Serialize(Double value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        try {
            byteStream.write(ByteBuffer.allocate(8).putDouble(value).array());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return byteStream;
    }

    @Override
    public Tuple<Double, Integer> Deserialize(byte[] raw, int position) {
        double value = ByteBuffer.wrap(Arrays.copyOfRange(raw, position + 1, position + 9)).getDouble();
        return new Tuple<>(value, position + 9);
    }
}
