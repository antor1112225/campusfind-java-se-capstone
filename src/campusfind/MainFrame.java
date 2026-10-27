package campusfind;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main graphical user interface for the CampusFind desktop prototype.
 * 
 * Software Engineering Note:
 * This interface is designed for clean usability and clear presentation
 * during academic evaluation. It cleanly delegates all business operations
 * (search, validation, claim filing, review) to RecoveryService.
 */
public class MainFrame extends JFrame {

    private final RecoveryService recoveryService;

    // Tabs
    private JTabbedPane tabbedPane;

    // Tab 1: Browse
    private ItemTableModel itemTableModel;
    private JTable itemTable;
    private JTextField txtSearchKeyword;
    private JComboBox<String> cmbSearchCategory;
    private JComboBox<String> cmbSearchStatus;
    private JComboBox<String> cmbSearchType;
    private JTextArea txtBrowseDetails;
    private JButton btnClaimSelected;

    // Tab 2: Report
    private JComboBox<String> cmbReportType;
    private JTextField txtReportTitle;
    private JComboBox<String> cmbReportCategory;
    private JTextArea txtReportPublicDesc;
    private JTextField txtReportLocation;
    private JTextField txtReportContact;
    private JTextArea txtReportPrivateClue;
    private JLabel lblPrivateClueHelp;

    // Tab 3: Claim
    private JTextField txtClaimItemId;
    private JLabel lblClaimItemPreview;
    private JTextField txtClaimantName;
    private JTextField txtClaimantContact;
    private JTextArea txtClaimAnswer;

    // Tab 4: Review Claims
    private JTable claimTable;
    private DefaultTableModel claimTableModel;
    private JComboBox<String> cmbClaimFilter;
    private JTextArea txtComparisonFounderClue;
    private JTextArea txtComparisonClaimantAnswer;
    private JTextField txtAdminReviewNotes;
    private JLabel lblReviewItemSummary;
    private JButton btnAcceptClaim;
    private JButton btnRejectClaim;

    // Header & Status
    private JLabel lblStatsBadge;
    private JLabel lblStatusBar;

    public MainFrame(RecoveryService recoveryService) {
        this.recoveryService = recoveryService;
        initUI();
        refreshAllData();
    }

    private void initUI() {
        setTitle("CampusFind — Campus Lost-and-Found System with Private Claim Verification");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout(8, 8));
        mainContainer.setBorder(new EmptyBorder(10, 12, 10, 12));

        // 1. Top Banner
        mainContainer.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Tabbed Content
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("1. Browse & Search Items", createBrowsePanel());
        tabbedPane.addTab("2. Report Lost / Found Item", createReportPanel());
        tabbedPane.addTab("3. Submit Claim", createClaimPanel());
        tabbedPane.addTab("4. Review Claims (Demo Admin)", createReviewPanel());

        mainContainer.add(tabbedPane, BorderLayout.CENTER);

