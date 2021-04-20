package me.sores.arrow.util.profile;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import me.sores.arrow.Init;
import me.sores.arrow.config.ArrowConfig;
import me.sores.arrow.kit.Kit;
import me.sores.arrow.util.ArrowUtil;
import me.sores.arrow.util.enumerations.HealingItem;
import me.sores.arrow.util.scoreboard.BoardHandler;
import me.sores.arrow.util.theme.Theme;
import me.sores.impulse.util.StringUtil;
import me.sores.impulse.util.profile.PlayerProfile;
import org.bson.Document;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by sores on 4/20/2021.
 */
public class ArrowProfile extends PlayerProfile {

    /**
     * Saved Data
     */
    private String previousKit;
    private int coins, kills, deaths, streak;
    private boolean scoreboard, bloodEffect;
    private HealingItem healingItem;

    private Theme selectedTheme;

    /**
     * Non-Saved Data
     */
    private Kit selectedKit;
    private boolean building;
    private long combatTimer = -1, lastPearlThrow = -1;
    private EnderPearl lastPearl;

    public ArrowProfile(UUID uuid) {
        super(uuid);
        this.selectedKit = null;
        this.previousKit = null;

        coins = 0;
        kills = 0;
        deaths = 0;
        streak = 0;
        healingItem = HealingItem.SOUP;
        scoreboard = true;
        bloodEffect = false;

        selectedTheme = ArrowConfig.defaultTheme;

        building = false;
    }

    public void clean(){
        setCoins(0);
        setKills(0);
        setDeaths(0);
        resetStreak();

        ProfileHandler.getInstance().save(this);
    }

    @Override
    public void saveData() {
        try{
            MongoCollection<Document> collection = Init.getInstance().getMongoBase().getCollection();
            Document fetched = fetchCurrentObject();

            if(fetched != null){
                collection.replaceOne(fetched, createDocument());
            }else{
                collection.insertOne(createDocument());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void loadData() {
        Document fetched = fetchCurrentObject();

        if(fetched != null){
            try{
                //load their data
            }catch (Exception ex){
                StringUtil.log("&c[Arrow] Data load failure for " + getName() + "'s profile.");
                ex.printStackTrace();
            }
        }else{
            saveData();
        }
    }

    @Override
    public Document createDocument() {
        Document document = new Document("_id", getID().toString());

        document.put("name", getName());

        return document;
    }

    @Override
    public Document fetchCurrentObject() {
        FindIterable<Document> cursor = Init.getInstance().getMongoBase().getCollection().find(new Document("_id", getID().toString()));

        return cursor.first();
    }


    /**
     * Saved Data methods
     */

    public String getPreviousKit() {
        return previousKit;
    }

    public void setPreviousKit(String previousKit) {
        this.previousKit = previousKit;
    }

    public boolean hasPreviousKit(){
        return previousKit != null && !previousKit.isEmpty();
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill(){
        setKills(getKills() + 1);
        setStreak(getStreak() + 1);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addDeath(){
        setDeaths(getDeaths() + 1);
    }

    public Double calculateRatio(){
        if(deaths == 0) return (double) kills;

        return Double.parseDouble(ArrowUtil.decimalFormat.format((double) kills / (double) deaths));
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public boolean hasStreak(){
        return streak != 0;
    }

    public void resetStreak(){
        setStreak(0);
    }

    public boolean isScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(boolean scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void showScoreboard(){
        BoardHandler.getInstance().addBoard(getPlayer());
        setScoreboard(true);
    }

    public void hideScoreboard(){
        BoardHandler.getInstance().removeBoard(getPlayer());
        setScoreboard(false);
    }

    public boolean isBloodEffect() {
        return bloodEffect;
    }

    public void setBloodEffect(boolean bloodEffect) {
        this.bloodEffect = bloodEffect;
    }

    public HealingItem getHealingItem() {
        return healingItem;
    }

    public void setHealingItem(HealingItem healingItem) {
        this.healingItem = healingItem;
    }

    /**
     * Non-Saved Data methods
     */

    public Kit getSelectedKit() {
        return selectedKit;
    }

    public void setSelectedKit(Kit selectedKit) {
        this.selectedKit = selectedKit;
    }

    public boolean hasKit(){
        return selectedKit != null;
    }

    public void clearKit(Player player){
        Kit old = getSelectedKit();
        if(getSelectedKit().getRegisteredAbility() != null) getSelectedKit().getRegisteredAbility().destroy(player);

        setPreviousKit(old.getName());
        setSelectedKit(null);
        ArrowUtil.clean(player);
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public long getCombatTimer() {
        return combatTimer;
    }

    public void setCombatTimer(long combatTimer) {
        this.combatTimer = combatTimer;
    }

    public boolean inCombat(){
        return combatTimer > System.currentTimeMillis();
    }

    public void enterCombat(long time){
        setCombatTimer(System.currentTimeMillis() + time);
        if(time <= 0) this.combatTimer = -1;
    }

    public long getLastPearlThrow() {
        return lastPearlThrow;
    }

    public void setLastPearlThrow(long lastPearlThrow) {
        this.lastPearlThrow = lastPearlThrow;
    }

    public boolean hasPearlCooldown(){
        return lastPearlThrow > System.currentTimeMillis();
    }

    public EnderPearl getLastPearl() {
        return lastPearl;
    }

    public void setLastPearl(EnderPearl lastPearl) {
        this.lastPearl = lastPearl;
    }

    public void cleanPearl(Player player){
        cleanPearl(player.getUniqueId());
    }

    public void cleanPearl(UUID uuid){
        if(lastPearl != null && !lastPearl.isDead()) lastPearl.remove();
        lastPearlThrow = -1;
    }

}
