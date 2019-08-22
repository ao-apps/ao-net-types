/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-net-types.
 *
 * ao-net-types is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-net-types is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-net-types.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.net;

import com.aoindustries.io.Encoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * String utilities copied from {@code com.aoindustries.util.StringUtility} to avoid dependency on <code>aocode-public</code>.
 * TODO: Move StringUtility to ao-lang to avoid this?
 *
 * @author  AO Industries, Inc.
 */
class StringUtility {

	private StringUtility() {}

	/**
	 * Finds the first occurrence of any of the supplied characters starting at the specified index.
	 *
	 * @param  S  the <code>String</code> to search
	 * @param  chars  the characters to look for
	 * @param  start  the starting index.
	 *
	 * @return  the index of the first occurrence of <code>-1</code> if none found
	 */
	static int indexOf(String S, char[] chars, int start) {
		int Slen=S.length();
		int clen=chars.length;
		for(int c=start;c<Slen;c++) {
			char ch=S.charAt(c);
			for(int d=0;d<clen;d++) if(ch==chars[d]) return c;
		}
		return -1;
	}

	/**
	 * Replaces all occurrences of a character with a String.
	 * Please consider the variant with the {@link Appendable} for higher performance.
	 */
	static String replace(String string, char ch, String replacement) {
		int pos = string.indexOf(ch);
		if (pos == -1) return string;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 16);
		int lastpos = 0;
		do {
			sb.append(string, lastpos, pos).append(replacement);
			lastpos = pos + 1;
			pos = string.indexOf(ch, lastpos);
		} while (pos != -1);
		if(lastpos<len) sb.append(string, lastpos, len);
		return sb.toString();
	}

	/**
	 * Replaces all occurrences of a character with a String, appends the replacement
	 * to <code>out</code>.
	 */
	static void replace(String string, char find, String replacement, Appendable out) throws IOException {
		int pos = string.indexOf(find);
		if (pos == -1) {
			out.append(string);
		} else {
			int lastpos = 0;
			do {
				out.append(string, lastpos, pos).append(replacement);
				lastpos = pos + 1;
				pos = string.indexOf(find, lastpos);
			} while (pos != -1);
			int len = string.length();
			if(lastpos<len) out.append(string, lastpos, len);
		}
	}

	/**
	 * Replaces all occurrences of a character with a String, appends the replacement
	 * to <code>out</code>.
	 */
	static void replace(String string, char find, String replacement, Appendable out, Encoder encoder) throws IOException {
		if(encoder == null) {
			replace(string, find, replacement, out);
		} else {
			int pos = string.indexOf(find);
			if (pos == -1) {
				encoder.append(string, out);
			} else {
				int lastpos = 0;
				do {
					encoder.append(string, lastpos, pos, out).append(replacement, out);
					lastpos = pos + 1;
					pos = string.indexOf(find, lastpos);
				} while (pos != -1);
				int len = string.length();
				if(lastpos<len) encoder.append(string, lastpos, len, out);
			}
		}
	}

	/**
	 * Splits a string on the given delimiter.
	 * Does include all empty elements on the split.
	 *
	 * @return  the modifiable list from the split
	 */
	static List<String> splitString(String line, char delim) {
		return splitString(line, 0, line.length(), delim, new ArrayList<String>());
	}

	/**
	 * Splits a string on the given delimiter over the given range.
	 * Does include all empty elements on the split.
	 *
	 * @param  words  the words will be added to this collection.
	 *
	 * @return  the collection provided in words parameter
	 */
	static <C extends Collection<String>> C splitString(String line, int begin, int end, char delim, C words) {
		int pos = begin;
		while (pos < end) {
			int start = pos;
			pos = line.indexOf(delim, pos);
			if(pos == -1 || pos > end) pos = end;
			words.add(line.substring(start, pos));
			pos++;
		}
		// If ending in a delimeter, add the empty string
		if(end>begin && line.charAt(end-1)==delim) words.add("");
		return words;
	}
}
