package com.farm.monitor.enums;

import lombok.Getter;

@Getter
public enum Location {
    HOUSING_9_START(1, "Housing #9. Start"),
    HOUSING_9_MIDDLE(2, "Housing #9. Middle"),
    HOUSING_9_END(3, "Housing #9, End");

    private final int orderId;
    private final String name;

    Location(int orderId, String name){
        this.orderId = orderId;
        this.name = name;
    }
}
