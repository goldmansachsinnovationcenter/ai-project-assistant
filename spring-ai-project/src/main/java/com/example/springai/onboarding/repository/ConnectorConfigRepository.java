package com.example.springai.onboarding.repository;

import com.example.springai.onboarding.connector.ConnectorType;
import com.example.springai.onboarding.entity.ConnectorConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectorConfigRepository extends JpaRepository<ConnectorConfig, Long> {

    List<ConnectorConfig> findByEnabled(boolean enabled);

    List<ConnectorConfig> findByConnectorType(ConnectorType connectorType);

    Optional<ConnectorConfig> findByName(String name);
}
