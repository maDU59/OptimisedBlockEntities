package fr.madu59.obe.client.compat.sophisticatedstorage;

import org.jetbrains.annotations.Nullable;

/** Tiny guarded-mixin extension for detecting static appearance changes. */
public interface SophisticatedAppearanceExt {
    @Nullable Object obe$appearanceFingerprint();
    void obe$appearanceFingerprint(Object fingerprint);
}
