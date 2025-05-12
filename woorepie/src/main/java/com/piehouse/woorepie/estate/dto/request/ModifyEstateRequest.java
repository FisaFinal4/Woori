package com.piehouse.woorepie.estate.dto.request;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class ModifyEstateRequest {

    private Long estateId;

    private String estateDescription;

}
