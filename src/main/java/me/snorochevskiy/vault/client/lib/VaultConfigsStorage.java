package me.snorochevskiy.vault.client.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class VaultConfigsStorage {

    public static final String VAULT_UI_DIR = System.getProperty("user.home") + File.separator + ".vault_ui";
    public static final String CONFIGS_FILE = VAULT_UI_DIR + File.separator + "configs.properties";

    private static final String PROPERTY_VAULT_URL = "vault-url";
    private static final String PROPERTY_ROOT_SECRET = "root-secret";
    private static final String PROPERTY_GITHUB_TOKEN = "github-token";
    private static final String PROPERTY_GITHUB_ORGANIZATION = "github-organization";

    public static void initStorage() {
        File vaultUiHomeDir = new File(VAULT_UI_DIR);
        if (!vaultUiHomeDir.exists()) {
            vaultUiHomeDir.mkdir();
        }
    }

    public static VaultConfigs read() {
        initStorage();
        Properties storage = new Properties();
        try {
            storage.load(new FileInputStream(CONFIGS_FILE));

            VaultConfigs vc = new VaultConfigs();
            vc.setVaultUrl(storage.getProperty(PROPERTY_VAULT_URL));
            vc.setRooSecret(storage.getProperty(PROPERTY_ROOT_SECRET));
            vc.setGitToken(storage.getProperty(PROPERTY_GITHUB_TOKEN));
            vc.setOrganization(storage.getProperty(PROPERTY_GITHUB_ORGANIZATION));
            return vc;
        } catch (IOException e) {
            e.printStackTrace();
            VaultConfigs vc = new VaultConfigs();
            vc.setVaultUrl("");
            vc.setRooSecret("");
            vc.setGitToken("");
            vc.setOrganization("");
            return vc;
        }
    }

    public static void write(VaultConfigs vc) {
        Properties storage = new Properties();
        storage.setProperty(PROPERTY_VAULT_URL, vc.getVaultUrl());
        storage.setProperty(PROPERTY_ROOT_SECRET, vc.getRooSecret());
        storage.setProperty(PROPERTY_GITHUB_TOKEN, vc.getGitToken());
        storage.setProperty(PROPERTY_GITHUB_ORGANIZATION, vc.getOrganization());

        initStorage();
        try {
            storage.store(new FileOutputStream(CONFIGS_FILE), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
