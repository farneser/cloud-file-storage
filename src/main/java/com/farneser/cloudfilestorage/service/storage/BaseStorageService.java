package com.farneser.cloudfilestorage.service.storage;

import com.farneser.cloudfilestorage.models.User;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseStorageService implements StorageService {
    protected static final String FOLDER_STATIC_FILE_NAME = "folder.ini";

    protected long getUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (User) authentication.getPrincipal();

        return userDetails.getId();
    }
}