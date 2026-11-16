package fr.madu59.obe.client.compat.immediatelyfast;

import net.raphimc.immediatelyfast.ImmediatelyFast;

public class ImmediatelyFastCompat {
    public static boolean isSignTextBufferingEnabled() {
        return ImmediatelyFast.config.experimental_sign_text_buffering;
    }
}
