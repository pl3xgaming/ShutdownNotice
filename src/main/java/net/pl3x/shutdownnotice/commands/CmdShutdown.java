package net.pl3x.shutdownnotice.commands;

import net.pl3x.shutdownnotice.Shutdown;
import net.pl3x.shutdownnotice.ShutdownNotice;
import net.pl3x.shutdownnotice.ShutdownType;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdShutdown implements CommandExecutor {
	private ShutdownNotice plugin;

	public CmdShutdown(ShutdownNotice plugin) {
		this.plugin = plugin;
	}

	private String getFinalArg(final String[] args, final int start) {
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) {
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}

	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("shutdown"))
			return false;
		if (!cs.hasPermission("shutdownnotice.command.shutdown")) {
			cs.sendMessage(plugin.colorize("&4You do not have permission for that command!"));
			plugin.log(cs.getName() + " was denied access to that command!");
			return true;
		}
		if (args.length == 0) {
			cs.sendMessage(plugin.colorize("&4Must specify a time to delay!"));
			return true;
		}
		Shutdown shutdown = plugin.getShutdown();
		if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("abort") || args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("0")) {
			if (shutdown.getTime() == null) {
				cs.sendMessage(plugin.colorize("&4Nothing to cancel!"));
				return true;
			}
			shutdown.broadcastMessage(plugin.getConfig().getString("cancel-message", "&1[&4ATTENTION&1] &eThe &4{shutdowntype} &ehas been cancelled!"));
			if (plugin.getConfig().getBoolean("use-scoreboard", true)) {
				shutdown.getScoreboardController().getScoreboard().resetScores(Bukkit.getOfflinePlayer(plugin.colorize(plugin.getConfig().getString("timeleft-scoreboard", "&aIn:"))));
				for (Player player : Bukkit.getOnlinePlayers())
					player.setScoreboard(shutdown.getScoreboardController().getScoreboardManager().getNewScoreboard());
			}
			shutdown.setTime(null);
			return true;
		}
		if (shutdown.getTime() != null) {
			cs.sendMessage(plugin.colorize("&4There is a shutdown already in progress!"));
			return true;
		}
		Integer delay;
		try {
			delay = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			cs.sendMessage(plugin.colorize("&4Delay must be a number!"));
			return true;
		}
		if (args.length > 1)
			shutdown.setReason(getFinalArg(args, 1));
		ShutdownType shutdownType = ShutdownType.SHUTDOWN;
		if (label.equalsIgnoreCase("RESTART"))
			shutdownType = ShutdownType.RESTART;
		if (label.equalsIgnoreCase("REBOOT"))
			shutdownType = ShutdownType.REBOOT;
		shutdown.setTime(delay - 1);
		shutdown.setType(shutdownType);
		cs.sendMessage(plugin.colorize("&dShutdown task started!"));
		plugin.getShutdown().broadcastMessage(plugin.getConfig().getString("shutdown-message", "&1[&4ATTENTION&1] &eThe server will &4{shutdowntype} &ein &7{timeleft}&e for {reason}!"));
		shutdown.setTime(delay);
		return true;
	}
}
