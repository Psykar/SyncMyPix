//
//    SyncMyPixPreferencesReal.java is part of SyncMyPix
//
//    Authors:
//        Neil Loknath <neil.loknath@gmail.com>
//
//    Copyright (c) 2009 Neil Loknath
//
//    SyncMyPix is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    SyncMyPix is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with SyncMyPix.  If not, see <http://www.gnu.org/licenses/>.
//

package com.nloko.android.syncmypix;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class SyncMyPixPreferencesReal implements SyncMyPixPreferences {

	protected Context context;
	
	private final boolean googleSyncToggledOff;
	public boolean isGoogleSyncToggledOff() {
		return googleSyncToggledOff;
	}

	private final boolean allowGoogleSync;
	public boolean getAllowGoogleSync() {
		return allowGoogleSync;
	}

	private final boolean skipIfExists;
	public boolean getSkipIfExists() {
		return skipIfExists;
	}

	private final boolean skipIfConflict;
	public boolean getSkipIfConflict() {
		return skipIfConflict;
	}

	private final boolean overrideReadOnlyCheck;
	public boolean overrideReadOnlyCheck() {
		return overrideReadOnlyCheck;
	}

	private final boolean maxQuality;
	public boolean getMaxQuality() {
		return maxQuality;
	}

	private final boolean cropSquare;
	public boolean getCropSquare() {
		return cropSquare;
	}

	private final boolean cache;
	public boolean getCache() {
		return cache;
	}

	private final boolean intelliMatch;
	public boolean getIntelliMatch() {
		return intelliMatch;
	}

	private final boolean phoneOnly;
	public boolean getPhoneOnly() {
		return phoneOnly;
	}

	private final boolean considerDiminutives;
	public boolean getConsiderDiminutives() {
		return considerDiminutives;
	}
	
	private final boolean romanizeGreek;
	public boolean getRomanizeGreek() {
		return romanizeGreek;
	}

	public String getSource() {
		return "Facebook";
	}
	
	public SyncMyPixPreferencesReal(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context");
		}
		
		this.context = context;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Legacy backend preferences; will not touch
		googleSyncToggledOff = prefs.getBoolean("googleSyncToggledOff", false); 
		skipIfConflict = prefs.getBoolean("skipIfConflict", false);
		maxQuality = true;
		allowGoogleSync = true;
		skipIfExists = false;
		overrideReadOnlyCheck = prefs.getBoolean("overrideReadOnlyCheck", true);
		
		// Active user-changeable preferences
    	cropSquare = prefs.getBoolean("cropSquare", false);
    	phoneOnly = prefs.getBoolean("phoneOnly", false);
    	cache = prefs.getBoolean("cache", true);
    	intelliMatch = prefs.getBoolean("intelliMatch", true);
    	considerDiminutives = prefs.getBoolean("matchDiminutives", true);
    	romanizeGreek = prefs.getBoolean("romanizeGreek", false);
	}

}
