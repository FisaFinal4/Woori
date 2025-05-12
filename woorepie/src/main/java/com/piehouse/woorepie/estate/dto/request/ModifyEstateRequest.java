package com.piehouse.woorepie.estate.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyEstateRequest {
    private Long estateId;
    private String estateDescription;
}
