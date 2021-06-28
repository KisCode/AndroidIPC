
# AIDL使用详解
1. aidl申明（客户端和服务端） 语法
- 支持参数类型
- in out inout onway
2. 远程回调RemoteCallbackList
3. 线程问题
4. Binder死亡监听
5. 权限校验
6. AIDL流程分析
7. Binder连接池
8. 手写AIDL


# 1. AIDL概述
> AIDL (Android Interface Define LanguAge )即Android接口定义语言,可以根据.aidl文件内声明客户端和服务端通信的接口,从而实现跨进程调用；AIDL 底层通过Binder实现跨进程通信。
## 1.1 AIDL在Android中使用流程
1. 声明AIDL接口
2. 服务端Service实现AIDL接口方法
3. 客户端通过绑定Service,将服务端返回的Binder对象转化为AIDL接口所属的类型对象，通过该对象调用AIDL接口方法，从而实现远程调用；

## 1.2 AIDL具体应用
首先，我们通过一个简易跨进程图书管理系统来初步学习AIDL的应用，具体使用细节我们在后面逐个讲解
### 1.2.1 AIDL 接口声明
1. 声明一个图书管理类接口，有getBookList,addBook两个接口方法
```
// Book.aidl
package com.aidl.kiscode;

// Declare any non-default types here with import statements
parcelable Book;
```
```
// IBookManager.aidl
package com.aidl.kiscode;

import com.aidl.kiscode.Book;
// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();

    //oneway 异步执行
    oneway void addBook(in Book book);

}
```
### 1.2.2 实现服务端Service，并实现AIDL方法
1. 创建一个远程服务BookManagerService，并在AndroidManifest.xml中通过android:process指定了设置Service的进程名称
```xml
<service
    android:name=".BookManagerService"
    android:enabled="true"
    android:exported="true"
    android:process=":remote" />
```

2. 在BookManagerService中负责AIDL接口方法的具体实现
由于编译器会通过根据AIDL接口生成的IBookManager.Stub的Binder抽象类，我们在BookManagerService中实例化IBookManager.Stub对象mBookManger，并在Service的onBind方法 返回mBookManger对象；

```java
package com.aidl.kiscode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private IBookManager.Stub mBookManger = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.i(TAG, "getBookList in " + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.i(TAG, "addBook in " + Thread.currentThread().getName()); try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBookList.add(book);
        }
    };

    public BookManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate " + Thread.currentThread().getName());

        doWork();
    }

    /***
     * 模拟耗时操作 10秒后自动结束
     */
    private void doWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                stopSelf();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mBookManger;
    }
}
```

### 1.2.3 客户端进行AIDL远程调用
此时我们的服务端Service已经完成，客户端已Activity为例对BookManagerService进行远程调用；
- 在Activity内通过bindService启动绑定远程BookManagerService

``` .java
Intent intent = new Intent(this, BookManagerService.class);
startService(intent);
bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

```

- 在Service绑定成功后，即在ServiceConnection的onServiceConnected方法内可获取服务端Binder对象，并通过Stub.asInterface方法将服务端Binder对象转化为客户端所需的AIDL接口类型对象 mBookManager

```java
private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //此处service为服务端与客户端通信的桥梁
        //Stub.asInterface方法将服务端Binder对象转化为客户端所需的AIDL接口类型对象
        mBookManager = IBookManager.Stub.asInterface(service);
        try {
            mBookManager.addBook(new Book(1, "Android", 99.9));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
};
```

- 客户端通过AIDL类型对象mBookManager调用其getBookList,addBook方法
```java
List<Book> bookList = mBookManager.getBookList();
```

```java
Random random = new Random();
    int id = random.nextInt();
    double price = random.nextDouble() * 100;
    Book book = new Book(id, "NewBook" + id, price);
    try {
        Log.i(TAG, "addBook in " + Thread.currentThread().getName());
        mBookManager.addBook(book);
        Toast.makeText(this, "Add  " + book.getName(), Toast.LENGTH_SHORT).show();
    } catch (RemoteException e) {
        e.printStackTrace();
    }
```


以上我们完成了一个AIDL跨进程通信的图书管理系统，从大体明白了AIDL的使用流程，具体使用细节请接着往下看；


# 2. AIDL使用详解
## 2.1 AIDL数据类型
### 2.1.1 AIDL仅支持以下6种数据类型
- java基本数据类型（int、long、float、double、boolean、char）
- 字符串类型：String 和 CharSequence
- List：只支持ArrayList
- Map: 只支持HashMap
- Parcelable: 所有实现了Parcelable接口的对象
- AIDL: 所有AIDL接口本身

### 2.1.2  AIDL数据类型使用中的注意事项
1. 如果AIDL中使用了自定义Parcelabel对象，需要新建一个同名的AIDL文件。如AIDL需实现了Parcelable的Book对象，需要创建一个book.aidl文件

```
// Book.aidl
package com.aidl.kiscode;

// Declare any non-default types here with import statements
parcelable Book;
```
2. 如果AIDL中使用了自定义Parcelabel对象或AIDL文件，需要通过import 引入进来
```
package com.aidl.kiscode;

//通过import显示引入自定义Parcelabel对象或AIDL文件
import com.aidl.kiscode.Book;
// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
}
```
3. AIDL方法除了基本数据类型，其他类型的参数需标明方向：in/out/inout
- in 表示输入型参数,客户端数据对象流向服务端，并且服务端对该数据对象的修改不会影响客户端段
- out 表示输出型参数，数据对象由服务端流向客户端，客户端传递的数据对象此时服务端收到的对象内容为空，服务端可以对该数据对象修改，并传给客户端
- inout 表示输入输出型参数，以上两种数据流向的结合体


