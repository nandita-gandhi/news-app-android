package com.newss.app

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.newss.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnNewsClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NewsListAdapter(this)
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter

        loadNews()
    }

    private fun loadNews() {
        // bc57de8dac664f4cb5a842d9d5014ddb - News API Key
        val url =
            "https://newsapi.org/v2/top-headlines?country=in&apiKey=bc57de8dac664f4cb5a842d9d5014ddb"

        // Request a string response from the provided url
        try {
            val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    val newsJsonArray = response.getJSONArray("articles")
                    val newsArray = ArrayList<NewsData>()
                    (0 until newsJsonArray.length()).forEach { i ->
                        val objectInfo = newsJsonArray.getJSONObject(i)
                        val news = NewsData(
                            title = objectInfo.getString("title"),
                            imageLink = objectInfo.getString("urlToImage"),
                            newsLink = objectInfo.getString("url"),
                            author = objectInfo.getString("author"),
                        )
                        newsArray.add(news)
                    }

                    adapter.updateNewsItems(newsArray)
                },
                { error ->
                    Log.d("error", "$error")
                    Toast.makeText(this,
                        "Something went wrong! ${error.message}",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    return headers
                }
            }

            // Add the request to request queue
            MySingleton.getInstance(this).addToRequestQueue(jsonRequest)

        } catch (e: AuthFailureError) {
            e.printStackTrace()
        }
    }

    override fun onNewsClick(news: NewsData) {
        val builder = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorToolbar))
                .build())
            .build()

        builder.launchUrl(this, Uri.parse(news.newsLink))
    }
}