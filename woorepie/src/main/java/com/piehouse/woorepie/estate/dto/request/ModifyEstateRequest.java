package com.piehouse.woorepie.estate.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyEstateRequest {

    private Long estateId;

    private String estateDescription;

}
