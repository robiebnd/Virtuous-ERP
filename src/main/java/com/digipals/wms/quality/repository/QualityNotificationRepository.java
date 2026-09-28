package com.digipals.wms.quality.repository;
import com.digipals.wms.quality.entity.QualityNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface QualityNotificationRepository extends JpaRepository<QualityNotification,UUID>{ Optional<QualityNotification> findByNotificationNumber(String number); }