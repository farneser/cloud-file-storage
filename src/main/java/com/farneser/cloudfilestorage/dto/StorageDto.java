package com.farneser.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageDto {

    private String itemPath;
    private boolean isDir;

    public String getItemName() {
        var path = Paths.get(itemPath);

        return path.getFileName().toString();
    }
}
