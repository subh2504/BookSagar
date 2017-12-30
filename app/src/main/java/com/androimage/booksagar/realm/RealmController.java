package com.androimage.booksagar.realm;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.androimage.booksagar.model.Book;

import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the BuyNSell istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Book.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    public RealmResults<Book> getBooks() {

        return realm.where(Book.class).findAll();
    }

    //query a single item with the given id
    public Book getBook(Integer id) {

        return realm.where(Book.class).equalTo("id", id).findFirst();
    }

    //check if Book.class is empty
    public boolean hasBooks() {

        return !realm.allObjects(Book.class).isEmpty();
    }


    //query example
    public RealmResults<Book> queryedBooks(String author, String title) {

        return realm.where(Book.class)
                .contains("author", author, false)
                .or()
                .contains("title", title, false)
                .findAll();

    }

    public RealmResults<Book> filter(Map<String, List<Pair<String, Boolean>>> filterCollections) {
        List<Pair<String, Boolean>> language = filterCollections.get("Language");
        List<Pair<String, Boolean>> author = filterCollections.get("Author");

        List<Pair<String, Boolean>> standard = filterCollections.get("Standard");
        List<Pair<String, Boolean>> type = filterCollections.get("Type");
        List<Pair<String, Boolean>> location = filterCollections.get("Location");


        RealmQuery<Book> query = realm.where(Book.class);
        int i = 0;
        if (language.size() == 0) {
            // We want to return an empty list if the list of ids is empty.
            // Just search for an id that does not exist.
            query = query.equalTo("language", "qqqqq");
        } else {

            for (Pair<String, Boolean> l : language) {
                // The or() operator requires left hand and right hand elements.
                // If articleIds had only one element then it would crash with
                // "Missing right-hand side of OR"

                if (l.second) {
                    if (i++ > 0) {
                        query = query.or();
                    }
                    query = query.equalTo("language", l.first);
                }

            }
        }


        if (author.size() == 0) {
            // We want to return an empty list if the list of ids is empty.
            // Just search for an id that does not exist.
            query = query.equalTo("author", "qqqqq");
        } else {

            for (Pair<String, Boolean> a : author) {
                // The or() operator requires left hand and right hand elements.
                // If articleIds had only one element then it would crash with
                // "Missing right-hand side of OR"

                if (a.second) {
                    if (i++ > 0) {
                        query = query.or();
                    }
                    query = query.equalTo("author", a.first);
                }

            }
        }


        if (standard.size() == 0) {
            // We want to return an empty list if the list of ids is empty.
            // Just search for an id that does not exist.
            query = query.equalTo("standard", "qqqqq");
        } else {

            for (Pair<String, Boolean> s : standard) {
                // The or() operator requires left hand and right hand elements.
                // If articleIds had only one element then it would crash with
                // "Missing right-hand side of OR"

                if (s.second) {
                    if (i++ > 0) {
                        query = query.or();
                    }
                    query = query.equalTo("standard", s.first);
                }

            }
        }
        if (type.size() == 0) {
            // We want to return an empty list if the list of ids is empty.
            // Just search for an id that does not exist.
            query = query.equalTo("type", "qqqqq");
        } else {

            for (Pair<String, Boolean> s : type) {
                // The or() operator requires left hand and right hand elements.
                // If articleIds had only one element then it would crash with
                // "Missing right-hand side of OR"

                if (s.second) {
                    if (i++ > 0) {
                        query = query.or();
                    }
                    query = query.equalTo("type", s.first);
                }

            }
        }
        if (location.size() == 0) {
            // We want to return an empty list if the list of ids is empty.
            // Just search for an id that does not exist.
            query = query.equalTo("location", "qqqqq");
        } else {

            for (Pair<String, Boolean> s : location) {
                // The or() operator requires left hand and right hand elements.
                // If articleIds had only one element then it would crash with
                // "Missing right-hand side of OR"

                if (s.second) {
                    if (i++ > 0) {
                        query = query.or();
                    }
                    query = query.equalTo("location", s.first);
                }

            }
        }
        return query.findAll();

    }

    public RealmResults<Book> myBooks(String userid) {

        return realm.where(Book.class)
                .equalTo("seller_id", Integer.parseInt(userid))
                .findAll();

    }
}
