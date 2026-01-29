package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.enums.MerchType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "studentName", expression = "java(getStudentName(order))")
    OrderResponseDTO toResponseDTO(Order order);

    default String getStudentName(Order order) {
        var profile = order.getStudent().getUserAccount().getUserProfile();
        String middle = profile.getMiddleName();
        String middleInitial = (middle == null || middle.isEmpty()) ? "" : middle.charAt(0) + ". ";
        return profile.getFirstName() + " " + middleInitial + profile.getLastName();
    }

 
}