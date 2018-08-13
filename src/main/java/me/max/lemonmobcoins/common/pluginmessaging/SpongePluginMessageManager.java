/*
 *
 *  *
 *  *  * MobCoins - Earn coins for killing mobs.
 *  *  * Copyright (C) 2018 Max Berkelmans AKA LemmoTresto
 *  *  *
 *  *  * This program is free software: you can redistribute it and/or modify
 *  *  * it under the terms of the GNU General Public License as published by
 *  *  * the Free Software Foundation, either version 3 of the License, or
 *  *  * (at your option) any later version.
 *  *  *
 *  *  * This program is distributed in the hope that it will be useful,
 *  *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  *  * GNU General Public License for more details.
 *  *  *
 *  *  * You should have received a copy of the GNU General Public License
 *  *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  *
 *
 */

package me.max.lemonmobcoins.common.pluginmessaging;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.max.lemonmobcoins.common.data.CoinManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public class SpongePluginMessageManager extends AbstractPluginMessageManager {

    public SpongePluginMessageManager(CoinManager coinManager, Logger logger) {
        super(coinManager, logger);
    }

    public void sendPluginMessage(UUID uuid) {
        Player p = Iterables.getFirst(Sponge.getGame().getServer().getOnlinePlayers(), null);
        if (p == null) {
            if (getCache().contains(uuid)) return;
            getCache().add(uuid);
            return;
        }

        double balance = getCoinManager().getCoinsOfPlayer(uuid);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("LemonMobCoins");
        out.writeUTF(uuid.toString());
        out.writeDouble(balance);

        //todo send plugin msg

        getLogger().info("Sent information of Player " + uuid + ". Balance sent: " + balance);
    }

}