import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { BackendService} from './backend.service';

import {
	MatSidenavModule,
	MatFormFieldModule,
	MatInputModule,
	MatButtonModule,
	MatMenuModule,
	MatToolbarModule,
	MatIconModule,
	MatCardModule,
	MatListModule,
	MatGridListModule,
	MatCheckboxModule,
	MatChipsModule,
	MatProgressBarModule
} from '@angular/material';

@NgModule({
	declarations: [
		AppComponent
	],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		FormsModule,
		AppRoutingModule,
		HttpClientModule,

		MatSidenavModule,
		MatFormFieldModule,
		MatInputModule,
		MatButtonModule,
		MatMenuModule,
		MatToolbarModule,
		MatIconModule,
		MatCardModule,
		MatListModule,
		MatGridListModule,
		MatCheckboxModule,
		MatChipsModule,
		MatProgressBarModule
	],
	providers: [BackendService],
	bootstrap: [AppComponent]
})
export class AppModule { }
