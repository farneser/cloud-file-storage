package com.farneser.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageDto {

    private String itemPath;
    private boolean isDir;
}
