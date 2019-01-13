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
		
		//TODO: SPARQL QUERY STUFF

		return new RecipeResponse(counter.incrementAndGet(),
				"TAGS:" + tagBuilder.toString() + "INGREDIENTS:" + ingredientBuilder.toString());
	}
}