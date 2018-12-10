package me.snorochevskiy.vault.client.ui;

import me.snorochevskiy.vault.client.lib.VaultClient;
import me.snorochevskiy.vault.client.lib.VaultConfigs;
import me.snorochevskiy.vault.client.lib.VaultConfigsStorage;
import me.snorochevskiy.vault.client.lib.properties.PropertiesFilePatcher;
import me.snorochevskiy.vault.client.ui.table.PairsTableModel;
import me.snorochevskiy.vault.client.ui.table.VaultValueType;
import me.snorochevskiy.vault.client.ui.tree.SecretNode;
import me.snorochevskiy.vault.client.ui.tree.SecretNodeVaultHelper;
import me.snorochevskiy.vault.client.ui.tree.SecretsTreeModel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainWindow extends JFrame {

    private JButton connectButton = new JButton("Connect");;

    private JTree secretsTree;
    private JTable pairsTable = new JTable();
    private JButton addPairButton = new JButton("Add");
    private JButton editPairButton = new JButton("Edit");
    private JButton deletePairButton = new JButton("Delete");
    private JButton writePairsButton = new JButton("Write to Vault");

    private JLabel statusLabel = new JLabel("Status: Not connected");

    private VaultConfigsWindow vaultConfigsWindow;

    private VaultConfigs vaultConfigs;
    private VaultClient vaultClient;
    private SecretNodeVaultHelper secretNodeVaultHelper = new SecretNodeVaultHelper(()-> vaultClient);

    public MainWindow() {

        initState();

        initMenu();
        initToolBar();
        initControls();

        this.setTitle("Vault UI client");
        this.setSize(640, 480);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initState() {
        vaultConfigs = VaultConfigsStorage.read();
        vaultClient = new VaultClient(vaultConfigs);

        vaultConfigsWindow = new VaultConfigsWindow(this, (modifiedConfig)-> {
            vaultConfigs = modifiedConfig;
            vaultClient = new VaultClient(vaultConfigs);
            VaultConfigsStorage.write(vaultConfigs);
        });
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        {
            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu);

            JMenuItem exitMenuItem = new JMenuItem("Exit");
            exitMenuItem.addActionListener((i) -> System.exit(0));
            fileMenu.add(exitMenuItem);
        }

        {
            JMenu secretMenu = new JMenu("Secret");
            menuBar.add(secretMenu);
            JMenuItem patchPropertiesFileMenuItem = new JMenuItem("Patch properties file");
            patchPropertiesFileMenuItem.addActionListener(i -> {
                if (pairsTable.getModel() == null || !(pairsTable.getModel() instanceof PairsTableModel)) {
                    JOptionPane.showMessageDialog(this, "No secret selected");
                    return;
                }
                PairsTableModel model = (PairsTableModel)pairsTable.getModel();
                JFileChooser fc = new JFileChooser();
                int res = fc.showOpenDialog(this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    try {
                        PropertiesFilePatcher.patch(fc.getSelectedFile().getAbsolutePath(), model.getPairs());
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            });
            secretMenu.add(patchPropertiesFileMenuItem);
        }
        {
            JMenu toolsMenu = new JMenu("Tools");
            menuBar.add(toolsMenu);

            JMenuItem logViewerMenuItem = new JMenuItem("Log viewer");
            logViewerMenuItem.addActionListener((i) -> JOptionPane.showMessageDialog(this, "Not implemented"));
            toolsMenu.add(logViewerMenuItem);
        }

        {
            JMenu settingsMenu = new JMenu("Settings");
            menuBar.add(settingsMenu);

            JMenuItem vaultConfigsMenuItem = new JMenuItem("Vault configuration...");
            vaultConfigsMenuItem.addActionListener(i -> {
                vaultConfigsWindow.setVaultConfigs(vaultConfigs);
                vaultConfigsWindow.setVisible(true);
                });
            settingsMenu.add(vaultConfigsMenuItem);
        }

        this.setJMenuBar(menuBar);
    }

    private void initToolBar() {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        connectButton.addActionListener(e -> {
            new ConnectWorker().execute();
        });

        toolBarPanel.add(connectButton);

        this.getContentPane().add(toolBarPanel, BorderLayout.NORTH);
    }

    private void initControls() {

        // components
        secretsTree = initSecretsTree();

        addPairButton.addActionListener(e -> {
            PairEditWindow pairEditWindow = new PairEditWindow(this, null);
            pairEditWindow.setVisible(true);
            if (pairEditWindow.getRow() != null) {
                PairsTableModel model = (PairsTableModel)pairsTable.getModel();
                model.addRow(pairEditWindow.getRow());
            }
        });
        editPairButton.addActionListener(e -> {
            int selectedRow = pairsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "No row selected");
                return;
            }
            PairEditWindow pairEditWindow = new PairEditWindow(this,
                    ((PairsTableModel)pairsTable.getModel()).getRow(selectedRow));
            pairEditWindow.setVisible(true);
            if (pairEditWindow.getRow() != null) {
                PairsTableModel model = (PairsTableModel)pairsTable.getModel();
                model.setRow(pairEditWindow.getRow(), selectedRow);
            }
        });
        deletePairButton.addActionListener(e -> {
            int selectedRow = pairsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "No row selected");
                return;
            }
            if (pairsTable.getSelectedRow() >= 0) {
                PairsTableModel model = (PairsTableModel) pairsTable.getModel();
                model.removeRow(pairsTable.getSelectedRow());
            }
        });
        writePairsButton.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Write changes to Vault", "Confirm write to Vault", JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.YES_NO_CANCEL_OPTION) {
                return;
            }
            new WriteToVaultWorker().execute();
        });

        // layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(5);
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        {
            JScrollPane treePanel = new JScrollPane(secretsTree);
            treePanel.setBorder(new EmptyBorder(0, 10, 0, 5));
            treePanel.setMinimumSize(new Dimension(200,200));
            splitPane.setLeftComponent(treePanel);
        }
        {
            JPanel secretInfoPanel = new JPanel(new BorderLayout());
            secretInfoPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
            {
                JScrollPane tablePane = new JScrollPane(pairsTable);
                pairsTable.setFillsViewportHeight(true);
                secretInfoPanel.add(tablePane, BorderLayout.CENTER);
            }
            {
                JPanel pairsTableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                pairsTableButtonsPanel.add(addPairButton);
                pairsTableButtonsPanel.add(editPairButton);
                pairsTableButtonsPanel.add(deletePairButton);
                pairsTableButtonsPanel.add(writePairsButton);
                secretInfoPanel.add(pairsTableButtonsPanel, BorderLayout.SOUTH);
            }

            splitPane.setRightComponent(secretInfoPanel);
        }

        {
            JPanel statusPanel = new JPanel();
            statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            statusPanel.setPreferredSize(new Dimension(this.getWidth(), 28));
            statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
            statusPanel.add(statusLabel);

            getContentPane().add(statusPanel, BorderLayout.SOUTH);
        }

        //splitPane.setDividerLocation(0.5);
        Container centralPanel = this.getContentPane();
        centralPanel.add(splitPane);

        enableControls(false);
        connectButton.setEnabled(true);
    }

    private JTree initSecretsTree() {
        secretsTree = new JTree(new DefaultTreeModel(null));
        secretsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        secretsTree.addTreeSelectionListener(e -> new LoadSelectWorker().execute());
        secretsTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int x = e.getX();
                    int y = e.getY();
                    JTree tree = (JTree)e.getSource();
                    TreePath path = tree.getPathForLocation(x, y);
                    if (path == null)
                        return;

                    tree.setSelectionPath(path);

                    SecretNode obj = (SecretNode)path.getLastPathComponent();

                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem addSecretMenuItem = new JMenuItem("Add");
                    addSecretMenuItem.addActionListener(me -> {
                        String newSecretName = JOptionPane.showInputDialog(this, "Specify new secret name");
                        if (newSecretName != null) {
                            SecretsTreeModel model = (SecretsTreeModel) secretsTree.getModel();
                            model.addDraft(obj, newSecretName);
                            secretsTree.updateUI();
                        }
                    });
                    popup.add(addSecretMenuItem);
                    popup.show(tree, x, y);
                }
            }
        });
        //secretsTree.setShowsRootHandles(true);

        return secretsTree;
    }

    private void updateTable(SecretNode node) {
        PairsTableModel model = new PairsTableModel(node.getEntries());
        pairsTable.setModel(model);

        // TODO: delete
        TableColumn typeColumn = pairsTable.getColumnModel().getColumn(1);
        JComboBox<String> comboBox = new JComboBox();
        comboBox.addItem(VaultValueType.STRING.name());
        comboBox.addItem(VaultValueType.LIST.name());
        comboBox.addItem(VaultValueType.MAP.name());
        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));

    }

    public static class  SecretsTreeModelListener implements TreeModelListener {
        @Override
        public void treeNodesChanged(TreeModelEvent e) { }
        @Override
        public void treeNodesInserted(TreeModelEvent e) { }
        @Override
        public void treeNodesRemoved(TreeModelEvent e) { }
        @Override
        public void treeStructureChanged(TreeModelEvent e) { }
    }

    private VaultClient getVaultClient() {
        return vaultClient;
    }

    private void enableControls(boolean enable) {
        connectButton.setEnabled(enable);
        secretsTree.setEnabled(enable);
        pairsTable.setEnabled(enable);
        addPairButton.setEnabled(enable);
        editPairButton.setEnabled(enable);
        deletePairButton.setEnabled(enable);
        writePairsButton.setEnabled(enable);
    }

    private class ConnectWorker extends SwingWorker<String, Void> {

        @Override
        protected String doInBackground() throws Exception {
            statusLabel.setText("Connecting to vault...");
            enableControls(false);
            return getVaultClient().login();
        }

        @Override
        public void done() {
            try {
                String error = get();
                if (error!=null) {
                    JOptionPane.showMessageDialog(MainWindow.this, error, "Vault login error", JOptionPane.WARNING_MESSAGE);
                    statusLabel.setText("Unable to connect to Vault");
                }
                enableControls(true);
                statusLabel.setText("Connected to " + vaultConfigs.getVaultUrl());
                secretsTree.setModel(new SecretsTreeModel(secretNodeVaultHelper));
                secretsTree.updateUI();
            } catch (InterruptedException | ExecutionException e) {
                statusLabel.setText("Connection to Vault interrupter");
                enableControls(true);
            }
        }
    }

    private class LoadSelectWorker extends SwingWorker<SecretNode, Void> {
        @Override
        protected SecretNode doInBackground() throws Exception {
            MainWindow.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SecretNode node = (SecretNode)secretsTree.getLastSelectedPathComponent();
//            String fullPath = node.fullName();
//            return vaultClient.read(fullPath);

            return secretNodeVaultHelper.fillNodeEntries(node);
        }
        @Override
        public void done() {
            try {
                SecretNode node = get();
                updateTable(node);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                MainWindow.this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private class WriteToVaultWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            enableControls(false);
            PairsTableModel model = (PairsTableModel) pairsTable.getModel();
            SecretNode currentSecret = (SecretNode) secretsTree.getLastSelectedPathComponent();
            System.out.println(currentSecret);
            vaultClient.write(currentSecret.fullName(), model.getPairs());
            enableControls(true);
            return null;
        }
    }


}
