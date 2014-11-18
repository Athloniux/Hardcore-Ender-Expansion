package chylex.hee.world.structure.island.biome.feature.island.laboratory;

public enum LaboratoryElementType{
	NONE(-1), HALL_X(2,0), HALL_Z(0,2), SMALL_ROOM(4), LARGE_ROOM(5);
	
	public final byte halfSizeX, halfSizeZ;
	
	LaboratoryElementType(int halfSize){
		this(halfSize,halfSize);
	}
	
	LaboratoryElementType(int halfSizeX, int halfSizeZ){
		this.halfSizeX = (byte)halfSizeX;
		this.halfSizeZ = (byte)halfSizeZ;
	}
}