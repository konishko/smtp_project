package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

public class CharSerializator implements IBaseSerializator<Character> {
    private final HashSet<String> names;
    private final byte id = (byte)2;
    public CharSerializator(){
        names = new HashSet<>();
        names.add("char");
        names.add("java.lang.Character");
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
    public ByteArrayOutputStream Serialize(Character value, ByteArrayOutputStream byteStream) {
        byteStream.write(id);
        try {
            byteStream.write(ByteBuffer.allocate(2).putChar(value).array());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Tuple<Character, Integer> Deserialize(byte[] raw, int position) {
        char value = ByteBuffer.wrap(Arrays.copyOfRange(raw, position + 1, position + 3)).getChar();
        return new Tuple<>(value, position + 3);
    }
}

