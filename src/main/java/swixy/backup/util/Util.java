package swixy.backup.util;


import java.io.File;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util
{
    public static File mcLocation;
    @Deprecated
    public static MinecraftServer getServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    @Deprecated
    public static File getMinecraftFolder() {
        return mcLocation;
    }

    public static File getWorldFolder() {
        final String world = (getServer().isDedicatedServer() ? "" : "saves/") + getWorldName();
        return new File(mcLocation, world);
    }

    public static String getWorldName() {
        return getServer().getFolderName();
    }

    public static final Logger logger = LogManager.getLogger("BackupLogger");

}
