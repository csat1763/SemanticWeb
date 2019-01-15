package recipeBackend;

import java.util.List;

public class RecipeResponse {

    private final long id;
    private final String content;
    
    private final List<Recipe> recipes;

    public RecipeResponse(long id, String content, List<Recipe> recipes) {
        this.id = id;
        this.content = content;
        this.recipes = recipes;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

	public List<Recipe> getRecipes() {
		return recipes;
	}

}
