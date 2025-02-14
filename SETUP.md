# GE-Prospector Setup Guide

## Development Environment Setup

### Prerequisites
1. Java Development Kit 11 or higher
2. Git
3. Gradle 7.0+
4. IntelliJ IDEA (recommended) or Eclipse
5. RuneLite Development Tools

### Initial Setup

1. Clone RuneLite Repository
```bash
git clone https://github.com/runelite/runelite.git
cd runelite
./gradlew clean build
```

2. Configure IDE
- Import Gradle project
- Enable annotation processing
- Set Java language level to 11
- Configure code style to match RuneLite

### Plugin Development Setup

1. Project Structure
```plaintext
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── geprospect/
│   │           ├── GEProspectorPlugin.java
│   │           ├── GEProspectorConfig.java
│   │           └── ui/
│   └── resources/
│       └── com/
│           └── geprospect/
│               └── icons/
└── test/
    └── java/
        └── com/
            └── geprospect/
                └── GEProspectorPluginTest.java
```

2. Dependencies
Add to build.gradle:
```gradle
dependencies {
    compileOnly group: 'net.runelite', name: 'client', version: runeLiteVersion
    compileOnly group: 'org.projectlombok', name: 'lombok', version: '1.18.20'
    annotationProcessor group: 'org.projectlombok', name: 'lombok', version: '1.18.20'
    testImplementation group: 'junit', name: 'junit', version: '4.12'
}
```

### Configuration

1. RuneLite Plugin Configuration
```properties
# runelite-plugin.properties
displayName=GE Prospector
author=Your Name
support=https://github.com/yourusername/ge-prospector
description=Grand Exchange flipping assistant with time estimation
tags=grand exchange,flipping,money making,merching
plugins=com.geprospect.GEProspectorPlugin
```

2. API Configuration
```java
// src/main/resources/config.properties
ge.api.baseUrl=https://prices.runescape.wiki/api/v1/osrs
ge.api.timeout=5000
ge.api.retries=3
```

### Development Workflow

1. Building
```bash
# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Generate documentation
./gradlew javadoc
```

2. Running in Development
- Start RuneLite in development mode
- Enable developer tools
- Load plugin from classes

3. Debugging
- Use RuneLite developer tools
- Enable debug logging
- Monitor API calls
- Profile performance

### Testing Environment

1. Test Configuration
```java
// src/test/resources/test-config.properties
test.mode=true
test.data.path=src/test/resources/testdata
```

2. Mock Data Setup
- Create test data files
- Configure mock API responses
- Set up test database

### Deployment Preparation

1. Version Control
- Use semantic versioning
- Tag releases
- Update changelog

2. Release Checklist
- All tests passing
- Documentation updated
- Version bumped
- Changelog updated
- Release notes prepared

### Troubleshooting

1. Common Issues
- API connection problems
- Cache invalidation issues
- Memory management
- Thread synchronization

2. Debug Tools
- RuneLite developer tools
- Java profiler
- Memory analyzer
- Network monitor

### Resources

1. Documentation
- RuneLite Wiki
- OSRS API Documentation
- Java Swing UI Guidelines
- Grand Exchange API Reference

2. Support Channels
- RuneLite Discord
- GitHub Issues
- Developer Forums
- Community Resources