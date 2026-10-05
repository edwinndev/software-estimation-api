package com.intecx.inscope.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        EQ igual, NE distinto, LK contiene (texto), IN uno de la lista, \
        GT mayor, LT menor, GE mayor o igual, LE menor o igual, BT entre dos valores""")
public enum FilterOperator {
    EQ,
    NE,
    LK,
    IN,
    GT,
    LT,
    GE,
    LE,
    BT
}
