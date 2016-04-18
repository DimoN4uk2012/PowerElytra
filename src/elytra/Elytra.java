package elytra;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import static org.bukkit.Sound.valueOf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Elytra extends JavaPlugin implements Listener{
    int version = 2;
    
    Map<String, Long> delay = new HashMap<>();
    Map<String, Long> delayMessage = new HashMap<>();
    int setDelay = 4;
    float velocity = 1.5F;
    
    boolean enablePowerParticle = true;
    boolean enablePowerSound = true;
    boolean enablePowerSoundReload = true;
    boolean enableFlyParticle = true;
    
    boolean enablePowerFuel = true;
    boolean enableLoreDetect = false;
    boolean enableMessagesFuel = true;
    int fuelId = 385;
    int fuelData = 0;
    int fuelCount = 1;
    List<String> fuelLore;
    String messageFuel = "You need have fuel - Fire Charge, to use power!";
    
    Particle particleElytra = Particle.valueOf("CLOUD");
    Particle particlePower = Particle.valueOf("FLAME");
    Sound soundPower = Sound.valueOf("ITEM_FIRECHARGE_USE");
    Sound soundReload = Sound.valueOf("BLOCK_FIRE_EXTINGUISH");
    
    float soundVolumePower = 1.0F;
    float soundVolumePowerReload = 1.0F;
    float soundVolumeElytra = 1.0F;
    
    float soundPichPower = 1.0F;
    float soundPichPowerReload = 1.0F;
    float soundPichElytra = 1.0F;
    
    int particleCountPower = 16;
    float particlePowerDx = 0.1F;
    float particlePowerDy = 0.1F;
    float particlePowerDz = 0.1F;
    float particlePowerSpeed = 0.15F;
    
    int particleCountElytra = 3;
    float particleFlyDx = 0.1F;
    float particleFlyDy = 0.1F;
    float particleFlyDz = 0.1F;
    float particleFlySpeed = 0.1F;
    
    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info("[PowerElitra] Plugin - Enable!");
        reloadConfigElytra();
    }
    
    @Override
    public void onDisable(){
        Bukkit.getLogger().info("[PowerElitra] Plugin - Disable.");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String com = "elytra";
        if (sender.hasPermission("powerelytra.admin.reload")){
            if (args.length == 1 && command.getName().equals(com) && args[0].equals("reload")){
                this.reloadConfig();
                reloadConfigElytra();
                sender.sendMessage("[PowerElitra] Reloaded.");
                return true;
            }
        }
        if (sender.hasPermission("powerelytra.admin.givefuel")){
            if (args.length > 1 && command.getName().equals(com) && args[0].equals("givefuel")){
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null){
                    if(args.length == 2 && args[1] != null){
                        ItemStack fuel = new ItemStack(this.fuelId, this.fuelCount, (short) this.fuelData);
                        if (this.enableLoreDetect){
                            fuel.getItemMeta().setLore(this.fuelLore);
                        }
                        Bukkit.getWorld(player.getWorld().getUID()).dropItem(player.getLocation(), fuel);
                        return true;
                    }
                    if(args.length == 3 && args[1] != null){
                        int count = Integer.parseInt(args[2]);
                        if(count > 0 && count < 129){
                            ItemStack fuel = new ItemStack(this.fuelId, count, (short) this.fuelData);
                            if (this.enableLoreDetect){
                                ItemMeta meta = fuel.getItemMeta();
                                meta.setLore(this.fuelLore);
                                fuel.setItemMeta(meta);
                            }
                            for (ItemStack extra : player.getInventory().addItem(fuel).values()) {
                                Item item = Bukkit.getWorld(player.getWorld().getUID()).dropItem(player.getLocation(), extra);
                                item.setPickupDelay(0);
                            }
                            return true;
                        }else{sender.sendMessage("/elytra givefuel PLAYER (1-128)");}
                    }else{sender.sendMessage("/elytra givefuel PLAYER 1");}
                }else{
                        sender.sendMessage("/elytra givefuel PLAYER 1");
                    }
                return true;
            }
        }
        return true;
    }
        
    private void reloadConfigElytra(){
        File cfgFile = new File(this.getDataFolder(), "config.yml");
        if (!cfgFile.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
            Bukkit.getLogger().info("[PowerElitra] Load Default Config File.");
        }else{
                File cfgFileOld = new File(this.getDataFolder(), "configOld-v" + this.getConfig().getInt("version") + ".yml");
                if (this.version != this.getConfig().getInt("version")){
                    cfgFile.renameTo(cfgFileOld);
                    cfgFile.delete();
                    if (!cfgFile.exists()) {
                        this.getConfig().options().copyDefaults(true);
                        this.saveDefaultConfig();
                        Bukkit.getLogger().info("[PowerElitra] Load Default Config File.");
                    }
                    Bukkit.getLogger().info("[PowerElitra] Load Default Config File. Config version != " + this.version);
                }
            }
        this.reloadConfig();
        if (cfgFile.exists()) {
            FileConfiguration c = this.getConfig();
            this.setDelay = c.getInt("delay");
            this.velocity = (float) c.getDouble("velocity");
            this.enablePowerParticle = c.getBoolean("power.particle.enable");
            this.particlePower = Particle.valueOf(c.getString("power.particle.type"));
            this.particleCountPower = c.getInt("power.particle.count");
            this.particlePowerDx = (float) c.getDouble("power.particle.dx");
            this.particlePowerDy = (float) c.getDouble("power.particle.dy");
            this.particlePowerDz = (float) c.getDouble("power.particle.dz");
            this.particlePowerSpeed = (float) c.getDouble("power.particle.speed");
            this.enablePowerSound = c.getBoolean("power.sound.enable");
            this.soundPower = Sound.valueOf(c.getString("power.sound.type"));
            this.soundVolumePower = (float) c.getDouble("power.sound.volume");
            this.soundPichPower = (float) c.getDouble("power.sound.pich");
            this.enablePowerSoundReload = c.getBoolean("power.soundReload.enable");
            this.soundReload = Sound.valueOf(c.getString("power.soundReload.type"));
            this.soundVolumePowerReload = (float) c.getDouble("power.soundReload.volume");
            this.soundPichPowerReload = (float) c.getDouble("power.soundReload.pich");
            
            this.enableFlyParticle = c.getBoolean("fly.particle.enable");
            this.particleElytra = Particle.valueOf(c.getString("fly.particle.type"));
            this.particleCountElytra = c.getInt("fly.particle.count");
            this.particleFlyDx = (float) c.getDouble("fly.particle.dx");
            this.particleFlyDy = (float) c.getDouble("fly.particle.dy");
            this.particleFlyDz = (float) c.getDouble("fly.particle.dz");
            this.particleFlySpeed = (float) c.getDouble("fly.particle.speed");
            
            this.enablePowerFuel = c.getBoolean("fuel.enable");
            this.enableLoreDetect = c.getBoolean("fuel.item.loreDetect.enable");
            this.enableMessagesFuel = c.getBoolean("fuel.messages.enable");
            this.fuelId = c.getInt("fuel.item.id");
            this.fuelData = c.getInt("fuel.item.data");
            this.fuelCount = c.getInt("fuel.item.count");
            this.fuelLore = c.getStringList("fuel.item.loreDetect.lore");
            this.messageFuel = c.getString("fuel.messages.message");
            
            Bukkit.getLogger().info("[PowerElitra] Config Reloaded.");
        }
    }
    
    public void power(Player player){
        this.delay.put(player.getName(), System.currentTimeMillis() + (this.setDelay * 1000L));
        Vector pv = player.getLocation().getDirection();
        Vector v = pv.multiply(velocity);
        player.setVelocity(v);
        if (enablePowerSound){
            player.getWorld().playSound(player.getLocation(), this.soundPower, this.soundVolumePower, this.soundPichPower);
        }
        if (enablePowerParticle){
            player.getWorld().spawnParticle(this.particlePower, player.getLocation(), this.particleCountPower, this.particlePowerDx, this.particlePowerDy, this.particlePowerDz, this.particlePowerSpeed);
        }
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent move) {
        Player player = move.getPlayer();
        ItemStack chestItem = player.getInventory().getChestplate();
        if (chestItem != null && chestItem.getType().equals(Material.ELYTRA) && !player.isOnGround() && !player.isFlying()) {
            if (player.hasPermission("powerelytra.player.use")){
                if (this.delay.get(player.getName()) == null || this.delay.get(player.getName()) <= System.currentTimeMillis()) {
                    if (player.isSneaking()){
                        if (this.enablePowerFuel){
                            if (player.hasPermission("powerelytra.admin.nofuel")){
                                power(player);
                            }else{
                                    ItemStack fuel = new ItemStack(this.fuelId, this.fuelCount, (short) this.fuelData);
                                    if (this.enableLoreDetect){
                                        ItemMeta meta = fuel.getItemMeta();
                                        meta.setLore(this.fuelLore);
                                        fuel.setItemMeta(meta);
                                    }
                                    ItemStack[] items = player.getInventory().getContents();
                                    int totalCount = 0;
                                    int delete = this.fuelCount;
                                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                                        ItemStack item = player.getInventory().getItem(i);
                                        if(fuel.isSimilar(item)){
                                            totalCount += item.getAmount();
                                        }
                                    }
                                    if (totalCount >= this.fuelCount){
                                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                                            ItemStack item = player.getInventory().getItem(i);
                                            if(fuel.isSimilar(item)){
                                                if (delete > 0){
                                                    if (item.getAmount() <= delete){
                                                        delete -= item.getAmount();
                                                        player.getInventory().setItem(i, null);
                                                        break;
                                                    }else{
                                                            item.setAmount(item.getAmount() - delete);
                                                        }
                                                }else{break;}
                                            }
                                        }
                                        power(player);
                                    }else{
                                            if(this.enableMessagesFuel){
                                                if (this.delayMessage.get(player.getName()) == null || this.delayMessage.get(player.getName()) <= System.currentTimeMillis()){
                                                    this.delayMessage.put(player.getName(), System.currentTimeMillis() + (this.setDelay * 1000L));
                                                    player.sendMessage(this.messageFuel);
                                                }
                                            }
                                        }
                                }
                        }
                    }
                    if (this.delay.get(player.getName()) != null && this.setDelay != 0){
                        if (this.delay.get(player.getName())/1000 == System.currentTimeMillis()/1000){
                            if (enablePowerSoundReload){
                                player.getWorld().playSound(player.getLocation(), this.soundReload, this.soundVolumeElytra, this.soundPichElytra);
                            }
                            this.delay.remove(player.getName());
                        }
                    }
                }else {
                        if (enableFlyParticle){
                            player.getWorld().spawnParticle(this.particleElytra, player.getLocation(), this.particleCountElytra, this.particleFlyDx, this.particleFlyDy, this.particleFlyDz, this.particleFlySpeed);
                        }
                    }
            }
        }
    }
    
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent quit){
        Player player = quit.getPlayer();
        this.delay.remove(player.getName());
        this.delayMessage.remove(player.getName());
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> complitions = new LinkedList<>();
        if (sender.hasPermission("powerelytra.admin.reload")){
            if (args.length == 1){
                complitions.add("reload");
            }
        }
        if (sender.hasPermission("powerelytra.admin.givefuel")){
            if (args.length == 1){
                complitions.add("givefuel");
            }
            if (args.length == 2 && args[0] != null){
                return null;
            }
            if (args.length == 3 && args[0] != null && args[1] != null){
                complitions.add("" + this.fuelCount);
            }
        }
        return complitions;
    }
    
    
}
