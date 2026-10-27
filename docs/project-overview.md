# CampusFind: Project Overview & System Architecture

## 1. Executive Summary
**CampusFind** is a lightweight Java desktop application developed as Part I of a Software Engineering Capstone Project. It provides a structured, campus-wide lost-and-found management solution with an innovative **Private Claim Verification** mechanism.

In conventional lost-and-found systems, publicly displaying all item characteristics enables fraudulent claims. Conversely, hiding all information prevents true owners from recognizing their belongings. CampusFind resolves this dilemma through a two-tier information disclosure model:
1. **Public Tier**: Visible attributes (item category, exterior color, brand, location found, and date) are displayed in an open catalog.
2. **Confidential Tier**: The finder enters a secret identifying detail ("Private Clue")—such as an item inside a pocket, an engraving, or wallpaper picture. This clue is withheld from public tables and revealed only to authorized reviewers to evaluate claimant assertions before item release.

---

## 2. Problem Statement & Motivation
University campuses experience high volumes of misplaced personal items, including student ID cards, laptops, calculators, keys, and water bottles. Existing informal approaches (social media groups, physical bulletin boards, scattered department reception desks) suffer from significant flaws:
- **Fragmentation**: Students must search across dozens of physical offices.
- **Fraud & False Claims**: Describing all item features publicly allows dishonest individuals to falsely claim high-value items.
- **Administrative Burden**: Staff lack an audit trail or standardized method to record handover justifications.

CampusFind provides a centralized, beginner-friendly desktop application that streamlines reporting, enforces data encapsulation, and establishes a clear audit trail for claim resolution.

---

## 3. Scope & Boundaries
### In Scope (Prototype Phase - Part I)
- Recording lost and found items with standardized categories.
- Storing confidential private clues for found items.
- Public search and filtering by keyword, category, status, and item type.
- Verification of information hiding (private clues strictly omitted from public views).
- Ownership claim submission with secret corroboration answers.
- Administrative demonstration review (side-by-side clue vs. answer comparison).
- One-click claim acceptance (auto-resolving item and auto-rejecting competing claims) and claim rejection with audit notes.
- Local persistence via structured CSV files (`data/items.csv` and `data/claims.csv`).

### Out of Scope (Explicit Limitations)
- Multi-user network server or cloud database (local file storage is used).
- Production-grade cryptographic security or single sign-on (SSO).
- Automated computer-vision or AI clue matching.
- Mobile applications or SMS/email gateway integrations.
- Physical campus locker integration.

---

## 4. Software Process Model
CampusFind follows an **Iterative and Incremental Development Model with Early UI Prototyping**.

### Process Justification for Beginner Java Developers
1. **Manageable Complexity**: Allows breaking development into small, achievable increments (Model -> Storage -> Service -> GUI).
2. **Early Feedback**: Building visual UI prototypes early validates functional requirements with stakeholders before backend completion.
3. **Continuous Testing**: Each increment is verified with automated and manual test cases, preventing defect accumulation.
4. **Low Risk**: Adjustments to business rules or data attributes can be made in early iterations without rewriting large codebases.

---

## 5. Technology Stack
- **Programming Language**: Java (Standard Edition, JDK 8 or higher)
- **GUI Framework**: Java Swing (standard library, native Look & Feel)
- **Persistence**: Flat-file CSV storage using standard `java.io` (UTF-8 encoding)
- **Modeling & Diagrams**: UML 2.5 (PlantUML, Mermaid, vector SVG exports)
- **Documentation**: LaTeX (`proposal.tex`) and Markdown (`proposal.md`)
- **Hosting & Web Portal**: GitHub Pages (`website/index.html`)
