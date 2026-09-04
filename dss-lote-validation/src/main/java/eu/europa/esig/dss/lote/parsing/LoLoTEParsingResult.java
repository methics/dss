/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.lote.parsing;

import eu.europa.esig.dss.model.lote.OtherListPointer;

import java.util.List;

/**
 * Represents a parsing result for a List of TS 119 602 Lists of Trusted Entities
 *
 */
public class LoLoTEParsingResult extends AbstractLoTEParsingResult {

    /** List of self pointers to the current list */
    private List<OtherListPointer> currentListPointers;

    /** List of List pointers */
    private List<OtherListPointer> otherListPointers;

    /**
     * Default constructor
     */
    public LoLoTEParsingResult() {
        super();
    }

    /**
     * Gets List of self pointer to the current list (i.e. used within the pivot processing)
     *
     * @return a list of {@link OtherListPointer}s
     */
    public List<OtherListPointer> getCurrentListPointers() {
        return currentListPointers;
    }

    /**
     * Sets List of self pointer to the current list
     *
     * @param currentListPointers a list of {@link OtherListPointer}s
     */
    public void setCurrentListPointers(List<OtherListPointer> currentListPointers) {
        this.currentListPointers = currentListPointers;
    }

    /**
     * Gets List to other TSL pointers
     *
     * @return a list of {@link OtherListPointer}s
     */
    public List<OtherListPointer> getOtherListPointers() {
        return otherListPointers;
    }

    /**
     * Sets List to other pointers
     *
     * @param otherListPointers a list of {@link OtherListPointer}s
     */
    public void setOtherListPointers(List<OtherListPointer> otherListPointers) {
        this.otherListPointers = otherListPointers;
    }

}
