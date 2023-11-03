package com.farneser.cloudfilestorage.dto;

import com.farneser.cloudfilestorage.utils.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageDto {

    protected String itemPath;
    protected boolean isDir;

    public boolean isDir() {
        return itemPath.endsWith("/") || isDir;
    }

    public String getItemName() {
        if (itemPath.equals("/")) {
            return "";
        }

        var path = Paths.get(itemPath);
        return path.getFileName().toString();
    }

    public String getValidUrlPath() {
        return UrlUtils.getValidUrlPath(itemPath);
    }
}
