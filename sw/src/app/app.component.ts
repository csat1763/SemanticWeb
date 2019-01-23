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
		{name: "Low-Carb", value: 'low-carb', checked: false},
		{name: "Red meat-free", value: 'red-meat-free', checked: false},
		{name: "Vegan", value: 'vegan', checked: false},
		{name: "Vegetarian", value: 'vegan', checked: false},
	];
	search_text = "";
	search_ingredients: string[] = [];
	results: any;

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

	search() {
		var search_tags = [];
		for (let tag of this.tags) {
			if (tag.checked) {
				search_tags.push(tag.value);
			}
		}
		this.backend_service.getRecipies(this.search_ingredients, search_tags).subscribe(data => this.results = data);
	}
}
