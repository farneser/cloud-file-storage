package com.farneser.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageDto {

    private String itemPath;
    private boolean isDir;

    public boolean isDir() {
        if (itemPath.endsWith("/")) {
            return true;
        }

        return isDir;
    }

    public String getItemName() {
        if (itemPath.equals("/")) {
            return "";
        }

        var path = Paths.get(itemPath);
        return path.getFileName().toString();
    }

    public String getValidUrlPath() {
        return URLEncoder.encode(itemPath, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("%21", "!")
                .replaceAll("%23", "#")
                .replaceAll("%3F", "?");
    }
}
