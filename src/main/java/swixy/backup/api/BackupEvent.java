package swixy.backup.api;

import java.io.File;
import java.util.Map;
import java.util.Set;
import cpw.mods.fml.common.eventhandler.Event;

public abstract class BackupEvent extends Event
{
    private BackupEvent() {
    }

    public static class BackupStartEvent extends BackupEvent
    {
        public final Set<Map.Entry<File, String>> list;

        public BackupStartEvent(final Set<Map.Entry<File, String>> list) {
            this.list = list;
        }
    }

    public static class BackupDoneEvent extends BackupEvent
    {
        public BackupDoneEvent() {
        }
    }
}
