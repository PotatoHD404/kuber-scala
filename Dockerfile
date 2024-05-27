# Build stage
FROM openjdk:11-jdk AS build

# Install Scala 3 and sbt
RUN apt-get update && apt-get install -y curl gzip

# Download Coursier CLI binary and decompress it
RUN curl -fLo /tmp/cs.gz https://github.com/coursier/coursier/releases/download/v2.1.10/cs-x86_64-pc-linux.gz \
    && gzip -d /tmp/cs.gz \
    && mv /tmp/cs /usr/local/bin/cs \
    && chmod +x /usr/local/bin/cs

# Setup PATH
ENV PATH="/root/.local/share/coursier/bin:${PATH}"

# Install sbt
RUN cs install sbt

WORKDIR /app

COPY . /app

RUN sbt "autoscaler/assembly"

# Run stage
FROM openjdk:11-jre

WORKDIR /app

COPY --from=build /app/autoscaler/target/scala-3.2.2/autoscaler.jar /app/autoscaler.jar

CMD ["java", "-jar", "autoscaler.jar"]