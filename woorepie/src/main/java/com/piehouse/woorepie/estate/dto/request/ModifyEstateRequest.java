package com.piehouse.woorepie.estate.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifyEstateRequest {

    private Long estateId;

    private String estateDescription;

}
