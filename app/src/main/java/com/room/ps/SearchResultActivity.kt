package com.room.ps

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class SearchResultActivity : AppCompatActivity(), BookListAdapter.OnDeleteClickListener {

	private lateinit var searchViewModel: SearchViewModel
	private var bookListAdapter: BookListAdapter? = null
	private val TAG = this.javaClass.simpleName

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)

		fab.visibility = View.INVISIBLE

		searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

		bookListAdapter = BookListAdapter(this, this)
		recyclerview.adapter = bookListAdapter
		recyclerview.layoutManager = LinearLayoutManager(this)

		handleIntent(intent)
	}

	private fun handleIntent(intent: Intent) {
		if (Intent.ACTION_SEARCH == intent.action) {

			val searchQuery = intent.getStringExtra(SearchManager.QUERY)

			Log.i(TAG, "Search Query is $searchQuery")

			searchViewModel.getBooksByBookOrAuthor("%$searchQuery%")?.observe(this, Observer { books ->
				books?.let { bookListAdapter!!.setBooks(books) }
			})
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == SearchResultActivity.UPDATE_BOOK_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			// Code to edit book
			val bookId = data!!.getStringExtra(EditBookActivity.ID)
			val authorName = data.getStringExtra(EditBookActivity.UPDATED_AUTHOR)
			val bookName = data.getStringExtra(EditBookActivity.UPDATED_BOOK)
			val description = data.getStringExtra(EditBookActivity.UPDATED_DESCRIPTION)
			val currentTime = Calendar.getInstance().time

			val book = Book(bookId!!, authorName, bookName, description, currentTime)

			searchViewModel.update(book)

			Toast.makeText(applicationContext, R.string.updated, Toast.LENGTH_LONG).show()

		} else {
			Toast.makeText(applicationContext, R.string.not_saved, Toast.LENGTH_LONG).show()
		}
	}

	override fun onDeleteClickListener(myBook: Book) {
		searchViewModel.delete(myBook)
		Toast.makeText(applicationContext, R.string.deleted, Toast.LENGTH_LONG).show()
	}

	companion object {
		const val UPDATE_BOOK_ACTIVITY_REQUEST_CODE = 2
	}
}
