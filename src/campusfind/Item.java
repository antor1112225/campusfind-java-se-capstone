package campusfind;

import java.io.Serializable;

/**
 * Represents a lost or found item reported in the CampusFind system.
 * 
 * Software Engineering Note:
 * This model encapsulates both public item details and the private clue.
 * The private clue is kept confidential and is never displayed in the
 * public search or browsing interface.
 */
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemId;            // Unique identifier, e.g., "ITM-101"
    private String itemType;          // "LOST" or "FOUND"
    private String title;             // Brief title of the item
    private String category;          // e.g., "Electronics", "Bags", "IDs/Cards", "Books"
    private String publicDescription; // Visible details (color, brand, visible marks)
    private String location;          // Campus location where lost or found
    private String reportDate;         // ISO date string, e.g., "2026-10-25"
    private String contactInfo;       // Reporter's university email or phone
    private String privateClue;       // Confidential detail (e.g. sticker inside, serial no, wallpaper)
    private String itemStatus;        // "OPEN" or "RESOLVED"

    // Default constructor
    public Item() {
        this.itemStatus = "OPEN";
    }

    // Full constructor
    public Item(String itemId, String itemType, String title, String category,
                String publicDescription, String location, String reportDate,
                String contactInfo, String privateClue, String itemStatus) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.title = title;
        this.category = category;
        this.publicDescription = publicDescription;
        this.location = location;
        this.reportDate = reportDate;
        this.contactInfo = contactInfo;
        this.privateClue = (privateClue != null) ? privateClue : "";
        this.itemStatus = (itemStatus != null && !itemStatus.isEmpty()) ? itemStatus : "OPEN";
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getPrivateClue() {
        return privateClue;
    }

    public void setPrivateClue(String privateClue) {
        this.privateClue = privateClue;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public boolean isFound() {
        return "FOUND".equalsIgnoreCase(this.itemType);
    }

    public boolean isLost() {
        return "LOST".equalsIgnoreCase(this.itemType);
    }

    public boolean isResolved() {
        return "RESOLVED".equalsIgnoreCase(this.itemStatus);
    }

    @Override
    public String toString() {
        return "[" + itemId + "] (" + itemType + ") " + title + " - " + itemStatus;
    }
}
