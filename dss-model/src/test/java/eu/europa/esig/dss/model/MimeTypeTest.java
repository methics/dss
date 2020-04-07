package eu.europa.esig.dss.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.Test;

public class MimeTypeTest {
	
	@Test
	public void test() {
		assertNotNull(new MimeType("text/css", "css"));
		assertNotNull(new MimeType("audio/webm", "webm"));
		assertNotNull(new MimeType("application/vnd.etsi.asic-s+zip\n", "asics"));
		
		Exception exception = assertThrows(DSSException.class, () -> new MimeType("text/plain", "txt"));
		assertEquals("'text/plain' corresponding MimeType exists already! "
				+ "Use #fromMimeTypeString method to obtain the corresponding object.", exception.getMessage());
		
		exception = assertThrows(NullPointerException.class, () -> new MimeType(null, "txt"));
		assertEquals("The mimeTypeString cannot be null!", exception.getMessage());
		
		exception = assertThrows(IllegalArgumentException.class, () -> new MimeType("text/new", null));
		assertEquals("The extension cannot be null or blank!", exception.getMessage());
	}
	
	@Test
	public void fromFileNameTest() {
		assertEquals(MimeType.TEXT, MimeType.fromFileName("test.txt"));
		assertEquals(MimeType.PDF, MimeType.fromFileName("pades.pdf"));
		assertEquals(MimeType.PKCS7, MimeType.fromFileName("cades.p7s"));
		assertEquals(MimeType.PKCS7, MimeType.fromFileName("CADES.P7S"));
		assertEquals(MimeType.BINARY, MimeType.fromFileName("binaries"));
		assertEquals(MimeType.BINARY, MimeType.fromFileName("new.folder/binaries"));
		
		assertEquals(MimeType.BINARY, MimeType.fromFileName(""));
		assertEquals(MimeType.BINARY, MimeType.fromFileName(null));
	}
	
	@Test
	public void getExtensionTest() {
		assertEquals("txt", MimeType.getExtension(MimeType.TEXT));
		assertEquals("pdf", MimeType.getExtension(MimeType.PDF));
		assertEquals("p7s", MimeType.getExtension(MimeType.PKCS7));
		
		MimeType newMimeType = new MimeType("new/mimeType", "new");
		assertEquals("new", MimeType.getExtension(newMimeType));
		
		assertNull(MimeType.getExtension(MimeType.BINARY));
		
		Exception exception = assertThrows(NullPointerException.class, () -> MimeType.getExtension(null));
		assertEquals("The MimeType must be provided!", exception.getMessage());
	}
	
	@Test
	public void getFileExtensionTest() {
		assertEquals("txt", MimeType.getFileExtension("test.txt"));
		assertEquals("pdf", MimeType.getFileExtension("pades.pdf"));
		assertEquals("p7s", MimeType.getFileExtension("cades.p7s"));
		assertEquals("P7S", MimeType.getFileExtension("CADES.P7S"));
		assertEquals("", MimeType.getFileExtension("binaries"));
		assertEquals("folder/binaries", MimeType.getFileExtension("new.folder/binaries"));
		
		assertNull(MimeType.getFileExtension(""));
		assertNull(MimeType.getFileExtension(null));
	}
	
	@Test
	public void fromFileTest() {
		assertEquals(MimeType.BINARY, MimeType.fromFile(new File("src/test/resources/AdobeCA.p7c")));
		MimeType certMimeType = new MimeType("application/cert", "p7c");
		assertEquals(certMimeType, MimeType.fromFile(new File("src/test/resources/AdobeCA.p7c")));
		assertEquals(MimeType.BINARY, MimeType.fromFile(new File("D-TRUST_CA_3-1_2016.cer")));
		certMimeType.defineFileExtension("cer");
		assertEquals(certMimeType, MimeType.fromFile(new File("D-TRUST_CA_3-1_2016.cer")));

		Exception exception = assertThrows(IllegalArgumentException.class, () -> certMimeType.defineFileExtension(""));
		assertEquals("The extension cannot be null or blank!", exception.getMessage());

		exception = assertThrows(IllegalArgumentException.class, () -> certMimeType.defineFileExtension(null));
		assertEquals("The extension cannot be null or blank!", exception.getMessage());
		
		exception = assertThrows(NullPointerException.class, () -> MimeType.fromFile(null));
		assertEquals("The file cannot be null!", exception.getMessage());
	}
	
	@Test
	public void fromMimeTypeStringTest() {
		assertEquals(MimeType.XML, MimeType.fromMimeTypeString("text/xml"));
		assertEquals(MimeType.PDF, MimeType.fromMimeTypeString("application/pdf"));
		assertEquals(MimeType.PNG, MimeType.fromMimeTypeString("image/png"));
		assertEquals(MimeType.ASICE, MimeType.fromMimeTypeString("application/vnd.etsi.asic-e+zip"));
		
		MimeType asiceNewLineMimeType = MimeType.fromMimeTypeString("application/vnd.etsi.asic-e+zip\n");
		assertNotNull(asiceNewLineMimeType);
		assertNotEquals(MimeType.ASICE, asiceNewLineMimeType);
	}

}
