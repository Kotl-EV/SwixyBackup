// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import swixy.backup.util.Util;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;

public class ThreadSchedule extends Thread
{
    private static ThreadSchedule instance;
    private boolean shouldrun;
    static long nextbackup;
    
    public ThreadSchedule() {
        this.shouldrun = true;
        (ThreadSchedule.instance = this).setName("SwixyBackup-Schedule");
        this.setDaemon(true);
        ThreadSchedule.nextbackup = System.currentTimeMillis() + ModConfig.delay * 60000L;
        this.start();
    }
    
    @Override
    public void run() {
        Util.logger.log(Level.INFO,"Starting Backup Schedule.");
        while (this.shouldrun && MinecraftServer.getServer() != null) {
            if (ThreadSchedule.nextbackup <= System.currentTimeMillis()) {
                if (!ThreadBackup.isRunning()) {
                    Util.logger.log(Level.INFO,"Doing a non-forced Backup.");
                    ThreadBackup.doBackup();
                    continue;
                }
            }
            try {
                Thread.sleep(2500L);
            }
            catch (InterruptedException e) {}
        }
        Util.logger.log(Level.INFO,"shouldrun = " + this.shouldrun + " Server = " + MinecraftServer.getServer());
        Util.logger.log(Level.INFO,"Stopping Backup Schedule.");
    }
    
    public static boolean isScheduleThreadRunning() {
        return ThreadSchedule.instance != null && ThreadSchedule.instance.shouldrun;
    }
    
    public static void startNewThread() {
        if (!isScheduleThreadRunning()) {
            new ThreadSchedule();
        }
    }
    
    public static void stopThread() {
        if (isScheduleThreadRunning()) {
            ThreadSchedule.instance.shouldrun = false;
        }
    }
}
