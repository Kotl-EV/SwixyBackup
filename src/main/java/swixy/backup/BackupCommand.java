// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import ml.luxinfine.helper.utils.MsgUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class BackupCommand extends CommandBase implements ICommand {



    @Override
    public String getCommandName() {
        return "backup";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/backup";
    }

    @Override
    public void processCommand(final ICommandSender sender, final String[] args) {
        if (args == null || args.length >= 1) {}
        if (ThreadBackup.isRunning()) {
            MsgUtils.send(sender, "swixybackup:command.alreadyrunning");
        }
        else {
            MsgUtils.send(sender,"swixybackup:command.playerstarted",sender.getCommandSenderName());
            ThreadBackup.doBackup(true);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
        return true;
    }
}

















/*
public class BackupCommand extends AromaBaseCommand
{
    public String getCommandName() {
        return "backup";
    }
    
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return sender != null && (Config.instance.allPlayers || ServerUtil.isPlayerAdmin(sender));
    }
    
    public String getCommandUsage(final ICommandSender sender) {
        return "/backup";
    }
    
    public void processCommand(final ICommandSender sender, final String[] args) {
        if (args == null || args.length >= 1) {}
        if (ThreadBackup.isRunning()) {
            LogHelper.sendMessageToPlayer(AromaBackup.instance.logger, sender, StatCollector.translateToLocal("aromabackup:command.alreadyrunning"));
        }
        else {
            LogHelper.sendMessageToPlayers(AromaBackup.instance.logger, StatCollector.translateToLocalFormatted("aromabackup:command.playerstarted", new Object[] { sender.getCommandSenderName() }));
            ThreadBackup.doBackup(true);
        }
    }
}*/
