package me.snorochevskiy.vault.client.ui;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import me.snorochevskiy.vault.client.ui.table.SecretEntry;
import me.snorochevskiy.vault.client.ui.table.PairsTableModel;
import me.snorochevskiy.vault.client.ui.table.VaultValueType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PairEditWindow extends JDialog {

    private static final Gson GSON = new Gson();

    private JTextField nameFiled = new JTextField();
    private JComboBox<String> typeField = new JComboBox<>();
    private JTextArea valueField = new JTextArea();

    private JButton okButton = new JButton("Ok");

    private SecretEntry row = null;

    public PairEditWindow(JFrame owner, SecretEntry row) {
        super(owner, true);
        setSize(320,240);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);

        typeField.addItem(VaultValueType.STRING.name());
        typeField.addItem(VaultValueType.LIST.name());
        typeField.addItem(VaultValueType.MAP.name());

        if (row == null) {
            this.setTitle("Add new value");
            nameFiled.setText("Name");
            valueField.setText("");
        } else {
            this.setTitle("Edit value");
            nameFiled.setText(row.getName());
            String value = row.getValue() instanceof String
                    ? (String) row.getValue()
                    : GSON.toJson(row.getValue());
            valueField.setText(value);
            typeField.setSelectedItem(PairsTableModel.rowType(row.getValue()).name());
        }

        okButton.addActionListener(evt -> {
            if (nameFiled.getText() == null || nameFiled.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (VaultValueType.LIST.name().equals(typeField.getSelectedItem())) {
                    this.row = new SecretEntry(nameFiled.getText(), GSON.fromJson(valueField.getText(), ArrayList.class));
                } else if (VaultValueType.MAP.name().equals(typeField.getSelectedItem())) {
                    this.row = new SecretEntry(nameFiled.getText(), GSON.fromJson(valueField.getText(), LinkedTreeMap.class));
                } else {
                    this.row = new SecretEntry(nameFiled.getText(), valueField.getText());
                }
                this.setVisible(false);
            } catch (JsonSyntaxException ex) {
                JOptionPane.showMessageDialog(this, "Malformed value:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        initControls();
    }

    private void initControls() {

        // layout
        Container container = this.getContentPane();
        container.setLayout(new MigLayout(
                "",//"debug",
                "[fill,max]",
                "[min][min][min][min][min][top,fill,max][min]"));

        container.add(new JLabel("Name:"), "cell 0 0");
        container.add(nameFiled, "cell 0 1");
        container.add(new JLabel("Type:"), "cell 0 2");
        container.add(typeField, "cell 0 3");
        container.add(new JLabel("Value:"), "cell 0 4");
        container.add(valueField, "cell 0 5");
        container.add(okButton, "cell 0 6");

    }

    public SecretEntry getRow() {
        return row;
    }
}
