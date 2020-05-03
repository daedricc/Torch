package com.climpy.torch.locale;

import com.climpy.torch.TorchPlugin;
import com.climpy.torch.config.FileConfig;
import com.climpy.torch.profile.Profile;
import com.climpy.torch.utils.C;
import com.climpy.torch.utils.ConfigUtils;
import lombok.Getter;

@Getter
public enum Locale {
    TURKISH(new FileConfig(TorchPlugin.getInstance(), "./locales/" + "tr-TR" + ".yml"), "TURKISH"),
    ENGLISH(new FileConfig(TorchPlugin.getInstance(), "./locales/" + "en-US" + ".yml"), "ENGLISH");

    private FileConfig fileConfig;
    private String langName;

    Locale(FileConfig fileConfig, String langName) {
        this.fileConfig = fileConfig;
        this.langName = langName;
    }

    public static ConfigUtils turkishLangT = new ConfigUtils(TURKISH.fileConfig, "messages");
    public static ConfigUtils englishLangT = new ConfigUtils(ENGLISH.fileConfig, "messages");


    public static void replaceStrings() {
        turkishLangT.set("NO_PERM", C.RED + "Bu işlemi gerçekleştirmek için rütbeniz yeterli değil.");
        englishLangT.set("NO_PERM", C.RED + "Your rank is not enough for this operation.");

        turkishLangT.set("INVALID_USAGE", C.RED + "Kullanım: %1$s");
        turkishLangT.set("INVALID_USAGE", C.RED + "Usage: %1$s");

        turkishLangT.set("INVALID_PLAYER", C.WHITE + "%1$s" + C.RED + " ismine sahip bir oyuncu bulunamadı.");
        englishLangT.set("INVALID_PLAYER", C.RED + "No result for '" + C.WHITE + "%1$s" + C.RED + "' name.");

        turkishLangT.configSave();
        englishLangT.configSave();
    }

    public static Locale getDefaultLanguageOrDefault(String lang) {
        Locale locale;
        try {
            locale = valueOf(lang);
        } catch (Exception ex) {
            locale = ENGLISH;
        }

        return locale;
    }

    public static String message(Profile profile, String path, String... args) {
        if(profile.getLocale() == Locale.ENGLISH) {
            return String.format(englishLangT.getString(path), (Object[]) args);
        }

        if(profile.getLocale() == Locale.TURKISH) {
            return String.format(turkishLangT.getString(path), (Object[]) args);
        }

        return null;
    }
}
