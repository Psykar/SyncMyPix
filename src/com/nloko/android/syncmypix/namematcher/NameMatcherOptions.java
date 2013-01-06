//
//    NameMatcherOptions.java is part of SyncMyPix
//
//    Authors:
//        Andrés Cordero <syncmypix@andrew67.com>
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
package com.nloko.android.syncmypix.namematcher;

import java.io.InputStream;

public class NameMatcherOptions {
	public boolean withPhone = false;
	public boolean withDiminutives = false;
	public InputStream diminutives = null;
	
	// chainable setters
	public NameMatcherOptions setWithPhone(final boolean withPhone) {
		this.withPhone = withPhone;
		return this;
	}
	public NameMatcherOptions setWithDiminutives(final boolean withDiminutives) {
		this.withDiminutives = withDiminutives;
		return this;
	}
	public NameMatcherOptions setDiminutives(final InputStream diminutives) {
		this.diminutives = diminutives;
		return this;
	}
}
