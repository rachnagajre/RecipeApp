package edu.scu.rachna.yummyrecipes.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
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

public class MyRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private FloatingActionButton addNewRecipeButton;

    private GridView myRecipesGridView;

    private BackendlessUser loggedInUser;

    private TextView nameField;

    private TextView emailField;

    private BackendlessCollection<Recipe> mBackendlessCollection;

    private DashboardRecipesAdapter adapter;

    private List<Recipe> recipesList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY,
                Default.VERSION);

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

        initializeGridView();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                initializeGridView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void initializeGridView() {
        initializeMyRecipesList();
        myRecipesGridView = (GridView) findViewById(R.id.myRecipesGridView);
        adapter = new DashboardRecipesAdapter(this, recipesList);
        myRecipesGridView.setAdapter(adapter);
        myRecipesGridView.setOnItemClickListener(this);
    }

    private void navigateToAddNewRecipe() {
        Intent addNewRecipeIntent = new Intent(MyRecipeActivity.this, AddRecipeActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null != searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.search:
                //Toast.makeText(getApplicationContext(), "Search clicked!!.", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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
                Backendless.UserService.logout( new DefaultCallback<Void>(MyRecipeActivity.this)
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


    @Override
    public void onStart() {
        super.onStart();
        initializeGridView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeGridView();
    }

    private void initializeMyRecipesList() {
        BackendlessDataQuery query = new BackendlessDataQuery();
        String whereclause = "ownerId='" + Backendless.UserService.CurrentUser().getObjectId()+"' and likes>-1";
        query.setWhereClause(whereclause);
        QueryOptions options = new QueryOptions();
        options.addSortByOption("likes desc");
        query.setQueryOptions(options);
        Recipe.getRecipesbySearch(query,
                new LoadingCallback<BackendlessCollection<Recipe>>(this, "Getting My Recipes", true) {
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
        recipesList.clear();
        recipesList.addAll(nextPage.getCurrentPage());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent searchIntent = new Intent(MyRecipeActivity.this, SearchActivity.class);
        searchIntent.putExtra("query", query);
        searchIntent.putExtra("allRecipes", false);
        startActivity(searchIntent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
