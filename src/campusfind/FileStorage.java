package campusfind;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistent storage of Item and Claim records using simple CSV files.
 * 
 * Software Engineering Note:
 * Using simple CSV files provides lightweight, human-readable local persistence
 * without requiring an external database server or external libraries.
 * If data files do not exist, initial demonstration data is automatically created.
 */
public class FileStorage {

    private final String dataDirectory;
    private final String itemsFilePath;
    private final String claimsFilePath;

    public FileStorage() {
        this("data");
    }

    public FileStorage(String directoryName) {
        this.dataDirectory = directoryName;
        this.itemsFilePath = directoryName + File.separator + "items.csv";
        this.claimsFilePath = directoryName + File.separator + "claims.csv";
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ==========================================
    // ITEM PERSISTENCE
    // ==========================================

    public List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        File file = new File(itemsFilePath);

        if (!file.exists() || file.length() == 0) {
            // Seed initial demo data if file is missing
            items = createInitialDemoItems();
            saveItems(items);
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip CSV header
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> fields = parseCsvLine(line);
                if (fields.size() >= 10) {
                    Item item = new Item(
                            fields.get(0), // itemId
                            fields.get(1), // itemType
                            fields.get(2), // title
                            fields.get(3), // category
                            fields.get(4), // publicDescription
                            fields.get(5), // location
                            fields.get(6), // reportDate
                            fields.get(7), // contactInfo
                            fields.get(8), // privateClue
                            fields.get(9)  // itemStatus
                    );
                    items.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading items file: " + e.getMessage());
        }

        return items;
    }

    public boolean saveItems(List<Item> items) {
        ensureDirectoryExists();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(itemsFilePath), StandardCharsets.UTF_8))) {
            // Header
            writer.println("itemId,itemType,title,category,publicDescription,location,reportDate,contactInfo,privateClue,itemStatus");
            for (Item item : items) {
                StringBuilder sb = new StringBuilder();
                sb.append(escapeCsv(item.getItemId())).append(",");
                sb.append(escapeCsv(item.getItemType())).append(",");
                sb.append(escapeCsv(item.getTitle())).append(",");
                sb.append(escapeCsv(item.getCategory())).append(",");
                sb.append(escapeCsv(item.getPublicDescription())).append(",");
                sb.append(escapeCsv(item.getLocation())).append(",");
                sb.append(escapeCsv(item.getReportDate())).append(",");
                sb.append(escapeCsv(item.getContactInfo())).append(",");
                sb.append(escapeCsv(item.getPrivateClue())).append(",");
                sb.append(escapeCsv(item.getItemStatus()));
                writer.println(sb.toString());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // CLAIM PERSISTENCE
    // ==========================================

    public List<Claim> loadClaims() {
        List<Claim> claims = new ArrayList<>();
        File file = new File(claimsFilePath);

        if (!file.exists() || file.length() == 0) {
            // Seed initial demo claims if file is missing
            claims = createInitialDemoClaims();
            saveClaims(claims);
            return claims;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip CSV header
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> fields = parseCsvLine(line);
                if (fields.size() >= 8) {
                    Claim claim = new Claim(
                            fields.get(0), // claimId
                            fields.get(1), // itemId
                            fields.get(2), // claimantName
                            fields.get(3), // claimantContact
                            fields.get(4), // ownershipAnswer
                            fields.get(5), // claimStatus
                            fields.get(6), // submissionDate
                            fields.get(7)  // reviewNotes
                    );
                    claims.add(claim);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading claims file: " + e.getMessage());
        }

        return claims;
    }

    public boolean saveClaims(List<Claim> claims) {
        ensureDirectoryExists();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(claimsFilePath), StandardCharsets.UTF_8))) {
            // Header
            writer.println("claimId,itemId,claimantName,claimantContact,ownershipAnswer,claimStatus,submissionDate,reviewNotes");
            for (Claim claim : claims) {
                StringBuilder sb = new StringBuilder();
                sb.append(escapeCsv(claim.getClaimId())).append(",");
                sb.append(escapeCsv(claim.getItemId())).append(",");
                sb.append(escapeCsv(claim.getClaimantName())).append(",");
                sb.append(escapeCsv(claim.getClaimantContact())).append(",");
                sb.append(escapeCsv(claim.getOwnershipAnswer())).append(",");
                sb.append(escapeCsv(claim.getClaimStatus())).append(",");
                sb.append(escapeCsv(claim.getSubmissionDate())).append(",");
                sb.append(escapeCsv(claim.getReviewNotes()));
                writer.println(sb.toString());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving claims: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // DEMO DATA SEEDING
    // ==========================================

    private List<Item> createInitialDemoItems() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(
                "ITM-101", "FOUND", "Blue Dell Laptop Backpack", "Bags",
                "Dark blue backpack found on 2nd floor library near table 4",
                "Campus Library 2nd Floor", "2026-10-25",
                "library-desk@campus.edu",
                "Contains a silver 16GB flash drive with an anime sticker and blue hydroflask",
                "OPEN"
        ));
        list.add(new Item(
                "ITM-102", "FOUND", "Casio fx-991EX Scientific Calculator", "Electronics",
                "Black scientific calculator with slide-on protective hard case",
                "Science Building Lab 302", "2026-10-26",
                "sec-lab302@campus.edu",
                "Battery cover held in place by small piece of yellow masking tape",
                "OPEN"
        ));
        list.add(new Item(
                "ITM-103", "LOST", "Student ID Card (Jordan Smith)", "IDs/Cards",
                "Undergraduate student card in clear plastic lanyard sleeve",
                "Student Center Cafeteria", "2026-10-27",
                "jsmith@student.univ.edu",
                "",
                "OPEN"
        ));
        list.add(new Item(
                "ITM-104", "FOUND", "Silver Stainless Steel Water Bottle", "Other",
                "32oz insulated bottle with several national park stickers",
                "Gymnasium Bleachers", "2026-10-27",
                "athletics@campus.edu",
                "Scratch on bottom with scratched initials J.D.",
                "OPEN"
        ));
        return list;
    }

    private List<Claim> createInitialDemoClaims() {
        List<Claim> list = new ArrayList<>();
        list.add(new Claim(
                "CLM-501", "ITM-101", "Alex Rivera",
                "arivera@student.univ.edu",
                "My backpack has my silver 16GB USB drive with an anime sticker and blue hydroflask inside.",
                "PENDING", "2026-10-27", ""
        ));
        list.add(new Claim(
                "CLM-502", "ITM-102", "Taylor Brooks",
                "tbrooks@student.univ.edu",
                "I think that is my calculator, I lost one yesterday in the lab.",
                "REJECTED", "2026-10-27",
                "Claimant could not describe the battery cover or identifying marks."
        ));
        return list;
    }

    // ==========================================
    // SIMPLE CSV UTILITIES
    // ==========================================

    private String escapeCsv(String value) {
        if (value == null) {
            return "\"\"";
        }
        // Replace internal quotes with double quotes
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder curVal = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    // Escaped quote
                    curVal.append('\"');
                    i++; // skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(curVal.toString());
                curVal = new StringBuilder();
            } else {
                curVal.append(ch);
            }
        }
        result.add(curVal.toString());
        return result;
    }
}
