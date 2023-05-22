package terraform.parser

def toCamelCase(str: String): String = {
  "_([a-z\\d])".r.replaceAllIn(str, _.group(1).toUpperCase())
}
