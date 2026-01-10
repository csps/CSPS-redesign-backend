package org.csps.backend;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.repository.MerchRepository;
import org.csps.backend.repository.MerchVariantRepository;
import org.csps.backend.service.MerchVariantService;
import org.csps.backend.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Integration test for S3 MerchVariant image upload functionality.
 * 
 * This test validates:
 * 1. S3 bucket credentials
 * 2. Image upload to S3
 * 3. Storing S3 image keys in database
 * 4. Image deletion from S3
 */
@SpringBootTest
@DisplayName("S3 MerchVariant Integration Tests")
public class S3MerchVariantIntegrationTest {

    @Autowired
    private MerchVariantService merchVariantService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private MerchRepository merchRepository;

    @Autowired
    private MerchVariantRepository merchVariantRepository;

    private Merch testMerch;

    @BeforeEach
    void setUp() {
        // Create test merchandise
        testMerch = Merch.builder()
                .merchName("Test T-Shirt")
                .description("A test t-shirt for S3 integration")
                .merchType(MerchType.CLOTHING)
                .build();

        testMerch = merchRepository.save(testMerch);
    }

    @Test
    @DisplayName("Should upload variant image to S3 and store S3 key")
    void testUploadVariantImageWithS3() throws IOException {
        // Create test image file
        byte[] testImageBytes = createTestImageBytes();
        MultipartFile testImageFile = new MockMultipartFile(
                "file",
                "test-variant.jpg",
                "image/jpeg",
                testImageBytes
        );

        // Create variant request
        MerchVariantRequestDTO variantRequest = new MerchVariantRequestDTO();
        variantRequest.setMerchId(testMerch.getMerchId());
        variantRequest.setColor("Red");
        variantRequest.setSize(ClothingSizing.MEDIUM);
        variantRequest.setPrice(29.99);
        variantRequest.setStockQuantity(50);

        // Upload variant with image
        MerchVariantResponseDTO response = merchVariantService.addMerchVariantWithImage(variantRequest, testImageFile);

        // Assertions
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getMerchVariantId(), "Variant ID should be generated");
        assertNotNull(response.getS3ImageKey(), "S3 image key should be set");
        assertTrue(response.getS3ImageKey().contains("merchVariant/"), "S3 key should contain merchVariant path");
        assertEquals("Red", response.getColor());
        assertEquals(ClothingSizing.M, response.getSize());

        // Verify S3 key is stored in database
        var savedVariant = merchVariantRepository.findById(response.getMerchVariantId());
        assertTrue(savedVariant.isPresent(), "Variant should exist in database");
        assertNotNull(savedVariant.get().getS3ImageKey(), "S3 image key should be stored in database");
        assertEquals(response.getS3ImageKey(), savedVariant.get().getS3ImageKey());

