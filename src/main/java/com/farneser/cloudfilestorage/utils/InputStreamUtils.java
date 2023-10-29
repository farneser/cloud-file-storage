package com.farneser.cloudfilestorage.utils;

import com.farneser.cloudfilestorage.dto.FileDto;
import com.farneser.cloudfilestorage.exception.InternalServerException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class InputStreamUtils {

    public static InputStream compressToZip(List<FileDto> fileDtoList) throws IOException, InternalServerException {
        if (fileDtoList == null || fileDtoList.isEmpty()) {
            throw new InternalServerException("Files not found");
        }

        var byteArrayOutputStream = new ByteArrayOutputStream();

        var zipOutputStream = new ZipOutputStream(byteArrayOutputStream, StandardCharsets.UTF_8);

        for (FileDto fileDto : fileDtoList) {
            var entryPath = Paths.get(fileDto.getPath(), fileDto.getFileName()).toString();

            ZipEntry zipEntry = new ZipEntry(entryPath);
            zipOutputStream.putNextEntry(zipEntry);

            var buffer = new byte[1024];
            var len = 0;
            var inputStream = fileDto.getFile();

            while ((len = inputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }

            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
