import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
	providedIn: 'root'
})
export class BackendService {

	constructor(private http: HttpClient) { }

	getRecipies(search_ingredients: string[], tags: string[]) {
		var tag_string = "";
		for (let tag of tags) {
			tag_string += tag + "+";
		}
		tag_string = tag_string.slice(0, -1);
		var ingredients_string = "";
		for (let ingredient of search_ingredients) {
			ingredients_string += encodeURIComponent(ingredient) + ",";
		}
		ingredients_string = ingredients_string.slice(0, -1);
		return this.http.get("http://localhost:8080/recipeRequest?tags=" + tag_string + "&ingredients=" + ingredients_string);
	}
}
