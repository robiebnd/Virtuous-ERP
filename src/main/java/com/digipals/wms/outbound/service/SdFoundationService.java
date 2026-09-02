package com.digipals.wms.outbound.service;

import com.digipals.wms.outbound.dto.SdFoundationRequests.*;
import com.digipals.wms.outbound.entity.*;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class SdFoundationService {
    private final EntityManager em;
    private final ProductRepository products;
    private final WarehouseRepository warehouses;
    private final com.digipals.wms.outbound.repository.CustomerRepository customers;

    @Transactional public Object createOrganization(OrganizationRequest r){
        require(r.code(),"code"); require(r.name(),"name");
        var x=SdFoundationEntities.SalesOrganization.builder().code(r.code()).name(r.name()).currency(r.currency()==null?"USD":r.currency()).country(r.country()).build(); return em.merge(x);
    }
    @Transactional public Object createChannel(CodeNameRequest r){var x=SdFoundationEntities.DistributionChannel.builder().code(r.code()).name(r.name()).build();return em.merge(x);}
    @Transactional public Object createDivision(CodeNameRequest r){var x=SdFoundationEntities.Division.builder().code(r.code()).name(r.name()).build();return em.merge(x);}
    @Transactional public Object createSalesArea(SalesAreaRequest r){
        var so=find(SdFoundationEntities.SalesOrganization.class,"code",r.salesOrganizationCode());
        var dc=find(SdFoundationEntities.DistributionChannel.class,"code",r.distributionChannelCode());
        var d=find(SdFoundationEntities.Division.class,"code",r.divisionCode());
        return em.merge(SdFoundationEntities.SalesArea.builder().code(r.code()).salesOrganization(so).distributionChannel(dc).division(d).build());
    }
    @Transactional public Object createShippingPoint(ShippingPointRequest r){var w=warehouses.findByCode(r.warehouseCode()).orElseThrow(()->new IllegalArgumentException("Warehouse not found: "+r.warehouseCode()));return em.merge(SdFoundationEntities.ShippingPoint.builder().code(r.code()).name(r.name()).warehouse(w).address(r.address()).build());}
    @Transactional public Object createSalesOffice(SalesOfficeRequest r){var so=find(SdFoundationEntities.SalesOrganization.class,"code",r.salesOrganizationCode());return em.merge(SdFoundationEntities.SalesOffice.builder().code(r.code()).name(r.name()).salesOrganization(so).build());}
    @Transactional public Object createSalesGroup(SalesGroupRequest r){var office=find(SdFoundationEntities.SalesOffice.class,"code",r.salesOfficeCode());return em.merge(SdFoundationEntities.SalesGroup.builder().code(r.code()).name(r.name()).salesOffice(office).build());}
    @Transactional public Object assignCustomerSalesArea(CustomerSalesAreaRequest r){var c=customers.findByCustomerNumber(r.customerNumber()).orElseThrow(()->new IllegalArgumentException("Customer not found: "+r.customerNumber()));var sa=find(SdFoundationEntities.SalesArea.class,"code",r.salesAreaCode());var office=r.salesOfficeCode()==null?null:find(SdFoundationEntities.SalesOffice.class,"code",r.salesOfficeCode());var group=r.salesGroupCode()==null?null:find(SdFoundationEntities.SalesGroup.class,"code",r.salesGroupCode());return em.merge(SdFoundationEntities.CustomerSalesArea.builder().customer(c).salesArea(sa).salesOffice(office).salesGroup(group).paymentTerms(r.paymentTerms()).customerPricingGroup(r.customerPricingGroup()).deliveryPriority(r.deliveryPriority()).shippingCondition(r.shippingCondition()).creditLimit(r.creditLimit()).creditExposure(BigDecimal.ZERO).creditBlocked(false).build());}
    @Transactional public Object assignProductSalesData(ProductSalesDataRequest r){var p=products.findBySkuIgnoreCase(r.sku()).orElseThrow(()->new IllegalArgumentException("Product not found: "+r.sku()));var sa=find(SdFoundationEntities.SalesArea.class,"code",r.salesAreaCode());var w=r.deliveringWarehouseCode()==null?null:warehouses.findByCode(r.deliveringWarehouseCode()).orElseThrow(()->new IllegalArgumentException("Warehouse not found: "+r.deliveringWarehouseCode()));var sp=r.shippingPointCode()==null?null:find(SdFoundationEntities.ShippingPoint.class,"code",r.shippingPointCode());return em.merge(SdFoundationEntities.ProductSalesData.builder().product(p).salesArea(sa).salesUnit(r.salesUnit()).taxClassification(r.taxClassification()).itemCategoryGroup(r.itemCategoryGroup()).deliveringPlant(w).shippingPoint(sp).minimumOrderQuantity(r.minimumOrderQuantity()==null?BigDecimal.ZERO:r.minimumOrderQuantity()).salesStatus("ACTIVE").build());}
    @Transactional public Object createCustomerMaterial(CustomerMaterialRequest r){var c=customers.findByCustomerNumber(r.customerNumber()).orElseThrow(()->new IllegalArgumentException("Customer not found: "+r.customerNumber()));var p=products.findBySkuIgnoreCase(r.sku()).orElseThrow(()->new IllegalArgumentException("Product not found: "+r.sku()));return em.merge(SdFoundationEntities.CustomerMaterialInfo.builder().customer(c).product(p).customerMaterialNumber(r.customerMaterialNumber()).customerDescription(r.customerDescription()).customerUnit(r.customerUnit()).build());}
    @Transactional public Object createPricingCondition(PricingConditionRequest r){var sa=r.salesAreaCode()==null?null:find(SdFoundationEntities.SalesArea.class,"code",r.salesAreaCode());var c=r.customerNumber()==null?null:customers.findByCustomerNumber(r.customerNumber()).orElseThrow(()->new IllegalArgumentException("Customer not found: "+r.customerNumber()));var p=r.sku()==null?null:products.findBySkuIgnoreCase(r.sku()).orElseThrow(()->new IllegalArgumentException("Product not found: "+r.sku()));if(r.rate()==null||r.rate().signum()<0)throw new IllegalArgumentException("Pricing rate must be non-negative");if(r.validFrom()==null||r.validTo()==null||r.validTo().isBefore(r.validFrom()))throw new IllegalArgumentException("Invalid pricing validity period");return em.merge(SdFoundationEntities.PricingCondition.builder().conditionType(r.conditionType()).salesArea(sa).customer(c).product(p).validFrom(r.validFrom()).validTo(r.validTo()).rate(r.rate()).rateType(r.rateType()==null?"AMOUNT":r.rateType()).currency(r.currency()==null?"USD":r.currency()).priority(r.priority()==null?100:r.priority()).build());}

    @SuppressWarnings("unchecked") private <T> T find(Class<T> type,String field,String value){if(value==null||value.isBlank())throw new IllegalArgumentException(field+" is required");return em.createQuery("select x from "+type.getSimpleName()+" x where x."+field+" = :v and x.active = true",type).setParameter("v",value).getResultStream().findFirst().orElseThrow(()->new IllegalArgumentException(type.getSimpleName()+" not found: "+value));}
    private void require(String v,String n){if(v==null||v.isBlank())throw new IllegalArgumentException(n+" is required");}
}
