import json
import re

from tqdm import tqdm

test = False

if not test:
	in_file = 'train_ptb.tsv'
	out_file = 'train.tsv'
	i_file = 'train.txt'
else:
	in_file = 'test_ptb.tsv'
	out_file = 'test.tsv'
	i_file = 'test.txt'

#with open('beefFromEdamam_utf-8.jsonld', 'r') as f:
#	jld = json.load(f)
#
#i_set = []
#for recipe in jld:
#	ingredients = recipe['recipeIngredient']
#	if not test:
#		i_set += ingredients[:int(len(ingredients)*0.8)]
#	else:
#		i_set += ingredients[int(len(ingredients)*0.8):]

i_set = []
with open(i_file, 'r') as f:
	for i_line in f:
		i_set.append(i_line)

premade = []
with open(out_file, 'r') as f:
	for p_line in f:
		premade.append(p_line)

ignore_guess = False
j = -1
it = iter(tqdm(range(len(i_set))))
with open(in_file, 'r') as tokens_file:
	with open(out_file, 'w') as f:
		i = -1
		i_line = ''
		amount = ''
		unit = ''
		name = ''
		match = False
		for line in tokens_file:
			j += 1
			if line == '\n':
				next(it) # prints progress
				ignore_guess = False
				i += 1
				if i >= len(i_set):
					f.write('\n');
					break
				i_line = i_set[i]
				#m = re.search(r'^\*? ?([0-9\.\/½¼¾]* ?[0-9\.\/½¼¾]+)[ \-]?([a-zA-Z\.\(\)]+)? +([a-zA-Z0-9 \+\-\,\/\.\(\)®%\'éèîñ&]+)$', i_line)
				m = re.search(r'^\*? ?([0-9\.\/½¼¾]* ?[0-9\.\/½¼¾]+) ([a-zA-Z\.\(\)]+)? +([a-zA-Z0-9 \+\-,\/\.\(\)®%\'éèîñ&]+)$', i_line)
				match = m is not None
				if match:
					def norm(s):
						if s is None:
							return s
						s = s.replace('½', '1/2')
						s = s.replace('¼', '1/4')
						s = s.replace('¾', '3/4')
						return s
					amount = norm(m.group(1))
					unit = norm(m.group(2))
					name = norm(m.group(3))
				f.write('\n')
			else:
				line = line.split('\t')[0]
				pline = line.replace(' ', ' ') # replace nbsp with space
				if pline == '-LRB-': pline = '('
				if pline == '-RRB-': pline = ')'
				def handle(guess, acc):
					global ignore_guess
					print()
					print(i_line.strip())
					print(pline.strip())
					if j < len(premade):
						#print(j, premade)
						print('using premade', premade[j].split('\t')[1].strip())
						f.write(premade[j])
						return acc.replace(pline, '', 1).strip()

					print('0 O  1 QUANTITY  2 UNIT  3 NAME')
					user_guess = ['O', 'QUANTITY', 'UNIT', 'NAME'][int(input())]
					if not acc.startswith(pline):
						f.write(f'{line}\t{user_guess}\n')
						return ''
					else:
						if not ignore_guess and user_guess != guess and guess is not None:
							print(f'hmm.. I guessed {guess} instead. keep {user_guess}?')
							a = input()
							while a not in ['y', 'n']:
								print('(y/n)')
								a = input()
							if a == 'n':
								user_guess = guess
								print(f'using {guess} instead. happy to be helping')
							else:
								print('ok. I\'ll shut up for this ingredient line')
								ignore_guess = True
						f.write(f'{line}\t{user_guess}\n')
						return acc.replace(pline, '', 1).strip()

				if match:
					if amount != '':
						# amount
						amount = handle('QUANTITY', amount)
					else:
						if unit != '' and unit is not None:
							# unit
							unit = handle('UNIT', unit)
						else:
							# name
							name = handle('NAME', name)
				else:
					handle(None, '')
