package campusfind;

import java.io.File;
import java.util.List;

/**
 * Headless Automated Verification Test Suite for CampusFind.
 * 
 * Software Engineering Note:
 * This class runs verification tests against the core requirements without
 * requiring a graphical display. It provides definitive pass/fail reporting
 * for quality assurance and demonstration purposes.
 */
public class CampusFindTestRunner {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   CampusFind Quality Assurance Automated Test Suite");
        System.out.println("======================================================================");

        // Use a temporary test data directory
        String testDir = "test_data_" + System.currentTimeMillis();
        FileStorage testStorage = new FileStorage(testDir);
        RecoveryService service = new RecoveryService(testStorage);

        try {
            testInitialDemoData(service);
            testAddLostItem(service);
            testAddFoundItemWithPrivateClue(service);
            testInformationHidingTableModel(service);
            testSearchAndFiltering(service);
            testSubmitValidClaim(service);
            testClaimValidationFailures(service);
            testReviewAndRejectClaim(service);
            testReviewAndAcceptClaim(service);
            testPersistenceAndReload(testStorage, testDir);
        } finally {
            // Clean up temporary test files
            deleteDirectory(new File(testDir));
        }

        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("TEST SUMMARY: " + testsPassed + " / " + testsRun + " tests passed.");
        if (testsPassed == testsRun) {
            System.out.println("STATUS: ALL TESTS PASSED SUCCESSFULLY (100% PASS RATE).");
        } else {
            System.out.println("STATUS: FAILURES DETECTED.");
        }
        System.out.println("======================================================================");
    }

    private static void assertTrue(String testName, boolean condition, String failureDetail) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } else {
            System.err.println("[FAIL] " + testName + " -> " + failureDetail);
        }
    }

    private static void testInitialDemoData(RecoveryService service) {
        List<Item> items = service.getAllItems();
        List<Claim> claims = service.getAllClaims();
        assertTrue("Test 1: Default Demo Data Seeding",
                items.size() >= 4 && claims.size() >= 2,
                "Expected at least 4 items and 2 claims, found: items=" + items.size() + ", claims=" + claims.size());
    }

    private static void testAddLostItem(RecoveryService service) {
        Item lostItem = new Item(
                null, "LOST", "Lost Blue Umbrella", "Other",
                "Navy blue compact umbrella with wooden curved handle",
                "Main Auditorium Lobby", "2026-10-28",
                "student1@campus.edu", "", "OPEN"
        );
        service.addItem(lostItem);
        Item retrieved = service.getItemById(lostItem.getItemId());
        assertTrue("Test 2: Record Lost Item",
                retrieved != null && "LOST".equals(retrieved.getItemType()) && "OPEN".equals(retrieved.getItemStatus()),
                "Failed to retrieve newly added lost item.");
    }

    private static void testAddFoundItemWithPrivateClue(RecoveryService service) {
        Item foundItem = new Item(
                null, "FOUND", "Found Leather Wallet", "IDs/Cards",
                "Brown tri-fold leather wallet found near gym entrance",
                "Recreation Center Locker Room", "2026-10-28",
                "gym-desk@campus.edu", "Contains family photograph and 1995 lucky coin in zip pocket", "OPEN"
        );
        service.addItem(foundItem);
        Item retrieved = service.getItemById(foundItem.getItemId());
        assertTrue("Test 3: Record Found Item with Private Clue",
                retrieved != null && !retrieved.getPrivateClue().isEmpty(),
                "Private clue was not properly recorded.");
    }

    private static void testInformationHidingTableModel(RecoveryService service) {
        List<Item> allItems = service.getAllItems();
        ItemTableModel model = new ItemTableModel(allItems);

        boolean clueLeaked = false;
        int cols = model.getColumnCount();
        for (int c = 0; c < cols; c++) {
            String colName = model.getColumnName(c);
            if (colName.toLowerCase().contains("clue") || colName.toLowerCase().contains("secret") || colName.toLowerCase().contains("private")) {
                clueLeaked = true;
                break;
            }
        }

        // Also check cell values
        for (int r = 0; r < model.getRowCount(); r++) {
            for (int c = 0; c < cols; c++) {
                Object val = model.getValueAt(r, c);
                if (val != null && val.toString().contains("lucky coin")) {
                    clueLeaked = true;
                    break;
                }
            }
        }

        assertTrue("Test 4: Information Hiding in ItemTableModel (Private Clue Hidden)",
                !clueLeaked,
                "Security breach: Private clue or column found in public table model!");
    }

    private static void testSearchAndFiltering(RecoveryService service) {
        List<Item> bagResults = service.searchItems("", "Bags", "All Statuses", "All Types");
        List<Item> kwResults = service.searchItems("Casio", "All Categories", "All Statuses", "All Types");
        List<Item> lostResults = service.searchItems("", "All Categories", "All Statuses", "LOST");

        assertTrue("Test 5: Multi-Criteria Search & Filtering",
                !bagResults.isEmpty() && !kwResults.isEmpty() && !lostResults.isEmpty(),
                "Search filters failed to return expected subsets.");
    }

    private static void testSubmitValidClaim(RecoveryService service) {
        Claim claim = new Claim(
                null, "ITM-101", "Morgan Bailey",
                "mbailey@student.univ.edu",
                "Inside is my silver USB drive with anime sticker and blue hydroflask bottle.",
                "PENDING", "2026-10-28", ""
        );
        service.submitClaim(claim);
        Claim retrieved = service.getClaimById(claim.getClaimId());

        assertTrue("Test 6: Submit Valid Claim for Found Item",
                retrieved != null && "PENDING".equals(retrieved.getClaimStatus()),
                "Claim was not saved with PENDING status.");
    }

    private static void testClaimValidationFailures(RecoveryService service) {
        boolean failedMissingName = false;
        try {
            service.submitClaim(new Claim(null, "ITM-101", "", "contact@test.com", "answer", "PENDING", "2026-10-28", ""));
        } catch (IllegalArgumentException e) {
            failedMissingName = true;
        }

        boolean failedMissingItem = false;
        try {
            service.submitClaim(new Claim(null, "ITM-99999", "Name", "contact@test.com", "answer", "PENDING", "2026-10-28", ""));
        } catch (IllegalArgumentException e) {
            failedMissingItem = true;
        }

        assertTrue("Test 7: Claim Validation Rules (Empty fields and invalid item ID rejected)",
                failedMissingName && failedMissingItem,
                "Validation failed to intercept invalid inputs.");
    }

    private static void testReviewAndRejectClaim(RecoveryService service) {
        Claim claim = new Claim(
                null, "ITM-102", "Charlie Vance",
                "cvance@campus.edu", "I think it is my calculator with white tape.",
                "PENDING", "2026-10-28", ""
        );
        service.submitClaim(claim);
        boolean reviewed = service.reviewClaim(claim.getClaimId(), false, "Color of tape did not match.");

        Claim updated = service.getClaimById(claim.getClaimId());
        assertTrue("Test 8: Review and Reject Claim",
                reviewed && updated != null && "REJECTED".equals(updated.getClaimStatus()),
                "Claim status not updated to REJECTED.");
    }

    private static void testReviewAndAcceptClaim(RecoveryService service) {
        // Submit two competing claims for ITM-104 (Water bottle)
        Claim validClaim = new Claim(
                null, "ITM-104", "Jesse Doe",
                "jdoe@campus.edu", "Scratch on bottom with my initials J.D.",
                "PENDING", "2026-10-28", ""
        );
        Claim competingClaim = new Claim(
                null, "ITM-104", "Random Claimant",
                "random@campus.edu", "It is just a silver bottle.",
                "PENDING", "2026-10-28", ""
        );
        service.submitClaim(validClaim);
        service.submitClaim(competingClaim);

        // Accept valid claim
        boolean accepted = service.reviewClaim(validClaim.getClaimId(), true, "Initials match private clue.");

        Claim updatedValid = service.getClaimById(validClaim.getClaimId());
        Claim updatedCompeting = service.getClaimById(competingClaim.getClaimId());
        Item updatedItem = service.getItemById("ITM-104");

        assertTrue("Test 9: Review and Accept Claim (Item auto-resolves, competing claims rejected)",
                accepted && updatedValid != null && "ACCEPTED".equals(updatedValid.getClaimStatus())
                        && updatedItem != null && "RESOLVED".equals(updatedItem.getItemStatus())
                        && updatedCompeting != null && "REJECTED".equals(updatedCompeting.getClaimStatus()),
                "Failed state transition upon claim acceptance.");
    }

    private static void testPersistenceAndReload(FileStorage originalStorage, String testDir) {
        // Instantiate a second fresh storage pointing to the same directory
        FileStorage secondStorage = new FileStorage(testDir);
        RecoveryService freshInstance = new RecoveryService(secondStorage);

        List<Item> reloadedItems = freshInstance.getAllItems();
        List<Claim> reloadedClaims = freshInstance.getAllClaims();

        assertTrue("Test 10: Local File Persistence and Reload Integrity",
                !reloadedItems.isEmpty() && !reloadedClaims.isEmpty(),
                "Reloaded instance failed to read persisted CSV records.");
    }

    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDirectory(f);
                }
            }
        }
        dir.delete();
    }
}
