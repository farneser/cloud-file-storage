package com.farneser.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    private InputStream file;
    private String fileName;
    private String path;
}
