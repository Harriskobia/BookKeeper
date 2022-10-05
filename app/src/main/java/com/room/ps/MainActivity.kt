package com.room.ps

import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(), BookListAdapter.OnDeleteClickListener {

    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val bookListAdapter = BookListAdapter(this, this)
        recyclerview.adapter = bookListAdapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener { view ->
            val intent = Intent(this, NewBookActivity::class.java)
            startActivityForResult(intent, NEW_BOOK_ACTIVITY_REQUEST_CODE)
        }

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel::class.java)

        bookViewModel.allBooks.observe(this, androidx.lifecycle.Observer { books ->
            books?.let {
                bookListAdapter.setBooks(books)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_BOOK_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val id = UUID.randomUUID().toString()
            val authorName = data!!.getStringExtra(NewBookActivity.NEW_AUTHOR)
            val bookName = data.getStringExtra(NewBookActivity.NEW_BOOK)
            val description = data.getStringExtra(NewBookActivity.NEW_DESCRIPTION)
            val currentTime = Calendar.getInstance().time

            val book = Book(id, authorName, bookName, description, currentTime)

            bookViewModel.insert(book)

            Toast.makeText(applicationContext, R.string.saved, Toast.LENGTH_LONG).show()

        } else if (requestCode == UPDATE_BOOK_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val id = data!!.getStringExtra(EditBookActivity.ID)
            val authorName = data.getStringExtra(EditBookActivity.UPDATED_AUTHOR)
            val bookName = data.getStringExtra(EditBookActivity.UPDATED_BOOK)
            val description = data.getStringExtra(EditBookActivity.UPDATED_DESCRIPTION)
            val currentTime = Calendar.getInstance().time

            val book = Book(id!!, authorName, bookName, description, currentTime)

            // Code to update
            bookViewModel.update(book)

            Toast.makeText(applicationContext, R.string.updated, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, R.string.not_saved, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDeleteClickListener(myBook: Book) {
        bookViewModel.delete(myBook)
        Toast.makeText(applicationContext, R.string.deleted, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as android.widget.SearchView
//        val searchView = menu.findItem(R.id.search).actionView as SearchView

        // Setting the SearchResultActivity to show the result
        val componentName = ComponentName(this, SearchResultActivity::class.java)
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView.setSearchableInfo(searchableInfo)

        return true
    }

    companion object {
        private const val NEW_BOOK_ACTIVITY_REQUEST_CODE = 1
        const val UPDATE_BOOK_ACTIVITY_REQUEST_CODE = 2
    }
}
