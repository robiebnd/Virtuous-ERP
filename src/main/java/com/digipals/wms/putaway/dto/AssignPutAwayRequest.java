package com.digipals.wms.putaway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignPutAwayRequest {

    @NotNull(message = "User is required")
    private UUID userId;
}
