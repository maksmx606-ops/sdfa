package dev.attackvisual;

import net.fabricmc.loader.api.FabricLoader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** All module switches. Saved to config/attackvisual.properties */
public final class Config {
	public static boolean fullBright = false;
	public static boolean hitSound = true;
	public static boolean hitParticles = true;
	public static boolean hitMarker = true;
	public static boolean jumpCircles = true;
	public static boolean keystrokes = true;
	public static boolean potions = true;
	public static boolean armor = true;
	public static boolean crosshair = true;
	public static boolean infoPanel = true;
	public static boolean watermark = true;
	public static boolean targetHud = true;
	public static boolean targetEsp = true;
	public static int espMode = 0;
	public static boolean chinaHat = false;
	public static boolean trail = false;
	public static boolean killEffect = true;
	public static boolean zoom = true;
	public static boolean noHurtCam = false;
	public static boolean noNausea = false;
	public static boolean noFovEffects = false;
	public static boolean noBob = false;
	public static int crosshairStyle = 0;
	public static boolean screenTheme = true;
	public static int soundIndex = 0;
	public static int themeIndex = 0;

	private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("attackvisual.properties"); }

	public static void load() {
		try {
			Path f = file();
			if (!Files.exists(f)) return;
			Properties p = new Properties();
			try (Reader r = Files.newBufferedReader(f)) { p.load(r); }
			fullBright = b(p, "fullBright", fullBright);
			hitSound = b(p, "hitSound", hitSound);
			hitParticles = b(p, "hitParticles", hitParticles);
			hitMarker = b(p, "hitMarker", hitMarker);
			jumpCircles = b(p, "jumpCircles", jumpCircles);
			keystrokes = b(p, "keystrokes", keystrokes);
			potions = b(p, "potions", potions);
			armor = b(p, "armor", armor);
			crosshair = b(p, "crosshair", crosshair);
			infoPanel = b(p, "infoPanel", infoPanel);
			screenTheme = b(p, "screenTheme", screenTheme);
			watermark = b(p, "watermark", watermark);
			targetHud = b(p, "targetHud", targetHud);
			targetEsp = b(p, "targetEsp", targetEsp);
			chinaHat = b(p, "chinaHat", chinaHat);
			trail = b(p, "trail", trail);
			killEffect = b(p, "killEffect", killEffect);
			zoom = b(p, "zoom", zoom);
			noHurtCam = b(p, "noHurtCam", noHurtCam);
			noNausea = b(p, "noNausea", noNausea);
			noFovEffects = b(p, "noFovEffects", noFovEffects);
			noBob = b(p, "noBob", noBob);
			crosshairStyle = Math.floorMod(Integer.parseInt(p.getProperty("crosshairStyle", "0")), AttackHud.CROSSHAIRS.length);
			espMode = Math.floorMod(Integer.parseInt(p.getProperty("espMode", "0")), Esp.MODES.length);
			HudLayout.load(p);
			soundIndex = Math.floorMod(Integer.parseInt(p.getProperty("soundIndex", "0")), HitSounds.NAMES.length);
			themeIndex = Math.floorMod(Integer.parseInt(p.getProperty("themeIndex", "0")), Theme.values().length);
		} catch (Exception ignored) {}
	}

	public static void save() {
		try {
			Properties p = new Properties();
			p.setProperty("fullBright", String.valueOf(fullBright));
			p.setProperty("hitSound", String.valueOf(hitSound));
			p.setProperty("hitParticles", String.valueOf(hitParticles));
			p.setProperty("hitMarker", String.valueOf(hitMarker));
			p.setProperty("jumpCircles", String.valueOf(jumpCircles));
			p.setProperty("keystrokes", String.valueOf(keystrokes));
			p.setProperty("potions", String.valueOf(potions));
			p.setProperty("armor", String.valueOf(armor));
			p.setProperty("crosshair", String.valueOf(crosshair));
			p.setProperty("infoPanel", String.valueOf(infoPanel));
			p.setProperty("screenTheme", String.valueOf(screenTheme));
			p.setProperty("watermark", String.valueOf(watermark));
			p.setProperty("targetHud", String.valueOf(targetHud));
			p.setProperty("targetEsp", String.valueOf(targetEsp));
			p.setProperty("chinaHat", String.valueOf(chinaHat));
			p.setProperty("trail", String.valueOf(trail));
			p.setProperty("killEffect", String.valueOf(killEffect));
			p.setProperty("zoom", String.valueOf(zoom));
			p.setProperty("noHurtCam", String.valueOf(noHurtCam));
			p.setProperty("noNausea", String.valueOf(noNausea));
			p.setProperty("noFovEffects", String.valueOf(noFovEffects));
			p.setProperty("noBob", String.valueOf(noBob));
			p.setProperty("crosshairStyle", String.valueOf(crosshairStyle));
			p.setProperty("espMode", String.valueOf(espMode));
			HudLayout.save(p);
			p.setProperty("soundIndex", String.valueOf(soundIndex));
			p.setProperty("themeIndex", String.valueOf(themeIndex));
			try (Writer w = Files.newBufferedWriter(file())) { p.store(w, "Attack Visual"); }
		} catch (Exception ignored) {}
	}

	private static boolean b(Properties p, String k, boolean d) {
		return Boolean.parseBoolean(p.getProperty(k, String.valueOf(d)));
	}
}
