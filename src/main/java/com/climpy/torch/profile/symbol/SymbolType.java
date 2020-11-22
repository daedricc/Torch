package com.climpy.torch.profile.symbol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum SymbolType {
    SYMBOL_16("Symbol 16", "§e§l#CLIMPY ", "torch.symbol"),
    SYMBOL_15("Symbol 15", "§e§lGOD ", "torch.symbol"),
    SYMBOL_14("Symbol 14", "§e§lMASTER ", "torch.symbol"),
    SYMBOL_13("Symbol 13", "§e§lPOWER ", "torch.symbol"),
    SYMBOL_12("Symbol 12", "§e§l300IQ ", "torch.symbol"),
    SYMBOL_11("Symbol 11", "§c♚ ", "torch.symbol"),
    SYMBOL_10("Symbol 10","§c☯ ", "torch.symbol"),
    SYMBOL_9("Symbol 9", "§b❁ ", "torch.symbol"),
    SYMBOL_8("Symbol 8", "§b✦ ", "torch.symbol"),
    SYMBOL_7("Symbol 7", "§b✎ ", "torch.symbol"),
    SYMBOL_6("Symbol 6", "§d☘ ", "torch.symbol"),
    SYMBOL_5("Symbol 5", "§d♚ ", "torch.symbol"),
    SYMBOL_4("Symbol 4", "§d❥ ", "torch.symbol"),
    SYMBOL_3("Symbol 3", "§f♫ ", "torch.symbol"),
    SYMBOL_2("Symbol 2", "§f✿ ", "torch.symbol"),
    SYMBOL_1("Symbol 1", "§f★ ", "torch.symbol"),
    SYMBOL_0("Symbol 0", "", "torch.default");

    private final String name;
    private final String displayName;
    private final String permission;

    public static SymbolType getSymbolOrDefault(String internalName) {
        SymbolType symbolType;
        try {
            symbolType = valueOf(internalName.toUpperCase());
        } catch (Exception ex) {
            symbolType = SYMBOL_0;
        }

        return symbolType;
    }

}
