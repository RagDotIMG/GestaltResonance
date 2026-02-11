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

        // --- Power indicators (3x rectangles 12x4) above the bars ---
        // Placement: 1px gap above the bars' OUTER border; 2px between rectangles; align with bars' inner left (x)
        int powerW = 12;
        int powerH = 4;
        int gapAbove = 1; // 1px gap above the bars' border
        int gapBetween = 2; // 2px gap between rectangles
        int powersTopY = borderY - gapAbove - powerH; // directly above the top border with 1px gap
        int powerCount = Math.max(0, activeGestalt.getPowerCount());
        int maxSquares = Math.min(3, powerCount == 0 ? 3 : powerCount);

        for (int i = 0; i < maxSquares; i++) {
            int sx = x + i * (powerW + gapBetween);
            int sy = powersTopY;

            // Background matches stamina bar background (gray)
            drawContext.fill(sx, sy, sx + powerW, sy + powerH, 0xFF808080);

            boolean toggle = activeGestalt.isPowerToggle(i);
            if (toggle) {
                if (activeGestalt.isPowerActive(i)) {
                    // Full foreground (white)
                    drawContext.fill(sx, sy, sx + powerW, sy + powerH, 0xFFFFFFFF);
                }
            } else {
                // Cooldown-based: fill left to right by progress [0..1]
                float p = activeGestalt.getPowerCooldownProgress(i);
                if (p > 0.0f) {
                    p = Math.max(0.0f, Math.min(1.0f, p));
                    int filledW2 = Math.round(p * powerW);
                    if (filledW2 > 0) {
                        drawContext.fill(sx, sy, sx + filledW2, sy + powerH, 0xFFFFFFFF);
                    }
                }
            }
        }

        // --- Level box (to the LEFT of the bars) ---
        // Only a bordered square with centered level number (white, no shadow).
        // Matches the total height of the bars INCLUDING their outer border thickness.
        int gap = 1; // 1px gap between the bars' border and the level box (requested: 1px less than before)
        int squareOuterSize = borderHeight; // make it a square whose size equals the bars' full framed height

        // Place the square to the left of the bars' left border with a 1px gap
        int squareX = borderX - gap - squareOuterSize;
        int squareY = borderY;                     // align vertically with the bars' border top

        // Draw square border (1px thick, same as bars)
        // Top
        drawContext.fill(squareX, squareY, squareX + squareOuterSize, squareY + 1, 0xFFFFFFFF);
        // Bottom
        drawContext.fill(squareX, squareY + squareOuterSize - 1, squareX + squareOuterSize, squareY + squareOuterSize, 0xFFFFFFFF);
        // Left
        drawContext.fill(squareX, squareY + 1, squareX + 1, squareY + squareOuterSize - 1, 0xFFFFFFFF);
        // Right
        drawContext.fill(squareX + squareOuterSize - 1, squareY + 1, squareX + squareOuterSize, squareY + squareOuterSize - 1, 0xFFFFFFFF);

        // Draw the level number centered inside the square (no shadow), with dynamic scaling
        String lvlText = Integer.toString(activeGestalt.getLvl());
        var textRenderer = MinecraftClient.getInstance().textRenderer;

        int baseFontHeight = textRenderer.fontHeight;          // vanilla height (typically 9)
        int shrinkPx = 1;                                      // always 1px smaller
        if (activeGestalt.getLvl() >= 10) {
            shrinkPx += 3;                                     // additional 3px shrink for 10+
        }
        int targetHeight = Math.max(1, baseFontHeight - shrinkPx);
        float scale = targetHeight / (float) baseFontHeight;   // scale to achieve target pixel height

        int textWidth = textRenderer.getWidth(lvlText);
        float scaledTextWidth = textWidth * scale;
        float scaledTextHeight = baseFontHeight * scale;

        int innerX = squareX + 1;
        int innerY = squareY + 1;
        int innerW = squareOuterSize - 2;
        int innerH = squareOuterSize - 2;

        // Center the scaled text within the inner box
        float drawX = innerX + Math.max(0f, (innerW - scaledTextWidth) / 2f);
        float drawY = innerY + Math.max(0f, (innerH - scaledTextHeight) / 2f);

        // Apply requested offset: move 2px to the right and 2px down
        drawX += 1.0f;
        drawY += 1.0f;

        // Apply matrix scaling to render smaller font precisely
        var matrices = drawContext.getMatrices();
        matrices.push();
        matrices.scale(scale, scale, 1.0f);

        // Determine text color: when at current max level, use EXP bar fill color; otherwise white
        // At max level, EXP is kept equal to getMaxExp() (see GestaltBase#setExp logic)
        boolean atMaxLevel = activeGestalt.getExp() >= activeGestalt.getMaxExp();
        int expBarColor = 0xFFFFD580; // same as EXP bar fill
        int textColor = atMaxLevel ? expBarColor : 0xFFFFFFFF;

        // Because the matrix is scaled, supply coordinates divided by the scale to land at pixel-perfect drawX/drawY
        drawContext.drawText(textRenderer,
                lvlText,
                (int) Math.floor(drawX / scale),
                (int) Math.floor(drawY / scale),
                textColor,
                false);
        matrices.pop();
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
