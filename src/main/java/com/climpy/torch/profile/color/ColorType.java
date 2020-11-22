package com.climpy.torch.profile.color;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.profile.symbol.SymbolType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor @Getter
public enum  ColorType {
    COLOR_9("Blue", ChatColor.BLUE, 9),
    COLOR_8("Dark Gray", ChatColor.DARK_GRAY, 7),
    COLOR_7("Dark Red", ChatColor.DARK_RED, 14),
    COLOR_6("Gold", ChatColor.GOLD, 1),
    COLOR_5("Dark Purple", ChatColor.DARK_PURPLE, 2),
    COLOR_3("Dark Aqua", ChatColor.DARK_AQUA, 9),
    COLOR_2("Dark Green", ChatColor.DARK_GREEN, 13),
    COLOR_1("Dark Blue", ChatColor.DARK_BLUE, 11),
    COLOR_F("White", ChatColor.WHITE, 0),
    COLOR_E("Yellow", ChatColor.YELLOW, 4),
    COLOR_D("Light Purple", ChatColor.LIGHT_PURPLE, 6),
    COLOR_C("Red", ChatColor.RED, 14),
    COLOR_B("Aqua", ChatColor.AQUA, 9),
    COLOR_A("Green", ChatColor.GREEN, 5),
    COLOR_DEFAULT("Default", ChatColor.RESET, 0);

    private final String name;
    private final ChatColor chatColor;
    private final int variant;

    public static ColorType getColorOrDefault(String internalName) {
        ColorType colorType;
        try {
            colorType = valueOf(internalName.toUpperCase());
        } catch (Exception ex) {
            colorType = COLOR_DEFAULT;
        }

        return colorType;
    }

}
