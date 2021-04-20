package me.sores.arrow;

import me.sores.arrow.commands.Command_arrowreload;
import me.sores.arrow.config.AbilityConfig;
import me.sores.arrow.config.ArrowConfig;
import me.sores.arrow.config.KitsConfig;
import me.sores.arrow.kit.AbilityHandler;
import me.sores.arrow.kit.KitsHandler;
import me.sores.arrow.listener.Listener_kitlistener;
import me.sores.arrow.listener.Listener_playerlistener;
import me.sores.arrow.listener.Listener_worldlistener;
import me.sores.arrow.util.ImpulseHook;
import me.sores.arrow.util.profile.ProfileHandler;
import me.sores.arrow.util.scoreboard.BoardHandler;
import me.sores.impulse.util.StringUtil;
import me.sores.impulse.util.abstr.AbstractInit;
import me.sores.impulse.util.cmdfrmwrk.BaseCommand;
import me.sores.impulse.util.database.MongoBase;
import org.bukkit.plugin.Plugin;

/**
 * Created by sores on 4/20/2021.
 */
public class Init extends AbstractInit {

    private static Init instance;
    private MongoBase mongoBase;
    
    public Init(Plugin plugin) {
        super(plugin);
        instance = this;

        ImpulseHook.hook(Arrow.getInstance());
        
        initInstances();
        registerCommands();
        registerEvents();
    }

    @Override
    public void initInstances() {
        new ArrowConfig();
        
        try{
            mongoBase = new MongoBase(ArrowConfig.DATABASE_HOST, ArrowConfig.DATABASE_USERNAME, ArrowConfig.DATABASE_PASSWORD, ArrowConfig.DATABASE_NAME, ArrowConfig.DATABASE_COLLECTION);
        }catch (Exception ex){
            StringUtil.log("&c[Arrow] Could not connect to MongoDB Database, make sure it is setup.");
        }

        new KitsConfig();
        new AbilityConfig();

        initHandler(new ProfileHandler(), true);
        initHandler(new BoardHandler(), false);
        initHandler(new AbilityHandler(), false);
        initHandler(new KitsHandler(), false);
        
    }

    @Override
    public void registerEvents() {
        registerListener(new Listener_playerlistener(), new Listener_worldlistener(), new Listener_kitlistener());
    }

    @Override
    public void registerCommands() {
        registerCommand("arrowreload", new Command_arrowreload());
    }

    @Override
    public void unload() {
        getHandlerList().forEach(handler -> {
            try{
                handler.unload();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        instance = null;
    }

    public void registerCommand(String title, BaseCommand command) {
        registerCommand(getCommandRegistrar(), title, command);
    }

    public MongoBase getMongoBase() {
        return mongoBase;
    }

    public static Init getInstance() {
        return instance;
    }
}
