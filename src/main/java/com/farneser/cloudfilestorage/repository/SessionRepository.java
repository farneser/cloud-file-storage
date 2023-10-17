package com.farneser.cloudfilestorage.repository;

import com.farneser.cloudfilestorage.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
}