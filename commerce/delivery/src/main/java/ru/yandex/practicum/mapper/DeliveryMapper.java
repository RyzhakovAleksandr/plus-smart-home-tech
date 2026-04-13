package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    Address toAddressEntity(AddressDto addressDto);

    AddressDto toAddressDto(Address addressEntity);

    DeliveryDto toDto(Delivery delivery);

    Delivery toEntity(DeliveryDto deliveryDto);
}
