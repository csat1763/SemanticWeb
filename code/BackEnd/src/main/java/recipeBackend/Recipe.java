package recipeBackend;

import java.util.List;

public class Recipe {

    private long id;
    private String name;
    private String sameAs;
    private String nutrition;
    private List<String> keywords;
    private String recipeYield;
    private String image;
    
    private String cookTime;
    private String prepTime;
    private String totalTime;
    
    private String recipeInstructions;
    
    private List<String> recipeIngredient;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSameAs() {
		return sameAs;
	}

	public void setSameAs(String sameAs) {
		this.sameAs = sameAs;
	}

	public String getNutrition() {
		return nutrition;
	}

	public void setNutrition(String nutrition) {
		this.nutrition = nutrition;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getRecipeYield() {
		return recipeYield;
	}

	public void setRecipeYield(String recipeYield) {
		this.recipeYield = recipeYield;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCookTime() {
		return cookTime;
	}

	public void setCookTime(String cookTime) {
		this.cookTime = cookTime;
	}

	public String getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(String prepTime) {
		this.prepTime = prepTime;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getRecipeInstructions() {
		return recipeInstructions;
	}

	public void setRecipeInstructions(String recipeInstructions) {
		this.recipeInstructions = recipeInstructions;
	}

	public List<String> getRecipeIngredient() {
		return recipeIngredient;
	}

	public void setRecipeIngredient(List<String> recipeIngredient) {
		this.recipeIngredient = recipeIngredient;
	}

}
