package org.atechtrade.rent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContactType {
    EMAIL("email"),
    PHONE("тел."),
    SOCIAL("соц. профил"),
    WEBSITE("уеб сайт"),
    EDELIVERY("за доставка");

    private final String name;
}
