// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import swixy.backup.util.ServerUtil;
import swixy.backup.util.Util;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;

@Mod(modid = "SwixyBackup", name = "SwixyBackup", acceptableRemoteVersions = "*")
public class SwixyBackup
{
    //@Mod.Instance("SwixyBackup")
    //public static SwixyBackup instance;


    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        final EventListener el = new EventListener();
        FMLCommonHandler.instance().bus().register((Object)el);
        MinecraftForge.EVENT_BUS.register((Object)el);
    }
    
    @Mod.EventHandler
    public void serverStarting(final FMLServerStartingEvent event) {
        event.registerServerCommand(new BackupCommand());
        WorldIndex.load();
        if (ModConfig.general.delay * 60000L != 0L) {
            ThreadSchedule.startNewThread();
        }
        ThreadBackup.shouldBackup = true;
    }
    
    @Mod.EventHandler
    public void serverStarted(final FMLServerStartedEvent event) {
        try {
            final BasicFileAttributes attr = Files.readAttributes(Util.getWorldFolder().toPath(), BasicFileAttributes.class, new LinkOption[0]);
            final long fileCreated = attr.creationTime().toMillis();
            if (ModConfig.general.onStartup) {
                if (fileCreated > System.currentTimeMillis() - 10000L && fileCreated <= System.currentTimeMillis()) {
                    ThreadSchedule.nextbackup = System.currentTimeMillis() + 60000L;
                }
                else {
                    ThreadSchedule.nextbackup = System.currentTimeMillis() + 5000L;
                }
            }
            else {
                ThreadSchedule.nextbackup = System.currentTimeMillis() + ModConfig.general.delay * 60000L;
            }
        }
        catch (IOException e) {
            if (ModConfig.general.onStartup) {
                ThreadSchedule.nextbackup = System.currentTimeMillis() + 60000L;
            }
            else {
                ThreadSchedule.nextbackup = System.currentTimeMillis() + ModConfig.general.delay * 60000L;
            }
            e.printStackTrace();
        }
        Util.logger.log(Level.INFO, "Next Backup in: " + (ThreadSchedule.nextbackup - System.currentTimeMillis() + 1L) / 1000L + " seconds.");
    }
    
    @Mod.EventHandler
    public void serverStopping(final FMLServerStoppingEvent event) {
        ThreadSchedule.stopThread();
    }
    
    public String getCommandSenderName() {
        return "SwixyBackup";
    }

    public boolean canCommandSenderUseCommand(final int i, final String s) {
        return true;
    }

    public World getEntityWorld() {
        return null;
    }
    
    public void addChatMessage(final IChatComponent comp) {
        Util.logger.log(Level.TRACE, comp.getUnformattedText());
    }
    
    //@NetworkCheckHandler
    //public boolean checkModLists(final Map<String, String> modList, final Side side) {
        //return true;
    //}
    
    public IChatComponent getFormattedCommandSenderName() {
        return (IChatComponent) ServerUtil.getChatForString(this.getCommandSenderName());
    }
    
    public ChunkCoordinates getCommandSenderPosition() {
        return new ChunkCoordinates();
    }
}
