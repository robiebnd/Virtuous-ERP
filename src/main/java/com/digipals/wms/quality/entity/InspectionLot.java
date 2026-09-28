package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
@Entity @Table(name="qm_inspection_lots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class InspectionLot extends BaseEntity {
 @Column(name="lot_number",nullable=false,unique=true) private String lotNumber;
 @Column(name="inspection_type",nullable=false) private String inspectionType;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @Column(name="plant_code",nullable=false) private String plantCode;
 @Column(name="source_document_type") private String sourceDocumentType;
 @Column(name="source_document_id") private java.util.UUID sourceDocumentId;
 @Column(name="quantity",nullable=false) private java.math.BigDecimal quantity;
 @Column(nullable=false) private String status;
 @Column(name="created_on",nullable=false) private LocalDateTime createdOn;
}