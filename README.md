## Optimized Block Entities (OBE)

OBE is a fully client side mod that significantly improves the rendering speed of block entities but not just that!

You won't lose animations, this mod keeps the animations of animated block entities likes chest and shulker boxes, it also supports **EMF** and **ETF**, which means it is compatible with **FA: Objects**!

You should also check out [Better Block Entities](https://modrinth.com/mod/better-block-entities), it was the first mod to optimize block entities on 1.21+ and did that very well. Both mods do not work in the same way, but he gave me some inspiration and 1 or 2 clever tricks.

## How it works

This mod renders supported block entities just like any other block, this means they are meshed into the terrain and rendering terrain is much faster than rendering entities so it gives a **HUGE** performances boost. To keep animations, they are only rendered through the terrain when they are static, otherwise it goes through the vanilla renderer.

## Features

- Improved FPS : Huge performance boost that scales with the number of block entities visible.
- Better lighting on block entities : they now support smooth lighting and ambient occlusion.
- Better render distance : block entities can be seen from a lot further away
- Config : Every block entity optimization can be toggled, same for the ambient occlusion.

## Performances

<table>
  <caption>FPS: 170 OBE / 130 BBE / 10 Vanilla</caption>
  <tr><td><img alt = "170 fps with OBE" src = "https://cdn.modrinth.com/data/cached_images/aa06e30a06eb1b9b42b826251b5530bffee379a7.png"></td>
  <td><img alt ="130 fps with BBE" src = "https://cdn.modrinth.com/data/cached_images/cc201d847b46db942ce2431def7fe7b937598294.png"></td>
  <td><img alt = "10 FPS vanilla" src = "https://cdn.modrinth.com/data/cached_images/ca48790971ca5cd4f9da5332e524f94e7256bccb.png"></td></tr>
</table>

## Compatibility:

- Compatible with EMF/ETF and any resource packs made for those (like FA: Objects) *Since OBE 1.1.4*
- Compatible with Quark's chests *Since OBE 1.1.3 and Quark 4.1-481*

## Why 

Mods like this already exists like:
- [BBE](https://modrinth.com/mod/better-block-entities), but it requires Sodium to run, isn't available on Neoforge (at least not at the time of OBE development) and heavily modifies Minecraft's rendering pipeling
- [EBE](https://modrinth.com/mod/ebe), but it is sadly not updated anymore and isn't available on Neoforge

Both are awesome, the developers did great work but I wanted to make one that was:
- Updated
- Less intrusive, which means better compatibility with other mods
- Available on Neoforge as well as Fabric
and I even managed to make it faster somehow even though they are both really well done and optimized

## Modpacks

Feel free to include this mod in any of your modpacks, telling me would be nice but is not mandatory.