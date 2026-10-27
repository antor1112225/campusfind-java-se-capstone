# Quality Assurance & Testing Plan

This document details the Software Quality Assurance (SQA) testing strategy, automated test matrix, test cases, and verification results for the **CampusFind** application.

---

## 1. Testing Strategy & Methodology
The testing approach balances two testing levels:
1. **Automated Unit & Integration Testing**: Implemented in [`CampusFindTestRunner.java`](file:///e:/03%20ICT/software%20engineering%20and%20management/src/campusfind/CampusFindTestRunner.java). Executes headless logic verification on data modeling, validation rules, business logic transitions, information hiding, and file persistence.
2. **Interactive UI Verification**: Verifies graphical event flows, Swing table renders, modal dialog alerts, tab navigation, and responsive status reporting.

---

## 2. Test Execution Matrix

| Test ID | Test Category | Feature Under Test | Test Input / Action | Expected Result | Execution Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Initialization | Seed Demo Records | Launch app with missing/empty CSV files in `data/` directory. | System auto-creates `data/` directory and populates 4 demo items and 2 demo claims without throwing errors. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-02** | Functional | Record Lost Item | Submit report: Type="LOST", Title="Lost Blue Umbrella", Category="Other", Location="Auditorium". | Item assigned sequential ID (`ITM-105`), Status="OPEN", Type="LOST", saved to storage. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-03** | Functional | Record Found Item with Private Clue | Submit report: Type="FOUND", Title="Leather Wallet", Clue="Contains lucky 1995 coin in zip pocket". | Item recorded with `privateClue` stored in memory and CSV; Status="OPEN". | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-04** | Security / Privacy | Information Hiding Verification | Inspect `ItemTableModel` columns and cell values across all rendered rows. | Column names contain NO clue references; cell values NEVER reveal the private clue to public viewers. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-05** | Functional | Multi-Criteria Search & Filter | Filter by Category="Bags", Keyword="Casio", and Type="LOST". | Table displays only records matching criteria; search excludes private clue field. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-06** | Functional | Submit Valid Claim | Target `ITM-101`, Claimant="Alex Rivera", Answer="Silver 16GB USB drive with anime sticker". | Claim assigned ID (`CLM-503`), Status="PENDING", linked via `itemId="ITM-101"`. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-07** | Validation | Incomplete Claim Form Rejection | Submit claim with blank Claimant Name or non-existent Item ID (`ITM-99999`). | `IllegalArgumentException` thrown; UI displays descriptive error message; invalid claim rejected. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-08** | Business Logic | Review & Reject Claim | Admin selects pending claim, enters rejection note, clicks "Reject Claim". | Claim status updated to `REJECTED`; rejection note recorded; Item status remains `OPEN`. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-09** | Business Logic | Review & Accept Claim (Auto-Resolve) | Admin reviews matching claim, enters approval note, clicks "Accept Claim". | Target claim marked `ACCEPTED`; Item marked `RESOLVED`; any competing pending claims auto-marked `REJECTED`. | **PASS** (Executed via `CampusFindTestRunner`) |
| **TC-10** | Persistence | CSV Storage & Instance Reload | Write records, close session, instantiate fresh `FileStorage` and `RecoveryService`. | Fresh instance loads identical records from `items.csv` and `claims.csv` with 100% data integrity. | **PASS** (Executed via `CampusFindTestRunner`) |

---

## 3. Automated Test Execution Output
The test suite in [`CampusFindTestRunner.java`](file:///e:/03%20ICT/software%20engineering%20and%20management/src/campusfind/CampusFindTestRunner.java) produces the following standardized console log:

```text
======================================================================
   CampusFind Quality Assurance Automated Test Suite
======================================================================
[PASS] Test 1: Default Demo Data Seeding
[PASS] Test 2: Record Lost Item
[PASS] Test 3: Record Found Item with Private Clue
[PASS] Test 4: Information Hiding in ItemTableModel (Private Clue Hidden)
[PASS] Test 5: Multi-Criteria Search & Filtering
[PASS] Test 6: Submit Valid Claim for Found Item
[PASS] Test 7: Claim Validation Rules (Empty fields and invalid item ID rejected)
[PASS] Test 8: Review and Reject Claim
[PASS] Test 9: Review and Accept Claim (Item auto-resolves, competing claims rejected)
[PASS] Test 10: Local File Persistence and Reload Integrity

----------------------------------------------------------------------
TEST SUMMARY: 10 / 10 tests passed.
STATUS: ALL TESTS PASSED SUCCESSFULLY (100% PASS RATE).
======================================================================
```

---

## 4. Defect Tracking & Mitigation
During prototype iterations, the following test discoveries were addressed:
1. **CSV Comma Splitting**: Public descriptions containing commas caused parsing errors. 
   - *Fix*: Implemented RFC-style quote wrapping (`"field, with, commas"`) and character-by-character CSV parsing in `FileStorage.java`.
2. **Competing Claim Stacking**: When multiple claims were filed on one item and one was accepted, others lingered in PENDING status.
   - *Fix*: In `RecoveryService.reviewClaim()`, accepting a claim automatically iterates over competing claims for that `itemId` and transitions them to `REJECTED` with an explanatory note.
