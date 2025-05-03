package com.demo.mappers;

import com.demo.dto.request.CategoriesNoIDRequest;
import com.demo.dto.request.CategoriesRequest;
import com.demo.dto.response.CategoriesResponse;
import com.demo.entities.Categories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoriesMapper {

    @Mapping(source = "parent.id", target = "parentId")
    CategoriesResponse toCategoryResponse(Categories category);

    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapParent")
    Categories toCategory(CategoriesRequest request);

    @Mapping(source = "parentId", target = "parent", qualifiedByName = "mapParent")
    Categories toCategoryNoID(CategoriesNoIDRequest request);

    @Named("mapParent")
    default Categories mapParent(Long parentId) {
        if (parentId == null) return null;
        Categories parent = new Categories();
        parent.setId(parentId);
        return parent;
    }
}
