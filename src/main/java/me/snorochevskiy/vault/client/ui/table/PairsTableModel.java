package me.snorochevskiy.vault.client.ui.table;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PairsTableModel extends AbstractTableModel {

    private static final Gson GSON = new Gson();

    private List<SecretEntry> rows;

    public PairsTableModel(List<SecretEntry> rows) {
        this.rows = rows;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: return "Name";
            case 1: return "Type";
            case 2: return "Value";
            default: return "Unknown";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            // name is always editable
            return true;
        } else if (columnIndex == 1) {
            // type column is calculated
            return false;
        }

        Class valueType = rows.get(rowIndex).getValue().getClass();

        return String.class.equals(valueType);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return rows.get(rowIndex).getName();
            case 1: return rowType(rows.get(rowIndex).getValue()).name();
            case 2: return valToString(rows.get(rowIndex).getValue());
            default: return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SecretEntry row = rows.get(rowIndex);

        if (columnIndex == 0) {
            row.setName((String)aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
            return;
        }
    }

    public void addRow(SecretEntry row) {
        rows.add(row);
        fireTableRowsInserted(rows.size()-1, rows.size()-1);
    }

    public SecretEntry getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    public void setRow(SecretEntry row, int rowIndex) {
        rows.set(rowIndex, row);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void removeRow(int rowIndex) {
        rows.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public Map<String, Object> getPairs() {
        return rows.stream()
                .collect(Collectors.toMap(SecretEntry::getName, SecretEntry::getValue));
    }

    public static VaultValueType rowType(Object value) {
        if (value instanceof String) {
            return VaultValueType.STRING;
        } else if (value instanceof List) {
            return VaultValueType.LIST;
        } else if (value instanceof Map) {
            return VaultValueType.MAP;
        } else {
            return VaultValueType.STRING;
        }
    }

    public static String valToString(Object value) {
        if (value instanceof String) {
            return (String)value;
        }
        Gson gson = new Gson();
        return gson.toJson(value);
    }

    public static Object valFromString(String str, VaultValueType type) {
        if (type == VaultValueType.MAP) {
            return GSON.fromJson(str, LinkedTreeMap.class);
        } else if (type == VaultValueType.LIST) {
            return GSON.fromJson(str, ArrayList.class);
        }
        return str;
    }

}
