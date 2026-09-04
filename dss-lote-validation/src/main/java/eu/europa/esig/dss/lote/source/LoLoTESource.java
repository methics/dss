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
package eu.europa.esig.dss.lote.source;

import eu.europa.esig.dss.model.lote.OtherListPointer;

import java.util.function.Predicate;

/**
 * Represents a List of LoTEs definition
 *
 */
public class LoLoTESource extends LoTESource {

    /**
     * Predicate which filters the LoLoTEs
     */
    private Predicate<OtherListPointer> lolotePredicate;

    /**
     * Allows specifying pointers to other lists to be extracted during the parsing process
     */
    private Predicate<OtherListPointer> lotePredicate;

    /**
     * Default constructor instantiating object with null values
     */
    public LoLoTESource() {
        // empty
    }

    /**
     * Gets a LoLoTE predicate to filter lists of lists
     *
     * @return LoLoTE predicate
     */
    public Predicate<OtherListPointer> getLolotePredicate() {
        return lolotePredicate;
    }

    /**
     * Sets a LoLoTE filtering predicate
     *
     * @param lolotePredicate LoLoTE predicate
     */
    public void setLolotePredicate(Predicate<OtherListPointer> lolotePredicate) {
        this.lolotePredicate = lolotePredicate;
    }

    /**
     * Gets a predicate to filter {@code OtherListPointer} in order to extract pointers to other Lists
     *
     * @return other lists pointer predicate
     */
    public Predicate<OtherListPointer> getLotePredicate() {
        return lotePredicate;
    }

    /**
     * Sets a predicate allowing to filter {@code OtherListPointer} in order to extract pointers to other Lists,
     * to be used for further processing (for instance, pointers to other LoTEs from a LoLoTE).
     *
     * @param lotePredicate other lists pointer predicate
     */
    public void setLotePredicate(Predicate<OtherListPointer> lotePredicate) {
        this.lotePredicate = lotePredicate;
    }

}
