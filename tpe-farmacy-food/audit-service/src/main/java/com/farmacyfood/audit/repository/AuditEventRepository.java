package com.farmacyfood.audit.repository;

import com.farmacyfood.audit.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    List<AuditEvent> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, String entityId);

    List<AuditEvent> findByServiceNameOrderByTimestampDesc(String serviceName);

    Page<AuditEvent> findByServiceName(String serviceName, Pageable pageable);

    Page<AuditEvent> findByEntityType(String entityType, Pageable pageable);

    Page<AuditEvent> findByAction(String action, Pageable pageable);

    @Query("SELECT a FROM AuditEvent a WHERE " +
           "(:serviceName IS NULL OR a.serviceName = :serviceName) AND " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:from IS NULL OR a.timestamp >= :from) AND " +
           "(:to IS NULL OR a.timestamp <= :to) " +
           "ORDER BY a.timestamp DESC")
    Page<AuditEvent> search(@Param("serviceName") String serviceName,
                            @Param("entityType") String entityType,
                            @Param("entityId") String entityId,
                            @Param("action") String action,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to,
                            Pageable pageable);

    void deleteByTimestampBefore(LocalDateTime before);
}
