package dev.attackvisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;

/** Hit sound, hit particles, jump circles, full bright. */
public final class Effects {
	public static long hitTime = 0;
	public static Entity lastTarget = null;
	private static boolean wasOnGround = true;
	private static boolean fbActive = false;
	private static double savedGamma = 1.0;
	private static boolean killHandled = true;
	private static boolean zooming = false;
	private static int savedFov = 70;
	private static final Double[] tiltBox = new Double[1], distBox = new Double[1], fovFxBox = new Double[1];
	private static final Boolean[] bobBox = new Boolean[1];
	public static double bps = 0;
	private static double prevX, prevZ;
	private static boolean hasPrev = false;

	public static void onHit(Entity target) {
		MinecraftClient mc = MinecraftClient.getInstance();
		hitTime = System.currentTimeMillis();
		lastTarget = target;
		killHandled = false;
		if (Config.hitSound) {
			mc.getSoundManager().play(PositionedSoundInstance.master(net.minecraft.registry.entry.RegistryEntry.of(HitSounds.EVENTS[Config.soundIndex]), 1.2f, 0.8f));
		}
		if (Config.hitParticles && mc.world != null) {
			DustParticleEffect dust = new DustParticleEffect(Theme.current().accent & 0xFFFFFF, 1.0f);
			for (int i = 0; i < 12; i++) {
				mc.particleManager.addParticle(dust,
					target.getX() + (Math.random() - 0.5) * 0.6,
					target.getBodyY(0.3 + Math.random() * 0.6),
					target.getZ() + (Math.random() - 0.5) * 0.6,
					(Math.random() - 0.5) * 0.2, Math.random() * 0.15, (Math.random() - 0.5) * 0.2);
			}
		}
	}

	public static void tick(MinecraftClient mc) {
		PlayerEntity p = mc.player;
		if (p == null || mc.world == null) return;

		// Speed (blocks per second, smoothed)
		if (hasPrev) bps = bps * 0.8 + Math.hypot(p.getX() - prevX, p.getZ() - prevZ) * 20 * 0.2;
		prevX = p.getX(); prevZ = p.getZ(); hasPrev = true;

		// China hat (third person only)
		if (Config.chinaHat && !mc.options.getPerspective().isFirstPerson() && mc.world.getTime() % 3 == 0) {
			DustParticleEffect hat = new DustParticleEffect(Theme.current().accent & 0xFFFFFF, 0.9f);
			double top = p.getY() + p.getHeight();
			for (int i = 0; i < 12; i++) {
				double a = Math.PI * 2 * i / 12 + mc.world.getTime() * 0.1;
				mc.particleManager.addParticle(hat, p.getX() + Math.cos(a) * 0.5, top + 0.08, p.getZ() + Math.sin(a) * 0.5, 0, 0, 0);
				mc.particleManager.addParticle(hat, p.getX() + Math.cos(a) * 0.28, top + 0.28, p.getZ() + Math.sin(a) * 0.28, 0, 0, 0);
			}
			mc.particleManager.addParticle(hat, p.getX(), top + 0.45, p.getZ(), 0, 0, 0);
		}

		// Jump circle
		boolean onGround = p.isOnGround();
		if (Config.jumpCircles && wasOnGround && !onGround && p.getVelocity().y > 0.1) {
			DustParticleEffect dust = new DustParticleEffect(Theme.current().accent & 0xFFFFFF, 1.0f);
			for (int i = 0; i < 24; i++) {
				double a = Math.PI * 2 * i / 24;
				mc.particleManager.addParticle(dust, p.getX() + Math.cos(a) * 0.7, p.getY() + 0.05, p.getZ() + Math.sin(a) * 0.7, 0, 0, 0);
			}
		}
		wasOnGround = onGround;

		// Trail
		if (Config.trail && bps > 1.0) {
			DustParticleEffect tr = new DustParticleEffect(Theme.current().accent & 0xFFFFFF, 1.1f);
			for (int i = 0; i < 2; i++) {
				mc.particleManager.addParticle(tr, p.getX() + (Math.random() - 0.5) * 0.4, p.getY() + 0.1 + Math.random() * 0.2, p.getZ() + (Math.random() - 0.5) * 0.4, 0, 0, 0);
			}
		}

		// Kill effect: burst when the entity you hit dies
		if (Config.killEffect && !killHandled && lastTarget instanceof LivingEntity le && !le.isAlive()
			&& System.currentTimeMillis() - hitTime < 4000) {
			killHandled = true;
			DustParticleEffect k = new DustParticleEffect(Theme.current().accent & 0xFFFFFF, 1.6f);
			for (int i = 0; i < 40; i++) {
				double a = Math.random() * Math.PI * 2, up = Math.random() * 0.4;
				mc.particleManager.addParticle(k, le.getX(), le.getBodyY(0.5), le.getZ(), Math.cos(a) * 0.25, up, Math.sin(a) * 0.25);
			}
		}

		// Zoom: hold C
		var fov = mc.options.getFov();
		boolean cDown = Config.zoom && mc.currentScreen == null && mc.getWindow() != null
			&& GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS;
		if (cDown) {
			if (!zooming) { savedFov = fov.getValue(); zooming = true; }
			if (fov.getValue() != 30) fov.setValue(30);
		} else if (zooming) {
			fov.setValue(savedFov);
			zooming = false;
		}

		// Comfort removals (use vanilla options, restored when turned off)
		holdD(mc.options.getDamageTiltStrength(), Config.noHurtCam, tiltBox);
		holdD(mc.options.getDistortionEffectScale(), Config.noNausea, distBox);
		holdD(mc.options.getFovEffectScale(), Config.noFovEffects, fovFxBox);
		var bob = mc.options.getBobView();
		if (Config.noBob) {
			if (bobBox[0] == null) bobBox[0] = bob.getValue();
			if (bob.getValue()) bob.setValue(false);
		} else if (bobBox[0] != null) {
			bob.setValue(bobBox[0]);
			bobBox[0] = null;
		}

		// Full bright (max gamma, restored when turned off)
		var gamma = mc.options.getGamma();
		if (Config.fullBright) {
			if (!fbActive) { savedGamma = gamma.getValue(); fbActive = true; }
			if (gamma.getValue() < 1.0) gamma.setValue(1.0);
		} else if (fbActive) {
			gamma.setValue(savedGamma);
			fbActive = false;
		}
	}

	private static void holdD(SimpleOption<Double> o, boolean on, Double[] box) {
		if (on) {
			if (box[0] == null) box[0] = o.getValue();
			if (o.getValue() != 0.0) o.setValue(0.0);
		} else if (box[0] != null) {
			o.setValue(box[0]);
			box[0] = null;
		}
	}
}
