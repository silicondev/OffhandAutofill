package uk.phyre.OffhandAutofill;

import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import uk.phyre.OffhandAutofill.events.InventoryEvents;

import javax.annotation.Nonnull;

public class OffhandAutofill extends JavaPlugin {

    public OffhandAutofill(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, InventoryEvents::onInventoryChange);
    }
}