## 2.2 跨进程回调RemoteCallBackList
当我们试图通过aidl方法添加一个回调函数时，发现回调并没有生效。其原因是对象是无法跨进程的，跨进程方法调用参数传递的本质是反序列化过程，当客户端跨进程向服务端传递一个回调参数时，首先会将该对象序列化后再传向服务端，服务端接收再通过反序列化出一个新的回调参数。</br>
RemoteCallBackList便是官方提供的解决跨进程通信的接口，RemoteCallBackList支持任意AIDL接口

```java
public class RemoteCallbackList<E extends IInterface>
```
1. 声明远程回调接口aidl
```
// INewBookArriveListener.aidl
package com.aidl.kiscode;

// Declare any non-default types here with import statements
import com.aidl.kiscode.Book;

interface INewBookArriveListener {
    void onArriveNewBook(in Book book);
}
```
2. 在IBookManager中添加注册与反注册接口
```
// IBookManager.aidl
package com.aidl.kiscode;

import com.aidl.kiscode.Book;
import com.aidl.kiscode.INewBookArriveListener;
// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();

    //oneway 异步执行
    oneway void addBook(in Book book);

    void registerBookArriveListener(in INewBookArriveListener listener);
    void unRegisterBookArriveListener(in INewBookArriveListener listener);
}
```

3. 服务端Service实现远程回调注册与反注册
```java
private RemoteCallbackList<INewBookArriveListener> mListenerList = new RemoteCallbackList<>();
private IBookManager.Stub mBookManger = new IBookManager.Stub() {
    //此处省略部分其他业务实现 getBookList/addBook
    @Override
    public void registerBookArriveListener(INewBookArriveListener listener) throws RemoteException {
        Log.i(TAG, "register " + listener.asBinder());
        mListenerList.register(listener);
    }

    @Override
    public void unRegisterBookArriveListener(INewBookArriveListener listener) throws RemoteException {
        Log.i(TAG, "unRegister  " + listener.asBinder());
        mListenerList.unregister(listener);
    }
};
```

4. 服务端Service实现具体回调通知，
RemoteCallBackList对象在使用中必须beginBroadcast()和finishBroadcast()配套使用

```java
private void onNewBookArrive(Book book) {
    int count = mListenerList.beginBroadcast();
    for (int i = 0; i < count; i++) {
        INewBookArriveListener listener = mListenerList.getBroadcastItem(i);
        try {
            Log.i(TAG, "onNewBookArrive " + listener.asBinder());
            listener.onArriveNewBook(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    mListenerList.finishBroadcast();
}
```

5. 客户端注册远程回调
```
INewBookArriveListener.Stub bookArriveListener = new INewBookArriveListener.Stub() {
        @Override
        public void onArriveNewBook(Book book) throws RemoteException {
            Log.i(TAG, "onArriveNewBook at index " + i);
        }
    };
    try {
        mBookManager.registerBookArriveListener(bookArriveListener);
    } catch (RemoteException e) {
        e.printStackTrace();
    }
```


## 2.3 跨进程线程使用
前面已经提到，AIDL跨进程通讯底层是Binder实现的，其线程使用需注意以下3点：
- 在服务端Service内的业务方法均是在Binder线程池管理下的线程中运行，所以开发服务端Service时无需额外开启线程去运行;
- 在客户端调用服务端远程方法运行在主线程，会挂起等待服务端方法执行完毕，此时如果如果服务端方法是耗时操作，可能导致客户端进程ANR，所以需尽量避免在客户端主线程调用远程耗时方法，或声明远程方法时通过onway关键字指明该方法为异步；

```
// IBookManager.aidl
package com.aidl.kiscode;

import com.aidl.kiscode.Book;

interface IBookManager {
    //oneway 异步执行
    oneway void addBook(in Book book);
}
```

## 2.4 进程死亡监听
服务端进程启动后，可能处于某些原因导致该进程被杀，此时我们可通过监听其死亡回调，并重新启动该进程。
### 2.4.1 通过给Binder设置DeathRecipient监听
当Binder死亡时，会收到binderDied()方法的回调，在binderDied()方法中进行重新连接 IBinder.linkToDeath(IBinder.DeathRecipient)

```
public interface DeathRecipient {
    public void binderDied();

    /**
     * @hide
     */
    default void binderDied(IBinder who) {
        binderDied();
    }
}
```

```java
//声明远程服务死亡监听
private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
    @Override
    public void binderDied() {
        Log.i(TAG, "remote process was kill,will reconnect");

        //重连
        mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
        mBookManager = null;
        connectService();
    }
};
```


```java
private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //此处service为服务端与客户端通信的桥梁
        //Stub.asInterface方法将服务端Binder对象转化为客户端所需的AIDL接口类型对象
        mBookManager = IBookManager.Stub.asInterface(service);
        try {
            mBookManager.addBook(new Book(1, "Android", 99.9));
            //死亡回调监听
            mBookManager.asBinder().linkToDeath(mDeathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
};
```

### 2.4.2 通过Service连接断开回调方法中重连
由于远程服务端运行在Service中，我们在客户端通过bindService启动远程服务，当远程服务端进程死亡，会在客户端触发ServiceConncetion的onServiceDisconnected回调方法。

```java
//bindService
Intent intent = new Intent(this, BookManagerService.class);
bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);


private ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBookManager = IBookManager.Stub.asInterface(service);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //TODO service断开连接，此处进行重连
    }
};
```

## 2.5 AIDL权限验证