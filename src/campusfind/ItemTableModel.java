package campusfind;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Swing TableModel for rendering items in the public browsing interface.
 * 
 * Software Engineering Note:
 * This model strictly implements Information Hiding / Data Privacy.
 * It provides only public item attributes to the view table and completely
 * omits the private identifying clue, ensuring sensitive verification clues
 * are never leaked to public table viewers.
 */
public class ItemTableModel extends AbstractTableModel {

    private final String[] columnNames = {
            "Item ID",
            "Type",
            "Title",
            "Category",
            "Public Description",
            "Location",
            "Date Reported",
            "Contact",
            "Status"
    };

    private List<Item> items;

    public ItemTableModel() {
        this.items = new ArrayList<>();
    }

    public ItemTableModel(List<Item> items) {
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void setItems(List<Item> newItems) {
        this.items = (newItems != null) ? new ArrayList<>(newItems) : new ArrayList<>();
        fireTableDataChanged();
    }

    public Item getItemAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < items.size()) {
            return items.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Read-only public browsing table
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= items.size()) {
            return null;
        }

        Item item = items.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getItemId();
            case 1: return item.getItemType();
            case 2: return item.getTitle();
            case 3: return item.getCategory();
            case 4: return item.getPublicDescription();
            case 5: return item.getLocation();
            case 6: return item.getReportDate();
            case 7: return item.getContactInfo();
            case 8: return item.getItemStatus();
            default: return "";
        }
        // Notice: item.getPrivateClue() is intentionally NEVER returned here!
    }
}
