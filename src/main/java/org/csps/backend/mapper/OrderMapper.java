    package org.csps.backend.mapper;

    import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
    import org.csps.backend.domain.dtos.response.OrderResponseDTO;
    import org.csps.backend.domain.entities.Order;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;


    @Mapper(componentModel = "spring")
    public interface OrderMapper {

        @Mapping(source = "orderId", target = "orderId")
        @Mapping(source = "merchVariant.merch.merchName", target = "merchVariantName")
        @Mapping(source = "merchVariant.color", target = "merchVariantColor")
        @Mapping(source = "merchVariant.size", target = "merchVariantSize")
        @Mapping(source = "quantity", target = "quantity")
        @Mapping(target = "studentName", expression = "java(getStudentName(order))")
        @Mapping(source = "totalPrice", target = "totalPrice")
        @Mapping(source = "orderStatus", target = "orderStatus")
        OrderResponseDTO toResponseDTO(Order order);

        @Mapping(source = "merchVariantId", target = "merchVariant.merchVariantId")
        @Mapping(source = "quantity", target = "quantity")
        @Mapping(target = "student", ignore = true)
        @Mapping(target = "orderDate", ignore = true)
        @Mapping(target = "totalPrice", ignore = true)
        Order toEntity(OrderPostRequestDTO orderPostRequestDTO);

        default String getStudentName(Order order) {
            var profile = order.getStudent().getUserAccount().getUserProfile();
            String middle = profile.getMiddleName();
            String middleInitial = (middle == null || middle.isEmpty()) ? "" : middle.charAt(0) + ". ";
            return profile.getFirstName() + " " + middleInitial + profile.getLastName();
        }
        
    }
