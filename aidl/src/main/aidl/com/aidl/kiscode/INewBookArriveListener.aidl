// INewBookArriveListener.aidl
package com.aidl.kiscode;

// Declare any non-default types here with import statements
import com.aidl.kiscode.Book;

interface INewBookArriveListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onArriveNewBook(in Book book);
}