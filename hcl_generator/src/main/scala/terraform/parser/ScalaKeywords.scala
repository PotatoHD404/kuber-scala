package terraform.parser

val keywords: Set[String] = Set(
  "abstract",
  "case",
  "catch",
  "class",
  "def",
  "do",
  "else",
  "extends",
  "false",
  "final",
  "finally",
  "for",
  "forSome",
  "if",
  "implicit",
  "import",
  "lazy",
  "match",
  "new",
  "null",
  "object",
  "override",
  "package",
  "private",
  "protected",
  "return",
  "sealed",
  "super",
  "this",
  "throw",
  "trait",
  "try",
  "true",
  "type",
  "val",
  "var",
  "while",
  "with",
  "yield"
)

implicit class RichString(input: String) {
  def escapeKeywords: String = {
    var result = input
    for(keyword <- keywords){
      result = result
        .replace(s"$keyword:", s"`$keyword`:")
        .replace(s".$keyword", s".`$keyword`")
    }
    result
  }
}