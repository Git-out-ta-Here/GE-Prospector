Third Party Client Guidelines
01 June 2022
Third Party Client Guidelines
The following guidelines have been put in place to protect the interests of the game by outlining what features aren't acceptable, and by acting if these features are abused. We'd like to note that it's impossible to list every potential feature, so here we give general principles and examples. Outlining and enforcing these rules is vital to protect the integrity of the game, as well as its economy and long-term health. This isn't about us unfairly punishing players by making the game more difficult.

As per our most recent statement, we'd like to remind you that the only third-party clients that are approved for use are those in our Approved Clients list below. The use of any other third-party client may result in permanent action being taken against your account.

Approved Clients

RuneLite
HDOS

Guidelines:

Any features which aid any boss fights by doing any of the following are prohibited (this includes all Raids sub-bosses, Slayer bosses, Demi-bosses, and wave-based minigames, including the Fight Caves and Inferno):

Next attack prediction (timing or attack style)
Projectile targets, or target locations, as well as impact locations
Prayer switching indicators
Attack counters
Anything that automatically indicates where to stand, or not to stand. This applies to only automatic indicators, and not tiles which have been manually marked.

In addition, the following menu changes are prohibited:

Any addition of new menu entries which cause actions to be sent to the server
Menu option changes for specifically Construction, Blackjacking and Attack (or similar) options in PvP.

The following interface changes are prohibited:

Any unhiding of interface components, such as the special attack bar and including the minimap (for example, in Barrows)
Any movement or resizing of click zones for 3d components
Any movement or resizing of click zones for any interface or component under combat options, inventory, worn equipment, or spell book
Any resizing of click zones for any interface or component under prayer book

Here’s a larger list of specific feature examples which are unacceptable. This is still not exhaustive but should give a clearer idea what is not acceptable.

Any feature which...	Category
Indicates where projectiles will land	Combat
Indicates the time where a boss mechanic may start or end	Combat
Adds additional visual or audio indicators of a boss mechanic except in cases where this is a manually triggered external helper.	Combat
Indicates what prayers to use in what order, for example in the Cerberus boss fight	Combat
Indicates players in an opposing clan in PVP	Combat
Helps you to know when to 'flinch' your opponent	Combat
Indicates how long an opponent is frozen for	Combat
Automatically informs you where or where not to stand in a boss fight. This applies to only automatic indicators, and not tiles which have been manually marked.	Combat
Makes it easier to target 3D entities with a spell by removing some options	Combat
Indicates which player an NPC is focused on	Combat
Indicates to other players what items/loot will drop in PvP	Combat
Indicates whom your opponent's opponent is in PVP	Combat
Indicates which prayer to use in any combat situation	Combat
Can resize interface components on the Spellbook interface	Combat
Can resize interface components on the Prayer interface	Combat
Offers additional information about other players, with the purpose of scouting PvP targets	Combat
Which gives summary information about a group of players, such as how many players are attackable and not in your own CC, what prayers they are mostly using, etc	Combat
Removes or deprioritises attack, cast (or similar) options from the minimenu in PVP	Combat
Reveals the maze layout in the Sotetseg boss fight in Theatre of Blood	Combat
Adds additional menu entries which cause actions to be sent to the server, with the exceptions of menu entries for the Max Cape and Achievement diary capes.	Menus
Modifies menu options for blackjacking, such as 'Pickpocket' and 'Knock-out'	Menus
Reorders or removes player-based options, such as 'Trade with'	Menus
Offers world interaction in any detached camera mode	Misc

Any features which act similarly to those described in the above list can also be considered unacceptable, and as we become aware of features we reserve the right to add features to the list in future.

We’ll continue to work with players and developers alike in order to make it perfectly clear what is not acceptable.

The Old School Team

Third party dependencies
We require any dependencies that are not a transitive dependency of runelite-client to be have their cryptographic hash verified during the build to prevent supply chain attacks and ensure build reproducability. To do this we rely on Gradle's dependency verification. To add a new dependency, add it to the thirdParty configuration in package/verification-template/build.gradle, then run ../gradlew --write-verification-metadata sha256 to update the metadata file. A maintainer must then verify the dependencies manually before your pull request will be merged.