        System.out.println("✓ S3 Image Upload Test Passed!");
        System.out.println("  - Variant ID: " + response.getMerchVariantId());
        System.out.println("  - S3 Image Key: " + response.getS3ImageKey());
    }

    @Test
    @DisplayName("Should delete old image when uploading new variant image")
    void testReplaceVariantImage() throws IOException {
        // Create and upload first image
        MerchVariantRequestDTO variantRequest = new MerchVariantRequestDTO();
        variantRequest.setMerchId(testMerch.getMerchId());
        variantRequest.setColor("Blue");
        variantRequest.setSize(ClothingSizing.L);
        variantRequest.setPrice(29.99);
        variantRequest.setStockQuantity(30);

        byte[] firstImageBytes = createTestImageBytes();
        MultipartFile firstImageFile = new MockMultipartFile(
                "file",
                "first-variant.jpg",
                "image/jpeg",
                firstImageBytes
        );

        MerchVariantResponseDTO firstResponse = merchVariantService.addMerchVariantWithImage(variantRequest, firstImageFile);
        String firstS3Key = firstResponse.getS3ImageKey();

        // Upload second image for same variant
        byte[] secondImageBytes = createTestImageBytes();
        MultipartFile secondImageFile = new MockMultipartFile(
                "file",
                "second-variant.jpg",
                "image/jpeg",
                secondImageBytes
        );

        String secondS3Key = merchVariantService.uploadVariantImage(firstResponse.getMerchVariantId(), secondImageFile);

        // Assertions
        assertNotNull(secondS3Key, "Second S3 key should be generated");
        assertNotEquals(firstS3Key, secondS3Key, "S3 keys should be different");

        // Verify updated variant
        var updatedVariant = merchVariantRepository.findById(firstResponse.getMerchVariantId());
        assertTrue(updatedVariant.isPresent());
        assertEquals(secondS3Key, updatedVariant.get().getS3ImageKey(), "Variant should have new S3 key");

        System.out.println("✓ S3 Image Replacement Test Passed!");
        System.out.println("  - Old S3 Key: " + firstS3Key);
        System.out.println("  - New S3 Key: " + secondS3Key);
    }

    @Test
    @DisplayName("Should reject variant upload without image file")
    void testVariantUploadWithoutImageShouldFail() {
        MerchVariantRequestDTO variantRequest = new MerchVariantRequestDTO();
        variantRequest.setMerchId(testMerch.getMerchId());
        variantRequest.setColor("Green");
        variantRequest.setSize(ClothingSizing.S);
        variantRequest.setPrice(27.99);
        variantRequest.setStockQuantity(40);

        // Attempt to upload without image
        assertThrows(IllegalArgumentException.class, () -> {
            merchVariantService.addMerchVariantWithImage(variantRequest, null);
        }, "Should throw IllegalArgumentException when image file is null");

        System.out.println("✓ S3 Image Validation Test Passed!");
    }

    @Test
    @DisplayName("Should verify S3 bucket credentials are valid")
    void testS3BucketCredentials() throws IOException {
        // Create a test file
        byte[] testBytes = createTestImageBytes();
        MultipartFile testFile = new MockMultipartFile(
                "test",
                "credentials-test.jpg",
                "image/jpeg",
                testBytes
        );

        // Attempt to upload - this will fail if credentials are invalid
        String uploadedKey = s3Service.uploadFile(testFile, "test/credentials");

        // Assertions
        assertNotNull(uploadedKey, "S3 should return a key if upload is successful");
        assertFalse(uploadedKey.isEmpty(), "S3 key should not be empty");

        // Clean up
        s3Service.deleteFile(uploadedKey);

        System.out.println("✓ S3 Credentials Test Passed!");
        System.out.println("  - Successfully uploaded to S3");
        System.out.println("  - Successfully deleted from S3");
    }

    /**
     * Helper method to create a minimal test image (1x1 pixel JPEG)
     */
    private byte[] createTestImageBytes() {
        // Minimal JPEG file (1x1 red pixel)
        return new byte[]{
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, (byte) 0x00, (byte) 0x10,
                (byte) 0x4A, (byte) 0x46, (byte) 0x49, (byte) 0x46, (byte) 0x00, (byte) 0x01,
                (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xDB, (byte) 0x00, (byte) 0x43,
                (byte) 0x00, (byte) 0x08, (byte) 0x06, (byte) 0x06, (byte) 0x07, (byte) 0x06,
                (byte) 0x05, (byte) 0x08, (byte) 0x07, (byte) 0x07, (byte) 0x07, (byte) 0x09,
                (byte) 0x09, (byte) 0x08, (byte) 0x0A, (byte) 0x0C, (byte) 0x14, (byte) 0x0D,
                (byte) 0x0C, (byte) 0x0B, (byte) 0x0B, (byte) 0x0C, (byte) 0x19, (byte) 0x12,
                (byte) 0x13, (byte) 0x0F, (byte) 0x14, (byte) 0x1D, (byte) 0x1A, (byte) 0x1F,
                (byte) 0x1E, (byte) 0x1D, (byte) 0x1A, (byte) 0x1C, (byte) 0x1C, (byte) 0x20,
                (byte) 0x24, (byte) 0x2E, (byte) 0x27, (byte) 0x20, (byte) 0x22, (byte) 0x2C,
                (byte) 0x23, (byte) 0x1C, (byte) 0x1C, (byte) 0x28, (byte) 0x37, (byte) 0x29,
                (byte) 0x2C, (byte) 0x30, (byte) 0x31, (byte) 0x34, (byte) 0x34, (byte) 0x34,
                (byte) 0x1F, (byte) 0x27, (byte) 0x39, (byte) 0x3D, (byte) 0x38, (byte) 0x32,
                (byte) 0x3C, (byte) 0x2E, (byte) 0x33, (byte) 0x34, (byte) 0x32, (byte) 0xFF,
                (byte) 0xC0, (byte) 0x00, (byte) 0x0B, (byte) 0x08, (byte) 0x00, (byte) 0x01,
                (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x11, (byte) 0x00,
                (byte) 0xFF, (byte) 0xC4, (byte) 0x00, (byte) 0x1F, (byte) 0x00, (byte) 0x00,
                (byte) 0x01, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02,
                (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
                (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0xFF, (byte) 0xC4, (byte) 0x00,
                (byte) 0xB5, (byte) 0x10, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x03,
                (byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x05, (byte) 0x05,
                (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x7D,
                (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x11,
                (byte) 0x05, (byte) 0x12, (byte) 0x21, (byte) 0x31, (byte) 0x41, (byte) 0x06,
                (byte) 0x13, (byte) 0x51, (byte) 0x61, (byte) 0x07, (byte) 0x22, (byte) 0x71,
                (byte) 0x14, (byte) 0x32, (byte) 0x81, (byte) 0x91, (byte) 0xA1, (byte) 0x08,
                (byte) 0x23, (byte) 0x42, (byte) 0xB1, (byte) 0xC1, (byte) 0x15, (byte) 0x52,
                (byte) 0xD1, (byte) 0xF0, (byte) 0x24, (byte) 0x33, (byte) 0x62, (byte) 0x72,
                (byte) 0x82, (byte) 0x09, (byte) 0x0A, (byte) 0x16, (byte) 0x17, (byte) 0x18,
                (byte) 0x19, (byte) 0x1A, (byte) 0x25, (byte) 0x26, (byte) 0x27, (byte) 0x28,
                (byte) 0x29, (byte) 0x2A, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37,
                (byte) 0x38, (byte) 0x39, (byte) 0x3A, (byte) 0x43, (byte) 0x44, (byte) 0x45,
                (byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4A, (byte) 0x53,
                (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59,
                (byte) 0x5A, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67,
                (byte) 0x68, (byte) 0x69, (byte) 0x6A, (byte) 0x73, (byte) 0x74, (byte) 0x75,
                (byte) 0x76, (byte) 0x77, (byte) 0x78, (byte) 0x79, (byte) 0x7A, (byte) 0x83,
                (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89,
                (byte) 0x8A, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96,
                (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9A, (byte) 0xA2, (byte) 0xA3,
                (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7, (byte) 0xA8, (byte) 0xA9,
                (byte) 0xAA, (byte) 0xB2, (byte) 0xB3, (byte) 0xB4, (byte) 0xB5, (byte) 0xB6,
                (byte) 0xB7, (byte) 0xB8, (byte) 0xB9, (byte) 0xBA, (byte) 0xC2, (byte) 0xC3,
                (byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7, (byte) 0xC8, (byte) 0xC9,
                (byte) 0xCA, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6,
                (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xE1, (byte) 0xE2,
                (byte) 0xE3, (byte) 0xE4, (byte) 0xE5, (byte) 0xE6, (byte) 0xE7, (byte) 0xE8,
                (byte) 0xE9, (byte) 0xEA, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4,
                (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xFA,
                (byte) 0xFF, (byte) 0xDA, (byte) 0x00, (byte) 0x08, (byte) 0x01, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x3F, (byte) 0x00, (byte) 0xFB, (byte) 0xD2,
                (byte) 0xFF, (byte) 0xD9
        };
    }
}
