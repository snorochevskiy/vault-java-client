package me.snorochevskiy.vault.client.ui.tree;

import me.snorochevskiy.vault.client.ui.table.SecretEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecretNode {

    private String name;
    private SecretNode parent;
    private boolean draft;
    private boolean hasChildren;
    private List<SecretNode> children;
    private List<SecretEntry> entries;

    public SecretNode() {

    }

    public SecretNode(String name, SecretNode parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SecretNode getParent() {
        return parent;
    }

    public void setParent(SecretNode parent) {
        this.parent = parent;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public List<SecretNode> getChildren() {
        return children;
    }

    public void setChildren(List<SecretNode> children) {
        this.children = children;
    }

    public void addChild(SecretNode child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        hasChildren = true;
    }

    public List<SecretEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<SecretEntry> entries) {
        this.entries = entries;
    }

    public Map<String, Object> exportEntries() {
        return entries.stream()
                .collect(Collectors.toMap(SecretEntry::getName, SecretEntry::getValue));
    }

    public void importEntries(Map<String, Object> pairs) {
        this.entries = pairs.entrySet().stream()
                .map(e -> new SecretEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public String fullName() {
        return parent == null ? name : parent.fullName() + "/" + name;
    }


    @Override
    public String toString() {
        return name;
    }

}
