import { Component } from '@angular/core';

import { BackendService } from './backend.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
	title = 'sw';
	tags = [
		{name: "Balanced", value: 'balanced', checked: false},
		{name: "High-Fiber", value: 'high-fiber', checked: false},
		{name: "High-Protein", value: 'high-protein', checked: false},
		{name: "Low-Carb", value: 'low-carb', checked: false},
		{name: "Low-Fat", value: 'low-fat', checked: false},
		{name: "Low-Sodium", value: 'low-sodium', checked: false},
		{name: "Alcohol-free", value: 'alcohol-free', checked: false},
		{name: "Celery-free", value: 'celery-free', checked: false},
		{name: "Crustacean-free", value: 'crustacean-free', checked: false},
		{name: "Dairy-free", value: 'dairy-free', checked: false},
		{name: "Eggs-free", value: 'egg-free', checked: false},
		{name: "Fish-free", value: 'fish-free', checked: false},
		{name: "Gluten-free", value: 'gluten-free', checked: false},
		{name: "Kidney friendly", value: 'kidney-friendly', checked: false},
		{name: "Kosher", value: 'kosher', checked: false},
		{name: "Low potassium", value: 'potassium', checked: false},
		{name: "Lupine-free", value: 'lupine-free', checked: false},
		{name: "Mustard-free", value: 'mustard-free', checked: false},
		{name: "No oil added", value: 'No-oil-added', checked: false},
		{name: "No-sugar", value: 'low-sugar', checked: false},
		{name: "Paleo", value: 'paleo', checked: false},
		{name: "Peanut-free", value: 'peanut-free', checked: false},
		{name: "Pescatarian", value: 'pescatarian', checked: false},
		{name: "Pork-free", value: 'pork-free', checked: false},
		{name: "Red meat-free", value: 'red-meat-free', checked: false},
		{name: "Sesame-free", value: 'sesame-free', checked: false},
		{name: "Shellfish-free", value: 'shellfish-free', checked: false},
		{name: "Soy-free", value: 'soy-free', checked: false},
		{name: "Sugar-conscious", value: 'sugar-conscious', checked: false},
		{name: "Tree Nut-free", value: 'tree-nut-free', checked: false},
		{name: "Vegan", value: 'vegan', checked: false},
		{name: "Vegetarian", value: 'vegetarian', checked: false},
		{name: "Wheat-free", value: 'wheat-free', checked: false},
	];
	search_text = "";
	search_ingredients: string[] = [];
	results: any;
	loading = false;

	constructor(private backend_service: BackendService) {}

	add_ingredient() {
		if (this.search_text != "") {
			this.search_ingredients.push(this.search_text);
			this.search_text = "";
		}
	}

	remove_ingredient(i) {
		this.search_ingredients.splice(i, 1);
	}

	find_tag(value: string) {
		for (let tag of this.tags) {
			if (tag.value == value.toLowerCase()) {
				return tag;
			}
		}
		return {name: "Unknown Tag", value: value, checked: false};
	}

	add_tag_search(tag) {
		if (!tag.checked) {
			tag.checked = true;
			this.search();
		}
	}

	search() {
		var search_tags = [];
		for (let tag of this.tags) {
			if (tag.checked) {
				search_tags.push(tag.value);
			}
		}
		this.loading = true;
		this.results = undefined;
		this.backend_service.getRecipies(this.search_ingredients, search_tags).subscribe(data => {this.results = data; this.loading = false;});
	}
}
