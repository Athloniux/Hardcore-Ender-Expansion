package chylex.hee.entity.technical;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockList;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.util.BlockPosM;

public class EntityTechnicalPuzzleChain extends EntityTechnicalBase{
	private byte dir;
	
	public EntityTechnicalPuzzleChain(World world){
		super(world);
	}
	
	public EntityTechnicalPuzzleChain(World world, BlockPosM pos, int dir){
		super(world);
		setPosition(pos.x+0.5D-Direction.offsetX[dir],pos.y+0.5D,pos.z+0.5D-Direction.offsetZ[dir]);
		this.dir = (byte)dir;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote)return;
		
		if (ticksExisted%8 == 1){
			setPosition(posX+Direction.offsetX[dir],posY,posZ+Direction.offsetZ[dir]);
			
			BlockPosM pos = new BlockPosM(this);
			
			if (pos.getBlock(worldObj) == BlockList.dungeon_puzzle){
				if (((BlockDungeonPuzzle)BlockList.dungeon_puzzle).updateChain(worldObj,x,y,z,dir)){
					PacketPipeline.sendToAllAround(dimension,x+0.5D,y+0.5D,z+0.5D,64D,new C20Effect(FXType.Basic.DUNGEON_PUZZLE_BURN,x+0.5D,y+0.5D,z+0.5D));
				}
				else setDead();
			}
			else{
				((BlockDungeonPuzzle)BlockList.dungeon_puzzle).checkWinConditions(worldObj,x-Direction.offsetX[dir],y,z-Direction.offsetZ[dir]);
				setDead();
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		nbt.setByte("chainDir",dir);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		dir = nbt.getByte("chainDir");
	}
}
