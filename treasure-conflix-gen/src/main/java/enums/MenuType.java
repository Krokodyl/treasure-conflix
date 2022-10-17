package enums;

import static services.Utils.x;

public enum MenuType {
    
    CODE_START(0),
    BRIDGE_MENU(x("6CC7")),
    SAVE_MENU(x("6EC5")),
    LOAD_MENU(x("6FAA")),
    FREE_SPACE(x("6D87")),
    MANUAL_POINTERS(x("6C39")),
    MANUAL_MAP_1(x("6CC7")),
    MANUAL_MAP_2(x("6CC7")),
    MANUAL_COMBAT_1(x("6CC7")),
    MANUAL_COMBAT_2(x("6CC7")),
    MANUAL_COMBAT_3(x("6CC7")),
    MANUAL_TOWN_1(x("6CC7")),
    MANUAL_TOWN_2(x("6CC7")),
    MANUAL_TOWN_3(x("6CC7")),
    MANUAL_TOWN_4(x("6CC7")),
    EQUIPMENT_MENU(x("6CC7")),
    EQUIPMENT_LIST(x("6CC7")),
    INTRODUCTION(x("6CC7")),
    LOCATION_LIST(x("6CC7")),
    CODE_END(x("6CC7"));

    private int offset;

    MenuType(int offset) {
        this.offset = offset;
    }
}
