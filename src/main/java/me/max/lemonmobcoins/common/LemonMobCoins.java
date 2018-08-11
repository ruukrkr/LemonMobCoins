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

package me.max.lemonmobcoins.common;

import me.max.lemonmobcoins.common.api.LemonMobCoinsAPI;
import me.max.lemonmobcoins.common.data.CoinManager;
import me.max.lemonmobcoins.common.data.DataProvider;
import me.max.lemonmobcoins.common.data.providers.MySqlProvider;
import me.max.lemonmobcoins.common.data.providers.YamlProvider;
import me.max.lemonmobcoins.common.exceptions.DataLoadException;
import me.max.lemonmobcoins.common.files.coinmob.CoinMobManager;
import me.max.lemonmobcoins.common.files.gui.GuiManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class LemonMobCoins {

    private static LemonMobCoinsAPI lemonMobCoinsAPI;
    private CoinManager coinManager;
    private GuiManager guiManager;
    private CoinMobManager coinMobManager;
    private Logger logger;

    public LemonMobCoins(Logger logger, String dataFolder) throws DataLoadException {
        this.logger = logger;

        try {
            info("Loading data..");
            ConfigurationLoader<ConfigurationNode> loader = YAMLConfigurationLoader.builder().setFile(new File(dataFolder, "config.yml")).build();
            ConfigurationNode node = loader.load();

            String storageType = node.getNode("storage", "type").getString("flatfile");
            DataProvider dataProvider;

            if (storageType.equalsIgnoreCase("mysql")){
                ConfigurationNode mysqlSection = node.getNode("storage", "mysql");
                dataProvider = new MySqlProvider(mysqlSection.getNode("hostname").getString(), mysqlSection.getNode("port").getString(), mysqlSection.getNode("username").getString(), mysqlSection.getNode("password").getString(), mysqlSection.getNode("database").getString());
            } else {
                error("Invalid storage type found! Using flatfile!");
                dataProvider = new YamlProvider(dataFolder);
            }

            coinManager = new CoinManager(dataProvider);
            guiManager = new GuiManager(node, getLogger());
            coinMobManager = new CoinMobManager(node, getLogger());
            info("Loaded data!");
        } catch (SQLException e){
            error("Failed loading MySql!");
            e.printStackTrace();
            return;
        } catch (DataLoadException | IOException e){
            error("Failed loading data!");
            e.printStackTrace();
            return;
        }

        info("Loading API..");
        lemonMobCoinsAPI = coinManager;
        info("Loaded API!");
    }

    public void disable() throws IOException, SQLException{
        getCoinManager().saveData();
    }

    private void info(String s){
        getLogger().info(s);
    }

    private void warn(String s){
        getLogger().warn(s);
    }

    private void error(String s){
        getLogger().error(s);
    }

    public CoinManager getCoinManager() {
        return coinManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public CoinMobManager getCoinMobManager() {
        return coinMobManager;
    }

    @SuppressWarnings("unused")
    public static LemonMobCoinsAPI getLemonMobCoinsAPI() {
        return lemonMobCoinsAPI;
    }

    private Logger getLogger() {
        return logger;
    }
}