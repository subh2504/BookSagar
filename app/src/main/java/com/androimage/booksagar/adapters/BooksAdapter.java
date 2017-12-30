package com.androimage.booksagar.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androimage.booksagar.R;
import com.androimage.booksagar.activity.DetailBookActivity;
import com.androimage.booksagar.activity.DetailBookSellerActivity;
import com.androimage.booksagar.activity.MyBooksActivity;
import com.androimage.booksagar.model.Book;
import com.androimage.booksagar.realm.RealmController;
import com.bumptech.glide.Glide;

import io.realm.Realm;

public class BooksAdapter extends RealmRecyclerViewAdapter<Book> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public BooksAdapter(Context context) {

        this.context = context;
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false);
        return new CardViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();

        // get the article
        final Book book = getItem(position);
        // cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        // set the title and the snippet
        holder.textTitle.setText(book.getTitle());

        holder.textAuthor.setText(book.getAuthor());

        holder.textMrpPrice.setPaintFlags(holder.textMrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.textMrpPrice.setText(book.getMrp());
        holder.textSellerPrice.setText("\u20B9" + " " + book.getPrice());


        // load the background image
        if (book.getImageUrl() != null) {
            Glide.with(context)
                    .load(book.getImageUrl().replace("https", "http"))
                    .asBitmap()
                    .fitCenter()
                    .into(holder.imageBackground);
        }

//                // remove single match
//                results.remove(position);
//                realm.commitTransaction();
//
//                if (results.size() == 0) {
//                    Prefs.with(context).setPreLoad(false);
//                }
//
//                notifyDataSetChanged();
//
//                Toast.makeText(context, title + " is removed from Realm", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        //update single match from BuyNSell
        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(context, context.toString(), Toast.LENGTH_SHORT);


                if (context.getClass().equals(MyBooksActivity.class)) {

                    Intent i = new Intent(context, DetailBookSellerActivity.class);
                    i.putExtra("id", book.getId());
                    context.startActivity(i);
                } else {

                    Intent i = new Intent(context, DetailBookActivity.class);
                    i.putExtra("id", book.getId());
                    context.startActivity(i);
                }


            }
        });

        holder.imageBackground.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(context, context.toString(), Toast.LENGTH_SHORT);


                if (context.getClass().equals(MyBooksActivity.class)) {

                    Intent i = new Intent(context, DetailBookSellerActivity.class);
                    i.putExtra("id", book.getId());
                    context.startActivity(i);
                } else {

                    Intent i = new Intent(context, DetailBookActivity.class);
                    i.putExtra("id", book.getId());
                    context.startActivity(i);
                }


            }
        });
    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView textTitle;
        public TextView textAuthor;

        public TextView textSellerPrice, textMrpPrice;
        public ImageView imageBackground, overflow;

        public CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_book);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            textAuthor = (TextView) itemView.findViewById(R.id.author);
            textSellerPrice = (TextView) itemView.findViewById(R.id.sellerPrice);
            textMrpPrice = (TextView) itemView.findViewById(R.id.mrpPrice);

            imageBackground = (ImageView) itemView.findViewById(R.id.thumbnail);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);

        }
    }


}
