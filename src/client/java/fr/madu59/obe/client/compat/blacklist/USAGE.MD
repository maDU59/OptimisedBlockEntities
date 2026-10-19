You can blacklist certain blocks from being modified by OBE using a config file.

1. Create a file named "obe-blacklist.json"
2. Either add the identifiers of the blocks to blacklist or the group, then the mod id. An identifier will blacklist the block while a group then a list of mods will blacklist all the block entities from those mods that end up being in this group. Here's an exemple:
```json
{
    "mods": {
        "chest": ["lootr"]
    },
    "blocks": [
        "minecraft:chest"
    ]
}
```