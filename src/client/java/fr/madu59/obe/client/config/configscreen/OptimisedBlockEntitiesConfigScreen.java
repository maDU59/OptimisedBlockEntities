package fr.madu59.obe.client.config.configscreen;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

import fr.madu59.obe.client.config.SettingsManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OptimisedBlockEntitiesConfigScreen extends Screen {
    
    private MyConfigListWidget list;
    private final Screen parent;

    protected OptimisedBlockEntitiesConfigScreen(Screen parent) {
        super(Component.literal("Obe configuration screen"));
        this.parent = parent;
    }

    public static void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                literal("obeConfig")
                    .executes(context -> {
                        Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.setScreen(new OptimisedBlockEntitiesConfigScreen(null)));
                        return 1;
                    })
            );
        });
    }

    @Override
    protected void init() {
        super.init();
        this.list = new MyConfigListWidget(this.minecraft, this.width, this.height - 80, 40, 26);

        list.category("obe.config.category.general").build();
        list.button(SettingsManager.MOD_TOGGLE).build();
        list.button(SettingsManager.EMF_COMPAT).build();

        list.category("obe.config.category.chests").build();
        list.button(SettingsManager.OPTIMISED_CHESTS).build();
        list.button(SettingsManager.CHEST_AMBIENT_OCCLUSION).isEnabled(() -> SettingsManager.OPTIMISED_CHESTS.getValue()).build();

        list.category("obe.config.category.banners").build();
        list.button(SettingsManager.OPTIMISED_BANNERS).build();
        list.button(SettingsManager.BANNER_AMBIENT_OCCLUSION).isEnabled(() -> SettingsManager.OPTIMISED_BANNERS.getValue()).build();

        list.category("obe.config.category.signs").build();
        list.button(SettingsManager.SIGN_TEXT_CULLING).build();

        list.category("obe.config.category.shulker_boxes").build();
        list.button(SettingsManager.OPTIMISED_SHULKER_BOXES).build();
        list.button(SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION).isEnabled(() -> SettingsManager.OPTIMISED_SHULKER_BOXES.getValue()).build();

        list.category("obe.config.category.skulls").build();
        list.button(SettingsManager.OPTIMISED_SKULLS).build();
        list.button(SettingsManager.SKULL_AMBIENT_OCCLUSION).isEnabled(() -> SettingsManager.OPTIMISED_SKULLS.getValue()).build();

        list.category("obe.config.category.bells").build();
        list.button(SettingsManager.OPTIMISED_BELLS).build();
        list.button(SettingsManager.BELL_AMBIENT_OCCLUSION).isEnabled(() -> SettingsManager.OPTIMISED_BELLS.getValue()).build();

        list.category("obe.config.category.decorated_pots").build();
        list.button(SettingsManager.OPTIMISED_DECORATED_POTS).build();
        list.button(SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION).isEnabled(() -> SettingsManager.OPTIMISED_DECORATED_POTS.getValue()).build();

        list.category("obe.config.category.copper_golems").build();
        list.button(SettingsManager.OPTIMISED_COPPER_GOLEMS).build();
        list.button(SettingsManager.COPPER_GOLEM_AMBIENT_OCCLUSION).isEnabled(() -> SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue()).build();

        list.category("obe.config.category.shelves").build();
        list.button(SettingsManager.OPTIMISED_SHELVES).build();

        list.category("obe.config.category.lecterns").build();
        list.button(SettingsManager.OPTIMISED_LECTERNS).build();

        list.category("obe.config.category.campfires").build();
        list.button(SettingsManager.OPTIMISED_CAMPFIRES).build();

        list.category("obe.config.category.beacons").build();
        list.button(SettingsManager.OPTIMISED_BEACONS).build();

        Button doneButton = Button.builder(Component.translatable("obe.config.done"), b -> {
            this.minecraft.gui.setScreen(this.parent);
            SettingsManager.saveSettings();
        }).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build();

        this.addRenderableWidget(this.list);
        this.addRenderableWidget(doneButton);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
        SettingsManager.saveSettings();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.list.extractRenderState(context, mouseX, mouseY, delta);
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}
