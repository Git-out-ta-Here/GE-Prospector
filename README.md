# GE-Prospector

A RuneLite plugin for intelligent Grand Exchange flipping with time estimation.

## Features

- Real-time profit margin tracking using RuneLite's GE offer events
- Smart flip time estimation with trading volume analysis
- Built-in RuneLite overlay system for price tracking
- Customizable price alerts integrated with RuneLite notifications
- Historical data analysis with trend visualization
- Profit/hour calculations and optimization
- Full RuneLite widget system integration

## Installation

1. Open RuneLite Settings (⚙️)
2. Click the Plugin Hub icon (🔌)
3. Search for "GE Prospector"
4. Click Install
5. Enable the plugin in RuneLite settings

## Quick Start

1. Open the Grand Exchange in RuneLite
2. Click the GE Prospector icon in the sidebar (📊)
3. View current profitable items with integrated RuneLite overlays
4. Click on an item to see detailed analysis
5. Use the time indicators for flip speed estimation:
   - 🟢 Very High Volume (< 5 mins)
   - 🟡 High Volume (5-15 mins)
   - 🟠 Medium Volume (15-30 mins)
   - 🔴 Low Volume (30-60 mins)
   - ⚫ Very Low Volume (> 1 hour)

## Configuration

### Basic Settings
- Minimum profit margin threshold
- Price alert notifications (uses RuneLite's notification system)
- Update frequency for price checks
- Display preferences for overlays

### Advanced Settings
- Custom time windows for analysis
- Risk tolerance levels for recommendations
- Volume thresholds for time estimation
- Alert conditions with RuneLite notifications

## Integration

The plugin leverages several RuneLite APIs:
- Client API for game state management
- ItemManager for item data and sprites
- ConfigManager for settings
- OverlayManager for UI elements
- EventBus for real-time updates

For detailed API documentation, see [TECHNICAL_REQUIREMENTS.md](DOCS/TECHNICAL_REQUIREMENTS.md)

## Development

See the following guides:
- [Development Roadmap](DOCS/DEVELOPMENT.md)
- [Technical Requirements](DOCS/TECHNICAL_REQUIREMENTS.md)
- [Setup Guide](DOCS/SETUP.md)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow RuneLite plugin guidelines
4. Test thoroughly
5. Create a Pull Request

## License

This project is licensed under the BSD-2-Clause license - see the [LICENSE](LICENSE) file for details.

## Support

- Create an issue on GitHub
- Check RuneLite plugin support forums
- Join our Discord community

