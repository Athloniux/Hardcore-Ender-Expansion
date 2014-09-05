package chylex.hee.mechanics.compendium.render;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;

public class PurchaseDisplayElement{
	public final Object object;
	public final int price;
	private final int y;
	private final boolean canAfford;
	
	public PurchaseDisplayElement(KnowledgeFragment fragment, int y, boolean canAfford){
		this.object = fragment;
		this.price = fragment.getPrice();
		this.y = y;
		this.canAfford = canAfford;
	}
	
	public PurchaseDisplayElement(KnowledgeObject<?> object, int y, boolean canAfford){
		this.object = object;
		this.price = object.getUnlockPrice();
		this.y = y;
		this.canAfford = canAfford;
	}
	
	public void render(GuiScreen gui, int pageCenterX){
		pageCenterX += 3;
		
		GL11.glColor4f(1F,1F,1F,0.96F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texPage);
		gui.drawTexturedModalRect(pageCenterX-27,y-13,155,0,54,26);
		
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),GuiEnderCompendium.knowledgeFragmentIS,pageCenterX-22,y-9);
		RenderHelper.disableStandardItemLighting();
		
		String price = String.valueOf(this.price);
		gui.mc.fontRenderer.drawString(price,pageCenterX-gui.mc.fontRenderer.getStringWidth(price)+20,y-4,4210752);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int pageCenterX){
		return mouseX >= (pageCenterX+3)-27 && mouseY >= y-13 && mouseX <= (pageCenterX+3)+27 && mouseY <= y+13;
	}
}
