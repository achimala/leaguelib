package com.achimala.leaguelib.models.runes;

public enum RuneStatType {
    LIFESTEAL("PercentLifeStealMod", "lifesteal", false),
    ARMOR_PENETRATION_PER_LEVEL("rFlatArmorPenetrationModPerLevel", "armor penetration/level", true),
    ENERGY_PER_LEVEL("rFlatEnergyModPerLevel", "energy/level", true),
    ARMOR_PER_LEVEL("rFlatArmorModPerLevel", "armor/level", true),
    ABILITY_POWER("FlatMagicDamageMod", "ability power", true),
    HEALTH_PER_LEVEL("rFlatHPModPerLevel", "health/level", true),
    MOVEMENT_SPEED("PercentMovementSpeedMod", "movement speed", false),
    MAGIC_RESIST("FlatSpellBlockMod", "magic resist", true),
    ENERGY_REGEN_PER_LEVEL("rFlatEnergyRegenModPerLevel", "energy regen/level", true),
    CRITICAL_DAMAGE("FlatCritDamageMod", "critical damage", true),
    ENERGY("FlatEnergyPoolMod", "energy", true),
    DODGE("rFlatDodgeMod", "dodge", true),
    ATTACK_DAMAGE_PER_LEVEL("rFlatPhysicalDamageModPerLevel", "attack damage/level", true),
    MANA_REGEN_PER_LEVEL("rFlatMPRegenModPerLevel", "mana regen/level", true),
    MANA("FlatMPPoolMod", "mana", true),
    ATTACK_DAMAGE("FlatPhysicalDamageMod", "attack damage", true),
    GOLD_PER_10("rFlatGoldPer10Mod", "gold/10", true),
    HEALTH("PercentHPPoolMod", "health", false),
    COOLDOWNS("rPercentCooldownMod", "cooldowns", false),
    ABILITY_POWER_PER_LEVEL("rFlatMagicDamageModPerLevel", "ability power/level", true),
    COOLDOWNS_PER_LEVEL("rPercentCooldownModPerLevel", "cooldowns/level", false),
    ATTACK_SPEED("PercentAttackSpeedMod", "attack speed", false),
    MAGIC_PENETRATION_PER_LEVEL("rFlatMagicPenetrationModPerLevel", "magic penetration/level", true),
    TIME_DEAD("rPercentTimeDeadMod", "time dead", false),
    HEALTH_REGEN_PER_LEVEL("rFlatHPRegenModPerLevel", "health regen/level", true),
    EXPERIENCE_GAINED("PercentEXPBonus", "experience gained", false),
    ENERGY_REGEN("FlatEnergyRegenMod", "energy regen", true),
    ATTACK_SPEED_PER_LEVEL("rPercentAttackSpeedModPerLevel", "attack speed/level", false),
    SPELLVAMP("PercentSpellVampMod", "spellvamp", false),
    CRITICAL_DAMAGE_PER_LEVEL("rFlatCritDamageModPerLevel", "critical damage/level", true),
    MANA_REGEN("FlatMPRegenMod", "mana regen", true),
    MANA_PER_LEVEL("rFlatMPModPerLevel", "mana/level", true),
    MAGIC_PENETRATION("rFlatMagicPenetrationMod", "magic penetration", true),
    ARMOR("FlatArmorMod", "armor", true),
    CRITICAL_CHANCE_PER_LEVEL("rFlatCritChanceModPerLevel", "critical chance/level", true),
    MAGIC_RESIST_PER_LEVEL("rFlatSpellBlockModPerLevel", "magic resist/level", true),
    HEALTH_REGEN("FlatHPRegenMod", "health regen", true),
    DODGE_PER_LEVEL("rFlatDodgeModPerLevel", "dodge/level", true),
    HEALTH("FlatHPPoolMod", "health", true),
    CRITICAL_CHANCE("FlatCritChanceMod", "critical chance", true),
    ARMOR_PENETRATION("rFlatArmorPenetrationMod", "armor penetration", true);
    
    private String _internalName, _unit;
    private boolean _flat;
    
    private RuneStatType(String internalName, String unit, boolean flat) {
        _internalName = internalName;
        _unit = unit;
        _flat = flat;
    }
    
    public String getHumanReadableString(double amount) {
        String n = "";
        if(!_flat)
            amount *= 100;
        if(amount == (int)amount)
            n += (int)amount;
        else
            n += amount;
        if(!_flat)
            n += "%";
        return String.format("%s %s", n, _unit);
    }
}