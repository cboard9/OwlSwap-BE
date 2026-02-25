package com.cboard.owlswap.owlswap_backend.model.context;

import com.cboard.owlswap.owlswap_backend.model.Category;
import com.cboard.owlswap.owlswap_backend.model.Location;
import com.cboard.owlswap.owlswap_backend.model.User;

public record ItemMappingContext (
        User user,
        Category category,
        Location location
){}
