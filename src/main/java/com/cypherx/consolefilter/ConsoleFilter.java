package com.cypherx.consolefilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ConsoleFilter extends JavaPlugin {
	private PluginDescriptionFile desc;
	private File dataFolder;
	private Logger log;
	private ArrayList<Logger> extraLoggers;

	public void onDisable() {
		log(Level.INFO, "v" + desc.getVersion() + " Disabled!");
	}

	public void onEnable() {
		desc = getDescription();
		dataFolder = getDataFolder();

		if (!dataFolder.exists())
			dataFolder.mkdirs();

		log = Logger.getLogger("Minecraft");
		log.setFilter(new CFFilter(loadFilter()));

		extraLoggers = new ArrayList<Logger>();
		extraLoggers.add(Logger.getLogger("Minecraft.CommandBook"));
		for (Logger l : extraLoggers)  {
			l.setFilter(new CFFilter(loadFilter()));
			log(Level.INFO, "Enabling logging on extra logger " + l.getName());
		}

		log(Level.INFO, "v" + desc.getVersion() + " Enabled!");	
	}

	private ArrayList<FilterInfo> loadFilter() {
		ArrayList<FilterInfo> filterList = new ArrayList<FilterInfo>();
		File file = new File(dataFolder, "config.yml");
		if (!file.exists())
			writeConfig(file);

		Configuration config = new Configuration(file);
		config.load();

		List<Object> list = config.getList("filter");
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

	private void writeConfig(File file) {
		String fileName = file.getName();
		log(Level.INFO, "Creating file: " + fileName);
		String content = getResourceAsString("/res/" + fileName);
		Writer out = null;

		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {}
		}
	}

	private String getResourceAsString(String resource) {
		InputStream input = ConsoleFilter.class.getResourceAsStream(resource);
		StringBuilder sb = new StringBuilder();

		if (input != null) {
			InputStreamReader isr = null;
			BufferedReader br = null;
			String newLine = System.getProperty("line.separator");
			String line = null;

			try {
				isr = new InputStreamReader(input);
				br = new BufferedReader(isr);

				while ((line = br.readLine()) != null)
					sb.append(line).append(newLine);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (isr != null)
						isr.close();
				} catch (IOException e) {}
				try {
					if (br != null)
						br.close();
				} catch (IOException e) {}
			}
		}

		return sb.toString();
	}

	private void log(Level level, String message) {
		log.log(level, "[ConsoleFilter] " + message);
	}
}
