package com.farneser.cloudfilestorage.utils;

import com.farneser.cloudfilestorage.dto.PathPartDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class UrlUtils {
    public static String getValidUrlPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("%21", "!")
                .replaceAll("%23", "#")
                .replaceAll("%3F", "?");
    }

    public static List<PathPartDto> getPathParts(String path) {

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        var pathParts = List.of(path.split("/"));

        var result = new ArrayList<PathPartDto>();

        var dto = new PathPartDto("/", "Root page /");

        result.add(dto);

        for (var i = 0; i < pathParts.size(); i++) {
            if (pathParts.get(i).isEmpty()) {
                continue;
            }

            result.add(new PathPartDto(pathParts.stream()
                    .limit(i + 1)
                    .collect(Collectors.joining("/")), pathParts.get(i) + "/"));
        }

        if (result.size() > 1) {
            var pathDto = result.get(result.size() - 1);
            pathDto.setLabel(pathDto.getLabel().substring(0, pathDto.getLabel().length() - 1));
        }

        return result;
    }
}
