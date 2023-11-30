package swixy.backup;

import com.google.common.collect.Lists;
import ml.luxinfine.config.api.ConfigCategory;
import ml.luxinfine.config.api.ConfigValue;
import java.util.List;

@ml.luxinfine.config.api.Config
public class ModConfig {


    @ConfigCategory
    public static class general {
        @ConfigValue("Если true то все игроки могут использовать команду /backup")
        public static boolean allplayers = true;

        @ConfigValue("Если true использовать белый список для бэкапа иначе бэкапить все")
        public static boolean useWhitelist = true;

        @ConfigValue("Интервал бэкапа в минутах")
        public static int delay = 30;

        @ConfigValue("Папка для сохранения бэкапов")
        public static String location = "./backups";

        @ConfigValue("Если true то делает бэкап при старте сервера")
        public static boolean onStartup = true;

        @ConfigValue("Если true пропускать бэкап когда на сервере нет игроков")
        public static boolean skipBackup = true;

        @ConfigValue("Коэфициент сжатия архива от 1 до 9")
        public static int compressionRate = 5;

        @ConfigValue("Количество бэкапов ппо достижению которого старые бэкапы будут перезаписываться")
        public static int toKeep = 5;

        @ConfigValue("Белый список миров для бэкапа")
        public static List<Integer> whitelist = Lists.newArrayList(0, 1, -1);

        @ConfigValue("Черный список миров для бэкапа")
        public static List<Integer> blacklist = Lists.newArrayList(1, -1);

    }

}















