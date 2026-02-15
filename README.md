# Java Design of Experiments (DOE)

A comprehensive Java library for Design of Experiments (DOE) algorithms including Box-Behnken, Central Composite, Factorial, and other DOE methods.

Link Maven Repository: https://mvnrepository.com/artifact/io.github.java-doe/doe-generator

## Features

- Box-Behnken Design
- Central Composite Design
- Full Factorial Design
- Fractional Factorial Design
- Various other DOE algorithms
- Comprehensive unit tests

## Installation

To use this library in your Maven project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.doe</groupId>
    <artifactId>doe-generator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### GitHub Packages Configuration

To install from GitHub Packages, you need to configure authentication in your `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

## Usage

Example usage:

```java
import com.doe.algorithms.BoxBehnkenDOE;
import org.apache.commons.math3.linear.RealMatrix;

public class Example {
    public static void main(String[] args) {
        // Generate a Box-Behnken design with 4 factors
        RealMatrix design = BoxBehnkenDOE.boxBehnkenDesign(4);
        System.out.println("Generated design matrix: " + design);
    }
}
```

## Building

To build the project:

```bash
mvn clean install
```

## Running Tests

To run the unit tests:

```bash
mvn test
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details.
