// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import swixy.backup.util.Util;
import java.io.FileWriter;
import java.io.File;
import java.util.Collections;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import ml.luxinfine.helper.utils.MsgUtils;

import java.util.ArrayList;

public class WorldIndex
{
    private static ArrayList<BackupInformation> info;
    
    static void load() {
        final File file = getFile();
        if (!file.exists()) {
            WorldIndex.info.clear();
            MsgUtils.broadcast("Could not find backupstore.txt file.");
            MsgUtils.broadcast("It will be created, when the first backup is done.");
        }
        else {
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(file));
                WorldIndex.info.clear();
                while (true) {
                    final String string = reader.readLine();
                    if (string == null) {
                        break;
                    }
                    WorldIndex.info.add(BackupInformation.loadFromString(string));
                }
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(WorldIndex.info);
    }
    
    static void add(final BackupInformation binf) {
        WorldIndex.info.add(binf);
    }
    
    static void save() {
        Collections.sort(WorldIndex.info);
        final File file = getFile();
        try {
            final FileWriter wr = new FileWriter(file);
            for (int i = 0; i < WorldIndex.info.size(); ++i) {
                final BackupInformation binfo = WorldIndex.info.get(i);
                if (binfo.exists()) {
                    wr.write(binfo.toString() + "\r\n");
                }
            }
            wr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static File getFile() {
        return new File(ModConfig.location + "/" + Util.getWorldName() + "/backupstore.txt");
    }
    
    static ArrayList<BackupInformation> getList() {
        return WorldIndex.info;
    }
    
    static {
        WorldIndex.info = new ArrayList<BackupInformation>();
    }
}
