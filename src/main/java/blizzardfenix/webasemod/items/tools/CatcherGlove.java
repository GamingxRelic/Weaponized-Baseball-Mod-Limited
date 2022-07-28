package blizzardfenix.webasemod.items.tools;

import blizzardfenix.webasemod.entity.BouncyBallEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class CatcherGlove extends Item {
	float range = 25;

	public CatcherGlove(Properties properties) {
		super(properties.defaultDurability(250));
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		level.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.NEUTRAL, 0.5F,
				0.4F / (random.nextFloat() * 0.4F + 0.8F));
		player.getCooldowns().addCooldown(this, 40);
		
		if (!level.isClientSide) {
			// Pick up any balls within a certain range and close enough to where the player is looking.
			Vector3d lookvec = player.getLookAngle();
			for(BouncyBallEntity ballentity : level.getEntitiesOfClass(BouncyBallEntity.class, player.getBoundingBox().inflate(range*1.5))) {
				Vector3d posdiff = ballentity.getCenterPositionVec().subtract(player.getEyePosition(1));
				float dist = (float) posdiff.lengthSqr();
				if (dist < range * range && posdiff.scale(1/Math.sqrt(dist)).dot(lookvec) > 0.8F) {
					ballentity.pickup(player);
				}
			}
			if (!player.abilities.instabuild) {
				itemstack.hurtAndBreak(1, player, (playerentity) -> {
					playerentity.broadcastBreakEvent(hand);
				});
			}
		}
		//player.awardStat(Stats.ITEM_USED.get(this));

		return ActionResult.sidedSuccess(itemstack, level.isClientSide());
	}
	
	// Experimenting with using a different animation
//	@Override
//	public void onUseTick(World level, LivingEntity entity, ItemStack item, int remTicks) {
//		LOGGER.debug("using " + test);
//		
//	}
//
//	@Override
//	public UseAction getUseAnimation(ItemStack p_77661_1_) {
//		LOGGER.debug("test " + test);
//		switch (test) {
//		case 0:
//			return UseAction.BLOCK;
//		case 1:
//			return UseAction.BOW;
//		case 2:
//			return UseAction.CROSSBOW;
//		case 3:
//			return UseAction.DRINK;
//		case 4:
//			return UseAction.EAT;
//		case 5:
//			return UseAction.SPEAR;
//		}
//		return UseAction.BLOCK;
//	}
//
//	@Override
//	public int getUseDuration(ItemStack p_77626_1_) {
//		return 32;
//	}
}
