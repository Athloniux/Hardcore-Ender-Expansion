package chylex.hee.mechanics.brewing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.ItemDamagePair;

public class PotionTypes{
	public static final List<AbstractPotionData> potionData = new ArrayList<>(Arrays.asList(
		/*  0 */ new EmptyPotion(null,0,16),
		/*  1 */ new InstantPotion(Potion.heal,16,8197,4),
		/*  2 */ new InstantPotion(Potion.harm,8197,8204,4),
		/*  3 */ new TimedPotion(Potion.moveSpeed,16,8194,4,60,720),
		/*  4 */ new TimedPotion(Potion.moveSlowdown,8194,8202,4,30,480,30),
		/*  5 */ new TimedPotion(Potion.damageBoost,16,8201,4,60,720),
		/*  6 */ new TimedPotion(Potion.weakness,8195,8200,4,30,480,30),
		/*  7 */ new TimedPotion(Potion.nightVision,16,8198,1,30,600),
		/*  8 */ new TimedPotion(Potion.invisibility,8198,8206,1,30,600),
		/*  9 */ new TimedPotion(Potion.regeneration,16,8193,4,15,120,15),
		/* 10 */ new TimedPotion(Potion.poison,16,8196,4,6,48,6),
		/* 11 */ new TimedPotion(Potion.fireResistance,16,8195,1,60,600),
		/* 12 */ new TimedPotion(Potion.waterBreathing,16,000,1,60,720,40),
		// NEW //
		/* 13 */ new TimedPotion(Potion.hunger,16,8193,4,6,48,6),
		/* 14 */ new TimedPotion(Potion.blindness,16,8197,1,6,48,6),
		/* 15 */ new TimedPotion(Potion.jump,16,8192,4,60,600),
		/* 16 */ new TimedPotion(Potion.confusion,8251,8253,1,6,48,6)
	));
	
	private static final Map<ItemDamagePair,Byte[]> itemToIndex = new HashMap<>();
	
	private static void mapItemToIndex(Item item, int...indexes){
		mapItemToIndex(item,(short)0,indexes);
	}
	
	private static void mapItemToIndex(Item item, Short damage, int...indexes){
		Byte[] byteIndexes = new Byte[indexes.length];
		for(int a = 0; a < indexes.length; a++)byteIndexes[a] = (byte)indexes[a];
		itemToIndex.put(new ItemDamagePair(item,damage),byteIndexes);
	}
	
	public static Byte[] getItemIndexes(ItemStack is){
		for(Entry<ItemDamagePair,Byte[]> entry:itemToIndex.entrySet()){
			ItemDamagePair item = entry.getKey();
			if (item.check(is))return entry.getValue();
		}
		return new Byte[0];
	}
	
	static{
		mapItemToIndex(Items.nether_wart,0);
		mapItemToIndex(Items.speckled_melon,1);
		mapItemToIndex(Items.sugar,3);
		mapItemToIndex(Items.blaze_powder,5);
		mapItemToIndex(Items.golden_carrot,7);
		mapItemToIndex(Items.ghast_tear,9);
		mapItemToIndex(Items.spider_eye,10);
		mapItemToIndex(Items.magma_cream,11);
		mapItemToIndex(Items.fish,(short)FishType.PUFFERFISH.func_150976_a(),new int[]{ 12 }); // OBFUSCATED get fish damage
		mapItemToIndex(Items.fermented_spider_eye,2,4,6,8/*,16*/);
		/*mapItemToIndex(Item.rottenFlesh,13);
		mapItemToIndex(Item.flint,14);
		mapItemToIndex(Item.feather,15);*/
	}
	
	public static PotionEffect getEffectIfValid(ItemStack is){
		List list = Items.potionitem.getEffects(is);
		return list == null || list.size() != 1 ? null : (PotionEffect)list.get(0);
	}
	
	public static AbstractPotionData getPotionData(ItemStack is){
		for(AbstractPotionData data:potionData){
			if (data.damageValue == (is.getItemDamage()&~16384))return data;
		}
		return null;
	}
	
