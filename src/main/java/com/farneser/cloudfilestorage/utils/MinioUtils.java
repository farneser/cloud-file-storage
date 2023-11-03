package com.farneser.cloudfilestorage.utils;

import com.farneser.cloudfilestorage.dto.SearchDto;
import com.farneser.cloudfilestorage.dto.StorageDto;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    public static List<SearchDto> convertItemToSearchDto(List<Item> items, String ignore) {
        var result = new ArrayList<SearchDto>();

        for (var item : items) {
            var searchDto = new SearchDto();

            searchDto.setItemPath(trimFromFirstSlash(item.objectName()));

            if (searchDto.getItemPath().endsWith(ignore)) {
                searchDto.setItemPath(searchDto.getItemPath().replace(ignore, "/"));
            }

            searchDto.setDir(searchDto.getItemPath().endsWith(ignore) || searchDto.getItemPath().endsWith("/"));

            searchDto.setPathParts(UrlUtils.getPathParts(searchDto.getItemPath()));

            result.add(searchDto);
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
