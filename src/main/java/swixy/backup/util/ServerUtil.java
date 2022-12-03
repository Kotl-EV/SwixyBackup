package swixy.backup.util;

import java.util.Iterator;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.Level;
import net.minecraft.util.IChatComponent;
import net.minecraft.command.ICommand;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentStyle;

public class ServerUtil
{
    public static ChatComponentStyle getChatForString(final String str) {
        return (ChatComponentStyle)new ChatComponentText(str);
    }

    public static boolean isPlayerAdmin(final ICommandSender sender) {
        if (sender == null) {
            return false;
        }
        if (sender instanceof EntityPlayerMP) {
            return isPlayerAdmin(((EntityPlayerMP)sender).getGameProfile());
        }
        return "Server".equals(sender.getCommandSenderName());
    }

    public static boolean isPlayerAdmin(final GameProfile player) {
        return (!MinecraftServer.getServer().isDedicatedServer() && player.equals((Object)Minecraft.getMinecraft().thePlayer.getGameProfile())) || MinecraftServer.getServer().getConfigurationManager().func_152596_g(player);
    }

    public static void printCommandUsage(final ICommandSender sender, final ICommand command) {
        sender.addChatMessage((IChatComponent)new ChatComponentText(command.getCommandUsage(sender)));
    }

    public static void sendMessageToAllPlayers(final String message) {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getConfigurationManager().sendChatMsg((IChatComponent)getChatForString(message));
        }
        else {

        }
    }

    public static void printPermissionDenied(final ICommandSender sender) {
        sender.addChatMessage((IChatComponent)getChatForString(StatCollector.translateToLocal("commands.generic.permission")));
    }

    public static EntityPlayerMP getPlayer(final String name) {
        for (final Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            final EntityPlayerMP player = (EntityPlayerMP)o;
            if (player.getCommandSenderName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
}
