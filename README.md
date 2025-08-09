PortalRegionPlugin is a Minecraft plugin that lets players create **region-based portals** for automatic teleportation to specific locations. Fully **Folia-compatible**, ensuring safe performance on multi-threaded servers.

## Features
- Create and delete portals using coordinate regions.
- Set teleport destinations across different worlds.
- Saves portal data automatically in `config.yml`.
- Works with **Paper** and **Folia** servers.

## Commands
```bash
/portalregion create <name> <x1> <y1> <z1> <x2> <y2> <z2> <world> <destX> <destY> <destZ> [yaw] [pitch]
/portalregion delete <name>
/portalregion list
```

## Installation
1. Download the latest `.jar` file from the [Releases](../../releases) page.
2. Place it into your server's `plugins` folder.
3. Start the server to generate a default `config.yml`.

## Compatibility
- **Minecraft**: 1.20+
- **Server**: Paper, Folia

## License
Released under the [MIT License](LICENSE).
