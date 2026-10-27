package campusfind;

import java.io.Serializable;

/**
 * Represents an ownership claim submitted by a campus member for a found item.
 * 
 * Software Engineering Note:
 * The claim stores the claimant's answer describing the private clue.
 * In a real-world system, this enables two-party verification before handover.
 */
public class Claim implements Serializable {
    private static final long serialVersionUID = 1L;

    private String claimId;          // e.g. "CLM-501"
    private String itemId;           // References Item.itemId
    private String claimantName;     // Full name of person claiming ownership
    private String claimantContact;  // Email or phone number
    private String ownershipAnswer;  // Private details explaining why item is theirs
    private String claimStatus;      // "PENDING", "ACCEPTED", "REJECTED"
    private String submissionDate;   // ISO date, e.g., "2026-10-26"
    private String reviewNotes;      // Administrative remarks during verification

    // Default constructor
    public Claim() {
        this.claimStatus = "PENDING";
        this.reviewNotes = "";
    }

    // Full constructor
    public Claim(String claimId, String itemId, String claimantName,
                 String claimantContact, String ownershipAnswer,
                 String claimStatus, String submissionDate, String reviewNotes) {
        this.claimId = claimId;
        this.itemId = itemId;
        this.claimantName = claimantName;
        this.claimantContact = claimantContact;
        this.ownershipAnswer = ownershipAnswer;
        this.claimStatus = (claimStatus != null && !claimStatus.isEmpty()) ? claimStatus : "PENDING";
        this.submissionDate = submissionDate;
        this.reviewNotes = (reviewNotes != null) ? reviewNotes : "";
    }

    // Getters and Setters
    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getClaimantContact() {
        return claimantContact;
    }

    public void setClaimantContact(String claimantContact) {
        this.claimantContact = claimantContact;
    }

    public String getOwnershipAnswer() {
        return ownershipAnswer;
    }

    public void setOwnershipAnswer(String ownershipAnswer) {
        this.ownershipAnswer = ownershipAnswer;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.claimStatus);
    }

    public boolean isAccepted() {
        return "ACCEPTED".equalsIgnoreCase(this.claimStatus);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(this.claimStatus);
    }

    @Override
    public String toString() {
        return "[" + claimId + "] for Item " + itemId + " by " + claimantName + " (" + claimStatus + ")";
    }
}
