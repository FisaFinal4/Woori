package com.piehouse.woorepie.estate.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyEstateRequest {

    @NotNull(message = "매물 ID는 필수입니다.")
    private Long estateId;

    @NotNull(message = "설명은 필수입니다.")
    private String estateDescription;
}
