package org.egov.infra.microservice.models;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class RemittanceDepositWorkDetail {

    @NotNull
    private String tenantId;

    private String id;

    @NotNull
    private String referenceNumber;

    @NotNull
    private Long referenceDate;

    private String voucherHeader;

    private String function;

    private String fund;

    private String remarks;

    private String reasonForDelay;

    private String status;

    private String creditAmount;
    
    private String reciptNumber;

    private AuditDetails auditDetails;

  

}
