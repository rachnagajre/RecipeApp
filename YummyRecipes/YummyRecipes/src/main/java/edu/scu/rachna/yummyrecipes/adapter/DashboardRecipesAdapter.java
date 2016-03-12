package edu.scu.rachna.yummyrecipes.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.activity.RecipeDetailActivity;
import edu.scu.rachna.yummyrecipes.data.DashboardRowData;
import edu.scu.rachna.yummyrecipes.data.Recipe;


public class DashboardRecipesAdapter extends BaseAdapter{

    private List<Recipe> recipes;
    private Context context;

    public DashboardRecipesAdapter(Context context, List<Recipe> recipes) {
        this.recipes = recipes;
        this.context = context;
    }



    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recipes.get(position).getRecipeName().hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DashboardRowDataHolder holder;

        View row = convertView;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.recipe_list, null);

            holder = new DashboardRowDataHolder();
            holder.recipeName = (TextView) row.findViewById(R.id.nameOfRecipeItem);
            holder.recipeImage = (ImageView) row.findViewById(R.id.imageOfRecipeItem);

            row.setTag(holder);
        } else {
            holder = (DashboardRowDataHolder) row.getTag();
        }

        //Set Image and recipe name
        holder.recipeName.setText(recipes.get(position).getRecipeName());
        String filename = recipes.get(position).getImage();
        Picasso.with(context).load(filename).fit().into(holder.recipeImage);


        //Set Onclick event for Image or TextView too
        holder.recipeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToRecipeDetails(recipes.get(position));
            }
        });
        holder.recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToRecipeDetails(recipes.get(position));
            }
        });

        return row;
    }


    public class DashboardRowDataHolder {
        TextView recipeName;
        ImageView recipeImage;
    }

    private void navigateToRecipeDetails(Recipe recipe) {
        //Go to Recipe Details page and pass the required recipeItemId in intent
        Intent recipeDetailIntent = new Intent(context, RecipeDetailActivity.class);
        recipeDetailIntent.putExtra("recipeId", recipe.getObjectId());
        context.startActivity(recipeDetailIntent);
    }

}
