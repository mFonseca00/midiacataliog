package com.catalog.midiacatalog.dto.Midia;

import com.catalog.midiacatalog.model.enums.Midiatype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MidiaResponseDTO {
    private Long id;
    private String title;
    private Midiatype type;
}
