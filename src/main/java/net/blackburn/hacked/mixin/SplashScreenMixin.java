/**
 * Thi sclass is for trying to customize the loading screen of the splash minecrafty
 */
package net.blackburn.hacked.mixin;


import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("resource")
@Mixin(value = SplashOverlay.class)
public abstract class SplashScreenMixin extends DrawableHelper {

	@Shadow private boolean reloading;
	@Shadow private long reloadCompleteTime;
	@Shadow private long reloadStartTime;
	@Shadow private float progress;
	@Shadow private ResourceReload reload;
	@Shadow private Consumer<Optional<Throwable>> exceptionHandler;

	private MinecraftClient mc = MinecraftClient.getInstance();

	private static final Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");
	private static final int MOJANG_RED = BackgroundHelper.ColorMixer.getArgb(255, 239, 50, 61);
	private static final int MONOCHROME_BLACK = BackgroundHelper.ColorMixer.getArgb(255, 0, 0, 0);
	private static final IntSupplier BRAND_ARGB = () -> {
		return MinecraftClient.getInstance().options.monochromeLogo ? MONOCHROME_BLACK : MOJANG_RED;
	};


    
 

	@Inject(at = @At(value = "HEAD"), method = "render", cancellable = true)
	protected void onRender(MatrixStack matrix, int mouseX, int mouseY, float delta, CallbackInfo info) {

		info.cancel();
		
		int i = this.mc.getWindow().getScaledWidth();
		int j = this.mc.getWindow().getScaledHeight();
		long l = Util.getMeasuringTimeMs();
		if (this.reloading && this.reloadStartTime == -1L) {
			this.reloadStartTime = l;
		}

		//Variables for the loading screen dark mode
		int color = new Color(0, 26, 0).getRGB();
		boolean isDarkmode = true;
		
		float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
		float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;
		float s;
		int m;
		if (f >= 1.0F) {
			if (this.mc.currentScreen != null) {
				this.mc.currentScreen.render(matrix, 0, 0, delta);
				
				//Firing events for post-rendering of the current screen
				
			}

			m = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
			//Disabling background transparency when fading to customizable menus to prevent layout bugs
			if ((MinecraftClient.getInstance().currentScreen != null) ) {
				m = 255;
			}
			if (!isDarkmode) {
				fill(matrix, 0, 0, i, j, withAlpha(BRAND_ARGB.getAsInt(), m));
			} else {
				fill(matrix, 0, 0, i, j, withAlpha(color, m));
			}
			s = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
		} else if (this.reloading) {
			if (this.mc.currentScreen != null && g < 1.0F) {
				this.mc.currentScreen.render(matrix, mouseX, mouseY, delta);
				

			}

			m = MathHelper.ceil(MathHelper.clamp((double)g, 0.15D, 1.0D) * 255.0D);
			//Disabling background transparency when fading to customizable menus to prevent layout bugs
			if ((MinecraftClient.getInstance().currentScreen != null)) {
				m = 255;
			}
			if (!isDarkmode) {
				fill(matrix, 0, 0, i, j, withAlpha(BRAND_ARGB.getAsInt(), m));
			} else {
				fill(matrix, 0, 0, i, j, withAlpha(color, 0));
			}
			s = MathHelper.clamp(g, 0.0F, 1.0F);
		} else {
			if (isDarkmode) {
				m = color;
			} else {
				m = BRAND_ARGB.getAsInt();
			}
			float p = (float)(m >> 16 & 255) / 255.0F;
			float q = (float)(m >> 8 & 255) / 255.0F;
			float r = (float)(m & 255) / 255.0F;
			GlStateManager._clearColor(p, q, r, 1.0F);
			GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
			s = 1.0F;
		}

		m = (int)((double)this.mc.getWindow().getScaledWidth() * 0.5D);
		int u = (int)((double)this.mc.getWindow().getScaledHeight() * 0.5D);
		double d = Math.min((double)this.mc.getWindow().getScaledWidth() * 0.75D, (double)this.mc.getWindow().getScaledHeight()) * 0.25D;
		int v = (int)(d * 0.5D);
		double e = d * 4.0D;
		int w = (int)(e * 0.5D);
		RenderSystem.setShaderTexture(0, LOGO);
		RenderSystem.enableBlend();
		RenderSystem.blendEquation(32774);
		RenderSystem.blendFunc(770, 1);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//Disabling logo transparency when fading to customizable menus to prevent layout bugs
		if ((MinecraftClient.getInstance().currentScreen != null)) {
			s = 1.0F;
		}
    
		RenderSystem.setShaderColor(222.0F,0F, 0F, s);
		drawTexture(matrix, m - w, u - v, w, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
		drawTexture(matrix, m, u - v, w, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		int x = (int)((double)this.mc.getWindow().getScaledHeight() * 0.8325D);
		float y = this.reload.getProgress();
		this.progress = MathHelper.clamp(this.progress * 0.95F + y * 0.050000012F, 0.0F, 1.0F);
		if (f < 1.0F) {
			float barTransparency = 1.0F - MathHelper.clamp(f, 0.0F, 1.0F);
			//Disabling background transparency when fading to customizable menus to prevent layout bugs
			if ((MinecraftClient.getInstance().currentScreen != null) ) {
				barTransparency = 1.0F;
			}
			this.renderProgressBar(matrix, i / 2 - w, x - 5, i / 2 + w, x + 5, barTransparency);
		}

		if (f >= 2.0F) {
			this.mc.setOverlay((Overlay)null);
		}

		if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0F)) {
			try {
				this.reload.throwException();
				this.exceptionHandler.accept(Optional.empty());
			} catch (Throwable var23) {
				this.exceptionHandler.accept(Optional.of(var23));
			}

			this.reloadCompleteTime = Util.getMeasuringTimeMs();
			if (this.mc.currentScreen != null) {
				this.mc.currentScreen.init(this.mc, this.mc.getWindow().getScaledWidth(), this.mc.getWindow().getScaledHeight());
			}
		}

	}

	@Shadow protected abstract void renderProgressBar(MatrixStack matrices, int i, int j, int k, int l, float opacity);
	
	@Shadow protected static int withAlpha(int color, int alpha) {return 0;}

   
    
}
