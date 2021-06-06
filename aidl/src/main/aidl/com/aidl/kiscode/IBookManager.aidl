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