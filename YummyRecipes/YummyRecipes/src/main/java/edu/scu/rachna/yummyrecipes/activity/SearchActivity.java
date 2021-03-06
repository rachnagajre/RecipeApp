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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.List;

import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.adapter.DashboardRecipesAdapter;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.DefaultCallback;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Recipe;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemClickListener {

    private FloatingActionButton addNewRecipeButton;

    private GridView SearchRecipesGridView;

    private List<Recipe> myRecipesList = new ArrayList<>();

    private BackendlessUser loggedInUser;

    private TextView nameField;

    private TextView emailField;

    private BackendlessCollection<Recipe> mBackendlessCollection;

    private DashboardRecipesAdapter adapter;

    private List<Recipe> recipesList = new ArrayList<>();

    String searchquery;

    boolean shouldSearchAllRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchquery = getIntent().getStringExtra("query");
        shouldSearchAllRecipes = getIntent().getBooleanExtra("allRecipes", false);

        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY, Default.VERSION);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_recipe_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.my_recipes_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeader = (View) navigationView.getHeaderView(0);
        nameField = (TextView) navigationHeader.findViewById(R.id.nameField);
        emailField = (TextView) navigationHeader.findViewById(R.id.emailField);
        loggedInUser = Backendless.UserService.CurrentUser();
        nameField.setText(loggedInUser.getProperty("name").toString());
        emailField.setText(loggedInUser.getEmail());

        addNewRecipeButton = (FloatingActionButton) findViewById(R.id.addNewRecipeButton);
        addNewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToAddNewRecipe();
            }
        });

        initializeMyRecipesList();

        SearchRecipesGridView = (GridView) findViewById(R.id.myRecipesGridView);
        adapter=new DashboardRecipesAdapter(this, recipesList);
        SearchRecipesGridView.setAdapter(adapter);
        SearchRecipesGridView.setOnItemClickListener(this);
    }

    private void navigateToAddNewRecipe() {
        Intent addNewRecipeIntent = new Intent(SearchActivity.this, AddRecipeActivity.class);
        startActivity(addNewRecipeIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_recipe_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                Backendless.UserService.logout( new DefaultCallback<Void>(SearchActivity.this)
                {
                    @Override
                    public void handleResponse( Void response )
                    {
                        super.handleResponse( response );
                        startActivity( new Intent( getBaseContext(), LoginActivity.class ) );
                        finish();
                    }
                } );
                break;
            case R.id.helpButton :
                Toast.makeText(getApplicationContext(), "Go to www.yummyrecipes.com. For Help.", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_recipe_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeMyRecipesList() {
        BackendlessDataQuery query = new BackendlessDataQuery();
        String whereclause = " recipeName LIKE '%" +searchquery+"%' and likes>-1 ";
        if(!shouldSearchAllRecipes) {
            //Only search recipes for current user
            whereclause = whereclause + " and ownerId='" + Backendless.UserService.CurrentUser().getObjectId()+"' ";
        }
        query.setWhereClause(whereclause);
        QueryOptions options = new QueryOptions();
        options.addSortByOption("likes desc");
        query.setQueryOptions(options);
        Log.i("test", whereclause);
        Recipe.getRecipesbySearch(query,
                new LoadingCallback<BackendlessCollection<Recipe>>(this, "Getting Recipes", true) {
                    @Override
                    public void handleResponse(BackendlessCollection<Recipe> loadedrecipes) {
                        mBackendlessCollection = loadedrecipes;

                        convertToList(loadedrecipes);

                        super.handleResponse(loadedrecipes);
                    }
                });

    }
    private void convertToList( BackendlessCollection<Recipe> nextPage )
    {
        recipesList.addAll(nextPage.getCurrentPage());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}