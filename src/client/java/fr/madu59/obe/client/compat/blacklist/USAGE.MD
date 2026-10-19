You can blacklist certain blocks from being modified by OBE using a config file.

1. Create a file named "obe-blacklist.json"
2. Fill in either or both of these keys:
   - `blocks`: exact block ids to blacklist. `some_mod:*` blacklists every block of that mod.
   - `mods`: a mod id, followed by the OBE groups to blacklist for that mod. Use `["*"]` for all groups.

Example:
```json
{
    "blocks": ["minecraft:chest", "some_mod:*"],
    "mods": {
        "lootr": ["chest"],
        "betterend":["chest","bell"],
        "some_mod": ["*"]
    }
}
```
Here `some_mod` is blacklisted twice, once in `blocks` and once in `mods`. Either entry alone has the same effect: OBE will not change anything from that mod.

List of all the OBE groups: chest, bell, skull, banner, shulker_box, decorated_pot, copper_golem_statue, sign, hanging_sign, bed