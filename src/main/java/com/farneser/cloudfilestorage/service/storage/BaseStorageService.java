package com.farneser.cloudfilestorage.service.storage;

import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.utils.UserUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseStorageService implements StorageService {
    protected static final String FOLDER_STATIC_FILE_NAME = "folder.ini";

    protected String getUserFolderPath() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (User) authentication.getPrincipal();

        return UserUtils.getUserBucket(userDetails.getId());
    }
}