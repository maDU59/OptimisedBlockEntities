package fr.madu59.obe.config.configscreen.entries.builders;

import java.util.function.BooleanSupplier;

import fr.madu59.obe.config.configscreen.MyConfigListWidget;
import fr.madu59.obe.config.configscreen.MyConfigListWidget.CategoryEntry;
import net.minecraft.ChatFormatting;

public class CategoryBuilder extends AbstractEntryBuilder{

    protected ChatFormatting[] style;

    public CategoryBuilder(MyConfigListWidget list, String name) {
        this.parent = list;
        this.name = name;
        this.style = new ChatFormatting[]{ChatFormatting.UNDERLINE};
    }

    public CategoryBuilder isEnabled(BooleanSupplier supplier) {
        this.isEnabledSupplier = supplier; 
        return this;
    }

    public CategoryBuilder style(ChatFormatting ... style) {
        this.style = style;
        return this;
    }

    public void build() {
        parent.addBuiltEntry(new CategoryEntry(parent, name, isEnabledSupplier, style));
    }
}
