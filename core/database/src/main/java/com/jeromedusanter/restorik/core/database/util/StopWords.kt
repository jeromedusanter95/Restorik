package com.jeromedusanter.restorik.core.database.util

private val frenchStopWordList = setOf(
    "le", "la", "les", "un", "une", "des",
    "de", "du", "à", "au", "aux",
    "et", "ou", "mais", "donc", "or", "ni", "car",
    "ce", "cet", "cette", "ces",
    "mon", "ma", "mes", "ton", "ta", "tes", "son", "sa", "ses",
    "notre", "votre", "leur", "nos", "vos", "leurs",
    "je", "tu", "il", "elle", "on", "nous", "vous", "ils", "elles",
    "me", "te", "se", "moi", "toi", "lui",
    "avec", "sans", "pour", "par", "dans", "sur", "sous", "vers", "chez",
    "qui", "que", "quoi", "dont", "où",
    "en", "y"
)

private val englishStopWordList = setOf(
    "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
    "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
    "to", "was", "will", "with", "i", "me", "my", "you", "your",
    "we", "our", "they", "their", "this", "these", "those",
    "but", "or", "so", "if", "when", "where", "who", "which",
    "have", "had", "do", "does", "did", "can", "could", "would", "should"
)

private val combinedStopWordList = frenchStopWordList + englishStopWordList

fun removeStopWords(query: String): String {
    return query
        .split(" ")
        .filter { word -> word.lowercase() !in combinedStopWordList }
        .joinToString(separator = " ")
        .trim()
}
