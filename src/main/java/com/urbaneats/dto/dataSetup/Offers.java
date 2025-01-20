package com.urbaneats.dto.dataSetup;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Offers {

    private String header;
    private String couponCode;
    private String offerType;
    private String offerLogo;

}
