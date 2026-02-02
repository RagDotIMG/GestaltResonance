package net.ragdot.gestaltresonance.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;

public class StaminaHudRenderer implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;

        // Find active gestalt owned by the player
        GestaltBase activeGestalt = findActiveGestalt(client);
        if (activeGestalt == null) return;

        float stamina = activeGestalt.getStamina();
        float maxStamina = activeGestalt.getMaxStamina();
        float percentage = stamina / maxStamina;

        int width = 100;
        int height = 3;
        int x = drawContext.getScaledWindowWidth() - width - 13; // Adjusted for border
        int y = drawContext.getScaledWindowHeight() - (height * 2) - 14; // Adjusted for border
        
        // --- Border ---
        // 1px broad white border with 1px gap
        int borderX = x - 2;
        int borderY = y - 2;
        int borderWidth = width + 4;
        int borderHeight = (height * 2) + 1 + 4;
        
        // Top
        drawContext.fill(borderX, borderY, borderX + borderWidth, borderY + 1, 0xFFFFFFFF);
        // Bottom
        drawContext.fill(borderX, borderY + borderHeight - 1, borderX + borderWidth, borderY + borderHeight, 0xFFFFFFFF);
        // Left
        drawContext.fill(borderX, borderY + 1, borderX + 1, borderY + borderHeight - 1, 0xFFFFFFFF);
        // Right
        drawContext.fill(borderX + borderWidth - 1, borderY + 1, borderX + borderWidth, borderY + borderHeight - 1, 0xFFFFFFFF);

        // Draw background (Gray)
        drawContext.fill(x, y, x + width, y + height, 0xFF808080); // Gray background

        // Draw foreground (White)
        int filledWidth = (int) (width * percentage);
        drawContext.fill(x, y, x + filledWidth, y + height, 0xFFFFFFFF);

        // --- EXP Bar ---
        int expY = y + height + 1; // Right under stamina bar with 1px gap
        float expPercentage = (float) activeGestalt.getExp() / activeGestalt.getMaxExp();

        // Draw background (Gray)
        drawContext.fill(x, expY, x + width, expY + height, 0xFF808080);

        // Draw foreground (Pale Yellow-Orange)
        // 0xFFFFD580 is a pale yellow-orange
        int filledExpWidth = (int) (width * expPercentage);
        drawContext.fill(x, expY, x + filledExpWidth, expY + height, 0xFFFFD580);
    }

    private GestaltBase findActiveGestalt(MinecraftClient client) {
        // We can search the world for a GestaltBase owned by the current player
        return client.world.getEntitiesByClass(
                GestaltBase.class,
                client.player.getBoundingBox().expand(256.0),
                gestalt -> client.player.getUuid().equals(gestalt.getOwnerUuid())
        ).stream().findFirst().orElse(null);
    }
}
