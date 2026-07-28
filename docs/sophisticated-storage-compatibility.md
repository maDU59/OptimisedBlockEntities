# Sophisticated Storage compatibility

OBE's Sophisticated Storage support is an optional client module. OBE remains fully usable when neither Sophisticated Storage nor Sophisticated Core is installed.

## Supported target

- Minecraft / NeoForge: 1.21.1
- Sophisticated Storage: declared compatible with `1.5.70` and later (NeoForge version range `[1.5.70,)`)
- Pinned compile target: Sophisticated Storage `1.5.70.1941` (CurseForge file `8389899`) and Sophisticated Core `1.4.77.2173` (CurseForge file `8467455`)
- Supplied ABI matrix: Storage `1.5.70.1941`, `1.5.71.1949`, `1.5.72.1956`, `1.5.73.1960`, `1.5.76.1972`, `1.5.77.1982`, `1.5.79.1991`, and `1.5.80.1999`
- Full live-validation endpoints: Storage `1.5.70.1941` and `1.5.80.1999`, both with Core `1.4.77.2173`

The compatibility code is compiled against the exact pinned jars. At loading time, the mixin plugin inspects the installed renderer bytecode and selects the legacy six-argument shulker body used through the supplied 1.5.73 jar or the seven-argument body used by the supplied 1.5.76--1.5.80 jars. Chest support is independently gated on its entry and shell-subrenderer contract. If a future jar matches neither supported contract, OBE does not register terrain rendering or dynamic predicates for that affected type and leaves Sophisticated's BER untouched.

A narrow guarded constructor redirect adapts Storage 1.5.70--1.5.73's six-argument item-display category allocation to Core 1.4.77's seven-argument constructor while preserving legacy slot deselection. The plugin requires both sides of that fact: Core must lack the six-argument constructor and expose the seven-argument constructor, and the installed Storage jar must contain the exact legacy allocation in the expected call site. Storage 1.5.76+ uses the newer call natively, so the bridge is not applied.

## Scope and rendering behavior

Sophisticated chests and shulker boxes are optimized while fully closed. Barrels and limited barrels are excluded.

Static shell submissions are moved into terrain geometry. `renderBoth(true)` deliberately remains enabled: the block entity must stay discoverable so opening animations and per-frame overlays can start immediately without a chunk remesh. A separate client-only dynamic-render predicate registry decides at each BER invocation whether the complete renderer is needed. Missing predicates, predicate exceptions, and unresolved state all fail open to the original renderer.

For an ordinary fully closed chest or shulker with usable committed terrain and no visible dynamic visual, OBE now skips the complete Sophisticated renderer. Under Sodium, the same generic decision runs before renderer lookup/extraction. OBE does not put held-tool decisions into `SpecialBlockEntityRenderingManager`: that manager also controls remesh-stable compiled-section list removal, which cannot safely represent state that changes when the player swaps hands or tool modes.

The original renderer remains active for opening/closing state, displayed items, visible or held-tool-previewed upgrade slots, disabled-upgrade indicators, visible or previewed padlocks, hidden-tier previews, and any unknown or failed terrain state. The existing shell-level interception remains installed as the hybrid fallback: when the BER runs for an overlay while terrain owns the shell, only its shell submissions are suppressed.

Chest geometry uses Sophisticated's/Minecraft's baked model layers. Chest material mappings come from `StorageTextureManager`, including resource-pack and custom wood definitions. A block-atlas sprite source scans merged `storage_texture_definitions` before stitching, follows parents with cycle protection, and submits referenced textures in time for baking.

## Safety and invalidation

Appearance and model caches contain immutable value keys only. The arbitrary model cache is capped at 4096 entries and cleared on resource reload.

Client packet updates invalidate appearance state. Double chests dirty both loaded halves, including pairs split across chunk and section boundaries. A terrain-to-terrain rebuild commits the newly prepared appearance at the section-completion barrier.

If appearance resolution, a material/sprite lookup, or model baking fails, OBE requests ENTITY mode and keeps the entire original renderer available. Static-shell suppression follows the active committed mode, so it is released only after the fallback removal rebuild commits. A different appearance or resource generation can retry safely.

The early gate requires a supported and enabled `BlockEntityExt`, no `forceEntity`, active committed `TERRAIN` mode, committed terrain for the current special-model cache generation, no pending fallback/current-generation failure, and the exact closed animation endpoint. Opening therefore executes the BER before the first moving frame; closing returns to whole-renderer skipping only after terrain recommits.

## Dynamic predicate API

Client integrations can register a per-frame predicate with:

```java
RegistryApi.registerDynamicRenderPredicate(blockEntityType, blockEntity -> shouldRenderDynamic(blockEntity));
```

`true` executes the BER; `false` skips it for that frame. The registry is keyed only by `BlockEntityType<?>` and stores predicates, not levels, block entities, players, renderers, or world references.

## Diagnostics

Debug diagnostics report appearance resolutions, model builds, cache hits/misses/evictions, atlas discoveries/misses, fallback count, static-shell suppression/pass-through calls, completed original renderers, whole chest/shulker skips and executions, Sodium pre-extraction skips, terrain-readiness rejections, and one primary dynamic reason per execution. Normal operation remains quiet except for actionable failures and reload summaries.

For the endpoint live results, see [the visual matrix](verification/sophisticated-storage-visual-matrix.md) and [the performance comparison](verification/sophisticated-storage-performance.md).
