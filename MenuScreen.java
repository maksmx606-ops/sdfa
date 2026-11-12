package dev.attackvisual;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Module menu (3 pages). Opens with Right Shift. */
public class MenuScreen extends Screen {
	private static final String[] PAGES = {"HUD", "Combat visuals", "World"};
	private static int page = 0;
	private int x0, y0, idx;

	public MenuScreen() { super(Text.literal("Attack Visual")); }

	@Override
	protected void init() {
		x0 = width / 2 - 100;
		y0 = Math.max(26, height / 2 - 100);
		idx = 0;

		switch (page) {
			case 0 -> {
				toggle("Watermark", () -> Config.watermark, v -> Config.watermark = v);
				toggle("Coords", () -> Config.infoPanel, v -> Config.infoPanel = v);
				toggle("Keystrokes", () -> Config.keystrokes, v -> Config.keystrokes = v);
				toggle("Potion HUD", () -> Config.potions, v -> Config.potions = v);
				toggle("Armor HUD", () -> Config.armor, v -> Config.armor = v);
				toggle("Target HUD", () -> Config.targetHud, v -> Config.targetHud = v);
				toggle("Crosshair", () -> Config.crosshair, v -> Config.crosshair = v);
				toggle("Screen Theme", () -> Config.screenTheme, v -> Config.screenTheme = v);
				int fy = rowsEnd();
				wide(fy, "Edit HUD (drag)", btn -> client.setScreen(new HudEditorScreen()));
				wide(fy + 24, "Theme: " + Theme.current().label, btn -> {
					Theme.next();
					btn.setMessage(Text.literal("Theme: " + Theme.current().label));
				});
				wide(fy + 48, "Crosshair: " + AttackHud.CROSSHAIRS[Config.crosshairStyle], btn -> {
					Config.crosshairStyle = (Config.crosshairStyle + 1) % AttackHud.CROSSHAIRS.length;
					Config.save();
					btn.setMessage(Text.literal("Crosshair: " + AttackHud.CROSSHAIRS[Config.crosshairStyle]));
				});
				nav(fy + 72);
			}
			case 1 -> {
				toggle("Hit Sound", () -> Config.hitSound, v -> Config.hitSound = v);
				toggle("Hit Particles", () -> Config.hitParticles, v -> Config.hitParticles = v);
				toggle("Hit Marker", () -> Config.hitMarker, v -> Config.hitMarker = v);
				toggle("Jump Circles", () -> Config.jumpCircles, v -> Config.jumpCircles = v);
				toggle("Target ESP", () -> Config.targetEsp, v -> Config.targetEsp = v);
				toggle("China Hat", () -> Config.chinaHat, v -> Config.chinaHat = v);
				toggle("Trail", () -> Config.trail, v -> Config.trail = v);
				toggle("Kill Effect", () -> Config.killEffect, v -> Config.killEffect = v);
				int fy = rowsEnd();
				wide(fy, "Hit sound: " + HitSounds.NAMES[Config.soundIndex], btn -> {
					Config.soundIndex = (Config.soundIndex + 1) % HitSounds.NAMES.length;
					Config.save();
					btn.setMessage(Text.literal("Hit sound: " + HitSounds.NAMES[Config.soundIndex]));
				});
				wide(fy + 24, "ESP mode: " + Esp.MODES[Config.espMode], btn -> {
					Config.espMode = (Config.espMode + 1) % Esp.MODES.length;
					Config.save();
					btn.setMessage(Text.literal("ESP mode: " + Esp.MODES[Config.espMode]));
				});
				nav(fy + 48);
			}
			default -> {
				toggle("Full Bright", () -> Config.fullBright, v -> Config.fullBright = v);
				toggle("Zoom (hold C)", () -> Config.zoom, v -> Config.zoom = v);
				toggle("No Hurt Cam", () -> Config.noHurtCam, v -> Config.noHurtCam = v);
				toggle("No Nausea", () -> Config.noNausea, v -> Config.noNausea = v);
				toggle("No FOV Effects", () -> Config.noFovEffects, v -> Config.noFovEffects = v);
				toggle("No View Bob", () -> Config.noBob, v -> Config.noBob = v);
				nav(rowsEnd());
			}
		}
	}

	private int rowsEnd() { return y0 + ((idx + 1) / 2) * 24; }

	private void nav(int y) {
		addDrawableChild(ButtonWidget.builder(Text.literal("Page " + (page + 1) + "/" + PAGES.length + ": " + PAGES[page] + "  >"), btn -> {
			page = (page + 1) % PAGES.length;
			clearAndInit();
		}).dimensions(x0, y + 4, 200, 20).build());
	}

	private void wide(int y, String label, ButtonWidget.PressAction action) {
		addDrawableChild(ButtonWidget.builder(Text.literal(label), action).dimensions(x0, y, 200, 20).build());
	}

	private void toggle(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
		int col = idx % 2, row = idx / 2;
		idx++;
		addDrawableChild(ButtonWidget.builder(label(name, get.get()), btn -> {
			set.accept(!get.get());
			Config.save();
			btn.setMessage(label(name, get.get()));
		}).dimensions(x0 + col * 102, y0 + row * 24, 98, 20).build());
	}

	private static Text label(String name, boolean on) {
		return Text.literal(name + ": " + (on ? "ON" : "OFF"));
	}

	@Override
	public void render(DrawContext ctx, int mx, int my, float delta) {
		super.render(ctx, mx, my, delta);
		ctx.drawCenteredTextWithShadow(textRenderer, "Attack Visual", width / 2, y0 - 14, Theme.current().accent);
	}
}
