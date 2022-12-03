// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class EventListener
{
    @SubscribeEvent
    public void login(final PlayerEvent.PlayerLoggedInEvent event) {
        ThreadBackup.shouldBackup = true;
    }
    
    @SubscribeEvent
    public void tick(final TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (ThreadBackup.isRunning()) {
            final Object marker = ThreadBackup.getInstance().getMarker();
            if (marker != null) {
                synchronized (marker) {
                    marker.notify();
                    try {
                        marker.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
