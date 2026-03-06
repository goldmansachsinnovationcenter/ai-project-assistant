package com.example.springai.onboarding.repository;

import com.example.springai.onboarding.entity.OnboardingStatus;
import com.example.springai.onboarding.entity.OnboardingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingUserRepository extends JpaRepository<OnboardingUser, Long> {

    Optional<OnboardingUser> findByEmail(String email);

    List<OnboardingUser> findByStatus(OnboardingStatus status);
}
