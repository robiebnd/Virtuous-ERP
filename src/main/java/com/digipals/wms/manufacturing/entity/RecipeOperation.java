package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
@Entity @Table(name="mfg_recipe_operations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class RecipeOperation extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="recipe_id") private MasterRecipe recipe;
 @Column(name="operation_no",nullable=false) private Integer operationNo;
 @Column(nullable=false) private String instruction;
 @Column(name="resource_name") private String resourceName;
 @Column(name="standard_minutes") private BigDecimal standardMinutes;
}