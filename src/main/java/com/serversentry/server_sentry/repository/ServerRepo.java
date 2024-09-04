package com.serversentry.server_sentry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serversentry.server_sentry.models.ServerData;

@Repository
public interface ServerRepo extends JpaRepository <ServerData, Long> {
}