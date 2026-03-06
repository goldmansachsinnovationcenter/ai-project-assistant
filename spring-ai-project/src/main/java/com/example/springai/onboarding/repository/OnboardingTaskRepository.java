package com.example.springai.onboarding.repository;

import com.example.springai.onboarding.connector.ConnectorType;
import com.example.springai.onboarding.entity.OnboardingStatus;
import com.example.springai.onboarding.entity.OnboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingTaskRepository extends JpaRepository<OnboardingTask, Long> {

    List<OnboardingTask> findByOnboardingUserId(Long userId);

    List<OnboardingTask> findByConnectorType(ConnectorType connectorType);

    List<OnboardingTask> findByStatus(OnboardingStatus status);

    List<OnboardingTask> findByOnboardingUserIdAndConnectorType(Long userId, ConnectorType connectorType);
}
