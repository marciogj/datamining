package br.com.senior.research.gpstracker.tracking.services.model;

/**
 * Created by marcio.jasinski on 13/11/2015.
 *
 *
 *
 */
public class TrackedIdentity {
    private String tenantId;
    private String deviceId;
    private String userId;
    private String username;
    private String email;

    public TrackedIdentity() {}

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
