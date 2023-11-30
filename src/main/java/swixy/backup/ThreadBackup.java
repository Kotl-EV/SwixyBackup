// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import java.util.*;

import com.gamerforea.eventhelper.util.EventUtils;
import ml.luxinfine.config.Config;
import ml.luxinfine.helper.utils.PlayerUtils;
import swixy.backup.api.BackupEvent;
import swixy.backup.util.Util;
import swixy.backup.util.ZipCompression;
import cpw.mods.fml.common.FMLCommonHandler;

import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;

import ml.luxinfine.helper.utils.MsgUtils;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldServer;
import java.io.File;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;

public class ThreadBackup extends Thread
{

    private Object marker;
    private static ThreadBackup current;
    static boolean shouldBackup;
    
    private ThreadBackup() {
        (ThreadBackup.current = this).setName("SwixyBackup");
        this.setDaemon(true);
        this.start();
    }
    
    @Override
    public void run() {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            Util.logger.log(Level.INFO,"Server instance was null. Stopping Backup.");
            this.finish();
            return;
        }
        PlayerUtils.getOnlinePlayers().stream().filter(player -> EventUtils.hasPermission(player,"backup.info")).forEach(player -> MsgUtils.send(player, "swixybackup:backup.start"));
        try {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.setLevelSaving(false);
        final Set<Map.Entry<File, String>> additionalFiles = getNewAdditionalFilesList();
        try {
            MinecraftForge.EVENT_BUS.post((Event)new BackupEvent.BackupStartEvent(additionalFiles));
        }
        catch (Exception e2) {
            Util.logger.log(Level.INFO,"SwixyBackup поймал исключение из своего API. Скорее всего, есть мод, вызывающий проблемы.", (Throwable)e2);
            Util.logger.log(Level.INFO,"В любом случае продолжайте резервное копирование. Некоторые дополнительные файлы могут не быть скопированы.");
        }
        synchronized (this.marker = new Object()) {
            try {
                Util.logger.log(Level.INFO,"Suspending Server Thread.");
                this.marker.wait();
            }
            catch (InterruptedException e3) {
                e3.printStackTrace();
            }
            this.save();
            this.marker.notify();
            Util.logger.log(Level.INFO,"Продолжающийся поток сервера.");
            this.marker = null;
        }
        Util.logger.log(Level.INFO,"Сохранение мира");
        Util.logger.log(Level.INFO,"Подождите пару секунд");
        long delay = 0L;
        try {
            final Calendar d = Calendar.getInstance();
            final BackupInformation binfo = new BackupInformation(Util.getWorldName(), d.get(1), d.get(2) + 1, d.get(5), d.get(11), d.get(12));
            Util.logger.log(Level.INFO,"Создание информации о резервной копии");
            final File dir = binfo.getDir();
            final File to = binfo.getFile();
            dir.mkdirs();
            to.getParentFile().mkdirs();
            Util.logger.log(Level.INFO,"Создание дирректории резервных копий");
            this.compress(to.getCanonicalPath(), additionalFiles);
            Util.logger.log(Level.INFO,"Сжатие резервной копии");
            WorldIndex.add(binfo);
            WorldIndex.save();
            Util.logger.log(Level.INFO,"Сохранение индекса мира.");
            delay = ModConfig.general.delay * 60000L;
            PlayerUtils.getOnlinePlayers().stream().filter(player -> EventUtils.hasPermission(player,"backup.info")).forEach(player -> MsgUtils.send(player, "swixybackup:backup.done"));
        }
        catch (Exception e4) {
            delay = ModConfig.general.delay * 60000L / 2L;
            PlayerUtils.getOnlinePlayers().stream().filter(player -> EventUtils.hasPermission(player,"backup.info")).forEach(player -> MsgUtils.send(player, "swixybackup:backup.failed"));
            Util.logger.log(Level.INFO,"Сбой резервного копирования сервера!", (Throwable)e4);
        }
        finally {
            Util.logger.log(Level.INFO,"Настройка следующего резервного копирования.");
            ThreadSchedule.nextbackup = System.currentTimeMillis() + delay;
            Util.logger.log(Level.INFO,"Следующее резервное копирование через " + delay / 60000L + " мин.");
            this.setLevelSaving(true);
            Util.logger.log(Level.INFO,"Запущено автосохранение мира.");
            PlayerUtils.getOnlinePlayers().stream().filter(player -> EventUtils.hasPermission(player,"backup.info")).forEach(player -> MsgUtils.send(player, "swixybackup:backup.next", ModConfig.general.delay));
            try {
                if (ModConfig.general.toKeep != 0) {
                    for (int removes = WorldIndex.getList().size() - ModConfig.general.toKeep, i = 0; i < removes; ++i) {
                        final BackupInformation binfo2 = WorldIndex.getList().get(i);
                        final File to2 = binfo2.getFile();
                        to2.delete();
                        File dir2 = binfo2.getDir();
                        while (dir2.list() != null && dir2.list().length == 0) {
                            dir2.delete();
                            dir2 = dir2.getParentFile();
                            if (dir2 == null) {
                                break;
                            }
                        }
                    }
                }
                Util.logger.info("Старые резервные копии удалены");
            }
            catch (Exception e5) {
                Util.logger.info("Не удалось удалить старые резервные копии.", (Throwable)e5);
                Util.logger.info("Это проблема, возможно, вы захотите ее решить...");
            }
            this.finish();
        }
    }

