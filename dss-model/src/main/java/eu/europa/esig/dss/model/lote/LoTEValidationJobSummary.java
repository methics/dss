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
package eu.europa.esig.dss.model.lote;

import eu.europa.esig.dss.model.identifier.Identifier;
import eu.europa.esig.dss.model.job.ValidationJobSummary;

import java.io.Serializable;
import java.util.List;

/**
 * Contains summary of the validation result of a List of Trusted Entities validation job
 *
 */
public class LoTEValidationJobSummary implements ValidationJobSummary<LoTEInfo, LoLoTEInfo>, Serializable {

    private static final long serialVersionUID = 8797791787153723132L;

    /**
     * List of infos for processed LoLoTESource's
     */
    private final List<LoLoTEInfo> loloteInfos;

    /**
     * List of infos for processed other LoTESource's
     */
    private final List<LoTEInfo> otherLoTEInfos;

    /**
     * The default constructor
     *
     * @param loloteInfos a list of {@link LoLoTEInfo}s
     * @param otherLoTEInfos a list of {@link LoTEInfo}s
     */
    public LoTEValidationJobSummary(final List<LoLoTEInfo> loloteInfos, final List<LoTEInfo> otherLoTEInfos) {
        if ((loloteInfos == null || loloteInfos.isEmpty()) && (otherLoTEInfos == null || otherLoTEInfos.isEmpty())) {
            throw new IllegalArgumentException("LoTE Info shall be provided!");
        }
        this.loloteInfos = loloteInfos;
        this.otherLoTEInfos = otherLoTEInfos;
    }

    /**
     * Gets a list of LoLoTE infos
     *
     * @return a list of {@link LoLoTEInfo}s
     */
    public List<LoLoTEInfo> getLoLoTEInfos() {
        return loloteInfos;
    }

    /**
     * Gets a list of other LoTE infos
     *
     * @return a list of {@link LoTEInfo}s
     */
    public List<LoTEInfo> getOtherLoTEInfos() {
        return otherLoTEInfos;
    }

    /**
     * Gets a number of processed LoTEs
     *
     * @return number of processed LoTEs
     */
    public int getNumberOfProcessedLoTEs() {
        int amount = 0;
        if (otherLoTEInfos != null && !otherLoTEInfos.isEmpty()) {
            amount += otherLoTEInfos.size();
        }
        if (loloteInfos != null && !loloteInfos.isEmpty()) {
            for (LoLoTEInfo loloteInfo : loloteInfos) {
                amount += loloteInfo.getChildrenInfos().size();
            }
        }
        return amount;
    }

    /**
     * Returns an amount of processed LoLoTEs
     * @return {@code int} number of processed LoLoTEs
     */
    public int getNumberOfProcessedLoLoTEs() {
        if (loloteInfos != null && !loloteInfos.isEmpty()) {
            return loloteInfos.size();
        }
        return 0;
    }

    /**
     * Gets a LoTE by a unique identifier
     *
     * @param identifier {@link Identifier}
     * @return {@link LoTEInfo}
     */
    public LoTEInfo getLoTEInfoById(Identifier identifier) {
        if (otherLoTEInfos != null && !otherLoTEInfos.isEmpty()) {
            for (LoTEInfo listInfo : otherLoTEInfos) {
                if (identifier.equals(listInfo.getDSSId())) {
                    return listInfo;
                }
            }
        }

        if (loloteInfos != null && !loloteInfos.isEmpty()) {
            for (LoLoTEInfo loloteInfo : loloteInfos) {
                if (loloteInfo.getChildrenInfos() != null && !loloteInfo.getChildrenInfos().isEmpty()) {
                    for (LoTEInfo loteInfo : loloteInfo.getChildrenInfos()) {
                        if (identifier.equals(loteInfo.getDSSId())) {
                            return loteInfo;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns a LoLoTEInfo object by Identifier
     *
     * @param identifier
     *            the Identifier of the searched LoLoTE
     * @return a LoLoTEInfo or null
     */
    public LoLoTEInfo getLoLoTEInfoById(Identifier identifier) {
        if (loloteInfos != null && !loloteInfos.isEmpty()) {
            for (LoLoTEInfo lotlInfo : loloteInfos) {
                if (identifier.equals(lotlInfo.getDSSId())) {
                    return lotlInfo;
                }
            }
        }
        return null;
    }

    @Override
    public List<LoLoTEInfo> getDocumentListInfos() {
        return getLoLoTEInfos();
    }

    @Override
    public List<LoTEInfo> getOtherDocumentInfos() {
        return getOtherLoTEInfos();
    }

}
