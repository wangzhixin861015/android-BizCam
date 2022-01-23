package com.bcnetech.bcnetchhttp.bean.response;

import com.alibaba.fastjson.JSON;
import com.bcnetech.bcnetchhttp.config.PreferenceConstants;
import com.bcnetech.bcnetechlibrary.util.JsonUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;

import org.json.JSONObject;

/**
 * 登录成功的用户信息
 *
 * @author xuan
 */
public class LoginedUser {
    private static LoginedUser loginedUser;

    private static final String KEY_ISLOGINED = "isLogined";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOGINDATA = "loginData";
    private static final String KEY_ISONLYWIFI = "isonlywifi";
    private static final String KEY_NICK_NAME = "nickname";
    private static final String KEY_HISTORY = "history";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SUPPORT_C2 = "support_c2";
    private static final String KEY_MARKET_COBOX = "market_cobox";
    private static final String KEY_UPLOAD_TIME = "upload_time";
    private static final String KEY_UPLOAD_STATUS = "upload_status";
    private static final String KEY_JWT="jwt";

    private boolean isLogined;// 表示是否处于登录状态
    private boolean isonlywifi;//仅wifi
    private boolean supportCamera2;//是否支持Camera2
    private String user_name;// 登陆名,注册的手机号
    private String nickname;//昵称
    private String password;// 密码
    private String secret;//登陆成功后返回的密钥
    private String tokenid;//登陆成功后返回的tokenid
    private String workspaceid;//登陆成功后返回的workspaceid
    private LoginReceiveData loginData;//登陆成功返回结果
    private String avatar;//头像
    private String type;//账号类型1个人账号，2企业账号，4企业子账号
    private String select_market_cobox;
    private String uploadTime; //上传时间
    private int uploadStatus;  //上传状态
    private String jwt;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取登陆用户信息
     *
     * @return LoginedUser对象永不为空
     */
    public static LoginedUser getLoginedUser() {
        if (null == loginedUser) {
            // activity因内存不足被系统回收时候可能不存在已登录用户，需要恢复
            loginedUser = getLastLoginedUserInfo();
        }

        return loginedUser;
    }

    /**
     * 设置登陆用户信息，会保存到本地文件
     *
     * @param user
     */
    public static void setLoginedUser(LoginedUser user) {
        loginedUser = user;
        saveToFile();
    }

    private void setNickname() {
        String nickname = loginData.getNickname();
        if (nickname != null) {
            this.setNickname(nickname);
        }
    }

    /**
     * 同步一次内存的数据到本地
     */
    public static void saveToFile() {
        LoginedUser.setLastLoginUserInfo(loginedUser);
    }

    /**
     * 获取最近一次登录的用户信息(用Json的格式存放在文件中)
     *
     * @return
     */
    public static LoginedUser getLastLoginedUserInfo() {
        SharePreferences preferences = SharePreferences.instance();
        String temp = (String) preferences.getString(PreferenceConstants.LAST_LOGIN_USER_INFO, "{}");

        LoginedUser loginedUser = new LoginedUser();
        try {
            JSONObject obj = new JSONObject(temp);
            loginedUser.setIsonlywifi(JsonUtil.getBoolean(obj, KEY_ISONLYWIFI));
            loginedUser.setLogined(JsonUtil.getBoolean(obj, KEY_ISLOGINED));
            loginedUser.setUser_name(JsonUtil.getString(obj, KEY_USER_NAME));
            loginedUser.setPassword(JsonUtil.getString(obj, KEY_PASSWORD));
            loginedUser.setNickname(JsonUtil.getString(obj, KEY_NICK_NAME));
            loginedUser.setType(JsonUtil.getString(obj, KEY_TYPE));
            loginedUser.setSupportCamera2(JsonUtil.getBoolean(obj, KEY_SUPPORT_C2));
            loginedUser.setUploadStatus(JsonUtil.getInt(obj, KEY_UPLOAD_STATUS));
            loginedUser.setUploadTime(JsonUtil.getString(obj, KEY_UPLOAD_TIME));
            loginedUser.setJwt(JsonUtil.getString(obj,KEY_JWT));
            String loginDataStr = JsonUtil.getString(obj, KEY_LOGINDATA);

            if (!StringUtil.isBlank(loginDataStr)) {
                loginedUser.setLoginData(JSON.parseObject(loginDataStr, LoginReceiveData.class));
            }


            loginedUser.setTokenid(loginedUser.getLoginData().getTokenid());
            loginedUser.setSecret(loginedUser.getLoginData().getSecret());
            loginedUser.setWorkspaceid(loginedUser.getLoginData().getUserid());
            loginedUser.setSelect_market_cobox(JsonUtil.getString(obj, KEY_MARKET_COBOX));
            loginedUser.setIsonlywifi(JsonUtil.getBoolean(obj, KEY_ISONLYWIFI));


        } catch (Exception e) {
            LogUtil.error(e);
        }
        return loginedUser;
    }

