package recipeBackend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackEndController {

	private final AtomicLong counter = new AtomicLong();
	private List<String> ingredientList = new ArrayList<String>();
	private List<String> tagList = new ArrayList<String>();

	@RequestMapping("/recipeRequest")
	public RecipeResponse recipeRequest(@RequestParam(value = "tags", defaultValue = "none") String tags,
			@RequestParam(value = "ingredients", defaultValue = "none") String ingredients) {

		ingredientList.clear();
		tagList.clear();

		String[] tagSplit = tags.split(" ");
		String[] ingredientSplit = ingredients.split(" ");

		for (String tag : tagSplit) {
			tagList.add(tag);
		}

		for (String ingredient : ingredientSplit) {
			ingredientList.add(ingredient);
		}

		StringBuilder ingredientBuilder = new StringBuilder();
		for (String ingredient : ingredientList) {
			ingredientBuilder.append("Ingredient: ").append(ingredient).append(", ");
		}

		StringBuilder tagBuilder = new StringBuilder();
		for (String tag : tagList) {
			tagBuilder.append("Tag: ").append(tag).append(", ");
		}

		// TODO: SPARQL QUERY STUFF

		// build json recipe response

		List<Recipe> recipes = new ArrayList<Recipe>();
		
		Recipe testRecipe = new Recipe();
		testRecipe.setId(0);
		testRecipe.setName("beef");
		testRecipe.setImage("img url");
		List<String> keywordList = new ArrayList<String>();
		keywordList.add("keyword 1");
		keywordList.add("keyword 2");
		testRecipe.setKeywords(keywordList);
		testRecipe.setNutrition("nutrition");
		testRecipe.setPrepTime("10 min");
		testRecipe.setCookTime("20 min");
		testRecipe.setTotalTime("30 min");
		testRecipe.setSameAs("same as url");
		List<String> ingredientList = new ArrayList<String>();
		ingredientList.add("beef");
		ingredientList.add("salt");
		ingredientList.add("pepper");
		testRecipe.setRecipeIngredient(ingredientList);
		testRecipe.setRecipeInstructions("instructions");
		testRecipe.setRecipeYield("yield");
		
		recipes.add(testRecipe);
		
		Recipe testRecipe2 = new Recipe();
		testRecipe2.setId(0);
		testRecipe2.setName("beef");
		testRecipe2.setImage("img url");
		List<String> keywordList2 = new ArrayList<String>();
		keywordList2.add("keyword 1");
		keywordList2.add("keyword 2");
		testRecipe2.setKeywords(keywordList2);
		testRecipe2.setNutrition("nutrition");
		testRecipe2.setPrepTime("10 min");
		testRecipe2.setCookTime("20 min");
		testRecipe2.setTotalTime("30 min");
		testRecipe2.setSameAs("same as url");
		List<String> ingredientList2 = new ArrayList<String>();
		ingredientList2.add("beef");
		ingredientList2.add("salt");
		ingredientList2.add("pepper");
		testRecipe2.setRecipeIngredient(ingredientList2);
		testRecipe2.setRecipeInstructions("instructions");
		testRecipe2.setRecipeYield("yield");
		
		recipes.add(testRecipe2);
		
		return new RecipeResponse(counter.incrementAndGet(),
				"TAGS:" + tagBuilder.toString() + "INGREDIENTS:" + ingredientBuilder.toString(), recipes);
	}
}
