package Serializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Serializator<T> {

    private final HashMap<String, Byte> ids;
    private final HashMap<Byte, IBaseSerializator> bases;
    private final byte[] marker = "?--".getBytes(StandardCharsets.UTF_8);

    public Serializator() {
        ids = new HashMap<>();
        bases = new HashMap<>();
    }

    public void register(IBaseSerializator serializator){
        byte id = serializator.getId();
        HashSet<String> names = serializator.getNames();
        for(String name : names) ids.put(name, id);
        bases.put(serializator.getId(), serializator);
    }

    public byte[] Serialize(T o){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            byteStream.write(marker);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return Serialize(o, byteStream).toByteArray();
    }

    private ByteArrayOutputStream Serialize(Object o, ByteArrayOutputStream byteStream){
        if(o == null){
            byteStream.write((byte)-1);
            return byteStream;
        }
        Class oClass = o.getClass();
        byte[] oClassName = oClass.getName().getBytes(StandardCharsets.UTF_8);
        byte oClassId;
        if(ids.containsKey(oClass.getName())) oClassId = ids.get(oClass.getName());
        else oClassId = 127;
        if(oClassId != 127) return bases.get(oClassId).Serialize(o, byteStream);
        byteStream.write(oClassId);
        Field[] oFields = oClass.getDeclaredFields();
        try {
            byteStream.write(ByteBuffer.allocate(4).putInt(oClassName.length).array());
            byteStream.write(oClassName);
            byteStream.write(ByteBuffer.allocate(4).putInt(oFields.length).array());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        for(Field field:oFields) {
            field.setAccessible(true);
            byte[] fieldName = field.getName().getBytes(StandardCharsets.UTF_8);
            Object fieldValue = null;
            try {
                fieldValue = field.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
            try {
                byteStream.write(ByteBuffer.allocate(4).putInt(fieldName.length).array());
                byteStream.write(fieldName);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            byteStream = Serialize(fieldValue, byteStream);
        }
        return byteStream;
    }

    public T Deserialize(byte[] raw) throws DeserializeException {
        if(!Arrays.equals(Arrays.copyOfRange(raw, 0, 3), marker)) throw new DeserializeException("not deserializable");
        return (T)Deserialize(raw, 3).x;
    }

    private Tuple<Object, Integer> Deserialize(byte[] raw, int position) throws DeserializeException {
        byte id = raw[position];
        if(id == (byte)-1) return new Tuple<>(null, position + 1);
        if(bases.containsKey(id)) return bases.get(id).Deserialize(raw, position);
        if(id != (byte)127) throw new DeserializeException("error during deserialization");
        int classNameLength = ByteBuffer.wrap(Arrays.copyOfRange(raw, position + 1, position + 5)).getInt();
        position += 5;
        String[] clsName = (new String(Arrays.copyOfRange(raw, position, position + classNameLength), StandardCharsets.UTF_8)).split("\\.");
        String className = "SMTPServer." + clsName[clsName.length - 1];
        position += classNameLength;
        int fieldsCount = ByteBuffer.wrap(Arrays.copyOfRange(raw, position, position + 4)).getInt();
        position += 4;
        Class oClass = null;
        Object o = null;
        try {
            oClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        try {
            o = oClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException();
        }
        for(int i = 0; i < fieldsCount; i++){
            int fieldNameLength = ByteBuffer.wrap(Arrays.copyOfRange(raw, position, position + 4)).getInt();
            position += 4;
            String fieldName = new String(Arrays.copyOfRange(raw, position, position + fieldNameLength), StandardCharsets.UTF_8);
            position += fieldNameLength;
            Tuple<Object, Integer> valueAndPosition = Deserialize(raw, position);
            Object fieldValue = valueAndPosition.x;
            position = valueAndPosition.y;
            Field field = null;
            try {
                field = oClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(o, fieldValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return new Tuple<>(o, position);
    }
}
