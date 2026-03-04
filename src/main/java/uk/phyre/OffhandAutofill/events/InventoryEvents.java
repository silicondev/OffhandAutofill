package uk.phyre.OffhandAutofill.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;

public class InventoryEvents
{
    public static void onInventoryChange(LivingEntityInventoryChangeEvent event) {
        var entity = event.getEntity();
        if (entity == null)
            return;

        if (entity.getReference() == null)
            return;

        if (!(event.getTransaction() instanceof ItemStackTransaction transaction))
            return;

        if (!transaction.succeeded())
            return;

        var inv = entity.getInventory();
        var offhand = inv.getUtility();
        var currentContainer = event.getItemContainer();

        // Already in offhand
        if (currentContainer == offhand)
            return;

        for (var slot : transaction.getSlotTransactions()) {
            if (slot == null || !slot.succeeded())
                continue;

            // We only care if we've just picked up the item.
            var slotBefore = slot.getSlotBefore();
            if (slotBefore != null)
                continue;

            var currentStack = slot.getSlotAfter();
            if (currentStack == null || currentStack.isEmpty())
                continue;

            int amountLeft = currentStack.getQuantity();
            for (short i = 0; i < offhand.getCapacity(); i++) {

                var offhandStack = offhand.getItemStack(i);
                if (offhandStack == null || !offhandStack.isStackableWith(currentStack))
                    continue;

                int maxStack = offhandStack.getItem().getMaxStack();
                int current = offhandStack.getQuantity();
                int diff = maxStack - current;
                int amountToMove = Math.min(diff, amountLeft);

                if (amountToMove == 0)
                    continue;

                offhand.setItemStackForSlot(i, offhandStack.withQuantity(current + amountToMove));
                amountLeft -= amountToMove;
                if (amountLeft <= 0)
                    break;
            }

            // If we have none left, remove the new stack from the regular inventory. If not, adjust the stack size to whatever we couldnt fit in the offhand.
            if (amountLeft <= 0) {
                currentContainer.removeItemStackFromSlot(slot.getSlot());
            }
            else {
                currentContainer.setItemStackForSlot(slot.getSlot(), currentStack.withQuantity(amountLeft));
            }
        }
    }

}