package com.newss.app

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.newss.app.databinding.RowNewsItemRvBinding

class NewsListAdapter(
    private val onClickAction: OnNewsClickListener,
) :
    RecyclerView.Adapter<NewsListViewHolder>() {

    private val items: ArrayList<NewsData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        return NewsListViewHolder(
            RowNewsItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickAction
        )
    }

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateNewsItems(newsList: ArrayList<NewsData>) {
        items.clear()
        items.addAll(newsList)
        notifyDataSetChanged()
    }
}

class NewsListViewHolder(
    private val binding: RowNewsItemRvBinding,
    private val listener: OnNewsClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(news: NewsData) {
        Glide.with(binding.root.context).load(news.imageLink).transition(
            DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    Toast.makeText(binding.root.context,
                        "Something went wrong!",
                        Toast.LENGTH_SHORT)
                        .show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }
            }).into(binding.ivImage)

        binding.apply {
            tvAuthor.text = news.author
            tvNewsTitle.text = news.title
        }

        binding.root.setOnClickListener {
            listener.onNewsClick(news)
        }
    }

}

interface OnNewsClickListener {
    fun onNewsClick(news: NewsData)
}