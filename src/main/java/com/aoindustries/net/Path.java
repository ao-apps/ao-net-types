/*
 * ao-net-types - Networking-related value types for Java.
 * Copyright (C) 2017  AO Industries, Inc.
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

import com.aoindustries.dto.DtoFactory;
import com.aoindustries.util.ComparatorUtils;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a full path.  Paths must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Start with a <code>/</code></li>
 *   <li>Not contain any null characters</li>
 *   <li>Not contain any /../ or /./ path elements</li>
 *   <li>Not end with /.. or /.</li>
 *   <li>Not contain any // in the path</li>
 * </ul>
 * <p>
 * Note, this concept of path is minimally restrictive and only represents a well-formed path.
 * The path may not be valid for some contexts, such as the path part of a URL.
 * This does not implement <ao:a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986</ao:a>.
 * </p>
 * <p>
 * TODO: This matches <code>UnixPath</code> in aoserv-client with the exception of allowing trailing slash.
 *       Remove this redundancy?
 * </p>
 *
 * @author  AO Industries, Inc.
 */
final public class Path implements
	Comparable<Path>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.Path>,
	Internable<Path>
{

	private static final long serialVersionUID = 1L;

	public static ValidationResult validate(String path) {
		// Be non-null
		if(path==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.isNull");
		// Be non-empty
		if(path.length()==0) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.empty");
		// Start with a /
		if(path.charAt(0)!='/') return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.startWithNonSlash", path.charAt(0));
		// Not contain any null characters
		if(path.indexOf('\0')!=-1) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.containsNullCharacter", path.indexOf('\0'));
		// Not contain any /../ or /./ path elements
		if(path.indexOf("/../")!=-1) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.containsDotDot", path.indexOf("/../"));
		if(path.indexOf("/./")!=-1) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.containsDot", path.indexOf("/./"));
		// Not end with /.. or /.
		if(path.endsWith("/.")) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.endsSlashDot");
		if(path.endsWith("/..")) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.endsSlashDotDot");
		// Not contain any // in the path
		if(path.indexOf("//")!=-1) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Path.validate.containsDoubleSlash", path.indexOf("//"));
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,Path> interned = new ConcurrentHashMap<String,Path>();

	/**
	 * @param path  when {@code null}, returns {@code null}
	 */
	public static Path valueOf(String path) throws ValidationException {
		if(path == null) return null;
		//UnixPath existing = interned.get(path);
		//return existing!=null ? existing : new UnixPath(path);
		return new Path(path);
	}

	final private String path;

	private Path(String path) throws ValidationException {
		this.path = path;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(path);
		if(!result.isValid()) throw new ValidationException(result);
	}

	/**
	 * Perform same validation as constructor on readObject.
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		validateObject();
	}

	@Override
	public void validateObject() throws InvalidObjectException {
		try {
			validate();
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	@Override
	public boolean equals(Object O) {
		return
			O!=null
			&& O instanceof Path
			&& path.equals(((Path)O).path)
		;
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public int compareTo(Path other) {
		return this==other ? 0 : ComparatorUtils.compareIgnoreCaseConsistentWithEquals(path, other.path);
	}

	@Override
	public String toString() {
		return path;
	}

	/**
	 * Interns this path much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public Path intern() {
		try {
			Path existing = interned.get(path);
			if(existing==null) {
				String internedPath = path.intern();
				Path addMe = path==internedPath ? this : new Path(internedPath);
				existing = interned.putIfAbsent(internedPath, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	@Override
	public com.aoindustries.net.dto.Path getDto() {
		return new com.aoindustries.net.dto.Path(path);
	}
}