    /**
     * 保存最近一次用户登录的信息(用Json的格式存放于文件中)
     */
    public static void setLastLoginUserInfo(LoginedUser loginedUser) {
        SharePreferences preferences = SharePreferences.instance();
        JSONObject obj = new JSONObject();
        try {
            obj.put(KEY_SUPPORT_C2, loginedUser.isSupportCamera2());
            obj.put(KEY_ISLOGINED, loginedUser.isLogined());
            obj.put(KEY_ISONLYWIFI, loginedUser.isonlywifi());
            obj.put(KEY_USER_NAME, loginedUser.getUser_name());
            obj.put(KEY_PASSWORD, loginedUser.getPassword());
            obj.put(KEY_NICK_NAME, loginedUser.getNickname());
            obj.put(KEY_TYPE, loginedUser.getType());
            obj.put(KEY_LOGINDATA, JSON.toJSON(loginedUser.getLoginData()));
//            if(!StringUtil.isBlank(loginedUser.getUploadTime())){
            obj.put(KEY_UPLOAD_TIME, loginedUser.getUploadTime());
            obj.put(KEY_JWT,loginedUser.getJwt());

//            }
//            if(!StringUtil.isBlank(loginedUser.isUpload())){
            obj.put(KEY_UPLOAD_STATUS, loginedUser.getUploadStatus());
//            }
            if (loginedUser.getSelect_market_cobox() != null)
                obj.put(KEY_MARKET_COBOX, loginedUser.getSelect_market_cobox());
            loginedUser.setNickname();
            preferences.putString(PreferenceConstants.LAST_LOGIN_USER_INFO, obj.toString());
        } catch (Exception e) {
            LogUtil.error(e);
        }
        LoginedUser.loginedUser = loginedUser;
    }


    /**
     * 打印用户信息
     */
    public void displayLog() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(KEY_ISLOGINED, loginedUser.isLogined());
            obj.put(KEY_ISONLYWIFI, loginedUser.isonlywifi());
            obj.put(KEY_USER_NAME, loginedUser.getUser_name());
            obj.put(KEY_PASSWORD, loginedUser.getPassword());
            obj.put(KEY_NICK_NAME, loginedUser.getNickname());
            obj.put(KEY_LOGINDATA, loginedUser.getLoginData().getAvatar());
            obj.put(KEY_TYPE, loginedUser.getType());
            LogUtil.debug(obj.toString());
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    public boolean isLogined() {
        return isLogined;
    }

    public void setLogined(boolean logined) {
        isLogined = logined;
    }

    public void quitLogin() {
        loginedUser.setTokenid("");
        loginedUser.setType("");
        loginedUser.setUploadTime("");
        loginedUser.setUploadStatus(0);
        loginedUser.setJwt("");
        loginedUser.setLoginData(null);
        setLastLoginUserInfo(loginedUser);
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public LoginReceiveData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginReceiveData loginData) {
        this.loginData = loginData;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getWorkspaceid() {
        return workspaceid;
    }

    public void setWorkspaceid(String workspaceid) {
        this.workspaceid = workspaceid;
    }

    public boolean isonlywifi() {
        return isonlywifi;
    }

    public void setIsonlywifi(boolean isonlywifi) {
        this.isonlywifi = isonlywifi;
    }

    public String getType() {
        return type;
    }

    public boolean isSupportCamera2() {
        return false /*supportCamera2*/;
    }

    public void setSupportCamera2(boolean supportCamera2) {
        this.supportCamera2 = supportCamera2;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getSelect_market_cobox() {
        return select_market_cobox;
    }

    public void setSelect_market_cobox(String select_market_cobox) {
        this.select_market_cobox = select_market_cobox;
    }

    public boolean isIsonlywifi() {
        return isonlywifi;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
