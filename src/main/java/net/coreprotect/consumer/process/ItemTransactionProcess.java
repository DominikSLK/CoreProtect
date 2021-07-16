package net.coreprotect.consumer.process;

import java.sql.PreparedStatement;
import java.util.Locale;

import org.bukkit.Location;

import net.coreprotect.config.ConfigHandler;
import net.coreprotect.consumer.Queue;
import net.coreprotect.database.logger.ItemLogger;

class ItemTransactionProcess extends Queue {

    static void process(PreparedStatement preparedStmt, int batchCount, int processId, int id, int forceData, int time, String user, Object object) {
        if (object instanceof Location) {
            Location location = (Location) object;
            String loggingItemId = user.toLowerCase(Locale.ROOT) + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

            if (ConfigHandler.loggingItem.get(loggingItemId) != null) {
                int current_chest = ConfigHandler.loggingItem.get(loggingItemId);
                if (ConfigHandler.itemsDrop.get(loggingItemId) == null && ConfigHandler.itemsPickup.get(loggingItemId) == null) {
                    return;
                }
                if (current_chest == forceData) {
                    int currentTime = (int) (System.currentTimeMillis() / 1000L);
                    if (currentTime > time) {
                        ItemLogger.log(preparedStmt, batchCount, location, user);
                        ConfigHandler.itemsDrop.remove(loggingItemId);
                        ConfigHandler.itemsPickup.remove(loggingItemId);
                        ConfigHandler.loggingItem.remove(loggingItemId);
                    }
                    else {
                        Queue.queueItemTransaction(user, location, time, forceData);
                    }
                }
            }
        }
    }
}