    public boolean shouldSaveDimension(final int dim) {
        if (ModConfig.general.useWhitelist) {
            for (int i = 0; i < ModConfig.general.whitelist.size(); ++i) {
                if (ModConfig.general.whitelist.get(i) == dim) {
                    return true;
                }
            }
            return false;
        }
        for (int i = 0; i < ModConfig.general.blacklist.size(); ++i) {
            if (ModConfig.general.blacklist.get(i) == dim) {
                return false;
            }
        }
        return true;
    }
    private void setLevelSaving(final boolean value) {
        final MinecraftServer server = MinecraftServer.getServer();
        for (int i = 0; i < server.worldServers.length; ++i) {
            if (server.worldServers[i] != null) {
                final WorldServer worldserver = server.worldServers[i];
                if (shouldSaveDimension(worldserver.provider.dimensionId)) {
                    worldserver.levelSaving = !value;
                }
            }
        }
    }
    
    private void save() {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server.getConfigurationManager() != null) {
            Util.logger.log(Level.INFO,"Сохранение данных игрока");
            server.getConfigurationManager().saveAllPlayerData();
        }
        try {
            for (int i = 0; i < server.worldServers.length; ++i) {
                if (server.worldServers[i] != null) {
                    try {
                        final WorldServer worldserver = server.worldServers[i];
                        if (!shouldSaveDimension(worldserver.provider.dimensionId)) {
                            Util.logger.log(Level.INFO,"Прорущенно измерение " + worldserver.provider.dimensionId + " при резервном копировании.");
                        }
                        else {
                            Util.logger.log(Level.INFO,"Сохранено измерение " + worldserver.provider.dimensionId + " при резервном копировании.");
                            worldserver.saveAllChunks(true, (IProgressUpdate)null);
                            worldserver.saveChunkData();
                        }
                    }
                    catch (Throwable t) {
                        Util.logger.log(Level.INFO,"Failed to save dimension " + i + " continuing with the next dimension.");
                        Util.logger.log(Level.INFO,"This is not AromaBackup's fault. I't probably caused by a block in that world.");
                        Util.logger.log(Level.INFO,"If you want to report it, report it to that block's author.");
                        Util.logger.log(Level.INFO,"Failed dimension save.", t);
                    }
                }
            }
        }
        catch (Throwable t2) {
            Util.logger.log(Level.INFO,"Failed to save the world. This is a severe error. Please report this.");
            Util.logger.log(Level.INFO,"Failed to save the world.", t2);
        }
    }
    
    private void compress(final String to, final Set<Map.Entry<File, String>> additionalFiles) throws Exception {
        final FileOutputStream fos = new FileOutputStream(to);
        final ZipOutputStream zip = new ZipOutputStream(fos);
        zip.setLevel(ModConfig.general.compressionRate);
        final File[] files = Util.getWorldFolder().listFiles();
        final List<String> folders = new ArrayList<String>();
        for (final File file : files) {
            Util.logger.info(file.getCanonicalPath());
            if (file.isFile()) {
                ZipCompression.addFileToZip(Util.getWorldName(), file.getCanonicalPath(), zip);
            }
            else {
                folders.add(file.getName());
            }
        }
        for (final WorldServer world : MinecraftServer.getServer().worldServers) {
            if (world != null) {
                final String name = world.provider.getSaveFolder();
                if (!shouldSaveDimension(world.provider.dimensionId) && folders.contains(name)) {
                    folders.remove(name);
                }
            }
        }
        for (final String file2 : folders) {
            ZipCompression.addFolderToZip(Util.getWorldName(), new File(Util.getWorldFolder(), file2).getCanonicalPath(), zip);
        }
        for (final Map.Entry<File, String> e : additionalFiles) {
            if (e.getKey().isFile()) {
                ZipCompression.addFileToZip("additionalfiles/" + e.getValue(), e.getKey().getCanonicalPath(), zip);
            }
            else {
                ZipCompression.addFolderToZip("additionalfiles/" + e.getValue(), e.getKey().getCanonicalPath(), zip);
            }
        }
        zip.flush();
        zip.close();
    }
    
    private void finish() {
        ThreadBackup.shouldBackup = (MinecraftServer.getServer().getAllUsernames().length > 0);
        ThreadBackup.shouldBackup = (ThreadBackup.shouldBackup || MinecraftServer.getServer().getConfigurationManager().playerEntityList.size() > 0);
        try {
            MinecraftForge.EVENT_BUS.post((Event)new BackupEvent.BackupDoneEvent());
        }
        catch (Exception e) {
            Util.logger.log(Level.INFO,"SwixyBackup поймал исключение из своего API. Скорее всего, есть мод, вызывающий проблемы.", (Throwable)e);
        }
        Util.logger.log(Level.INFO,"Резервное копирование завершено");
        ThreadBackup.current = null;
    }
    
    public Object getMarker() {
        return this.marker;
    }
    
    public static boolean isRunning() {
        return ThreadBackup.current != null;
    }
    
    static ThreadBackup getInstance() {
        return ThreadBackup.current;
    }
    
    public static boolean doBackup() {
        return doBackup(false);
    }
    
    public static boolean doBackup(final boolean force) {
        if (isRunning()) {
            Util.logger.log(Level.INFO,"Not doing a backup, because another backup is already running.");
            return false;
        }
        if (!force && ModConfig.general.skipBackup && !ThreadBackup.shouldBackup) {
            Util.logger.log(Level.INFO,"Skipping world backup because no players were on the server.");
            ThreadSchedule.nextbackup = System.currentTimeMillis() + ModConfig.general.delay * 60000L;
            Util.logger.log(Level.INFO,"Next Backup in: " + (ThreadSchedule.nextbackup - System.currentTimeMillis() + 1L) / 60000L + " minutes.");
            return false;
        }
        new ThreadBackup();
        return true;
    }
    
    private static String getSideString() {
        return FMLCommonHandler.instance().getSide().toString().toLowerCase();
    }
    
    private static Set<Map.Entry<File, String>> getNewAdditionalFilesList() {
        return new HashSet<Map.Entry<File, String>>() {
            @Override
            public boolean add(final Map.Entry<File, String> e) {
                return e.getKey().exists() && super.add(e);
            }
            
            @Override
            public boolean addAll(final Collection<? extends Map.Entry<File, String>> c) {
                for (final Map.Entry<File, String> e : c) {
                    if (!this.add(e)) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean remove(final Object arg0) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean removeAll(final Collection<?> arg0) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean retainAll(final Collection<?> arg0) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    static {
        ThreadBackup.current = null;
    }
}
