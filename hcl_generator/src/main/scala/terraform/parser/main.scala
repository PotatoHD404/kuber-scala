package terraform.parser

import terraform.parser.DocsParser

import scala.io.Source

object A {
  opaque type B = String
}

case class A(a: A.B)

@main
def main(): Unit = {

}