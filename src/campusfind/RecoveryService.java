package campusfind;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer containing the core business logic of the CampusFind system.
 * 
 * Software Engineering Note:
 * This class coordinates actions between the user interface and persistent storage.
 * It enforces business rules (such as validating claims, auto-resolving items upon
 * claim acceptance, and generating sequential identifiers) without mixing them
 * into GUI event handlers.
 */
public class RecoveryService {

    private final FileStorage storage;
    private final List<Item> items;
    private final List<Claim> claims;

    public RecoveryService() {
        this(new FileStorage());
    }

    public RecoveryService(FileStorage storage) {
        this.storage = storage;
        this.items = new ArrayList<>(storage.loadItems());
        this.claims = new ArrayList<>(storage.loadClaims());
    }

    // ==========================================
    // ITEM MANAGEMENT
    // ==========================================

    public synchronized List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public synchronized Item getItemById(String itemId) {
        if (itemId == null) return null;
        for (Item item : items) {
            if (item.getItemId().equalsIgnoreCase(itemId.trim())) {
                return item;
            }
        }
        return null;
    }

    public synchronized boolean addItem(Item item) {
        String error = validateItem(item);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }

        if (item.getItemId() == null || item.getItemId().trim().isEmpty()) {
            item.setItemId(generateNextItemId());
        }

