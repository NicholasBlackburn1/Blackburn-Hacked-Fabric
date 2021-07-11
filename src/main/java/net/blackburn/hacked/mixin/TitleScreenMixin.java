package net.blackburn.hacked.mixin;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.DrawableHelper;



@Mixin(value = TitleScreen.class, priority = 1001)
public class TitleScreenMixin  extends Screen{

	private int copyrightTextWidth;
	private int copyrightTextX;

	protected TitleScreenMixin(Text text) {
		super(text);
	
	}

	@Inject(method="init()V", at=@At("RETURN"))
	private void renderCustomButtons(CallbackInfo ci) {
		MinecraftClient.getInstance().getWindow().setTitle("Blackburn Hacked 1.17-B");
		System.out.println("Hellow Foxy boi hacked mc");
	}

	@Inject(at = @At("TAIL"), method = "render()V")
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta,CallbackInfo ci) {
		
  
		
		
		int j = this.width / 2 - 137;
		

		   
		   drawStringWithShadow(matrices, this.textRenderer, "Blackburn Hacked Client: 1.17.2-B", 2, this.height - 20, 16777215 | 22);
		   
		  
  
		   super.render(matrices, mouseX, mouseY, delta);
		   
  
		}
	 }
  