Plugin resources
Resources may be included with plugins, which are non-code and are bundled and distributed with the plugin, such as images and sounds. You may do this by placing them in src/main/resources. Plugins on the pluginhub are distributed in .jar form and the jars placed into the classpath. The plugin is not unpacked on disk, and you can not assume that it is. This means that using https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getResource-java.lang.String- will return a jar-URL when the plugin is deployed to the pluginhub, but in your IDE will be a file-URL. This almost certainly makes it behave differently from how you expect it to, and isn't what you want. Instead, prefer using https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getResourceAsStream-java.lang.String-.

# RuneLite Plugin Development Rules

## Code Style Guidelines

### Package Structure
```plaintext
com.prospector/
├── api/       # External API clients and interfaces
├── model/     # Data models and enums
├── service/   # Business logic and services
└── ui/        # RuneLite UI components and overlays
```

### Naming Conventions
1. RuneLite Components
- *Plugin classes: End with "Plugin"
- *Config interfaces: End with "Config"
- *Overlay classes: End with "Overlay"
- *Panel classes: End with "Panel"

2. Event Handlers
- Methods handling events should start with "on"
- Example: `onGrandExchangeOfferChanged`

### RuneLite API Usage

1. Client Access
```java
// DO:
@Inject
private Client client;

// DON'T:
private static Client client;
```

2. Event Bus Subscription
```java
// DO:
@Subscribe
public void onGameStateChanged(GameStateChanged event)

// DON'T:
client.getCallbacks().subscribe(event -> {});
```

3. Configuration
```java
// DO:
@ConfigItem(
    keyName = "configKey",
    name = "Config Name",
    description = "Config description"
)

// DON'T:
private static final String CONFIG_KEY = "configKey";
```

### Threading Guidelines

1. Game Thread Operations
- All game state modifications must be on game thread
- Use `clientThread.invoke()` for game state changes
- Keep operations short to prevent game lag

2. Background Operations
- Use CompletableFuture for async API calls
- Don't block game thread with long operations
- Handle timeouts appropriately

### Resource Management

1. Memory Usage
- Cache smartly with TTL
- Clean up resources in shutdown()
- Use weak references where appropriate
- Monitor memory usage

2. API Rate Limiting
- Respect Wiki API limits (300 req/min)
- Implement backoff strategies
- Cache frequently accessed data
- Use bulk endpoints when possible

### Error Handling

1. Game State
```java
// DO:
if (client.getGameState() != GameState.LOGGED_IN) {
    return;
}

// DON'T:
if (!isLoggedIn) {
    return;
}
```

2. Null Checking
```java
// DO:
@Nullable
private ItemContainer getGrandExchangeContainer() {
    return client.getItemContainer(InventoryID.GRAND_EXCHANGE);
}

// DON'T:
private ItemContainer container = client.getItemContainer(InventoryID.GRAND_EXCHANGE);
```

### UI Guidelines

1. Panel Components
- Use RuneLite's MaterialTab for navigation
- Follow RuneLite color scheme
- Use standard icon sizes (16x16, 32x32)
- Implement proper scaling support

2. Overlays
- Register/unregister properly
- Respect game viewport
- Handle layer priority correctly
- Clean up resources

### Testing Requirements

1. Unit Tests
- Test all configuration options
- Verify event handling
- Validate calculations
- Mock external dependencies

2. Integration Tests
- Test RuneLite API interaction
- Verify overlay rendering
- Check panel functionality
- Validate event flow

### Documentation

1. Code Comments
- Document complex calculations
- Explain RuneLite API usage
- Note thread safety considerations
- Document event handling logic

2. Configuration
- Clear description for each option
- Note default values
- Explain value constraints
- Document dependencies

### Plugin Submission

1. Required Files
- runelite-plugin.properties
- README.md with clear description
- LICENSE file
- Icon.png (128x128)

2. Quality Checks
- No checkstyle violations
- All tests passing
- Documentation complete
- Performance metrics met

### Security

1. Data Handling
- No sensitive data storage
- Secure API communication
- Input validation
- Rate limit compliance

2. Resource Protection
- Prevent memory leaks
- Handle timeout scenarios
- Implement fail-safes
- Monitor resource usage