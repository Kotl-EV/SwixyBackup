// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import java.util.Date;
import java.io.File;
import org.apache.logging.log4j.Logger;

public class BackupInformation implements Comparable
{
    public static Logger logger;

    public final int year;
    public final int month;
    public final int day;
    public final int hour;
    public final int minute;
    public final String world;
    
    public BackupInformation(final String world, final int year, final int month, final int day, final int hour, final int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.world = world;
    }
    
    @Override
    public String toString() {
        return this.world + "=" + this.year + "=" + this.month + "=" + this.day + "=" + this.hour + "=" + this.minute;
    }
    
    public static BackupInformation loadFromString(final String str) {
        final String[] data = str.split("=");
        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        String world = "";
        try {
            world = data[0];
            year = Integer.valueOf(data[1]);
            month = Integer.valueOf(data[2]);
            day = Integer.valueOf(data[3]);
            hour = Integer.valueOf(data[4]);
            minute = Integer.valueOf(data[5]);
        }
        catch (Exception e) {
            logger.info("Failed to load Backup Information.");
        }
        return new BackupInformation(world, year, month, day, hour, minute);
    }
    
    public boolean exists() {
        return this.getFile().exists();
    }
    
    public File getFile() {
        return new File(this.getDir(), "Backup-" + this.world + "-" + this.year + "-" + this.month + "-" + this.day + "--" + this.hour + "-" + this.minute + ".zip");
    }
    
    public File getDir() {
        return new File(ModConfig.general.location + "/" + this.world + "/" + this.year + "/" + this.month + "/" + this.day);
    }
    
    @Override
    public int compareTo(final Object arg0) {
        final BackupInformation other = (BackupInformation)arg0;
        return this.getDate().compareTo(other.getDate());
    }
    
    public Date getDate() {
        return new Date(this.year, this.month - 1, this.day, this.hour, this.minute);
    }
}
