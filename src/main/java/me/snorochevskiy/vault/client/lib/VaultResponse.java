package me.snorochevskiy.vault.client.lib;


import java.util.List;

/**
 * {
 *   "request_id":"16f284b4-2ebb-25ee-e0ec-b0968279ae6c",
 *   "lease_id":"",
 *   "renewable":false,
 *   "lease_duration":0,
 *   "data":null,
 *   "wrap_info":null,
 *   "warnings":null,
 *   "auth":{
 *     "client_token":"e6bcd6f5-2371-b0ae-6855-32ed7757ccd5",
 *     "accessor":"a4ca00cd-b184-e412-17c5-5f4c6f92e3d7",
 *     "policies":["default","myorg_role1","myorg_approle1"],
 *     "token_policies":["default","myorg_role1","myorg_approle1"],
 *     "metadata":{"org":"MYORG","username":"mylogin"},
 *     "lease_duration":600,
 *     "renewable":true,
 *     "entity_id":"74027c74-a529-30f1-818c-63bb89aa89b5"
 *   }
 * }
 */
public class VaultResponse<T> {

    private String requestId;
    private String leaseId;
    private boolean renewable;
    private int leaseDuration;
    private String wrapInfo;
    private String warnings;

    private Auth auth;
    private T data;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }

    public boolean isRenewable() {
        return renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }

    public int getLeaseDuration() {
        return leaseDuration;
    }

    public void setLeaseDuration(int leaseDuration) {
        this.leaseDuration = leaseDuration;
    }

    public String getWrapInfo() {
        return wrapInfo;
    }

    public void setWrapInfo(String wrapInfo) {
        this.wrapInfo = wrapInfo;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class Auth {
        private String clientToken;

        public String getClientToken() {
            return clientToken;
        }

        public void setClientToken(String clientToken) {
            this.clientToken = clientToken;
        }
    }
}


class SecretsList {
    private List<String> keys;

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
