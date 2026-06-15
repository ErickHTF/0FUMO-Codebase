INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Respiração 4-7-8', 'Inspire por 4 segundos, segure por 7 e expire por 8. Repita 3 vezes.', 'respiração', 'estresse'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Respiração 4-7-8');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Caminhada de 5 minutos', 'Saia do ambiente e caminhe por 5 minutos prestando atenção na respiração.', 'exercício', 'estresse'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Caminhada de 5 minutos');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Beber água gelada', 'Beba um copo de água gelada lentamente. A sensação ajuda a reduzir a fissura.', 'hábito substituto', 'estresse'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Beber água gelada');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Conversar com alguém', 'Ligue para um amigo ou familiar. A interação social reduz a vontade de fumar.', 'social', 'social'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Conversar com alguém');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Mascar chiclete', 'Mascar chiclete sem açúcar ajuda a ocupar a boca e reduzir a vontade.', 'hábito substituto', 'social'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Mascar chiclete');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Ouvir música relaxante', 'Coloque fones e ouça uma playlist calma por 5-10 minutos.', 'relaxamento', 'social'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Ouvir música relaxante');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Jogo no celular', 'Jogue algo leve por alguns minutos para distrair a mente do tédio.', 'distração', 'tédio'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Jogo no celular');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Alongamento', 'Faça uma sequência de alongamentos por 5 minutos. Melhora o foco e reduz a ansiedade.', 'exercício', 'tédio'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Alongamento');

INSERT INTO relaxation_resources (title, description, category, trigger)
SELECT 'Ler um artigo', 'Leia algo interessante por 10 minutos para ocupar a mente.', 'distração', 'tédio'
WHERE NOT EXISTS (SELECT 1 FROM relaxation_resources WHERE title = 'Ler um artigo');
