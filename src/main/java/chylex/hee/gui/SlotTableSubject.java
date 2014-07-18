package chylex.hee.gui;
import net.minecraft.inventory.Slot;
import chylex.hee.tileentity.IInventoryInvalidateable;

class SlotTableSubject extends Slot{
	private IInventoryInvalidateable inv;
	
	public SlotTableSubject(IInventoryInvalidateable inv, int id, int x, int z){
		super(inv,id,x,z);
		this.inv = inv;
	}
	
	@Override
	public void onSlotChanged(){
		super.onSlotChanged();
		inv.invalidateInventory();
	}
}
