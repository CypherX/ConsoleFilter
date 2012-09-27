package com.cypherx.consolefilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ConsoleFilter extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {
		
	}

	public void onEnable() {
		getDataFolder().mkdirs();
		loadConfiguration();

		log.setFilter(new CFFilter(loadFilter()));
		log(Level.INFO, "v" + getDescription().getVersion() + " Enabled!");	
	}

	private void loadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private ArrayList<FilterInfo> loadFilter() {
		ArrayList<FilterInfo> filterList = new ArrayList<FilterInfo>();
		filterList.clear();
		List<?> list = getConfig().getList("filter");
		for (int i = 0; i < list.size(); i++) {
			Object o = list.get(i);
			if (o instanceof LinkedHashMap) {
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, String> map = (LinkedHashMap<String, String>)o;
				boolean error = false;

				FilterType type = null;
				try {
					type = FilterType.valueOf(map.get("type").toUpperCase());
				} catch (IllegalArgumentException e) {
					log(Level.WARNING, "Filter " + (i + 1) + ": Invalid type (" + map.get("type") + ")");
				} catch (NullPointerException e) {
					log(Level.WARNING, "Filter " + (i + 1) + ": Type cannot be null");
				} finally {
					if (type == null)
						error = true;
				}

				String value = map.get("value");
				if (value == null) {
					log(Level.WARNING, "Filter " + (i + 1) + ": Value cannot be null");
					error = true;
				}

				String replace = null;
				if (map.containsKey("replace")) {
					replace = map.get("replace");
					if (replace == null)
						replace = "";
				}

				Level level = null;
				try {
					level = Level.parse(map.get("level").toUpperCase());
				} catch (IllegalArgumentException e) {
					log(Level.WARNING, "Filter " + (i + 1) + ": Invalid level (" + map.get("level") + ")");
				} catch (NullPointerException e) {
					log(Level.WARNING, "Filter " + (i + 1) + ": Level cannot be null");
				} finally {
					if (level == null)
						error = true;
				}

				if (!error)
					filterList.add(new FilterInfo(type, value, replace, level));
			}
		}

		log(Level.INFO, "Loaded " + filterList.size() + " filters!");
		return filterList;
	}

	private void log(Level level, String message) {
		log.log(level, "[ConsoleFilter] " + message);
	}
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        if ((sender instanceof Player)) {
            player = (Player)sender;
        }
        if ((command.getName().equalsIgnoreCase("cfreload")) && (args.length == 0)) {
                    if (player != null) {
                        player.sendMessage("[" + getDescription().getName() + "] This command is for console use only.");
                    } else {
                        this.reloadConfig();
                        log.setFilter(new CFFilter(loadFilter()));
                        log.info("[" + getDescription().getName() + "] Filters reloaded.");
                    }
                    return true;
        }
        return false;
    }
}