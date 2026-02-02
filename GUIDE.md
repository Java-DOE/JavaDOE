# Publishing Java DOE Library to GitHub Packages

This document outlines the process of publishing the Java Design of Experiments (DOE) library to GitHub Packages, following the official GitHub documentation.

## Prerequisites

1. **GitHub Account**: You need a GitHub account with appropriate permissions.
2. **Personal Access Token**: Create a personal access token with `write:packages`, `read:packages`, `delete:packages`, and `repo` scopes.
3. **Maven**: Ensure Maven is installed and configured on your system.
4. **Git**: Ensure Git is installed and configured.

## Step 1: Configure GitHub Personal Access Token

1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token"
3. Select the following scopes:
   - `repo` - to read and write repository contents
   - `write:packages` - to upload packages to GitHub Packages
   - `read:packages` - to download packages from GitHub Packages
   - `delete:packages` - to delete packages from GitHub Packages
4. Copy the generated token

## Step 2: Configure Maven Settings

Create or update your `~/.m2/settings.xml` file with the following content:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <name>GitHub Java-DOE Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/Java-DOE/JavaDOE</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

Replace `YOUR_GITHUB_USERNAME` with your GitHub username and `YOUR_PERSONAL_ACCESS_TOKEN` with the personal access token generated in Step 1.

## Step 3: Repository Setup

Ensure your repository is set up correctly:

1. The repository name should match what's specified in the POM: `Java-DOE/JavaDOE`
2. Make sure the remote URL is correct: `https://github.com/Java-DOE/JavaDOE.git`
3. Verify that your local code is up-to-date with the remote repository

## Step 4: Build the Project

Before publishing, ensure the project builds correctly:

```bash
mvn clean verify
```

This command will:
- Compile all source code
- Run all tests
- Generate JAR files (main, sources, and javadoc)
- Verify that everything is working properly

## Step 5: Publish the Package

To publish the package to GitHub Packages, run:

```bash
mvn deploy
```

This command will:
- Package the code into JAR files
- Upload the package to GitHub Packages using the credentials from settings.xml
- Make the package available at `https://maven.pkg.github.com/Java-DOE/JavaDOE`

## Step 6: Verify Publication

After publishing, you can verify the package was published successfully by:

1. Going to your GitHub repository → Settings → Packages
2. You should see the newly published package there
3. The package will be accessible using the coordinates defined in your POM:
   - Group ID: `com.doe`
   - Artifact ID: `doe-generator`
   - Version: `1.0.0`

## Using the Published Package

Other developers can use your package by:

1. Adding the GitHub Packages repository to their `pom.xml` or `settings.xml`:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Java-DOE/JavaDOE</url>
  </repository>
</repositories>
```

2. Adding the dependency to their `pom.xml`:

```xml
<dependency>
  <groupId>com.doe</groupId>
  <artifactId>doe-generator</artifactId>
  <version>1.0.0</version>
</dependency>
```

3. Configuring their `~/.m2/settings.xml` with appropriate GitHub credentials as shown in Step 2.

## Troubleshooting

Common issues and solutions:

- **Authentication Error**: Double-check your personal access token and settings.xml configuration
- **Repository Not Found**: Verify the repository URL in your POM's `<distributionManagement>` section
- **Permission Denied**: Ensure your personal access token has the required scopes
- **Build Failure**: Run `mvn clean compile` first to ensure the project builds correctly

## Maintaining the Package

To publish updates:
1. Update the version number in `pom.xml`
2. Commit and push your changes
3. Run `mvn deploy` again

GitHub Packages will store multiple versions of your package, allowing users to depend on specific versions.

## Security Considerations

- Store your personal access token securely and never commit it to version control
- Use environment variables or secure credential storage when automating deployments
- Regularly rotate your personal access tokens
- Limit token scope to only what is necessary