package com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto;

import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.Item;


public interface ItemToDtoMapper<T extends Item>
{
    ItemDto mapToDto(T item);
    Class<T> getMappedClass();

}
