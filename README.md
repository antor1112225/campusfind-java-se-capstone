# CampusFind: A Campus Lost-and-Found System with Private Claim Verification

[![Java SE](https://img.shields.io/badge/Java-SE%208%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![GUI](https://img.shields.io/badge/GUI-Java%20Swing-007396?logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![QA Tests](https://img.shields.io/badge/QA%20Tests-10%2F10%20PASS-success)](docs/test-plan.md)
[![License](https://img.shields.io/badge/License-Academic%20Submission-blue)](#)

> **Part I — Final Capstone Project Proposal and Prototyping**  
> **Course:** Software Engineering  
> **Repository:** [https://github.com/antor1112225/campusfind-java-se-capstone](https://github.com/antor1112225/campusfind-java-se-capstone)  
> **Project Landing Page:** [https://antor1112225.github.io/campusfind-java-se-capstone/website/](https://antor1112225.github.io/campusfind-java-se-capstone/website/)

---

## 1. Project Overview
**CampusFind** is a beginner-friendly Java desktop application created for a university community to record lost and found items, search reported items, submit ownership claims, and review claims before marking items as returned.

### The Core Problem & Our Distinctive Solution
In typical lost-and-found systems:
- If items are listed with complete descriptions, dishonest people can submit false claims.
- If items are hidden, genuine owners cannot find them.

CampusFind resolves this dilemma through **Two-Party Private Claim Verification**:
1. When a person reports a **FOUND** item, they enter public details (e.g., *Blue Dell Backpack found on Library 2nd floor*) plus a **Private Identifying Clue** (e.g., *Contains a silver 16GB flash drive with an anime sticker*).
2. The public browsing table displays the general info but **strictly hides the private clue**.
3. A claimant must describe that secret feature when filing a claim.
4. Campus staff review the claimant's answer side-by-side with the founder's confidential clue before marking the item resolved.

---

## 2. Main Features
- **Record Lost Items:** Intake for misplaced belongings with category, location, and reporter contact.
- **Record Found Items with Private Clues:** Capture confidential verification details known only to the true owner.
- **Public Catalog & Filtering:** Search by keyword, category, status (OPEN/RESOLVED), and type (LOST/FOUND).
- **Presentation-Layer Information Hiding:** `ItemTableModel` strictly omits private clue fields from the table.
- **Submit Ownership Claims:** Validates target found item status, capturing contact details and secret proof.
- **Administrative Review Console:** Side-by-side comparison of founder's clue vs claimant's answer with Accept/Reject actions.
- **Automated Lifecycle Transitions:** Accepting a claim marks the item as RESOLVED and auto-rejects competing pending claims.
- **Zero-Dependency Local Persistence:** Flat-file CSV storage (`data/items.csv` and `data/claims.csv`) with automatic demo seeding.
- **Headless Automated Test Suite:** 10 automated test cases with 100% pass rate in `CampusFindTestRunner.java`.

---

## 3. Technology Stack & Prerequisites
- **Language:** Java SE (Standard Edition)
- **Minimum Java Version:** JDK 8 or higher (JDK 11, 17, or 21 fully supported)
- **GUI Toolkit:** Java Swing (built into standard JDK, no external JARs required)
- **Persistence:** Local CSV flat files (`java.io`)
- **Documentation:** LaTeX (`proposal.tex`) and Markdown (`proposal.md`)
- **Web Portal:** HTML5 and CSS3 (`website/index.html` for GitHub Pages)

---

## 4. Repository Structure
```text
campusfind-java-se-capstone/
├── README.md                           # Main project documentation (this file)
├── .gitignore                          # Git exclusions (classes, temporary files)
├── src/                                # Java source files
│   └── campusfind/
│       ├── Main.java                   # Application entry point
│       ├── MainFrame.java              # Swing desktop graphical interface
│       ├── Item.java                   # Item domain model (public info + private clue)
│       ├── Claim.java                  # Claim domain model (claimant info + secret proof)
│       ├── RecoveryService.java        # Core business logic & claim resolution engine
│       ├── FileStorage.java            # CSV persistence & automatic demo data seeding
│       ├── ItemTableModel.java         # Custom table model enforcing information hiding
│       └── CampusFindTestRunner.java   # Headless automated SQA verification suite
├── data/                               # Local persistent storage
│   ├── items.csv                       # Persisted lost & found item records
│   └── claims.csv                      # Persisted ownership claim records
├── proposal/                           # Academic capstone proposal & assets
│   ├── proposal.tex                    # LaTeX source proposal document
│   ├── proposal.md                     # Markdown formatted proposal fallback
│   ├── references.bib                  # BibTeX bibliography references
│   ├── diagrams/                       # UML diagrams (PlantUML source & SVG exports)
│   │   ├── dfd-context.puml / .svg     # Level 0 Context Data Flow Diagram
│   │   ├── dfd-level1.puml / .svg      # Level 1 Process Decomposition DFD
│   │   ├── use-case.puml / .svg        # Use Case Diagram
│   │   ├── activity.puml / .svg        # Activity Diagram with Swimlanes
│   │   ├── erd.puml / .svg             # Entity Relationship Diagram
│   │   ├── class-diagram.puml / .svg   # Detailed UML Class Diagram (7 classes)
│   │   └── object-diagram.puml / .svg  # UML Object Diagram (Runtime Snapshot)
│   └── screenshots/                    # High-fidelity prototype visual screens
│       ├── ui-browse.svg               # Tab 1: Browse & Search Items
│       ├── ui-report.svg               # Tab 2: Report Lost/Found Item
│       ├── ui-claim.svg                # Tab 3: Submit Ownership Claim
│       └── ui-review.svg               # Tab 4: Review Claims (Demo Admin)
├── docs/                               # Detailed supplementary engineering documents
│   ├── project-overview.md             # Architecture & problem statement summary
│   ├── test-plan.md                    # SQA testing strategy & test execution matrix
│   ├── gantt-chart.md                  # 6-week schedule, milestones & acceptance criteria
│   └── viva-presentation-guide.md      # Beginner defense guide, Q&A, and demo script
└── website/                            # GitHub Pages project landing page
    ├── index.html                      # Responsive web landing page
    └── style.css                       # Clean modern stylesheet
```

---

## 5. How to Compile and Run

### Step 1: Open Terminal in Project Root
```bash
cd "campusfind-java-se-capstone"
```

### Step 2: Compile All Java Classes
```bash
javac -d bin src/campusfind/*.java
```

### Step 3: Run the Desktop GUI Application
```bash
java -cp bin campusfind.Main
```

### Step 4: Run the Automated Verification Suite (Headless)
```bash
java -cp bin campusfind.CampusFindTestRunner
```

---

## 6. How the Application Works (Beginner Explanation)

```mermaid
sequenceDiagram
    autonumber
    actor Founder as Campus Founder
    actor Claimant as Campus Claimant
    participant App as CampusFind GUI
    participant Svc as RecoveryService
    participant Store as FileStorage (CSV)
    actor Admin as Staff Reviewer

    Founder->>App: Report Found Item (Public info + Private Clue)
    App->>Svc: addItem(foundItem)
    Svc->>Store: saveItems()
    
    Claimant->>App: Browse Items (Private clue is HIDDEN)
    Claimant->>App: Submit Claim (Provide secret ownership proof)
    App->>Svc: submitClaim(claim)
    Svc->>Store: saveClaims()

    Admin->>App: Open Review Tab
    App->>Admin: Display Side-by-Side: Founder Clue vs Claimant Answer
    Admin->>App: Click "Accept Claim"
    App->>Svc: reviewClaim(claimId, true, remarks)
    Svc->>Store: Update item status to RESOLVED
    Svc->>Store: Update claim to ACCEPTED; reject competing claims
    App->>Admin: Confirm Handover Authorized
```

---

## 7. Class-by-Class Explanation

1. **`Item.java`**: Domain model storing item attributes. Crucially holds `privateClue`, which is kept separate from public display queries.
2. **`Claim.java`**: Domain model storing claim details, including claimant contact information and `ownershipAnswer` (secret proof).
3. **`FileStorage.java`**: Handles reading and writing CSV records. If files are missing, it seeds 4 demo items and 2 demo claims automatically.
4. **`RecoveryService.java`**: Core business logic layer. Validates form data, generates sequential IDs (`ITM-xxx`, `CLM-xxx`), filters searches, and manages claim approval/rejection.
5. **`ItemTableModel.java`**: Extends Swing's `AbstractTableModel`. Exposes 9 public columns and strictly omits `privateClue`, demonstrating *Information Hiding*.
6. **`MainFrame.java`**: The Swing desktop GUI with 4 tabs: Browse, Report, Claim, and Review Claims (Demo Admin).
7. **`Main.java`**: Entry point that applies the system native look-and-feel and launches `MainFrame` on the Swing Event Dispatch Thread (EDT).
8. **`CampusFindTestRunner.java`**: Standalone automated test runner verifying 10 test cases covering validation, search, information hiding, and persistence.

---

## 8. Live Demonstration Script

1. **Launch App**: Run `java -cp bin campusfind.Main`. Point out the 4 seeded demo items. Notice table columns terminate at Status (no private clues shown).
2. **Search**: Filter by keyword `"Casio"`. Notice only the calculator appears. Click **Reset**.
3. **Select Item**: Select `ITM-101` (*Blue Dell Backpack*). Click the blue button **"Claim Selected Found Item >>"**.
4. **Submit Claim**: Notice it auto-filled `ITM-101`. Type Claimant: `Alex Rivera`, Contact: `arivera@student.univ.edu`, and Secret Proof: `"My backpack has my silver 16GB USB drive with anime sticker and blue hydroflask"`. Click **Submit Claim for Review**.
5. **Review**: Switch to Tab 4 (*Review Claims*). Select `CLM-501`. Show the **Side-by-Side Comparison Box** proving the founder's clue and claimant's answer match.
6. **Accept**: Enter admin remarks and click **Accept Claim (Resolve)**.
7. **Verify**: Return to Tab 1. Notice `ITM-101` is now marked **`RESOLVED`** and its claim button is disabled.

---

## 9. Likely Instructor Viva Questions & Simple Answers

- **Q: Why Java Swing instead of a web framework?**  
  *A:* Swing is built directly into standard Java (`javax.swing`), allowing this capstone prototype to run on any computer with a standard JDK without requiring web servers, node modules, or external databases.
- **Q: How is the private clue protected from public view?**  
  *A:* In `ItemTableModel.java`, the `columnNames` array only defines public columns, and `getValueAt()` never calls `item.getPrivateClue()`. In `RecoveryService.java`, keyword search queries only evaluate title, description, and location.
- **Q: How does the application store data?**  
  *A:* Local CSV files (`data/items.csv` and `data/claims.csv`) managed by `FileStorage.java`. Missing files are automatically created with sample demo data.
- **Q: What happens when a claim is accepted?**  
  *A:* In `RecoveryService.java`, the claim is set to `ACCEPTED`, the item is marked `RESOLVED`, and any competing pending claims for that item are automatically marked `REJECTED`.

---

## 10. Building the LaTeX Proposal
If a TeX distribution (TeX Live, MiKTeX, MacTeX) is installed:
```bash
cd proposal
pdflatex proposal.tex
bibtex proposal
pdflatex proposal.tex
pdflatex proposal.tex
```
*Note: A complete, fully formatted Markdown alternative is maintained at [`proposal/proposal.md`](proposal/proposal.md).*

---

## 11. Current Limitations & Part II Roadmap
- Prototype authentication is illustrative; real-world deployment requires role-based access control (RBAC).
- Local CSV storage will be upgraded to an embedded SQL database (SQLite or PostgreSQL) in Part II.
- Email/SMS notification gateways will be introduced for automated status updates.

---

## 12. Academic Integrity & Metadata Placeholders
- **Student Name:** `[STUDENT NAME]`
- **Student ID:** `[STUDENT ID]`
- **Department:** `[DEPARTMENT]`
- **University:** `[UNIVERSITY NAME]`
- **Instructor:** `[INSTRUCTOR NAME]`
- **Submission Date:** October 28, 2026

*Fictional demonstration data only. All references are verified real sources from standard software engineering literature.*