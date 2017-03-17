/*
 * ao-net-types - Networking-related value types for Java.
 * Copyright (C) 2011-2013, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputValidation;
import java.io.ObjectOutput;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a set of one or more domain labels.  These may be prepended to a domain name
 * and, as long as the total length is not exceeded, will result in a valid domain name.
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>May not exceed DomainName.MAX_LENGTH</li>
 *   <li>Have at least one domain label, each label separated by dots.</li>
 * </ul>
 * 
 * @author  AO Industries, Inc.
 */
final public class DomainLabels
implements
	Comparable<DomainLabels>,
	FastExternalizable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.DomainLabels>,
	Internable<DomainLabels>
{

	/**
	 * Validates a set of domain labels.
	 *
	 * @see DomainLabel#validate(java.lang.String)
	 */
	public static ValidationResult validate(String labels) {
		if(labels==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainLabels.validate.isNull");
		int len = labels.length();
		if(len==0) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainLabels.validate.empty");
		if(len>DomainName.MAX_LENGTH) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainLabels.validate.tooLong", DomainName.MAX_LENGTH, len);
		int labelStart = 0;
		for(int pos=0; pos<len; pos++) {
			if(labels.charAt(pos)=='.') {
				ValidationResult result = DomainLabel.validate(labels, labelStart, pos);
				if(!result.isValid()) return result;
				labelStart = pos+1;
			}
		}
		ValidationResult result = DomainLabel.validate(labels, labelStart, len);
		if(!result.isValid()) return result;
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,DomainLabels> interned = new ConcurrentHashMap<>();

	/**
	 * If labels is null, returns null.
	 */
	public static DomainLabels valueOf(String labels) throws ValidationException {
		if(labels==null) return null;
		//DomainLabels existing = interned.get(labels);
		//return existing!=null ? existing : new DomainLabels(labels);
		return new DomainLabels(labels);
	}

	private String labels;
	private String lowerLabels;

	private DomainLabels(String labels) throws ValidationException {
		this(labels, labels.toLowerCase(Locale.ROOT));
	}

	private DomainLabels(String labels, String lowerLabels) throws ValidationException {
		this.labels = labels;
		this.lowerLabels = lowerLabels;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(labels);
		if(!result.isValid()) throw new ValidationException(result);
	}

	@Override
	public boolean equals(Object O) {
		return
			O!=null
			&& O instanceof DomainLabels
			&& lowerLabels.equals(((DomainLabels)O).lowerLabels)
		;
	}

	@Override
	public int hashCode() {
		return lowerLabels.hashCode();
	}

	/**
	 * Sorts by right-most label, then next to the left, then ...
	 */
	@Override
	public int compareTo(DomainLabels other) {
		if(this==other) return 0;
		return DomainName.compareLabels(labels, other.labels);
	}

	@Override
	public String toString() {
		return labels;
	}

	/**
	 * Gets the lower-case form of the labels.  If two different labels are
	 * interned and their toLowerCase is the same String instance, then they are
	 * equal in case-insensitive manner.
	 */
	public String toLowerCase() {
		return lowerLabels;
	}

	/**
	 * Interns this set of labels much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public DomainLabels intern() {
		try {
			DomainLabels existing = interned.get(labels);
			if(existing==null) {
				String internedLabels = labels.intern();
				String internedLowerLabels = lowerLabels.intern();
				DomainLabels addMe = labels==internedLabels && lowerLabels==internedLowerLabels ? this : new DomainLabels(internedLabels);
				existing = interned.putIfAbsent(internedLabels, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	@Override
	public com.aoindustries.aoserv.client.dto.DomainLabels getDto() {
		return new com.aoindustries.aoserv.client.dto.DomainLabels(labels);
	}

	// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
	private static final long serialVersionUID = -2681659044454796989L;

	public DomainLabels() {
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		FastObjectOutput fastOut = FastObjectOutput.wrap(out);
		try {
			fastOut.writeFastUTF(labels);
		} finally {
			fastOut.unwrap();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(labels!=null) throw new IllegalStateException();
		FastObjectInput fastIn = FastObjectInput.wrap(in);
		try {
			labels = fastIn.readFastUTF();
			lowerLabels = labels.toLowerCase(Locale.ROOT);
		} finally {
			fastIn.unwrap();
		}
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
	// </editor-fold>
}
