# GE-Prospector Setup Guide

## Development Environment Setup

### Prerequisites
1. Java Development Kit 11 or higher
2. Maven 3.6.3 or higher
3. Git
4. IDE with RuneLite plugin support (IntelliJ IDEA recommended)

### RuneLite Development Setup
1. Clone RuneLite repository (for API reference)
```bash
git clone https://github.com/runelite/runelite.git
cd runelite
./gradlew clean build
```

2. Setup Plugin Project
```bash
# In your project directory
git clone https://github.com/yourusername/ge-prospector.git
cd ge-prospector
```

### Configuration Files

1. RuneLite Plugin Configuration
```properties
# runelite-plugin.properties
displayName=GE Prospector
author=Your Name
support=https://github.com/yourusername/ge-prospector
description=Grand Exchange flipping assistant with time estimation
tags=grand exchange,flipping,money making,merching
plugins=com.prospector.ProspectorPlugin
```

2. Build Configuration
```groovy
// build.gradle
plugins {
    id 'java'
}

dependencies {
    compileOnly group: 'net.runelite', name: 'client', version: runeLiteVersion
    
    // RuneLite API dependencies
    compileOnly group: 'net.runelite', name: 'runelite-api', version: runeLiteVersion
    compileOnly group: 'net.runelite', name: 'jshell', version: runeLiteVersion
    
    // Testing dependencies
    testImplementation group: 'net.runelite', name: 'client', version: runeLiteVersion
    testImplementation group: 'net.runelite', name: 'jshell', version: runeLiteVersion
}
```

### IDE Setup

#### IntelliJ IDEA
1. Install RuneLite Plugin Development plugin
2. Import project as Gradle project
3. Set JDK 11 as project SDK
4. Enable annotation processing for Lombok

#### VS Code
1. Install Java Extension Pack
2. Install Gradle Extension
3. Install Lombok Annotations Support

### Running the Plugin

1. Development Mode
```bash
# Run RuneLite in development mode
./gradlew clean build run
```

2. Debug Mode
```bash
# Run with debug port 8000
./gradlew clean build run --debug-jvm
```

### Testing

1. Unit Tests
```bash
./gradlew test
```

2. Integration Tests
```bash
./gradlew integrationTest
```

### RuneLite API Integration

#### Required Imports
```java
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.*;
import net.runelite.client.game.*;
import net.runelite.client.plugins.*;
import net.runelite.client.ui.*;
```

#### Plugin Structure
```plaintext
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── prospector/
│   │           ├── api/
│   │           ├── model/
│   │           ├── service/
│   │           └── ui/
│   └── resources/
│       └── net.runelite.client.plugins.prospector/
└── test/
    └── java/
        └── com/
            └── prospector/
```

### Debugging Tips

1. RuneLite Developer Tools
- Enable developer tools in RuneLite settings
- Use widget inspector for UI development
- Monitor event system with event logger

2. Common Issues
- Plugin not loading: Check runelite-plugin.properties
- UI not updating: Verify event subscriptions
- API connection issues: Check rate limits
- Cache problems: Clear RuneLite cache

### Publishing

1. Prepare for Release
- Update version number
- Test with latest RuneLite client
- Verify all features work
- Update documentation

2. Submit to Plugin Hub
- Follow submission guidelines
- Include all required metadata
- Provide clear description
- Add demonstration screenshots

### Resources

1. Documentation
- RuneLite Wiki
- Plugin Hub Guidelines
- API Documentation
- Development Guides

2. Support Channels
- RuneLite Discord (#plugin-hub channel)
- GitHub Issues
- Developer Forums