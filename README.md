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
  <caption>FPS: 760 OBE / 520 BBE / 30 Sodium only</caption>
  <tr><td><img alt = "OBE: 760FPS" src = "https://cdn.modrinth.com/data/cached_images/6a8cc4f821534d1fa6acae0d24c4626db12fbc3e.png"></td>
  <td><img alt = "BBE: 520FPS" src = "https://cdn.modrinth.com/data/cached_images/16b9fa3f8a2de7cc2f588c624f04c20816f0ce90.png"></td>
  <td><img alt = "Sodium only: 30FPS" src = "https://cdn.modrinth.com/data/cached_images/1ccae3753f9912e1fecbdf26d9990bd986c19000.png"></td></tr>
</table>

<sub>*Performance tests ran on Minecraft 26.1.2 with OBE 1.1.27 and BBE 1.3.5 with Sodium 0.9.0 in HD*</sub>

## Compatibility:

- Compatible with EMF/ETF and any resource packs made for those (like FA: Objects) *Since OBE 1.1.4*
- Compatible with Quark's chests *Since OBE 1.1.3 and Quark 4.1-481*
- Compatible with Lootr *Since OBE 1.1.23*

## Why 

Mods like this already exists like:
- [BBE](https://modrinth.com/mod/better-block-entities), but it requires Sodium to run, isn't available on Neoforge (at least not at the time of OBE development) and heavily modifies Minecraft's rendering pipeling
- [EBE](https://modrinth.com/mod/ebe), but it is sadly not updated anymore and isn't available on Neoforge

Both are awesome, the developers did great work but I wanted to make one that was:
- Updated
- Less intrusive, which means better compatibility with other mods
- Available on Neoforge as well as Fabric
- Include a way to support modded block entities

and I even managed to make it faster somehow even though they are both really well done and optimized

## Trouble shooting:

Invisible block entities:

> Only vanilla, indigo and sodium (embeddium on forge 1.20.1) rendering pipelines are fully supported, issues may happen with other ones like Optifine, Embeddium, Optifabric etc... in that case it is recommended to disable the OBE optimizations causing the issues using OBE's config screen.

Crash on 1.20.1 Forge with Embeddium:

> You need the Forgified Fabric API mod if you want to use OBE and Embeddium at the same time.

## Modpacks

Feel free to include this mod in any of your modpacks, telling me would be nice but is not mandatory.