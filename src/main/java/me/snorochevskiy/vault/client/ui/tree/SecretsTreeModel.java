package me.snorochevskiy.vault.client.ui.tree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class SecretsTreeModel implements TreeModel {

    private SecretNodeVaultHelper secretNodeVaultHelper;

    // JTree$TreeModelhandler
    // BasicTreeUI$Handler
    private List<TreeModelListener> listeners = new ArrayList<>();

    private SecretNode root = null;

    public SecretsTreeModel(SecretNodeVaultHelper secretNodeVaultHelper) {
        this.secretNodeVaultHelper = secretNodeVaultHelper;
    }

    @Override
    public Object getRoot() {
        if (root == null) {
            root = new SecretNode(secretNodeVaultHelper.getVaultClient().getRoot(), null);
            root.setHasChildren(true);
        }
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        SecretNode secretNode = (SecretNode) parent;
        return secretNode.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        SecretNode secretNode = (SecretNode) parent;

        if (secretNode.getChildren() == null) {
            secretNode.setChildren(secretNodeVaultHelper.fetchChildren(secretNode));
        }

        int count = secretNode.getChildren().size();
        return count;
    }

    @Override
    public boolean isLeaf(Object node) {
        SecretNode secretNode = (SecretNode) node;
        return !secretNode.isHasChildren();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void addDraft(SecretNode parent, String secret) {
        SecretNode newNode = new SecretNode(secret, parent);
        newNode.setDraft(true);
        parent.addChild(newNode);

        TreePath path = new TreePath(parent);
        listeners.forEach(l -> l.treeNodesChanged(new TreeModelEvent(parent, path)));
    }
}
