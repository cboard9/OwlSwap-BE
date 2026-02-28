package com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.context.ItemMappingContext;


public interface DtoToItemMapper<D extends ItemDto>
{
    Item fromDto(D dto, ItemMappingContext ctx);
    Class<D> getDtoClass();
}
