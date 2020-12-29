package com.example.httplibrary.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ConscryptSSLProvider {
    static final HiLogLabel label=new HiLogLabel(HiLog.ERROR,0x00101,"网络请求");
    public static void install(){
        try {
//            Security.insertProviderAt(Conscrypt.newProviderBuilder().build(),1);
        }catch (NoClassDefFoundError ex){
            HiLog.error(label, "","java.lang.NoClassDefFoundError: org.conscrypt.Conscrypt, Please add org.conscrypt.Conscrypt to your dependency");
        }

    }
}
