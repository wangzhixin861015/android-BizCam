package com.bcnetech.bcnetchhttp.bean.response;

/**
 * Created by a1234 on 16/11/28.
 */

public class LoginResponseData {
    private String userid;
    private String phonenumber;
    private String password;
    private String avatar;
    private String nickname;//昵称
    private String serverSectet;//返回的服务器密钥
    private String clientSecret;//返回的客户端密钥
    private String token;//返回的tokenid
    private String workspaceId;//返回的workspaceid
    private String type;//账号类型1个人账号，2企业账号，4企业子账号


    //国际化新加自段
    private String workspace;
    //国际化新加自段（二次登录需要）
    private String jwt;

    public String getUserid() {
        return userid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSecret() {
        return serverSectet;
    }


    public String getTokenid() {
        return token;
    }


    public String getWorkspaceid() {
        return workspaceId;
    }

    public void setWorkspaceid(String workspaceid) {
        this.workspaceId = workspaceid;
    }

    public String getServerSectet() {
        return serverSectet;
    }

    public void setServerSectet(String serverSectet) {
        this.serverSectet = serverSectet;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    @Override
    public String toString() {
        return "LoginReceiveData{" +
                "userid='" + userid + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", useName='" + nickname + '\'' +
                ", secret='" + serverSectet + '\'' +
                ", tokenid='" + token + '\'' +
                ", workspace_id='" + workspaceId + '\'' +
                '}';
    }
}

