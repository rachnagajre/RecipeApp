package edu.scu.rachna.yummyrecipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;

import java.util.ArrayList;
import java.util.List;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.adapter.DashboardRecipesAdapter;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.DefaultCallback;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Recipe;

public class DashboardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton addNewRecipeButton;

    private BackendlessCollection<Recipe> mBackendlessCollection;

    private GridView recipesGridView;

    private DashboardRecipesAdapter adapter;

    private List<Recipe> recipesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY, Default.VERSION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeRecipesList();

        addNewRecipeButton = (FloatingActionButton) findViewById(R.id.addNewRecipeButton);
        addNewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToAddNewRecipe();
            }
        });

        recipesGridView = (GridView) findViewById(R.id.recipesGridView);
        adapter=new DashboardRecipesAdapter(this, recipesList);
        recipesGridView.setAdapter(adapter);
        recipesGridView.setOnItemClickListener(this);

    }

    private void initializeRecipesList() {
        Recipe.getAllRecipes(new LoadingCallback<BackendlessCollection<Recipe>>(this, "Getting Recipes", true) {
            @Override
            public void handleResponse(BackendlessCollection<Recipe> loadedrecipes) {
                mBackendlessCollection = loadedrecipes;

                convertToList(loadedrecipes);

                super.handleResponse(loadedrecipes);
            }
        });

    }

    private void navigateToAddNewRecipe() {
        Intent addNewRecipeIntent = new Intent(DashboardActivity.this, AddRecipeActivity.class);
        startActivity(addNewRecipeIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO : Go to Recipe Details page and pass the required recipeItemId in intent
        final Recipe recipe = recipesList.get(position);
        navigateToRecipeDetails(recipe);
    }

    private void navigateToRecipeDetails(Recipe recipe) {
        Intent recipeDetailIntent = new Intent(DashboardActivity.this, RecipeDetailActivity.class);
        recipeDetailIntent.putExtra("recipeId", recipe.getObjectId());
        startActivity(recipeDetailIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "Search clicked!!.", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void convertToList( BackendlessCollection<Recipe> nextPage )
    {
        recipesList.addAll(nextPage.getCurrentPage());
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.homeButton :
                startActivity(new Intent(this, DashboardActivity.class));
                break;
            case R.id.myRecipesButton :
               // Toast.makeText(getApplicationContext(), "Navigation My Recipes clicked!!.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MyRecipeActivity.class));
                break;
            case R.id.logOutButton :
              //Toast.makeText(getApplicationContext(), "Navigation Logout clicked!!.", Toast.LENGTH_SHORT).show();
                Backendless.UserService.logout(new DefaultCallback<Void>(DashboardActivity.this) {
                    @Override
                    public void handleResponse(Void response) {
                        super.handleResponse(response);
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        finish();
                    }
                });
                break;
            case R.id.helpButton :
                Toast.makeText(getApplicationContext(), "Navigation Help clicked!!.", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
