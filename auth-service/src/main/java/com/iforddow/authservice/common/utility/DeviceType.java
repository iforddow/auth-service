package com.iforddow.authservice.common.utility;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/**
 * A DeviceType enum representing different types of devices.
 * Each enum constant has an associated string value.
 * Includes a method to create an enum constant from a string value.
 *
 * @author IFD
 * @since 2025-11-09
 * */
@Getter
public enum DeviceType {
    WEB("web"),
    MOBILE("mobile");

    private final String type;

    DeviceType(String type) {
        this.type = type;
    }

    @JsonCreator
    public static DeviceType fromString(String text) {
        for(DeviceType deviceType : DeviceType.values()) {
            if(deviceType.type.equalsIgnoreCase(text)) {
                return deviceType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    @Override
    public String toString() {
        return type;
    }

}