        // 3. Status Bar
        mainContainer.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(mainContainer);
    }

    // ==========================================
    // TOP HEADER
    // ==========================================

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(24, 43, 73));
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("CampusFind — University Lost & Found");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Prototype v1.0 | Two-Party Private Clue Verification");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(190, 210, 235));

        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        lblStatsBadge = new JLabel("Loading stats...");
        lblStatsBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatsBadge.setForeground(new Color(255, 230, 120));
        lblStatsBadge.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(titleBox, BorderLayout.WEST);
        panel.add(lblStatsBadge, BorderLayout.EAST);
        return panel;
    }

    // ==========================================
    // TAB 1: BROWSE & SEARCH
    // ==========================================

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filterBar.setBorder(BorderFactory.createTitledBorder("Search & Filter Items"));

        filterBar.add(new JLabel("Keyword:"));
        txtSearchKeyword = new JTextField(12);
        filterBar.add(txtSearchKeyword);

        filterBar.add(new JLabel("Category:"));
        cmbSearchCategory = new JComboBox<>(new String[]{
                "All Categories", "Electronics", "Bags", "IDs/Cards", "Books", "Clothing", "Keys", "Other"
        });
        filterBar.add(cmbSearchCategory);

        filterBar.add(new JLabel("Type:"));
        cmbSearchType = new JComboBox<>(new String[]{"All Types", "FOUND", "LOST"});
        filterBar.add(cmbSearchType);

        filterBar.add(new JLabel("Status:"));
        cmbSearchStatus = new JComboBox<>(new String[]{"All Statuses", "OPEN", "RESOLVED"});
        filterBar.add(cmbSearchStatus);

        JButton btnSearch = new JButton("Filter");
        btnSearch.addActionListener(e -> performSearch());
        filterBar.add(btnSearch);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> resetSearch());
        filterBar.add(btnReset);

        panel.add(filterBar, BorderLayout.NORTH);

        // Table
        itemTableModel = new ItemTableModel();
        itemTable = new JTable(itemTableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setRowHeight(24);
        itemTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Custom highlight for LOST vs FOUND
        itemTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    if ("FOUND".equals(value)) {
                        c.setForeground(new Color(0, 120, 40));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("LOST".equals(value)) {
                        c.setForeground(new Color(180, 40, 20));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        });

        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateBrowseDetails();
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Details & Action
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Selected Item Public Details"));

        txtBrowseDetails = new JTextArea(4, 50);
        txtBrowseDetails.setEditable(false);
        txtBrowseDetails.setLineWrap(true);
        txtBrowseDetails.setWrapStyleWord(true);
        txtBrowseDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtBrowseDetails.setText("Select an item above to inspect public details.");
        bottomPanel.add(new JScrollPane(txtBrowseDetails), BorderLayout.CENTER);

        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnClaimSelected = new JButton("Claim Selected Found Item >>");
        btnClaimSelected.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClaimSelected.setBackground(new Color(30, 110, 200));
        btnClaimSelected.setForeground(Color.WHITE);
        btnClaimSelected.setEnabled(false);
        btnClaimSelected.addActionListener(e -> {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow >= 0) {
                Item item = itemTableModel.getItemAt(selectedRow);
                if (item != null && item.isFound()) {
                    txtClaimItemId.setText(item.getItemId());
                    updateClaimItemPreview();
                    tabbedPane.setSelectedIndex(2); // Switch to Claim tab
                }
            }
        });
        actionBox.add(btnClaimSelected);
        bottomPanel.add(actionBox, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void updateBrowseDetails() {
        int row = itemTable.getSelectedRow();
        if (row < 0) {
            txtBrowseDetails.setText("Select an item above to inspect public details.");
            btnClaimSelected.setEnabled(false);
            return;
        }

        Item item = itemTableModel.getItemAt(row);
        if (item == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(item.getItemId()).append(" | Type: ").append(item.getItemType())
          .append(" | Title: ").append(item.getTitle()).append(" | Status: ").append(item.getItemStatus()).append("\n");
        sb.append("Category: ").append(item.getCategory()).append(" | Location: ").append(item.getLocation())
          .append(" | Date Reported: ").append(item.getReportDate()).append("\n");
        sb.append("Contact: ").append(item.getContactInfo()).append("\n");
        sb.append("Public Description: ").append(item.getPublicDescription()).append("\n");
        sb.append("[NOTE: Private identifying clue is withheld from public display for security.]");

        txtBrowseDetails.setText(sb.toString());

        // Enable claim button only if item is FOUND and OPEN
        btnClaimSelected.setEnabled(item.isFound() && !item.isResolved());
    }

    private void performSearch() {
        String kw = txtSearchKeyword.getText();
        String cat = (String) cmbSearchCategory.getSelectedItem();
        String type = (String) cmbSearchType.getSelectedItem();
        String status = (String) cmbSearchStatus.getSelectedItem();

        List<Item> results = recoveryService.searchItems(kw, cat, status, type);
        itemTableModel.setItems(results);
        setStatusBar("Search completed. Found " + results.size() + " matching item(s).");
    }

    private void resetSearch() {
        txtSearchKeyword.setText("");
        cmbSearchCategory.setSelectedIndex(0);
        cmbSearchType.setSelectedIndex(0);
        cmbSearchStatus.setSelectedIndex(0);
        itemTableModel.setItems(recoveryService.getAllItems());
        setStatusBar("Filters cleared. Showing all " + itemTableModel.getRowCount() + " item(s).");
    }

    // ==========================================
    // TAB 2: REPORT ITEM
    // ==========================================

    private JPanel createReportPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout(8, 8));
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Report a Lost or Found Item",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int row = 0;

        // 1. Report Type
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Report Type:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        cmbReportType = new JComboBox<>(new String[]{"FOUND (I discovered an item)", "LOST (I lost my item)"});
        cmbReportType.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean isFound = cmbReportType.getSelectedIndex() == 0;
                txtReportPrivateClue.setEnabled(isFound);
                txtReportPrivateClue.setBackground(isFound ? Color.WHITE : new Color(240, 240, 240));
                lblPrivateClueHelp.setEnabled(isFound);
            }
        });
        form.add(cmbReportType, g);
        row++;

        // 2. Title
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Item Title:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtReportTitle = new JTextField(25);
        form.add(txtReportTitle, g);
        row++;

        // 3. Category
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Category:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        cmbReportCategory = new JComboBox<>(new String[]{
                "Electronics", "Bags", "IDs/Cards", "Books", "Clothing", "Keys", "Other"
        });
        form.add(cmbReportCategory, g);
        row++;

        // 4. Public Description
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Public Description:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtReportPublicDesc = new JTextArea(3, 25);
        txtReportPublicDesc.setLineWrap(true);
        txtReportPublicDesc.setWrapStyleWord(true);
        form.add(new JScrollPane(txtReportPublicDesc), g);
        row++;

        // 5. Location
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Campus Location:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtReportLocation = new JTextField(25);
        form.add(txtReportLocation, g);
        row++;

        // 6. Contact
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Reporter Contact (Email/Phone):*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtReportContact = new JTextField(25);
        form.add(txtReportContact, g);
        row++;

        // 7. Private Clue (Found items only)
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Private Identifying Clue:"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtReportPrivateClue = new JTextArea(3, 25);
        txtReportPrivateClue.setLineWrap(true);
        txtReportPrivateClue.setWrapStyleWord(true);
        form.add(new JScrollPane(txtReportPrivateClue), g);
        row++;

        // Clue help text
        g.gridx = 1; g.gridy = row;
        lblPrivateClueHelp = new JLabel("<html><i><b>Confidential:</b> Will NOT appear in public listing. " +
                "Used to verify the claimant (e.g. wallpaper picture, engraved mark, contents inside pocket).</i></html>");
        lblPrivateClueHelp.setForeground(new Color(120, 80, 20));
        form.add(lblPrivateClueHelp, g);
        row++;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSubmit = new JButton("Submit Report");
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSubmit.setBackground(new Color(20, 120, 60));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> handleReportSubmit());

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearReportForm());

        btnPanel.add(btnClear);
        btnPanel.add(btnSubmit);

        rootPanel.add(new JScrollPane(form), BorderLayout.CENTER);
        rootPanel.add(btnPanel, BorderLayout.SOUTH);

        return rootPanel;
    }

    private void handleReportSubmit() {
        String type = (cmbReportType.getSelectedIndex() == 0) ? "FOUND" : "LOST";
        String title = txtReportTitle.getText().trim();
        String category = (String) cmbReportCategory.getSelectedItem();
        String desc = txtReportPublicDesc.getText().trim();
        String location = txtReportLocation.getText().trim();
        String contact = txtReportContact.getText().trim();
        String privateClue = ("FOUND".equals(type)) ? txtReportPrivateClue.getText().trim() : "";
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        Item newItem = new Item(
                null, // ID generated by service
                type,
                title,
                category,
                desc,
                location,
                today,
                contact,
                privateClue,
                "OPEN"
        );

        try {
            recoveryService.addItem(newItem);
            JOptionPane.showMessageDialog(this,
                    "Item reported successfully!\nAssigned Item ID: " + newItem.getItemId(),
                    "Report Recorded", JOptionPane.INFORMATION_MESSAGE);
            clearReportForm();
            refreshAllData();
            tabbedPane.setSelectedIndex(0); // Switch to browse tab
            setStatusBar("New item " + newItem.getItemId() + " recorded.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearReportForm() {
        txtReportTitle.setText("");
        txtReportPublicDesc.setText("");
        txtReportLocation.setText("");
        txtReportContact.setText("");
        txtReportPrivateClue.setText("");
        cmbReportCategory.setSelectedIndex(0);
        cmbReportType.setSelectedIndex(0);
    }

    // ==========================================
    // TAB 3: SUBMIT CLAIM
    // ==========================================

    private JPanel createClaimPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout(8, 8));
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "File an Ownership Claim Against a Found Item",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 6, 8, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Item ID input
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Found Item ID:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        JPanel idPanel = new JPanel(new BorderLayout(6, 0));
        txtClaimItemId = new JTextField(12);
        JButton btnLookup = new JButton("Check Item");
        btnLookup.addActionListener(e -> updateClaimItemPreview());
        idPanel.add(txtClaimItemId, BorderLayout.CENTER);
        idPanel.add(btnLookup, BorderLayout.EAST);
        form.add(idPanel, g);
        row++;

        // Item Preview Box
        g.gridx = 1; g.gridy = row;
        lblClaimItemPreview = new JLabel("Enter an Item ID (e.g. ITM-101) and click 'Check Item'.");
        lblClaimItemPreview.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblClaimItemPreview.setForeground(new Color(60, 60, 100));
        form.add(lblClaimItemPreview, g);
        row++;

        // Claimant Name
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Your Full Name:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtClaimantName = new JTextField(25);
        form.add(txtClaimantName, g);
        row++;

        // Claimant Contact
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Your Contact (Email/Phone):*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtClaimantContact = new JTextField(25);
        form.add(txtClaimantContact, g);
        row++;

        // Ownership Answer / Secret Clue
        g.gridx = 0; g.gridy = row; g.weightx = 0.0;
        form.add(new JLabel("Ownership Proof / Secret Clue:*"), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        txtClaimAnswer = new JTextArea(4, 25);
        txtClaimAnswer.setLineWrap(true);
        txtClaimAnswer.setWrapStyleWord(true);
        form.add(new JScrollPane(txtClaimAnswer), g);
        row++;

        // Help info
        g.gridx = 1; g.gridy = row;
        JLabel lblNotice = new JLabel("<html><i>Describe concealed details that only the true owner would know " +
                "(e.g., items inside pockets, lock screen background, exact sticker details). " +
                "Campus staff will compare this against the confidential clue before returning the item.</i></html>");
        lblNotice.setForeground(new Color(100, 70, 20));
        form.add(lblNotice, g);
        row++;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSubmitClaim = new JButton("Submit Claim for Review");
        btnSubmitClaim.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSubmitClaim.setBackground(new Color(30, 110, 200));
        btnSubmitClaim.setForeground(Color.WHITE);
        btnSubmitClaim.addActionListener(e -> handleClaimSubmit());

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearClaimForm());

        btnPanel.add(btnClear);
        btnPanel.add(btnSubmitClaim);

        rootPanel.add(new JScrollPane(form), BorderLayout.CENTER);
        rootPanel.add(btnPanel, BorderLayout.SOUTH);

        return rootPanel;
    }

    private void updateClaimItemPreview() {
        String id = txtClaimItemId.getText().trim();
        if (id.isEmpty()) {
            lblClaimItemPreview.setText("Please enter an Item ID.");
            lblClaimItemPreview.setForeground(Color.RED);
            return;
        }

        Item item = recoveryService.getItemById(id);
        if (item == null) {
            lblClaimItemPreview.setText("Item not found. Please verify the ID.");
            lblClaimItemPreview.setForeground(Color.RED);
        } else if (!item.isFound()) {
            lblClaimItemPreview.setText("Target item is marked LOST. Claims can only be filed on FOUND items.");
            lblClaimItemPreview.setForeground(Color.RED);
        } else if (item.isResolved()) {
            lblClaimItemPreview.setText("Item is already RESOLVED / Claimed.");
            lblClaimItemPreview.setForeground(Color.RED);
        } else {
            lblClaimItemPreview.setText("Target Found: [" + item.getItemId() + "] " + item.getTitle() + " at " + item.getLocation());
            lblClaimItemPreview.setForeground(new Color(0, 120, 40));
        }
    }

    private void handleClaimSubmit() {
        String itemId = txtClaimItemId.getText().trim();
        String name = txtClaimantName.getText().trim();
        String contact = txtClaimantContact.getText().trim();
        String answer = txtClaimAnswer.getText().trim();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        Claim newClaim = new Claim(
                null, // Generated by service
                itemId,
                name,
                contact,
                answer,
                "PENDING",
                today,
                ""
        );

        try {
            recoveryService.submitClaim(newClaim);
            JOptionPane.showMessageDialog(this,
                    "Claim successfully submitted!\nClaim Reference: " + newClaim.getClaimId() +
                            "\nStatus: PENDING administrative verification.",
                    "Claim Submitted", JOptionPane.INFORMATION_MESSAGE);
            clearClaimForm();
            refreshAllData();
            setStatusBar("Claim " + newClaim.getClaimId() + " submitted for item " + itemId + ".");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearClaimForm() {
        txtClaimItemId.setText("");
        lblClaimItemPreview.setText("Enter an Item ID (e.g. ITM-101) and click 'Check Item'.");
        lblClaimItemPreview.setForeground(new Color(60, 60, 100));
        txtClaimantName.setText("");
        txtClaimantContact.setText("");
        txtClaimAnswer.setText("");
    }

    // ==========================================
    // TAB 4: REVIEW CLAIMS (DEMO ADMIN)
    // ==========================================

    private JPanel createReviewPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout(8, 8));
        rootPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Notice Header
        JPanel noticePanel = new JPanel(new BorderLayout(6, 4));
        noticePanel.setBackground(new Color(254, 243, 205));
        noticePanel.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel lblNotice = new JLabel("<html><b>DEMONSTRATION ADMIN PANEL:</b> In production, this verification interface " +
                "would be restricted to authorized campus staff via role-based access control.</html>");
        lblNotice.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblNotice.setForeground(new Color(133, 100, 4));
        noticePanel.add(lblNotice, BorderLayout.CENTER);

        // Filter combo
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterRow.setOpaque(false);
        filterRow.add(new JLabel("Show:"));
        cmbClaimFilter = new JComboBox<>(new String[]{"All Claims", "Pending Only", "Accepted Only", "Rejected Only"});
        cmbClaimFilter.addActionListener(e -> refreshClaimTable());
        filterRow.add(cmbClaimFilter);
        noticePanel.add(filterRow, BorderLayout.EAST);

        rootPanel.add(noticePanel, BorderLayout.NORTH);

        // Split Pane: Upper is Claims Table, Lower is Comparison & Decision Box
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45);

        // Table
        String[] columns = {"Claim ID", "Item ID", "Claimant Name", "Contact", "Status", "Date", "Review Notes"};
        claimTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        claimTable = new JTable(claimTableModel);
        claimTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        claimTable.setRowHeight(22);
        claimTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateClaimComparison();
            }
        });
        splitPane.setTopComponent(new JScrollPane(claimTable));

        // Verification & Decision Box
        JPanel decisionBox = new JPanel(new BorderLayout(6, 6));
        decisionBox.setBorder(BorderFactory.createTitledBorder("Two-Party Verification & Review Decision"));

        lblReviewItemSummary = new JLabel("Select a claim above to verify details.");
        lblReviewItemSummary.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblReviewItemSummary.setBorder(new EmptyBorder(4, 6, 4, 6));
        decisionBox.add(lblReviewItemSummary, BorderLayout.NORTH);

        // Comparison Panel (Founder Clue vs Claimant Answer)
        JPanel comparisonGrid = new JPanel(new GridLayout(1, 2, 8, 4));

        // Left: Founder's Clue
        JPanel founderPanel = new JPanel(new BorderLayout(4, 4));
        founderPanel.setBorder(BorderFactory.createTitledBorder("1. Founder's Confidential Clue (Recorded at Report)"));
        txtComparisonFounderClue = new JTextArea(4, 20);
        txtComparisonFounderClue.setEditable(false);
        txtComparisonFounderClue.setLineWrap(true);
        txtComparisonFounderClue.setWrapStyleWord(true);
        txtComparisonFounderClue.setBackground(new Color(245, 247, 250));
        founderPanel.add(new JScrollPane(txtComparisonFounderClue), BorderLayout.CENTER);
        comparisonGrid.add(founderPanel);

        // Right: Claimant's Answer
        JPanel claimantPanel = new JPanel(new BorderLayout(4, 4));
        claimantPanel.setBorder(BorderFactory.createTitledBorder("2. Claimant's Submitted Answer (Ownership Proof)"));
        txtComparisonClaimantAnswer = new JTextArea(4, 20);
        txtComparisonClaimantAnswer.setEditable(false);
        txtComparisonClaimantAnswer.setLineWrap(true);
        txtComparisonClaimantAnswer.setWrapStyleWord(true);
        txtComparisonClaimantAnswer.setBackground(new Color(245, 247, 250));
        claimantPanel.add(new JScrollPane(txtComparisonClaimantAnswer), BorderLayout.CENTER);
        comparisonGrid.add(claimantPanel);

        decisionBox.add(comparisonGrid, BorderLayout.CENTER);

        // Decision Controls
        JPanel actionControls = new JPanel(new BorderLayout(6, 6));
        actionControls.setBorder(new EmptyBorder(6, 4, 4, 4));

        JPanel notesPanel = new JPanel(new BorderLayout(6, 2));
        notesPanel.add(new JLabel("Admin Review Remarks:"), BorderLayout.WEST);
        txtAdminReviewNotes = new JTextField();
        notesPanel.add(txtAdminReviewNotes, BorderLayout.CENTER);
        actionControls.add(notesPanel, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btnAcceptClaim = new JButton("Accept Claim (Verify & Resolve Item)");
        btnAcceptClaim.setBackground(new Color(25, 135, 84));
        btnAcceptClaim.setForeground(Color.WHITE);
        btnAcceptClaim.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAcceptClaim.setEnabled(false);
        btnAcceptClaim.addActionListener(e -> handleReviewDecision(true));

        btnRejectClaim = new JButton("Reject Claim");
        btnRejectClaim.setBackground(new Color(220, 53, 69));
        btnRejectClaim.setForeground(Color.WHITE);
        btnRejectClaim.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRejectClaim.setEnabled(false);
        btnRejectClaim.addActionListener(e -> handleReviewDecision(false));

        btnRow.add(btnRejectClaim);
        btnRow.add(btnAcceptClaim);
        actionControls.add(btnRow, BorderLayout.SOUTH);

        decisionBox.add(actionControls, BorderLayout.SOUTH);

        splitPane.setBottomComponent(decisionBox);
        rootPanel.add(splitPane, BorderLayout.CENTER);

        return rootPanel;
    }

    private void refreshClaimTable() {
        claimTableModel.setRowCount(0);
        String filter = (String) cmbClaimFilter.getSelectedItem();

        List<Claim> allClaims = recoveryService.getAllClaims();
        for (Claim c : allClaims) {
            if ("Pending Only".equals(filter) && !c.isPending()) continue;
            if ("Accepted Only".equals(filter) && !c.isAccepted()) continue;
            if ("Rejected Only".equals(filter) && !c.isRejected()) continue;

            claimTableModel.addRow(new Object[]{
                    c.getClaimId(),
                    c.getItemId(),
                    c.getClaimantName(),
                    c.getClaimantContact(),
                    c.getClaimStatus(),
                    c.getSubmissionDate(),
                    c.getReviewNotes()
            });
        }
    }

    private void updateClaimComparison() {
        int row = claimTable.getSelectedRow();
        if (row < 0) {
            lblReviewItemSummary.setText("Select a claim above to verify details.");
            txtComparisonFounderClue.setText("");
            txtComparisonClaimantAnswer.setText("");
            txtAdminReviewNotes.setText("");
            btnAcceptClaim.setEnabled(false);
            btnRejectClaim.setEnabled(false);
            return;
        }

        String claimId = (String) claimTableModel.getValueAt(row, 0);
        Claim claim = recoveryService.getClaimById(claimId);
        if (claim == null) return;

        Item item = recoveryService.getItemById(claim.getItemId());
        if (item != null) {
            lblReviewItemSummary.setText("Reviewing Claim [" + claim.getClaimId() + "] for Item [" +
                    item.getItemId() + "] " + item.getTitle() + " | Current Status: " + claim.getClaimStatus());
            txtComparisonFounderClue.setText(item.getPrivateClue().isEmpty()
                    ? "[No private clue was entered for this item.]"
                    : item.getPrivateClue());
        } else {
            lblReviewItemSummary.setText("Reviewing Claim [" + claim.getClaimId() + "] | [Item not found]");
            txtComparisonFounderClue.setText("[Item record missing]");
        }

        txtComparisonClaimantAnswer.setText(claim.getOwnershipAnswer());
        txtAdminReviewNotes.setText(claim.getReviewNotes());

        // Buttons enabled only if claim is PENDING
        boolean isPending = claim.isPending();
        btnAcceptClaim.setEnabled(isPending);
        btnRejectClaim.setEnabled(isPending);
    }

    private void handleReviewDecision(boolean accept) {
        int row = claimTable.getSelectedRow();
        if (row < 0) return;

        String claimId = (String) claimTableModel.getValueAt(row, 0);
        String remarks = txtAdminReviewNotes.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + (accept ? "ACCEPT" : "REJECT") + " claim " + claimId + "?" +
                        (accept ? "\nThis will mark the item as RESOLVED and reject any competing pending claims." : ""),
                "Confirm Review Decision", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            recoveryService.reviewClaim(claimId, accept, remarks);
            JOptionPane.showMessageDialog(this,
                    "Claim " + claimId + " has been marked " + (accept ? "ACCEPTED" : "REJECTED") + ".",
                    "Decision Recorded", JOptionPane.INFORMATION_MESSAGE);
            refreshAllData();
            setStatusBar("Claim " + claimId + " marked " + (accept ? "ACCEPTED" : "REJECTED") + ".");
        }
    }

    // ==========================================
    // STATUS & REFRESH
    // ==========================================

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(6, 2));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(4, 6, 4, 6)));

        lblStatusBar = new JLabel("System Ready.");
        lblStatusBar.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JButton btnRefresh = new JButton("Reload Storage Data");
        btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnRefresh.addActionListener(e -> {
            recoveryService.reload();
            refreshAllData();
            setStatusBar("Data successfully reloaded from local storage.");
        });

        bar.add(lblStatusBar, BorderLayout.WEST);
        bar.add(btnRefresh, BorderLayout.EAST);
        return bar;
    }

    public void setStatusBar(String message) {
        lblStatusBar.setText(message + " [" + LocalDate.now() + "]");
    }

    public void refreshAllData() {
        List<Item> allItems = recoveryService.getAllItems();
        itemTableModel.setItems(allItems);
        refreshClaimTable();

        // Update stats badge
        long totalItems = allItems.size();
        long openFound = allItems.stream().filter(i -> i.isFound() && !i.isResolved()).count();
        long pendingClaims = recoveryService.getAllClaims().stream().filter(Claim::isPending).count();

        lblStatsBadge.setText("Total Items: " + totalItems + " | Open Found: " + openFound + " | Pending Claims: " + pendingClaims);
        updateBrowseDetails();
        updateClaimComparison();
    }
}
