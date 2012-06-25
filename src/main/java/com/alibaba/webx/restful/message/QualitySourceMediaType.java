/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.alibaba.webx.restful.message;

import java.text.ParseException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * An quality source media type.
 *
 * @author Paul Sandoz
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class QualitySourceMediaType extends MediaType {

    public static final String QUALITY_SOURCE_FACTOR = "qs";
    public static final int DEFAULT_QUALITY_SOURCE_FACTOR = 1000;
    private final int qs;

    public QualitySourceMediaType(String p, String s) {
        super(p, s);
        qs = DEFAULT_QUALITY_SOURCE_FACTOR;
    }

    public QualitySourceMediaType(String p, String s, int qs, Map<String, String> parameters) {
        super(p, s, parameters);
        this.qs = qs;
    }

    public QualitySourceMediaType(MediaType mt) {
        this(mt.getType(), mt.getSubtype(), getQs(mt), mt.getParameters());
    }

    public int getQualitySource() {
        return qs;
    }

    public static QualitySourceMediaType valueOf(HttpHeaderReader reader) throws ParseException {
        // Skip any white space
        reader.hasNext();

        // Get the type
        String type = reader.nextToken();
        reader.nextSeparator('/');
        // Get the subtype
        String subType = reader.nextToken();

        int qs = DEFAULT_QUALITY_SOURCE_FACTOR;
        Map<String, String> parameters = null;
        if (reader.hasNext()) {
            parameters = HttpHeaderReader.readParameters(reader);
            if (parameters != null) {
                qs = getQs(parameters.get(QUALITY_SOURCE_FACTOR));
            }
        }

        return new QualitySourceMediaType(type, subType, qs, parameters);
    }

    public static int getQualitySource(MediaType mt) {
        if (mt instanceof QualitySourceMediaType) {
            QualitySourceMediaType qsmt = (QualitySourceMediaType) mt;
            return qsmt.getQualitySource();
        } else {
            return getQs(mt);
        }
    }

    private static int getQs(MediaType mt) {
        try {
            return getQs(mt.getParameters().get(QUALITY_SOURCE_FACTOR));
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static int getQs(String v) throws ParseException {
        if (v == null) {
            return DEFAULT_QUALITY_SOURCE_FACTOR;
        }

        try {
            final int qs = (int) (Float.valueOf(v) * 1000.0);
            if (qs < 0) {
                throw new ParseException("The quality source (qs) value, " + v + ", must be non-negative number", 0);
            }
            return qs;
        } catch (NumberFormatException ex) {
            ParseException pe = new ParseException("The quality source (qs) value, " + v + ", is not a valid value", 0);
            pe.initCause(ex);
            throw pe;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QualitySourceMediaType)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final QualitySourceMediaType other = (QualitySourceMediaType) obj;
        if (this.qs != other.qs) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.qs;
        return hash;
    }
}