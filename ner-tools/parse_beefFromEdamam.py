import json
import re
import numpy as np

with open('beefFromEdamam_utf-8.jsonld', 'r') as f:
	jld = json.load(f)

matches = 0
total_ingredients = 0
i_set = []
for recipe in jld:
	ingredients = recipe['recipeIngredient']
	i_set += ingredients

np.random.seed(0)
np.random.shuffle(i_set)
train = i_set[:int(len(i_set)*0.8)]
test = i_set[int(len(i_set)*0.8):]

with open('train.txt', 'w') as f:
	for i in train:
		f.write(i + '\n')

with open('test.txt', 'w') as f:
	for i in test:
		f.write(i + '\n')

#i_words = i.split(' ')
		#for i_w in i_words:
		#	print(i_w)
		#print()

		"""
		m = re.search(r'^\*? ?([0-9\.\/½¼¾]* ?[0-9\.\/½¼¾]+)[ \-]?([a-zA-Z\.\(\)]+)? +([a-zA-Z0-9 \+\-\,\/\.\(\)®%\'éèîñ&]+)$', i)
		total_ingredients += 1
		if m is not None:
			matches += 1

			amount = m.group(1)
			amount = amount.replace('½', ' 1/2')
			amount = amount.replace('¼', ' 1/4')
			amount = amount.replace('¾', ' 3/4')
			amount = amount.replace('  ', ' ')
			amount = re.sub(r'^ +', '', amount)

			unit = m.group(2)
			if unit is None:
				unit = 'pcs'

			print('amount:', amount, 'unit:', unit, 'ingredient:', m.group(3))
		else:
			print(i)
		"""
#print(total_ingredients, matches, matches/total_ingredients)
