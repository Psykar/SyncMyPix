//
//    NameMatcherFactory.java is part of SyncMyPix
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
package com.psykar.android.syncmypix.namematcher;

import java.io.InputStream;

import com.psykar.android.syncmypix.SyncMyPixPreferences;

import android.content.Context;

// The point of this factory was to create NameMatcher instances
// depending on API level and parameters passed.
// As I've merged both and standardized params everywhere,
// this factory is at present time useless.
public class NameMatcherFactory {
	public static NameMatcher create(Context context, SyncMyPixPreferences prefs, InputStream diminutivesFile) throws Exception {
		return new NameMatcher(context, prefs, diminutivesFile);
	}
}
