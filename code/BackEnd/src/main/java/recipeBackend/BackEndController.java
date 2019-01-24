package recipeBackend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackEndController {

	private final AtomicLong counter = new AtomicLong();
	private List<String> ingredientList = new ArrayList<String>();
	private List<String> tagList = new ArrayList<String>();

	@CrossOrigin(origins = "*")
	@RequestMapping("/recipeRequest")
	public String recipeRequest(@RequestParam(value = "tags", defaultValue = "Alcohol-Free") String tags,
			@RequestParam(value = "ingredients", defaultValue = "salt") String ingredients) {

		ingredientList.clear();
		tagList.clear();

		String[] tagSplit = tags.split(" ");
		String[] ingredientSplit = ingredients.split(",");

		for (String tag : tagSplit) {
			// System.out.println(tag);
			tagList.add(tag);
		}

		for (String ingredient : ingredientSplit) {
			// System.out.println(ingredient);
			ingredientList.add(ingredient);
		}
		// System.out.println(ingredientList);
		// System.out.println(tagList);
		return FusekiEndpoint.getResutlsFromFuseki(ingredientList, tagList);
	}

	public RecipeResponse recipeRequestOld(@RequestParam(value = "tags", defaultValue = "none") String tags,
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
		// String queryResponse = queryCreator.createQuery(tagList, ingredientList);

		// build json recipe response

		List<Recipe> recipes = new ArrayList<Recipe>();

		Recipe testRecipe = new Recipe();
		testRecipe.setId(0);
		testRecipe.setName("beefy soup");
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
		List<RecipeIngredient> ingredientList = new ArrayList<RecipeIngredient>();

		RecipeIngredient newIngredient = new RecipeIngredient();
		newIngredient.setIngredientAmount("2");
		newIngredient.setIngredientName("beefy");
		newIngredient.setUnitText("kg");
		newIngredient.setPotentialAction("potentialActionUrl");

		RecipeIngredient newIngredient2 = new RecipeIngredient();
		newIngredient2.setIngredientAmount("1");
		newIngredient2.setIngredientName("salt");
		newIngredient2.setUnitText("tbls");
		newIngredient2.setPotentialAction("potentialActionUrl");

		ingredientList.add(newIngredient);
		ingredientList.add(newIngredient2);

		testRecipe.setRecipeIngredient(ingredientList);
		testRecipe.setRecipeInstructions("instructions");
		testRecipe.setRecipeYield("yield");

		recipes.add(testRecipe);

		Recipe testRecipe2 = new Recipe();
		testRecipe2.setId(0);
		testRecipe2.setName("beefo");
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
		List<RecipeIngredient> ingredientList2 = new ArrayList<RecipeIngredient>();

		ingredientList2.add(newIngredient);
		ingredientList2.add(newIngredient2);
		testRecipe2.setRecipeIngredient(ingredientList2);
		testRecipe2.setRecipeInstructions("instructions");
		testRecipe2.setRecipeYield("yield");

		recipes.add(testRecipe2);

		return new RecipeResponse(counter.incrementAndGet(),
				"TAGS:" + tagBuilder.toString() + "INGREDIENTS:" + ingredientBuilder.toString(), recipes);
	}
}
