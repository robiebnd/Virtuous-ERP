package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.LiquidityForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface LiquidityForecastRepository extends JpaRepository<LiquidityForecast,UUID>{
 List<LiquidityForecast> findByForecastDateBetweenAndStatusNotOrderByForecastDateAsc(LocalDate from,LocalDate to,String status);
}