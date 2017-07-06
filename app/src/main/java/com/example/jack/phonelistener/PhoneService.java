package com.example.jack.phonelistener;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class PhoneService extends Service {
    private long id;
    private boolean isOffhook;

    public boolean isOffhook() {
        return isOffhook;
    }

    public void setOffhook(boolean offhook) {
        isOffhook = offhook;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public class PhoneBinder extends Binder {
        public String doSomething() {
            return "something is done!";
        }
//        public void setId(long id) {
////            this.id = id;
//        }
//        public long getId() {
//            return this.id;
//        }
        public PhoneService getService() {
            return PhoneService.this;
        }
    }

    private PhoneBinder mBinder = new PhoneBinder();

    public PhoneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }
}
