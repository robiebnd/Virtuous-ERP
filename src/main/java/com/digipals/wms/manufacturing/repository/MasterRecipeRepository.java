package com.digipals.wms.manufacturing.repository;
import com.digipals.wms.manufacturing.entity.MasterRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MasterRecipeRepository extends JpaRepository<MasterRecipe,UUID>{Optional<MasterRecipe> findByRecipeNumber(String n);}