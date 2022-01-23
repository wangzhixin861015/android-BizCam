package com.bcnetech.hyphoto.data;

/**
 * 登录发送信息
 */
public class LoginInfoData {

    /**
     * username 	是 	用户名
     * userid 	是 	聚商宝号
     * avatar 	是 	头像
     * realname 	是 	用户真实姓名
     * nickname 	是 	昵称
     * certificate 	是 	{1: '未认证', 2: '认证中', 3: '认证成功', 4: '违规', 5: '认证失败', 6: '锁定中'}
     * QR 	是 	二维码信息
     * url 	是 	用户主页url
     * workspace_id 	是 	编号
     * ttl 	是 	最近一次登录时间
     * tokenid 	是 	会话ID
     * method 	是 	加密方式
     * secret 	是 	密钥
     * workspace_name 	是 	用户名
     */

    private boolean isLogined;// 表示是否处于登录状态
    private String login;//注册手机号
    private String password;// 密码
    private String deviceName;

    private String source;//	来源 {1: '商拍', 2: '浏览器', 3: '聚商宝', 4: '企客'}
    private String system;//系统 {1: 'ios', 2: 'android', 3: 'windows'}
    private String  apparatus ;//	设备 {1: '手机', 2: '平板电脑', 3: '个人电脑'}
    private String version_number;//	软件版本
    private String  appid;//对于APP登录是必须的 app_user_id 微门店实例编号

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getSource() {
        return source;
    }

    public String getSystem() {
        return system;
    }

    public String getApparatus() {
        return apparatus;
    }

    public String getVersion_number() {
        return version_number;
    }

    public String getAppid() {
        return appid;
    }


    public void setLogined(boolean logined) {
        isLogined = logined;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setApparatus(String apparatus) {
        this.apparatus = apparatus;
    }

    public void setVersion_number(String version_number) {
        this.version_number = version_number;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
