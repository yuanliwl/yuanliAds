package com.yuanli.pangolin.constants;

import com.yuanli.base.BaseAdBean;

public class PangolinAdBean extends BaseAdBean {

    private String bannerId;

    private String newInsertId;
    private final String appName;

    public PangolinAdBean(String appName, String appId,  String splashId, String insertId, String rewardId) {
        super(appId, splashId, insertId, rewardId);
        this.appName = appName;
    }

    public PangolinAdBean(String appName, String appId,  String splashId, String insertId, String rewardId,String bannerId) {
        super(appId, splashId, insertId, rewardId);
        this.bannerId = bannerId;
        this.appName = appName;
    }

    public PangolinAdBean(String appName, String appId,  String splashId, String insertId, String rewardId,String bannerId, String newInsertId) {
        super(appId, splashId, insertId, rewardId);
        this.bannerId = bannerId;
        this.newInsertId = newInsertId;
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public String getBannerId() {
        return bannerId;
    }

    public String getNewInsertId() {
        return newInsertId;
    }
}
