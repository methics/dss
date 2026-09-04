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
package eu.europa.esig.dss.asic.cades.validation;

import eu.europa.esig.dss.asic.common.ASiCContent;
import eu.europa.esig.dss.asic.common.SecureContainerHandlerBuilder;
import eu.europa.esig.dss.asic.common.ZipUtils;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.exception.IllegalInputException;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.validationreport.jaxb.SignersDocumentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("slow")
class ASICWithCAdESZipBombingTest extends AbstractASiCWithCAdESTestValidation {

    private static DSSDocument LARGE_FILE_ZEROS;

    private static DSSDocument ZIP_BOMB_ASiCE;
    private static DSSDocument ZIP_BOMB_ASiCS;

    private static DSSDocument ONE_LEVEL_ZIP_BOMB_ASiCE;
    private static DSSDocument ONE_LEVEL_ZIP_BOMB_ASiCS;

    private static DSSDocument MANY_FILES_ASiCE;
    private static DSSDocument MANY_FILES_ASiCS;

    private static DSSDocument document;

    @BeforeAll
    static void initDocuments() throws IOException {
        DSSDocument asicsContainer = new FileDocument("src/test/resources/validation/onefile-ok.asics");
        List<DSSDocument> asicsContainerContent = ZipUtils.getInstance().extractContainerContent(asicsContainer);
        removePackageZip(asicsContainerContent);

        DSSDocument asiceContainer = new FileDocument("src/test/resources/validation/onefile-ok.asice");
        List<DSSDocument> asiceContainerContent = ZipUtils.getInstance().extractContainerContent(asiceContainer);

        ASiCContent asicContent = new ASiCContent();
        asicContent.setSignedDocuments(asiceContainerContent);

        LARGE_FILE_ZEROS = generateLargeFileWithZeros("0.dll");

        List<DSSDocument> pages = getZipBombDirectories(Collections.singletonList(LARGE_FILE_ZEROS), "page", 16);
        List<DSSDocument> docs = getZipBombDirectories(pages, "doc", 16);
        List<DSSDocument> chapters = getZipBombDirectories(docs, "chapter", 16);
        List<DSSDocument> books = getZipBombDirectories(chapters, "book", 16);
        List<DSSDocument> libs = getZipBombDirectories(books, "lib", 16);

        asicContent.getUnsupportedDocuments().addAll(libs);

        ZIP_BOMB_ASiCE = ZipUtils.getInstance().createZipArchive(asicContent);
        ZIP_BOMB_ASiCE.setName("zipBomb.sce");

        DSSDocument packageZip = ZipUtils.getInstance().createZipArchive(libs);
        packageZip.setName("package.zip");

        asicContent = new ASiCContent();
        asicContent.setSignedDocuments(asicsContainerContent);
        asicContent.setUnsupportedDocuments(Collections.singletonList(packageZip));

        ZIP_BOMB_ASiCS = ZipUtils.getInstance().createZipArchive(asicContent);
        ZIP_BOMB_ASiCS.setName("zipBomb.scs");

        asicContent = new ASiCContent();
        asicContent.setSignedDocuments(asiceContainerContent);
        asicContent.setUnsupportedDocuments(Collections.singletonList(LARGE_FILE_ZEROS));

        ONE_LEVEL_ZIP_BOMB_ASiCE = ZipUtils.getInstance().createZipArchive(asicContent);
        ONE_LEVEL_ZIP_BOMB_ASiCE.setName("oneLevelZipBomb.sce");

        LARGE_FILE_ZEROS.setName("package.zip");

        asicContent = new ASiCContent();
        asicContent.setSignedDocuments(asicsContainerContent);
        asicContent.setUnsupportedDocuments(Collections.singletonList(LARGE_FILE_ZEROS));

        ONE_LEVEL_ZIP_BOMB_ASiCS = ZipUtils.getInstance().createZipArchive(asicContent);
        ONE_LEVEL_ZIP_BOMB_ASiCS.setName("oneLevelZipBomb.scs");

        List<DSSDocument> smallFiles = createSmallFiles("Hello World !".getBytes(), 2000);

        asicContent = new ASiCContent();
        asicContent.setSignedDocuments(asiceContainerContent);
        asicContent.setUnsupportedDocuments(smallFiles);

        MANY_FILES_ASiCE = ZipUtils.getInstance().createZipArchive(asicContent);
        MANY_FILES_ASiCE.setName("manyFiles.sce");

        packageZip = ZipUtils.getInstance().createZipArchive(smallFiles);
        packageZip.setName("package.zip");

        asicContent = new ASiCContent();
        asicContent.setSignedDocuments(asicsContainerContent);
        asicContent.setUnsupportedDocuments(Collections.singletonList(packageZip));

        MANY_FILES_ASiCS = ZipUtils.getInstance().createZipArchive(asicContent);
        MANY_FILES_ASiCS.setName("manyFiles.scs");
    }

    static FileDocument generateLargeFileWithZeros(String filename) throws IOException {
        // -Dlarge.file.size.mb=...
        String fileSizeStr = System.getProperty("large.file.size.mb", "2048");
        final int fileSizeMB = Integer.parseInt(fileSizeStr);

        File file = new File("target/" + filename);

        byte[] data = new byte[0x00FFFFFF]; // ~16 MB
        int reachedSize = 0;
        int byteArraySizeMB = 16;
        try (FileOutputStream fos = new FileOutputStream(file)) {
            while (reachedSize < fileSizeMB) {
                fos.write(data);
                reachedSize += byteArraySizeMB;
                if (reachedSize + byteArraySizeMB > fileSizeMB) {
                    byteArraySizeMB = fileSizeMB - reachedSize;
                }
            }
        }

        return new FileDocument(file);
    }

