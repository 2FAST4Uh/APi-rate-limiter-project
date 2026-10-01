package com.ratelimiter.admin.repository;

import com.ratelimiter.admin.entity.ApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiUsageRepository extends JpaRepository<ApiUsage, Long>, JpaSpecificationExecutor<ApiUsage> {
}
