package me.snorochevskiy.vault.client.lib;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * These test work with Vault service of one company.
 * And they work with real credentials so, obviously I cannot keep them in public git repo.
 */
@Ignore
public class VaultClientInnerTest {

    private static String vaultUrl;
    private static String organization;
    private static String gitToken;

    @BeforeClass
    public static void loadCreds() throws IOException {
        Properties creds = new Properties();
        creds.load(new FileInputStream("test_creds.properties"));
        vaultUrl = creds.getProperty("vault-url");
        organization = creds.getProperty("organization");
        gitToken = creds.getProperty("git-token");
    }

    @Test
    public void testLogin() {
        VaultConfigs config = VaultConfigs.forGithub(vaultUrl, gitToken, organization);

        VaultClient sut = new VaultClient(config);

        sut.login();

        Assert.assertNotNull(sut.getVaultToken());
        System.out.println(sut.getVaultToken());
    }

    @Test
    public void testList() {
        VaultConfigs config = VaultConfigs.forGithub(vaultUrl, gitToken, organization);

        VaultClient sut = new VaultClient(config);

        List<String> list = sut.listSecrets(organization.toLowerCase());

        Assert.assertNotNull(list);
        System.out.println(list);
    }

    @Test
    public void testListNested() {
        VaultConfigs config = VaultConfigs.forGithub(vaultUrl, gitToken, organization);

        VaultClient sut = new VaultClient(config);

        List<String> list = sut.listSecrets(organization.toLowerCase() + "/test_secret");

        Assert.assertNotNull(list);
        System.out.println(list);
    }

    @Test
    public void testListRoot() {
        VaultConfigs config = VaultConfigs.forGithub(vaultUrl, gitToken, organization);

        VaultClient sut = new VaultClient(config);

        List<String> list = sut.listRoot();

        Assert.assertNotNull(list);
        System.out.println(list);
    }

    @Test
    public void testRead() {
        VaultConfigs config = VaultConfigs.forGithub(vaultUrl, gitToken, organization);

        VaultClient sut = new VaultClient(config);

        Map<String,Object> pairs = sut.read(organization.toLowerCase() + "/test_secret");

        Assert.assertNotNull(pairs);
        System.out.println(pairs);
    }
}
