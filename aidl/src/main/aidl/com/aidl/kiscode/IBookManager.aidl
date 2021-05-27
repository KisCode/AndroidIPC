// IBookManager.aidl
package com.aidl.kiscode;

import com.aidl.kiscode.Book;
// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();

    //oneway 异步执行
    oneway void addBook(in Book book);
}