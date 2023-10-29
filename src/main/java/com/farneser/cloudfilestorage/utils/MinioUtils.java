package com.farneser.cloudfilestorage.utils;

import com.farneser.cloudfilestorage.dto.StorageDto;
import io.minio.messages.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class MinioUtils {
    public static List<StorageDto> convertItemToStorageDto(List<Item> items, String ignore) {
        var result = new ArrayList<StorageDto>();

        for (var item : items) {

            if (item.objectName().endsWith(ignore)) {
                continue;
            }

            var storageDto = new StorageDto();

            storageDto.setItemPath(trimFromFirstSlash(item.objectName()));

            storageDto.setDir(item.isDir());

            result.add(storageDto);
        }

        return result;
    }

    private static String trimFromFirstSlash(String path) {
        var slashIndex = path.indexOf('/');

        if (slashIndex != -1) {
            return path.substring(slashIndex);
        }

        return path;
    }
}
