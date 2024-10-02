package de.hysky.skyblocker.skyblock.mining;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.utils.ItemUtils;
import de.hysky.skyblocker.utils.Utils;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.CLIENT;
import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.DRILL;

/**
 * Disables mining abilities from being used on private island.
 * Prevents accidentally using especially for explosive ones.
 */
public class PrivateIslandAbilityBlock {

	//@Init(priority = 10) //TODO crashes whyyy
	public static void init() {
		InteractionEvent.RIGHT_CLICK_ITEM.register(PrivateIslandAbilityBlock::handleUseItem);
		InteractionEvent.RIGHT_CLICK_BLOCK.register(PrivateIslandAbilityBlock::handleUseItem);
	}

	private static EventResult handleUseItem(PlayerEntity player, Hand hand, BlockPos pos, Direction direction) {
		return handleUseItem(player).result();
	}

	private static CompoundEventResult<ItemStack> handleUseItem(PlayerEntity player, Hand hand) {
		return handleUseItem(player);
	}

	private static CompoundEventResult<ItemStack> handleUseItem(PlayerEntity player) {
		if (!SkyblockerConfigManager.get().mining.enablePrivateIslandAbilityBlock ||
				!Utils.isOnPrivateIsland() || CLIENT.player == null || !CLIENT.player.equals(player))
			return CompoundEventResult.pass();
		for (ItemStack item : player.getHandItems()) {
			if (ItemUtils.getLoreLineIfMatch(item, DRILL) == null) continue;
			return CompoundEventResult.interruptFalse(item);
		}
		return CompoundEventResult.pass();
	}
}
