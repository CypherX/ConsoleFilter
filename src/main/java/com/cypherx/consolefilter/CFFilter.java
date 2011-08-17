package com.cypherx.consolefilter;

import java.util.ArrayList;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CFFilter implements Filter {
	private ArrayList<FilterInfo> filterList;

	public CFFilter(ArrayList<FilterInfo> filterList) {
		this.filterList = filterList;
	}

	public boolean isLoggable(LogRecord record) {
		if (filterList.size() < 1)
			return true;

		String message = record.getMessage();

		for (FilterInfo filter : filterList) {
			if (!record.getLevel().equals(filter.getLevel()) && !filter.getLevel().equals(Level.ALL))
				continue;

			String regex = null;
			if (filter.getType().equals(FilterType.STRING))
				regex = Pattern.quote(filter.getValue());
			else
				regex = filter.getValue();

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(message);

			if (matcher.find()) {
				if (filter.getReplace() == null)
					return false;
				else
					message = message.replaceAll(regex, filter.getReplace());
			}
		}

		record.setMessage(message);
		return true;
	}	
}