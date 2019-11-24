package zip;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver {
    public  byte[] archiveFiles(final String[] files){
        List<String> archivedFiles = new ArrayList<>();
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(bos)){
            for(String filePath: files){
                File file = new File(filePath);
                String entryName = getFreeName(file.getName(), archivedFiles);
                archivedFiles.add(entryName);
                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(Files.readAllBytes(file.toPath()));
                zos.closeEntry();
            }
            zos.finish();
            zos.flush();
            zos.close();
            return bos.toByteArray();
        } catch (IOException e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    private String getFreeName(final String originalName, final List<String> usedNames){
        if(!usedNames.contains(originalName)){
            return originalName;
        } else{
            int index = 1;
            String newName;
            while(true){
                newName = addFileIndex(originalName, index);
                if(!usedNames.contains(newName)){
                    break;
                }
                index++;
            }
            return newName;
        }
    }

    private String addFileIndex(final String name, final int index){
         String[] nameParts = name.split("\\.");
         StringBuilder nameWithIndex = new StringBuilder();
         for(int i = 0; i < nameParts.length; i++){
             nameWithIndex.append(nameParts[i]);
             if(i == nameParts.length - 2){
                 nameWithIndex.append("(");
                 nameWithIndex.append(index);
                 nameWithIndex.append(")");
             }
             if(i != nameParts.length - 1) {
                 nameWithIndex.append(".");
             }
         }
         return nameWithIndex.toString();
    }
}
