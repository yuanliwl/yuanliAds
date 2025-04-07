测试第一版
建在广告初始化在application：
//穿山甲广告
PangolinAdBean pangolinAdBean = new PangolinAdBean(应用名称,广告代码位);
//电商广告
MentaBean mentaBean = new MentaBean("", "", "", "", "");
//百度广告
BaiduBean baiduBean = new BaiduBean("","","");
//快手广告
KsBean ksBean = new KsBean("", "",);
//优量汇广告
YlhBean ylhBean = new YlhBean("", "");
//穿山甲
TTAdManagerHolder.init(application.getApplicationContext(), pangolinAdBean);
MentaAdManagerHold.init(application, mentaBean);
BaiduManagerHolder.init(application,baiduBean);
KsManagerHolder.init(application,ksBean);
YlhManagerHolder.init(application,ylhBean);
