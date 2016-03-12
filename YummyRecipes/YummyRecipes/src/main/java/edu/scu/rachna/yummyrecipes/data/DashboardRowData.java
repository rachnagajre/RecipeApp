package edu.scu.rachna.yummyrecipes.data;


public class DashboardRowData {

    private int recipeId;

    private String recipeName;

    private String recipeImageFileName;

    public DashboardRowData(int recipeId, String recipeName, String recipeImageFileName) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeImageFileName = recipeImageFileName;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeImageFileName() {
        return recipeImageFileName;
    }

    public void setRecipeImageFileName(String recipeImageFileName) {
        this.recipeImageFileName = recipeImageFileName;
    }

}
