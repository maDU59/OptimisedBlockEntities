package fr.madu59.obe.client.compat.sophisticatedstorage;

/** Pure visibility state for one pinned upgrade-render-info entry. */
public record UpgradeVisualState(boolean stackPresent) {
    public static final UpgradeVisualState EMPTY = new UpgradeVisualState(false);
    public static final UpgradeVisualState PRESENT = new UpgradeVisualState(true);
}
