# CampusFind: Beginner Viva & Presentation Defense Guide

This guide is written specifically to help you explain **CampusFind** confidently to your instructor or evaluation panel, even if you are still learning Java fundamentals.

---

## 1. How the Application Works (30-Second Elevator Pitch)
> "Professor, **CampusFind** is a Java desktop application designed to solve the campus lost-and-found problem. 
> 
> Most lost-and-found systems fail because either they hide everything (so owners can't find their items) or they reveal everything (allowing dishonest people to claim items that aren't theirs).
> 
> CampusFind solves this through **Two-Party Private Claim Verification**:
> 1. When someone finds an item, they report the public details (like *'Blue Dell Backpack'*), but also enter a **private clue** (like *'Contains a silver 16GB flash drive with an anime sticker'*).
> 2. The public browsing table completely hides this private clue.
> 3. To claim the backpack, the owner must describe that secret detail.
> 4. Campus staff compare both answers side-by-side in the Admin panel and click **Accept**, which automatically marks the item resolved and closes any competing claims."

---

## 2. Class-by-Class Explanation (Beginner Friendly)

| Class Name | Everyday Analogy | What it does in our code |
| :--- | :--- | :--- |
| **`Item.java`** | A physical index card | Represents a lost or found item. Stores public attributes (title, category, location) and the confidential `privateClue`. It has simple getter and setter methods. |
| **`Claim.java`** | A claim ticket | Represents an ownership claim submitted by a student. Stores the claimant's name, contact info, and their secret proof (`ownershipAnswer`). |
| **`FileStorage.java`** | A file cabinet | Reads and writes `items.csv` and `claims.csv` so data is not lost when closing the app. If the files are missing on first run, it creates demo data automatically. |
| **`RecoveryService.java`** | The office manager | Holds the business logic. It validates inputs, generates next IDs (`ITM-105`, `CLM-503`), searches items, and updates claim/item status. The GUI asks this service to do all the real work. |
| **`ItemTableModel.java`** | A security filter | Tells the Swing table what columns to show. Crucially, it only includes public columns and **strictly leaves out `privateClue`**, demonstrating the software engineering concept of *Information Hiding*. |
| **`MainFrame.java`** | The front desk counter | The graphical interface built with Java Swing. It has 4 tabs: Browse, Report, Claim, and Review Claims. |
| **`Main.java`** | The ignition key | Contains `public static void main()`. It sets the modern Look-and-Feel, creates `FileStorage`, `RecoveryService`, and opens `MainFrame`. |
| **`CampusFindTestRunner.java`** | The automated inspector | A test program that runs 10 automated test cases without opening a window to prove that the code works 100%. |

---

## 3. Likely Instructor Questions & Model Answers

### Q1: Why did you choose Swing instead of Spring Boot, React, or JavaFX?
**Answer:**
> "I chose Java Swing because it is part of the standard Java library (`javax.swing`), meaning the application runs immediately on any computer with a standard JDK without requiring complicated web servers, NPM packages, external build tools, or paid cloud databases. This keeps the prototype lightweight, realistic for a student project, and easy to run and evaluate."

### Q2: What Software Process Model did you use, and why?
**Answer:**
> "I used the **Iterative and Incremental Development Model with Early UI Prototyping**. As a beginner, building in increments allowed me to first create and test the simple domain classes (`Item` and `Claim`), then build file persistence, then service logic, and finally the GUI. Prototyping the screens early helped clarify user requirements before writing complex backend logic."

### Q3: How do you protect the private clue from leaking to public users?
**Answer:**
> "Through architectural **Information Hiding**:
> 1. In `ItemTableModel.java`, the `columnNames` array only defines the 9 public columns. The method `getValueAt()` never calls `item.getPrivateClue()`.
> 2. In `RecoveryService.searchItems()`, the search query only checks the title, description, and location, ensuring keyword searches cannot uncover private clues."

### Q4: How is data stored? What happens if the database crashes?
**Answer:**
> "The prototype uses local flat-file storage with comma-separated values (`items.csv` and `claims.csv`) handled by `FileStorage.java`. There is no separate database server to crash. If the data files do not exist (e.g. on first launch), `FileStorage` automatically seeds realistic demonstration records so the system is immediately usable."

### Q5: What happens when an administrator accepts a claim?
**Answer:**
> "Three things happen automatically in `RecoveryService.reviewClaim()`:
> 1. The approved claim status changes from `PENDING` to `ACCEPTED`.
> 2. The corresponding item status changes from `OPEN` to `RESOLVED`.
> 3. Any other pending claims for that same item are automatically marked `REJECTED` with a note explaining that another claimant was verified."

### Q6: What would you improve in Part II (Future Work)?
**Answer:**
> "In Part II, we would replace local CSV files with a relational database like PostgreSQL or SQLite, implement real Role-Based Access Control (RBAC) with hashed passwords so students cannot access the Admin tab, and add automated campus email notifications when an item is claimed."

---

## 4. Live Demonstration Script (Step-by-Step)

Follow this exact 3-minute sequence during your project demonstration:

1. **Step 1: Open the Application**
   - Run `java -cp bin campusfind.Main` (or run `Main.java` in your IDE).
   - Point out the 4 pre-loaded demonstration items in the table.
   - Show that the table columns end at *Status*—the **Private Clue** is nowhere to be seen.

2. **Step 2: Test Search & Filtering**
   - Type `"Casio"` in the Keyword box and click **Filter**.
   - Show that only the scientific calculator appears.
   - Click **Reset** to restore all items.

3. **Step 3: Show the Found Item and Selected Row Details**
   - Click on `ITM-101` (*Blue Dell Laptop Backpack*).
   - Show the bottom details box showing public description: *"Dark blue backpack found on 2nd floor library near table 4"*.
   - Point out the active blue button: **"Claim Selected Found Item >>"**. Click it.

4. **Step 4: Demonstrate Claim Filing**
   - Notice that clicking the button automatically navigated to Tab 3 (*Submit Claim*) and pre-filled Item ID `ITM-101`.
   - Click **Check Item** to confirm the green banner confirms the backpack is OPEN.
   - Type Claimant Name: `"Alex Rivera"`.
   - Type Contact: `"arivera@student.univ.edu"`.
   - In the Secret Clue box, type: `"Inside is my silver 16GB flash drive with an anime sticker and blue hydroflask"`.
   - Click **Submit Claim for Review**. Show the confirmation dialog.

5. **Step 5: Demonstrate Administrative Review & Verification**
   - Switch to Tab 4 (*Review Claims (Demo Admin)*).
   - Select `CLM-501` for `ITM-101`.
   - Highlight the **Side-by-Side Comparison Box**:
     - Left box: Founder's private clue recorded when found.
     - Right box: Alex Rivera's submitted answer.
   - Show how easy it is for staff to verify they match!
   - In Admin Remarks, enter: `"Verified against flash drive sticker. Handover approved."`
   - Click **Accept Claim (Resolve)**. Confirm the dialog.

6. **Step 6: Show Resolution & Auto-Closure**
   - Switch back to Tab 1 (*Browse Items*).
   - Show that `ITM-101` now has Status: **`RESOLVED`**!
   - Select it: notice the "Claim This Item" button is now disabled because the item is closed.
   - Conclude: *"This demonstrates the complete lifecycle from report to private verification and final resolution."*
