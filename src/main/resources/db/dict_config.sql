--https://github.com/postgrespro/hunspell_dicts

CREATE TEXT SEARCH DICTIONARY russian_hunspell (
TEMPLATE = ispell,
  DictFile = ru_ru,
  AffFile = ru_ru,
  StopWords = russian
);

COMMENT ON TEXT SEARCH DICTIONARY russian_hunspell IS 'hunspell dictionary for russian language';

CREATE TEXT SEARCH CONFIGURATION russian_hunspell (
COPY = simple
);

COMMENT ON TEXT SEARCH CONFIGURATION russian_hunspell IS 'hunspell configuration for russian language';

ALTER TEXT SEARCH CONFIGURATION russian_hunspell
ALTER MAPPING FOR asciiword, asciihword, hword_asciipart
WITH russian_hunspell, english_stem;

ALTER TEXT SEARCH CONFIGURATION russian_hunspell
ALTER MAPPING FOR word, hword, hword_part
WITH russian_hunspell, russian_stem;

SET default_text_search_config = 'russian_hunspell';