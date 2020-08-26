package com.example.tximaudiodemo.audiocall;

/**
 * 作者: 王亚鹏
 * 时间: 2020/8/24 0024
 * 类描述:
 */
public class TXContents {
    public static final int APP_ID = 0;//您的APPID

    //角色1 主叫
    public static final String sign = GenerateTestUserSig.genTestUserSig("1");
    public static final String userId = "1";
    public static final String userImg = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1959779733,683430928&fm=26&gp=0.jpg";
    //角色2 被叫
    public static final String sign2 = GenerateTestUserSig.genTestUserSig("2");
    public static final String userId2 = "2";
    public static final String userImg2 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598415787834&di=711052678e05ce87b6766ff4bec958ab&imgtype=0&src=http%3A%2F%2Fww4.sinaimg.cn%2Fbmiddle%2F6910ab7bgw1egloghsfi3j20b40b40t6.jpg";
}
