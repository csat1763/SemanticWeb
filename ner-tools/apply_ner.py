import codecs
import json
import os
import subprocess
import sys
import urllib.parse
from tqdm import tqdm

print('handling', sys.argv[1] + '...')
#with codecs.open(sys.argv[1], 'r', 'iso-8859-1') as f:
with codecs.open(sys.argv[1], 'r', 'utf-8') as f:
	jld = json.load(f)

matches = 0
total_ingredients = 0
i_set = []
for recipe in tqdm(jld):
	ingredients = recipe['recipeIngredient']
	for ingredient in ingredients:
		full_name = ingredient['ingredientName']['ingridientFullName']
		with open('tmp' + str(os.getpid()) + '.tok', 'w') as tmp:
			proc = subprocess.run(['java', '-cp', 'stanford-ner/stanford-ner.jar', 'edu.stanford.nlp.process.PTBTokenizer'], input=full_name.encode('utf-8'), stdout=tmp, stderr=subprocess.PIPE);

		proc = subprocess.run(['java', '-cp', 'stanford-ner/stanford-ner.jar', 'edu.stanford.nlp.ie.crf.CRFClassifier', '-loadClassifier', 'ner-model.ser.gz', '-textFile', 'tmp' + str(os.getpid()) + '.tok'], capture_output=True);
		out = proc.stdout.decode('utf-8').replace('\\n', '')
		ingredient['ingredientName']['name'] = ''
		ingredient['ingridientAmount']['unitText'] = ''
		ingredient['ingridientAmount']['value'] = ''
		for tok_label in out.split(' '):
			tok = '/'.join(tok_label.split('/')[:-1])
			label = tok_label.split('/')[-1]
			if label == 'NAME':
				ingredient['ingredientName']['name'] += ' ' + tok
			elif label == 'UNIT':
				ingredient['ingridientAmount']['unitText'] += ' ' + tok
			elif label == 'QUANTITY':
				ingredient['ingridientAmount']['value'] += ' ' + tok

		if ingredient['ingredientName']['name'] != '':
			ingredient['ingredientName']['name'] = ingredient['ingredientName']['name'][1:]
			ingredient['potentialAction'] = {'@type': 'SearchAction', 'target': 'https://www.freshdirect.com/srch.jsp?searchParams=' + urllib.parse.quote_plus(ingredient['ingredientName']['name'])}
		if ingredient['ingridientAmount']['unitText'] != '':
			ingredient['ingridientAmount']['unitText'] = ingredient['ingridientAmount']['unitText'][1:]
		if ingredient['ingridientAmount']['value'] != '':
			ingredient['ingridientAmount']['value'] = ingredient['ingridientAmount']['value'][1:]

#with codecs.open('../code/TripleStore/Triple/src/main/resources/data/recipes/beefFromEdamam.jsonld', 'w', 'iso-8859-1') as f:
#with codecs.open(sys.argv[1], 'w', 'iso-8859-1') as f:
with codecs.open(sys.argv[1], 'w', 'utf-8') as f:
	json.dump(jld, f)
