package com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto;

import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.Item;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemToDtoFactory
{
    private final Map<Class<?>, ItemToDtoMapper<?>> mapperMap = new HashMap<>();

    public ItemToDtoFactory(List<ItemToDtoMapper<?>> mappers)
    {
        for(ItemToDtoMapper<?> mapper : mappers)
        {
            mapperMap.put(mapper.getMappedClass(), mapper);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Item> ItemDto toDto(T item) throws IllegalAccessException
    {
        ItemToDtoMapper<T> mapper = (ItemToDtoMapper<T>) mapperMap.get(item.getClass());

        if(mapper == null)
            throw new IllegalAccessException("No DTO mapper found for " + item.getClass());
        else
            return mapper.mapToDto(item);
    }



}
