package com.jnm.mallJnm.model.vo;

import com.jnm.mallJnm.model.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductVO extends Product {
    private List<String> noUsedImages;
    private List<String> usedImages;
}
