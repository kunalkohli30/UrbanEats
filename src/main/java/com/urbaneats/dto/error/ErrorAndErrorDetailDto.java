package com.urbaneats.dto.error;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ErrorAndErrorDetailDto {

    private Error error;
    private Map<Object, Object> details;
}
