package ru.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.hub.enums.DeviceType;
import ru.practicum.model.hub.enums.HubEventType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEvent extends HubEvent{
    @NotBlank
    String id;
    DeviceType type;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
