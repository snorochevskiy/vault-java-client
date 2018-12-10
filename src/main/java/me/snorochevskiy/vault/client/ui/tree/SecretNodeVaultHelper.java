package me.snorochevskiy.vault.client.ui.tree;

import me.snorochevskiy.vault.client.lib.VaultClient;
import me.snorochevskiy.vault.client.ui.table.SecretEntry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Stateful service
 */
public class SecretNodeVaultHelper {

    private Supplier<VaultClient> vaultClientProvider;

    public SecretNodeVaultHelper(Supplier<VaultClient> vaultClientProvider) {
        this.vaultClientProvider = vaultClientProvider;
    }

    public VaultClient getVaultClient() {
        return vaultClientProvider.get();
    }

    public List<SecretNode> fetchChildren(SecretNode parentNode) {
        List<String> childrenNames = vaultClientProvider.get().listSecrets(parentNode.fullName());

        LinkedHashMap<String, SecretNode> childrenMap = new LinkedHashMap<>();

        for (String name : childrenNames) {
            boolean isDirectory = name.endsWith("/");
            String nodeName = isDirectory
                    ? name.substring(0, name.length() - 1)
                    : name;
            SecretNode node = childrenMap.computeIfAbsent(nodeName, k -> new SecretNode(nodeName, parentNode));

            if (isDirectory) {
                node.setHasChildren(true);
            }
        }

        return childrenMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public SecretNode fillNodeEntries(SecretNode node) {
        if (node.getEntries() != null) {
            return node;
        }
        String fullPath = node.fullName();
        Map<String,Object> pairs = vaultClientProvider.get().read(fullPath);

        List<SecretEntry> secretEntries = pairs.entrySet().stream()
                .map(e -> new SecretEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        node.setEntries(secretEntries);
        return node;
    }
}
