package eu.europa.esig.dss.xades.signature;

/**
 * Generates specific ID elements to a XAdES document for interoperability.
 */
public class InteropId {

    /**
     * Get ID for Signature element.
     * @return
     */
    public static String getSignatureId() {
        return "idSignature";
    }

    public static String getSignedPropertiesId() {
        return "idSignedProperties";
    }

    public static String getQualifyingPropertiesId() {
        return "idQualifyingProperties";
    }

    public static String getKeyInfoId() {
        return "idKeyInfo";
    }

    public static String getSignatureValueId() {
        return "idSignatureValue";
    }

    public static String getTimestampId() {
        return "idTimeStamp";
    }

    public static String getDataToBeSignedId() {
        return "idDataToBeSigned";
    }

    public static String getUnsignedPropertiesId() {
        return "idUnsignedProperties";
    }
}