        items.add(item);
        return storage.saveItems(items);
    }

    public synchronized List<Item> searchItems(String keyword, String category, String status, String type) {
        List<Item> results = new ArrayList<>();
        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";

        for (Item item : items) {
            // Category filter
            if (category != null && !category.equalsIgnoreCase("All Categories") && !category.trim().isEmpty()) {
                if (!item.getCategory().equalsIgnoreCase(category.trim())) {
                    continue;
                }
            }

            // Status filter ("OPEN", "RESOLVED", or "All Statuses")
            if (status != null && !status.equalsIgnoreCase("All Statuses") && !status.trim().isEmpty()) {
                if (!item.getItemStatus().equalsIgnoreCase(status.trim())) {
                    continue;
                }
            }

            // Type filter ("LOST", "FOUND", or "All Types")
            if (type != null && !type.equalsIgnoreCase("All Types") && !type.trim().isEmpty()) {
                if (!item.getItemType().equalsIgnoreCase(type.trim())) {
                    continue;
                }
            }

            // Keyword match (title, public description, or location)
            // Note: privateClue is intentionally NOT searched to prevent public leakage!
            if (!kw.isEmpty()) {
                boolean matchesTitle = item.getTitle() != null && item.getTitle().toLowerCase().contains(kw);
                boolean matchesDesc = item.getPublicDescription() != null && item.getPublicDescription().toLowerCase().contains(kw);
                boolean matchesLoc = item.getLocation() != null && item.getLocation().toLowerCase().contains(kw);
                boolean matchesId = item.getItemId() != null && item.getItemId().toLowerCase().contains(kw);

                if (!matchesTitle && !matchesDesc && !matchesLoc && !matchesId) {
                    continue;
                }
            }

            results.add(item);
        }

        return results;
    }

    public synchronized boolean markItemResolved(String itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }
        item.setItemStatus("RESOLVED");
        return storage.saveItems(items);
    }

    public synchronized String generateNextItemId() {
        int maxNum = 100;
        for (Item item : items) {
            String id = item.getItemId();
            if (id != null && id.toUpperCase().startsWith("ITM-")) {
                try {
                    int num = Integer.parseInt(id.substring(4).trim());
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return "ITM-" + (maxNum + 1);
    }

    // ==========================================
    // CLAIM MANAGEMENT
    // ==========================================

    public synchronized List<Claim> getAllClaims() {
        return new ArrayList<>(claims);
    }

    public synchronized List<Claim> getClaimsForItem(String itemId) {
        List<Claim> list = new ArrayList<>();
        if (itemId == null) return list;
        for (Claim c : claims) {
            if (c.getItemId().equalsIgnoreCase(itemId.trim())) {
                list.add(c);
            }
        }
        return list;
    }

    public synchronized Claim getClaimById(String claimId) {
        if (claimId == null) return null;
        for (Claim c : claims) {
            if (c.getClaimId().equalsIgnoreCase(claimId.trim())) {
                return c;
            }
        }
        return null;
    }

    public synchronized boolean submitClaim(Claim claim) {
        String error = validateClaim(claim);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }

        if (claim.getClaimId() == null || claim.getClaimId().trim().isEmpty()) {
            claim.setClaimId(generateNextClaimId());
        }

        claims.add(claim);
        return storage.saveClaims(claims);
    }

    /**
     * Reviews a claim.
     * If accepted:
     *   1. The target claim status is set to ACCEPTED.
     *   2. The corresponding item is marked RESOLVED.
     *   3. Any other pending claims for that item are marked REJECTED.
     * If rejected:
     *   1. The claim status is set to REJECTED with the given reason.
     */
    public synchronized boolean reviewClaim(String claimId, boolean accept, String reviewNotes) {
        Claim claim = getClaimById(claimId);
        if (claim == null) {
            return false;
        }

        if (accept) {
            claim.setClaimStatus("ACCEPTED");
            claim.setReviewNotes((reviewNotes != null && !reviewNotes.trim().isEmpty())
                    ? reviewNotes.trim()
                    : "Claim verified against private clue. Approved for collection.");

            // Mark the corresponding item as RESOLVED
            Item item = getItemById(claim.getItemId());
            if (item != null) {
                item.setItemStatus("RESOLVED");
                storage.saveItems(items);
            }

            // Automatically close other pending claims for this item
            for (Claim other : claims) {
                if (!other.getClaimId().equalsIgnoreCase(claim.getClaimId())
                        && other.getItemId().equalsIgnoreCase(claim.getItemId())
                        && other.isPending()) {
                    other.setClaimStatus("REJECTED");
                    other.setReviewNotes("Item resolved to another claimant.");
                }
            }
        } else {
            claim.setClaimStatus("REJECTED");
            claim.setReviewNotes((reviewNotes != null && !reviewNotes.trim().isEmpty())
                    ? reviewNotes.trim()
                    : "Claimant detail does not match identifying records.");
        }

        return storage.saveClaims(claims);
    }

    public synchronized String generateNextClaimId() {
        int maxNum = 500;
        for (Claim c : claims) {
            String id = c.getClaimId();
            if (id != null && id.toUpperCase().startsWith("CLM-")) {
                try {
                    int num = Integer.parseInt(id.substring(4).trim());
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return "CLM-" + (maxNum + 1);
    }

    // ==========================================
    // VALIDATION RULES
    // ==========================================

    public String validateItem(Item item) {
        if (item == null) return "Item object cannot be null.";
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            return "Item title is required.";
        }
        if (item.getItemType() == null || (!item.getItemType().equalsIgnoreCase("LOST") && !item.getItemType().equalsIgnoreCase("FOUND"))) {
            return "Item type must be either 'LOST' or 'FOUND'.";
        }
        if (item.getLocation() == null || item.getLocation().trim().isEmpty()) {
            return "Location is required.";
        }
        if (item.getContactInfo() == null || item.getContactInfo().trim().isEmpty()) {
            return "Contact information is required.";
        }
        return null; // Valid
    }

    public String validateClaim(Claim claim) {
        if (claim == null) return "Claim object cannot be null.";
        if (claim.getItemId() == null || claim.getItemId().trim().isEmpty()) {
            return "Referenced Item ID is required.";
        }
        Item item = getItemById(claim.getItemId());
        if (item == null) {
            return "Referenced Item (" + claim.getItemId() + ") does not exist.";
        }
        if (!item.isFound()) {
            return "Claims can only be filed against 'FOUND' items.";
        }
        if (item.isResolved()) {
            return "This item has already been marked 'RESOLVED'. No new claims can be submitted.";
        }
        if (claim.getClaimantName() == null || claim.getClaimantName().trim().isEmpty()) {
            return "Claimant name is required.";
        }
        if (claim.getClaimantContact() == null || claim.getClaimantContact().trim().isEmpty()) {
            return "Claimant contact detail is required.";
        }
        if (claim.getOwnershipAnswer() == null || claim.getOwnershipAnswer().trim().isEmpty()) {
            return "Ownership proof / private clue description is required.";
        }
        return null; // Valid
    }

    public synchronized void reload() {
        items.clear();
        items.addAll(storage.loadItems());
        claims.clear();
        claims.addAll(storage.loadClaims());
    }
}
