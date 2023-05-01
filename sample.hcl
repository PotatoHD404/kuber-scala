provider "aws" {
  region = "us-west-2"
}

resource "aws_vpc" "example" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_instance" "example" {
  ami           = "Canonical:UbuntuServer:18.04-LTS:latest"
  instance_type = "Standard_B1s"

  tags = {
    Name = "example"
  }
}