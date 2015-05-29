package net.pl3x.bukkit.shutdownnotice.listener;

import net.pl3x.bukkit.shutdownnotice.Main;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {
	private Main plugin;

	public PingListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerListPing(ServerListPingEvent event) {
		if (!Config.UPDATE_PING_MOTD.getBoolean()) {
			return;
		}

		ServerStatus status = plugin.getStatus();
		State state = status.getState();
		Integer timeLeft = status.getTimeLeft();
		String reason = status.getReason();

		if (state == null || state.equals(State.RUNNING) || timeLeft == null) {
			return;
		}

		if (reason == null) {
			reason = "";
		}

		int seconds = timeLeft % 60;
		int minutes = timeLeft / 60;

		String time = Lang.TIME_FORMAT.get().replace("{minutes}", String.format("%02d", minutes)).replace("{seconds}", String.format("%02d", seconds));
		String action = state.equals(State.SHUTDOWN) ? Lang.SHUTTING_DOWN.get() : Lang.RESTARTING.get();

		String pingMessage = Lang.PING_MESSAGE.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);

		event.setMotd(ChatColor.translateAlternateColorCodes('&', pingMessage));
	}
}