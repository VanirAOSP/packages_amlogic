/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.core.optionEntries;

import java.util.*;

import org.geometerplus.zlibrary.core.options.ZLColorOption;
import org.geometerplus.zlibrary.core.util.*;

import org.geometerplus.zlibrary.core.dialogs.*;

public class ZLColorOptionBuilder {
	private ZLColorOptionsData myData;
	
	public ZLColorOptionBuilder() {
		myData = new ZLColorOptionsData();
		myData.myComboEntry = new ZLColorComboOptionEntry(myData);
		myData.myColorEntry = new ZLMultiColorOptionEntry(myData);
	}

	public void addOption(final String name, ZLColorOption option) {
		myData.myOptionNames.add(name);
		myData.myCurrentColors.put(name, option.getValue());
		myData.myOptions.put(name, option);
	}

	public void setInitial(final String name) {
		myData.myCurrentOptionName = name;
		myData.myPreviousOptionName = name;
	}

	public ZLOptionEntry comboEntry() {
		return myData.myComboEntry;
	}

	public ZLOptionEntry colorEntry() {
		return myData.myColorEntry;
	}
	
	private static class ZLMultiColorOptionEntry extends ZLColorOptionEntry {
		private ZLColorOptionsData myData;
		
		public ZLMultiColorOptionEntry(ZLColorOptionsData data) {
			myData = data;
		}

		//Override
		public ZLColor getColor() {
			ZLColor color = myData.myCurrentColors.get(myData.myCurrentOptionName);
			return (color != null) ? color : initialColor();
		}

		//Override
		public ZLColor initialColor() {
			return myData.myOptions.get(myData.myCurrentOptionName).getValue();
		}

		//Override
		public void onAccept(ZLColor color) {
			onReset(color);
			final ArrayList<String> optionNames = myData.myOptionNames;
			final HashMap<String,ZLColorOption> options = myData.myOptions;
			final HashMap<String,ZLColor> colors = myData.myCurrentColors;
			final int len = optionNames.size();
			for (int i = 0; i < len; i++) {
				String name = optionNames.get(i);
				options.get(name).setValue(colors.get(name));
			}
		}

		//Override
		public void onReset(ZLColor color) {
			myData.myCurrentColors.put(myData.myPreviousOptionName, color);
		}
	}

	private static class ZLColorComboOptionEntry extends ZLComboOptionEntry {
		private ZLColorOptionsData myData;
		
		public ZLColorComboOptionEntry(ZLColorOptionsData data) {
			myData = data;
		}
		
		//Override
		public ArrayList<String> getValues() {
			return myData.myOptionNames;
		}

		//Override
		public String initialValue() {
			return myData.myCurrentOptionName;
		}

		//Override
		public void onAccept(String value) {}

		//Override
		public void onReset() {
			myData.myCurrentColors.clear();
		}

		//Override
		public void onValueSelected(int index) {
			myData.myCurrentOptionName = getValues().get(index);
			myData.myColorEntry.resetView();
			myData.myPreviousOptionName = myData.myCurrentOptionName;
		}
	}

	private static class ZLColorOptionsData {
		private ZLComboOptionEntry myComboEntry;
		private ZLColorOptionEntry myColorEntry;
		private String myCurrentOptionName;
		private String myPreviousOptionName;
		private final ArrayList<String> myOptionNames = new ArrayList<String>();
		private final HashMap<String,ZLColor> myCurrentColors = new HashMap<String,ZLColor>();
		private final HashMap<String,ZLColorOption> myOptions = new HashMap<String,ZLColorOption>();
	}		
}
