package swixy.backup;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ml.luxinfine.config.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Config(name = "SwixyBackup")
public class ModConfig {

    @ConfigBoolean(category = "general", comment = "Если true то все игроки могут использовать команду /backup")
    public static boolean allplayers = true;

    @ConfigIntCollection(category = "general", comment = "Белый список миров для бэкапа")
    public static List<Integer> whitelist = Lists.newArrayList(0, 1 ,-1);

    @ConfigIntCollection(category = "general", comment = "Черный список миров для бэкапа")
    public static List<Integer> blacklist = Lists.newArrayList( 1 ,-1);

    @ConfigBoolean(category = "general", comment = "Если true использовать белый список для бэкапа иначе бэкапить все")
    public static boolean useWhitelist = true;

    @ConfigInt(category = "general", comment = "Интервал бэкапа в минутах")
    public static int delay = 30;

    @ConfigString(category = "general", comment = "Папка для сохранения бэкапов")
    public static String location = "./backups";

    @ConfigBoolean(category = "general", comment = "Если true то делает бэкап при старте сервера")
    public static boolean onStartup = true;

    @ConfigBoolean(category = "general", comment = "Если true пропускать бэкап когда на сервере нет игроков")
    public static boolean skipBackup = true;

    @ConfigInt(category = "general", comment = "Коэфициент сжатия архива от 1 до 9")
    public static int compressionRate = 5;

    @ConfigInt(category = "general", comment = "Количество бэкапов ппо достижению которого старые бэкапы будут перезаписываться")
    public static int toKeep = 5;



}
