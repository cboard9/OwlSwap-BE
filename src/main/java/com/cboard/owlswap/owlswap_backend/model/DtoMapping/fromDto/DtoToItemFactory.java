package com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto;

import com.cboard.owlswap.owlswap_backend.model.Item;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DtoToItemFactory
{
    private final Map<Class<?>, DtoToItemMapper<?>> mappers = new HashMap<>();

    public DtoToItemFactory(List<DtoToItemMapper<?>> mapperList)
    {
        for(DtoToItemMapper<?> mapper : mapperList)
            mappers.put(mapper.getDtoClass(), mapper);
    }

    @SuppressWarnings("unchecked")
    public <D extends ItemDto> Item fromDto(D dto) throws IllegalAccessException
    {
        DtoToItemMapper<D> mapper = (DtoToItemMapper<D>) mappers.get(dto.getClass());

        if(mapper == null)
            throw new IllegalAccessException("No DTO mapper found for " + dto.getClass());
        else
            return mapper.fromDto(dto);
    }
}
