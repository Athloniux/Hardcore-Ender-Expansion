package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C06ClearInventorySlot extends AbstractClientPacket{
	private byte slot;
	
	public C06ClearInventorySlot(){}
	
	public C06ClearInventorySlot(int slot){
		this.slot = (byte)slot;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(slot);
	}

	@Override
	public void read(ByteBuf buffer){
		slot = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		player.inventory.mainInventory[slot] = null;
	}
}
