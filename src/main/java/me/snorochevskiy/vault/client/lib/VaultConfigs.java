package me.snorochevskiy.vault.client.lib;

public class VaultConfigs {

    private String vaultUrl;
    private String rooSecret;

    private String gitToken;
    private String organization;

    public static VaultConfigs forGithub(String vaultUrl, String rooSecret, String gitToken, String organization) {
        VaultConfigs config = new VaultConfigs();
        config.vaultUrl = vaultUrl;
        config.rooSecret = rooSecret;
        config.gitToken = gitToken;
        config.organization = organization;
        return config;
    }

    public String getVaultUrl() {
        return vaultUrl;
    }

    public void setVaultUrl(String vaultUrl) {
        this.vaultUrl = vaultUrl;
    }

    public String getRooSecret() {
        return rooSecret;
    }

    public void setRooSecret(String rooSecret) {
        this.rooSecret = rooSecret;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
