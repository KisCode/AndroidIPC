package com.kiscode.binder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.List;

/***
 * 自行实现AIDL生产代码，使用Binder跨进程通信
 */
public interface IBookManager extends android.os.IInterface {

    List<Book> getBookList() throws RemoteException;

    void addBook(Book book) throws RemoteException;

    abstract class BookManagerImpl extends Binder implements IBookManager {

        static final int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;
        static String DESCRIPTOR = "com.kiscode.binder.IBookManager";

        public BookManagerImpl() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /***
         * 将binder对象转为 约定接口IBookManager类型的对象
         * @param binder binder对象
         * @return
         */
        public static IBookManager asInterface(IBinder binder) {
            if ((binder == null)) {
                return null;
            }
            //查询本地binder对象
            IInterface iin = binder.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IBookManager))) {
                return ((IBookManager) iin);
            }
            return new BookManagerImpl.Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION:
                    reply.writeString(descriptor);
                    return true;
                case TRANSACTION_addBook:
                    data.enforceInterface(descriptor);
                    Book book = null;
                    if (0 != data.readInt()) {
                        //反序列化出Book对象
                        book = Book.CREATOR.createFromParcel(data);
                    }
                    this.addBook(book);

                    //Parcel队头写入“无异常“
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getBookList:
                    data.enforceInterface(descriptor);
                    List<Book> bookList = this.getBookList();
                    reply.writeNoException();
                    //将bookList进行序列化后 传输
                    reply.writeTypedList(bookList);
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IBookManager {
            private IBinder mRemote;

            public Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override
            public List<Book> getBookList() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                List<Book> bookList;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_getBookList, data, reply, 0);
                    reply.readException();
                    bookList = reply.createTypedArrayList(Book.CREATOR);
                } finally {
                    data.recycle();
                    reply.recycle();
                }
                return bookList;
            }

            @Override
            public void addBook(Book book) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();

                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    if (book != null) {
                        data.writeInt(1);
                        book.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    //异步写入
//                    mRemote.transact(TRANSACTION_addBook, data, reply, IBinder.FLAG_ONEWAY);
                    mRemote.transact(TRANSACTION_addBook, data, reply, 0);
                    reply.readException();
                } finally {
                    data.recycle();
                    reply.recycle();
                }
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }
        }
    }
}
