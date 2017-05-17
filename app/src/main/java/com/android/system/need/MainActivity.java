package com.android.system.need;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //setContentView(null);
//        Intent intent = new Intent(this,MyService.class);
//        startService(intent);
       //checkPermission();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent intent = new Intent(this,MyService.class);
//        startService(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        Intent intent = new Intent(this,MyService.class);
//        startService(intent);
    }


//    private void checkPermission( ){
//
//        PermissionGen.with(this)
//                .addRequestCode(100)
//                .permissions(
//                        "android.permission.READ_SMS",
//                        "android.permission.READ_PHONE_STATE")
//                .request();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//
//    }
//
//    @PermissionSuccess(requestCode = 100)
//    public void doSomething(){
//        Log.i("###","--PermissionSuccess--");
//    }
//
//    @PermissionFail(requestCode = 100)
//    public void doFailSomething(){
//        Log.i("###","--PermissionFail--");
//    }
}