	public static short getRequiredPowder(Item ingredient, ItemStack is){
		if (is.getItemDamage() <= 16){
			if (is.getItemDamage() == 0 && (ingredient == ItemList.instability_orb || ingredient == ItemList.silverfish_blood))return 8;
			else return 0;
		}
		
		PotionEffect eff = getEffectIfValid(is);
		if (eff == null)return 0;

		if (ingredient == Items.redstone){
			AbstractPotionData potionData = getPotionData(is);
			if (potionData instanceof TimedPotion)return (short)(((TimedPotion)potionData).getDurationLevel(eff.getDuration())+1);
		}
		else if (ingredient == Items.glowstone_dust)return (short)(2*(eff.getAmplifier()+1));
		else if (ingredient == Items.gunpowder)return 3;
		return 0;
	}
	
	public static boolean canBeApplied(Item ingredient, ItemStack is){
		Byte[] indexes = getItemIndexes(is);
		if (indexes.length == 0){
			if (is.getItemDamage() <= 16){
				if ((ingredient == ItemList.instability_orb || ingredient == ItemList.silverfish_blood) ||
					(ingredient == Items.gunpowder && is.getItem() == ItemList.potion_of_instability))return is.getItemDamage() == 0;
				else return false;
			}
			
			if (ingredient == Items.gunpowder){
				return !ItemPotion.isSplash(is.getItemDamage());
			}
			else if (ingredient == Items.glowstone_dust){
				AbstractPotionData data = getPotionData(is);
				return data != null && data.canIncreaseLevel(is);
			}
			else if (ingredient == Items.redstone){
				AbstractPotionData data = getPotionData(is);
				return data instanceof TimedPotion && ((TimedPotion)data).canIncreaseDuration(is);
			}
			return false;
		}
		
		for(Byte b:indexes){
			AbstractPotionData data = potionData.get(b);
			if (data.requiredDamageValue == (is.getItemDamage()&~16384))return true;
		}
		
		return false;
	}
	
	public static ItemStack applyIngredientUnsafe(Item ingredient, ItemStack is){
		if (ingredient == ItemList.instability_orb)return new ItemStack(ItemList.potion_of_instability);
		else if (ingredient == ItemList.silverfish_blood)return new ItemStack(ItemList.infestation_remedy);
		else if (ingredient == Items.gunpowder && is.getItem() == ItemList.potion_of_instability)return new ItemStack(ItemList.potion_of_instability,1,1);
		
		Byte[] indexes = getItemIndexes(is);
		if (indexes.length == 0){
			PotionEffect eff = getEffectIfValid(is);
			if (eff == null)return is;
			
			if (ingredient == Items.gunpowder){
				setCustomPotionEffect(is,eff); // make sure splash doesn't change duration
				is.setItemDamage(is.getItemDamage()|16384);
				return is;
			}
			
			PotionEffect newEffect = null;
			if (ingredient == Items.glowstone_dust){
				newEffect = new PotionEffect(eff.getPotionID(),eff.getDuration(),eff.getAmplifier()+1,eff.getIsAmbient());
			}
			else if (ingredient == Items.redstone){
				AbstractPotionData data = getPotionData(is);
				newEffect = new PotionEffect(eff.getPotionID(),eff.getDuration()+((TimedPotion)data).getDurationStep(),eff.getAmplifier(),eff.getIsAmbient());
			}
			
			if (newEffect != null)setCustomPotionEffect(is,newEffect);
			return is;
		}
		
		for(Byte b:indexes){
			AbstractPotionData data = potionData.get(b);
			if (data != null && data.requiredDamageValue == (is.getItemDamage()&~16384)){
				PotionEffect prevEffect = getEffectIfValid(is);

				if (is.stackTagCompound != null)is.stackTagCompound.removeTag("CustomPotionEffects");
				data.onFirstBrewingFinished(is);
				
				if (prevEffect != null){
					PotionEffect curEffect = getEffectIfValid(is);
					if (curEffect != null)setCustomPotionEffect(is,new PotionEffect(curEffect.getPotionID(),prevEffect.getDuration(),prevEffect.getAmplifier(),prevEffect.getIsAmbient()));
				}
				break;
			}
		}
		
		return is;
	}
	
	public static ItemStack setCustomPotionEffect(ItemStack is, PotionEffect effect){
		NBTTagCompound nbt = is.stackTagCompound == null?new NBTTagCompound():is.stackTagCompound;
		NBTTagList potionList = new NBTTagList();
		potionList.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
		nbt.setTag("CustomPotionEffects",potionList);
		is.setTagCompound(nbt);
		return is;
	}
}