    static List<DSSDocument> getZipBombDirectories(List<DSSDocument> archiveDocuments, String archiveName, int zipNumber) {
        DSSDocument zipArchive = ZipUtils.getInstance().createZipArchive(archiveDocuments);

        List<DSSDocument> archives = new ArrayList<>();
        for (int i = 0; i < zipNumber; i++) {
            zipArchive = copy(zipArchive);
            assertNotNull(zipArchive);
            zipArchive.setName(archiveName + " " + Integer.toHexString(i) + ".zip");
            archives.add(zipArchive);
        }
        return archives;
    }

    static DSSDocument copy(DSSDocument document) {
        assertNotNull(document, "Document to copy is null!");
        if (document instanceof InMemoryDocument) {
            return new InMemoryDocument(((InMemoryDocument) document).getBytes(), document.getName());
        } else if (document instanceof FileDocument) {
            return new FileDocument(((FileDocument) document).getFile());
        }
        fail(String.format("Illegal state! Class '%s' is not supported in this method.", document.getClass().getSimpleName()));
        return null;
    }

    static void removePackageZip(List<DSSDocument> containerDocuments) {
        Iterator<DSSDocument> it = containerDocuments.iterator();
        while (it.hasNext()) {
            DSSDocument doc = it.next();
            if ("package.zip".equals(doc.getName())) {
                it.remove();
                break;
            }
        }
    }

    static List<DSSDocument> createSmallFiles(byte[] content, int number) {
        List<DSSDocument> result = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            result.add(new InMemoryDocument(content, "test" + i + ".txt"));
        }
        return result;
    }

    @AfterAll
    static void clean() {
        cleanFile(LARGE_FILE_ZEROS);
    }

    static void cleanFile(DSSDocument document) {
        FileDocument fileDocument = assertInstanceOf(FileDocument.class, document);
        File file = fileDocument.getFile();
        assertTrue(file.exists());
        assertTrue(file.delete());
        assertFalse(file.exists());
    }

    private static Stream<Arguments> data() {
        List<DSSDocument> docs = new ArrayList<>();
        docs.add(ZIP_BOMB_ASiCE);
        docs.add(ZIP_BOMB_ASiCS);

        List<Arguments> args = new ArrayList<>();
        for (DSSDocument document : docs) {
            args.add(Arguments.of(document));
        }
        return args.stream();
    }

    @ParameterizedTest(name = "Validation {index} : {0}")
    @MethodSource("data")
    void validateZip(DSSDocument fileToTest) {
        document = fileToTest;
        super.validate();
    }

    @AfterEach
    void reset() {
        ZipUtils.getInstance().setZipContainerHandlerBuilder(new SecureContainerHandlerBuilder());
    }

    @Override
    protected DSSDocument getSignedDocument() {
        return document;
    }

    @Override
    public void validate() {
        // do nothing
    }

    @Override
    protected void checkBLevelValid(DiagnosticData diagnosticData) {
        // do nothing
    }

    @Override
    protected void checkTimestamps(DiagnosticData diagnosticData) {
        // do nothing
    }

    @Override
    protected void checkSignatureScopes(DiagnosticData diagnosticData) {
        // do nothing
    }

    @Override
    protected void validateETSISignersDocument(SignersDocumentType signersDocument) {
        // do nothing
    }

    @Test
    void zipBombingOneLevelAsice() {
        Exception exception = assertThrows(IllegalInputException.class, () -> SignedDocumentValidator.fromDocument(ONE_LEVEL_ZIP_BOMB_ASiCE));
        assertEquals("Zip Bomb detected in the ZIP container. Validation is interrupted.", exception.getMessage());
    }

    @Test
    void zipBombingOneLevelAsice2() {
        // decreased value to pass the test on low memory configuration (less than -Xmx3072m)
        SecureContainerHandlerBuilder secureContainerHandler = new SecureContainerHandlerBuilder().setMaxCompressionRatio(20);
        ZipUtils.getInstance().setZipContainerHandlerBuilder(secureContainerHandler);

        Exception exception = assertThrows(IllegalInputException.class, () -> new ASiCContainerWithCAdESAnalyzer(ONE_LEVEL_ZIP_BOMB_ASiCE));
        assertEquals("Zip Bomb detected in the ZIP container. Validation is interrupted.", exception.getMessage());
    }

    @Test
    void zipBombingOneLevelAsics() {
        Exception exception = assertThrows(IllegalInputException.class, () -> SignedDocumentValidator.fromDocument(ONE_LEVEL_ZIP_BOMB_ASiCS));
        assertEquals("Zip Bomb detected in the ZIP container. Validation is interrupted.", exception.getMessage());
    }

    @Test
    void zipBombingOneLevelAsics2() {
        Exception exception = assertThrows(IllegalInputException.class, () -> new ASiCContainerWithCAdESAnalyzer(ONE_LEVEL_ZIP_BOMB_ASiCS));
        assertEquals("Zip Bomb detected in the ZIP container. Validation is interrupted.", exception.getMessage());
    }

    @Test
    void zipBombingTooManyFilesAsice() {
        Exception exception = assertThrows(IllegalInputException.class, () -> SignedDocumentValidator.fromDocument(MANY_FILES_ASiCE));
        assertEquals("Too many files detected. Cannot extract ASiC content from the file.", exception.getMessage());
    }

    @Test
    void zipBombingTooManyFilesAsics() {
        Exception exception = assertThrows(IllegalInputException.class, () -> SignedDocumentValidator.fromDocument(MANY_FILES_ASiCS));
        assertEquals("Too many files detected. Cannot extract ASiC content from the file.", exception.getMessage());
    }

}
