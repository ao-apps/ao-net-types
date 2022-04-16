/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2010-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-net-types.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.net;

import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.FastExternalizable;
import com.aoapps.lang.io.FastObjectInput;
import com.aoapps.lang.io.FastObjectOutput;
import com.aoapps.lang.util.ComparatorUtils;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a DNS domain label (a single part of a domain name between dots).  Domain labels must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Conforms to definition in <a href="https://wikipedia.org/wiki/DNS_label#Parts_of_a_domain_name">https://wikipedia.org/wiki/DNS_label#Parts_of_a_domain_name</a></li>
 *   <li>Conforms to <a href="http://tools.ietf.org/html/rfc2181#section-11">RFC 2181</a></li>
 *   <li>And allow all numeric as described in <a href="http://tools.ietf.org/html/rfc1123#page-13">RFC 1123</a></li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
// Matches src/main/sql/com/aoapps/net/DomainLabel-type.sql
public final class DomainLabel implements
	Comparable<DomainLabel>,
	FastExternalizable,
	DtoFactory<com.aoapps.net.dto.DomainLabel>,
	Internable<DomainLabel>
{

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, DomainLabel.class);

	public static final int MAX_LENGTH = 63;

	/**
	 * Validates a domain name label.
	 */
	public static ValidationResult validate(String label) {
		if(label==null) return new InvalidResult(RESOURCES, "validate.isNull");
		return validate(label, 0, label.length());
	}
	// Matches src/main/sql/com/aoapps/net/DomainLabel.validate-function.sql
	public static ValidationResult validate(String label, int beginIndex, int endIndex) {
		if(label==null) return new InvalidResult(RESOURCES, "validate.isNull");
		int len = endIndex-beginIndex;
		if(len==0) return new InvalidResult(RESOURCES, "validate.empty");
		if(len>MAX_LENGTH) return new InvalidResult(RESOURCES, "validate.tooLong", MAX_LENGTH, len);
		for(int pos=beginIndex; pos<endIndex; pos++) {
			char ch = label.charAt(pos);
			if(ch=='-') {
				if(pos==beginIndex) return new InvalidResult(RESOURCES, "validate.startsDash");
				if(pos==(endIndex-1)) return new InvalidResult(RESOURCES, "validate.endsDash");
			} else if(
				(ch<'a' || ch>'z')
				&& (ch<'A' || ch>'Z')
				&& (ch<'0' || ch>'9')
			) return new InvalidResult(RESOURCES, "validate.invalidCharacter", ch, pos-beginIndex);
		}
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String, DomainLabel> interned = new ConcurrentHashMap<>();

	/**
	 * @param label  when {@code null}, returns {@code null}
	 */
	public static DomainLabel valueOf(String label) throws ValidationException {
		if(label == null) return null;
		//DomainLabel existing = interned.get(label);
		//return existing!=null ? existing : new DomainLabel(label);
		return new DomainLabel(label, true);
	}

	private String label;
	private String lowerLabel;

	private DomainLabel(String label, boolean validate) throws ValidationException {
		this.label = label;
		this.lowerLabel = label.toLowerCase(Locale.ROOT);
		if(validate) validate();
	}

	/**
	 * @param  label  Does not validate, should only be used with a known valid value.
	 * @param  lowerLabel  Does not validate, should only be used with a known valid value.
	 */
	private DomainLabel(String label, String lowerLabel) {
		ValidationResult result;
		assert (result = validate(label)).isValid() : result.toString();
		assert label.toLowerCase(Locale.ROOT).equals(lowerLabel);
		this.label = label;
		this.lowerLabel = lowerLabel;
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(label);
		if(!result.isValid()) throw new ValidationException(result);
	}

	@Override
	public boolean equals(Object obj) {
		return
			(obj instanceof DomainLabel)
			&& lowerLabel.equals(((DomainLabel)obj).lowerLabel)
		;
	}

	@Override
	public int hashCode() {
		return lowerLabel.hashCode();
	}

	@Override
	public int compareTo(DomainLabel other) {
		return this==other ? 0 : ComparatorUtils.compareIgnoreCaseConsistentWithEquals(label, other.label);
	}

	@Override
	public String toString() {
		return label;
	}

	/**
	 * Gets the lower-case form of the label.  If two different domain labels are
	 * interned and their toLowerCase is the same String instance, then they are
	 * equal in case-insensitive manner.
	 */
	public String toLowerCase() {
		return lowerLabel;
	}

	/**
	 * Interns this label much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public DomainLabel intern() {
		DomainLabel existing = interned.get(label);
		if(existing==null) {
			String internedLabel = label.intern();
			String internedLowerLabel = lowerLabel.intern();
			@SuppressWarnings("StringEquality")
			DomainLabel addMe = (label == internedLabel) && (lowerLabel == internedLowerLabel) ? this : new DomainLabel(internedLabel, internedLowerLabel);
			existing = interned.putIfAbsent(internedLabel, addMe);
			if(existing==null) existing = addMe;
		}
		return existing;
	}

	@Override
	public com.aoapps.net.dto.DomainLabel getDto() {
		return new com.aoapps.net.dto.DomainLabel(label);
	}

	// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
	private static final long serialVersionUID = -3692661338685551188L;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  FastExternalizable
	 */
	@Deprecated/* Java 9: (forRemoval = false) */
	public DomainLabel() {
		// Do nothing
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		FastObjectOutput fastOut = FastObjectOutput.wrap(out);
		try {
			fastOut.writeFastUTF(label);
		} finally {
			fastOut.unwrap();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(label!=null) throw new IllegalStateException();
		FastObjectInput fastIn = FastObjectInput.wrap(in);
		try {
			label = fastIn.readFastUTF();
			lowerLabel = label.toLowerCase(Locale.ROOT);
		} finally {
			fastIn.unwrap();
		}
		try {
			validate();
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}
	// </editor-fold>
}
