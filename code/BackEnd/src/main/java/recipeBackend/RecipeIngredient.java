package recipeBackend;

public class RecipeIngredient {
	private String ingredientName;
	private String ingredientAmount;
	private String unitText;
	private String potentialAction;
	
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public String getIngredientAmount() {
		return ingredientAmount;
	}
	public void setIngredientAmount(String ingredientAmount) {
		this.ingredientAmount = ingredientAmount;
	}
	public String getUnitText() {
		return unitText;
	}
	public void setUnitText(String unitText) {
		this.unitText = unitText;
	}
	public String getPotentialAction() {
		return potentialAction;
	}
	public void setPotentialAction(String potentialAction) {
		this.potentialAction = potentialAction;
	}
	
}
