package me.snorochevskiy.vault.client.lib;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class VaultClient {

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private VaultConfigs vaultConfigs;

    private String vaultToken = null;

    private Map<String, List<String>> secretsCache = new HashMap<>();
    private Map<String, Map<String, Object>> secretPairsCache = new HashMap<>();

    public VaultClient(VaultConfigs vaultConfigs) {
        this.vaultConfigs = vaultConfigs;
    }

    public void setVaultConfigs(VaultConfigs vaultConfigs) {
        this.vaultConfigs = vaultConfigs;

        vaultToken = null;
        secretsCache.clear();
        secretPairsCache.clear();
    }

    public String getVaultToken() {
        return vaultToken;
    }

    public void setVaultToken(String vaultToken) {
        this.vaultToken = vaultToken;
    }

    public boolean isLoggedIn() {
        return vaultToken != null;
    }

    /**
     * curl -k -X POST \
     *   https://vault.somewhere.com:8200/v1/auth/github_MYORG/login \
     *   -H 'content-type: application/json' \
     *   -d '{ "token": "774152b34b20afea4d2529b351483d46171425a1" }'
     */
    public synchronized String login() {
        // TODO: change signature. null in case of success is not very java way
        LoginRequest request = new LoginRequest();
        request.setToken(vaultConfigs.getGitToken());

        String responseText = null;
        try {
            responseText = HttpClient.post(vaultConfigs.getVaultUrl() + "/v1/auth/" + vaultConfigs.getOrganization() + "/login",
                    map("Content-Type", "application/json"),
                    GSON.toJson(request)
            );
            VaultResponse vaultResponse = GSON.fromJson(responseText, VaultResponse.class);
            vaultToken = vaultResponse.getAuth().getClientToken();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * curl -k -X GET \
     *   'https://vault.somewhere.com:8200/v1/secret/rootnode?list=true' \
     *   -H 'x-vault-token: dff4e29d-fa51-fdad-dda6-43a4db4f86de'
     * @param parentSecret
     * @return
     */
    public List<String> listSecrets(String parentSecret) {
        System.out.println("List secret: " + parentSecret);

        if (secretsCache.containsKey(parentSecret)) {
            return secretsCache.get(parentSecret);
        }

        loginIfRequired();

        Supplier<String> call = () -> HttpClient.get(vaultConfigs.getVaultUrl() + "/v1/secret/" + parentSecret + "?list=true",
                map("x-vault-token", vaultToken));

        String responseText;
        try {
            responseText = call.get();
        } catch (AuthException e) {
            login();
            responseText = call.get();
        }

        List<String> secrets;
        if (responseText == null) {
            secrets = Collections.emptyList();
        } else {
            VaultResponse<SecretsList> response =
                    GSON.fromJson(responseText, new TypeToken<VaultResponse<SecretsList>>(){}.getType());
            secrets = response.getData().getKeys();
        }

        secretsCache.put(parentSecret, secrets);

        return secrets;
    }

    public List<String> listRoot() {
        String rootSecret = vaultConfigs.getRooSecret(); // Is it always in lower case?
        return listSecrets(rootSecret);
    }

    public String getRoot() {
        return vaultConfigs.getRooSecret();
    }

    public Map<String,Object> read(String secret) {
        System.out.println("Read secret: " + secret);

        if (secretPairsCache.containsKey(secret)) {
            return secretPairsCache.get(secret);
        }

        loginIfRequired();

        Supplier<String> call = () -> HttpClient.get(vaultConfigs.getVaultUrl() + "/v1/secret/" + secret,
                map("x-vault-token", vaultToken));

        String responseText;
        try {
            responseText = call.get();
        } catch (AuthException e) {
            login();
            responseText = call.get();
        }

        Map<String,Object> pairs;
        if (responseText == null) {
            pairs = Collections.emptyMap();
        } else {
            VaultResponse<Map<String,Object>> response =
                    GSON.fromJson(responseText, new TypeToken<VaultResponse<Map<String,Object>>>(){}.getType());
            pairs = response.getData();
        }

        secretPairsCache.put(secret, pairs);

        return pairs;
    }

    /**
     * curl -k -X PUT \
     *   https://vault.somewhere.com:8200/v1/secret/rootnode/tstkey \
     *   -H 'content-type: application/json' \
     *   -H 'x-vault-token: c3fcff11-ed65-efe7-fb8a-902af4a6ac0e' \
     *   -d '{
     *  "password": "ololo"
     * }'
     *
     * @param secret
     * @param pairs
     */
    public void write(String secret, Map<String,Object> pairs) {
        loginIfRequired();

        Supplier<String> call = () -> HttpClient.put(vaultConfigs.getVaultUrl() + "/v1/secret/" + secret,
                map("x-vault-token", vaultToken), GSON.toJson(pairs));

        try {
            call.get(); // response with 204
            secretPairsCache.put(secret, pairs);
        } catch (AuthException e) {
            login();
            call.get();
            secretPairsCache.put(secret, pairs);
        }
    }

    private void loginIfRequired() {
        if (vaultToken == null) {
            login();
        }
    }

    private Map<String, String> map(String key, String value) {
        Map<String, String> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }
}
