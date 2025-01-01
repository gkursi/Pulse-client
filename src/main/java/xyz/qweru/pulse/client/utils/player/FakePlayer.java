package xyz.qweru.pulse.client.utils.player;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;

import java.util.UUID;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class FakePlayer extends OtherClientPlayerEntity {
    public FakePlayer(String name, float health, boolean copyInv) {
        super(mc.world, new GameProfile(UUID.randomUUID(), name));

        copyPositionAndRotation(mc.player);

        prevYaw = getYaw();
        prevPitch = getPitch();
        headYaw = mc.player.headYaw;
        prevHeadYaw = headYaw;
        bodyYaw = mc.player.bodyYaw;
        prevBodyYaw = bodyYaw;

        Byte playerModel = mc.player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(mc.player.getAttributes());
        setPose(mc.player.getPose());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) getInventory().clone(mc.player.getInventory());
    }

    public void spawn() {
        unsetRemoved();
        mc.world.addEntity(this);
    }

    public void despawn() {
        mc.world.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.getWorld().isClient) {
            return false;
        } else if (this.isDead()) {
            return false;
        } else if (source.isIn(DamageTypeTags.IS_FIRE) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            return false;
        } else {
            if (this.isSleeping() && !this.getWorld().isClient) {
                this.wakeUp();
            }

            this.despawnCounter = 0;
            float f = amount;
            boolean bl = false;
            float g = 0.0F;
            if (amount > 0.0F && this.blockedByShield(source)) {
                this.damageShield(amount);
                g = amount;
                amount = 0.0F;
                if (!source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                    Entity entity = source.getSource();
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        this.takeShieldHit(livingEntity);
                    }
                }

                bl = true;
            }

            if (source.isIn(DamageTypeTags.IS_FREEZING) && this.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                amount *= 5.0F;
            }

            if (source.isIn(DamageTypeTags.DAMAGES_HELMET) && !this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                this.damageHelmet(source, amount);
                amount *= 0.75F;
            }

            this.limbAnimator.setSpeed(1.5F);
            boolean bl2 = true;
            if ((float)this.timeUntilRegen > 10.0F && !source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) {
                if (amount <= this.lastDamageTaken) {
                    return false;
                }

                this.applyDamage(source, amount - this.lastDamageTaken);
                this.lastDamageTaken = amount;
                bl2 = false;
            } else {
                this.lastDamageTaken = amount;
                this.timeUntilRegen = 20;
                this.applyDamage(source, amount);
                this.maxHurtTime = 10;
                this.hurtTime = this.maxHurtTime;
            }

            Entity entity2 = source.getAttacker();
            if (entity2 != null) {
                if (entity2 instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity)entity2;
                    if (!source.isIn(DamageTypeTags.NO_ANGER) && (!source.isOf(DamageTypes.WIND_CHARGE) || !this.getType().isIn(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE))) {
                        this.setAttacker(livingEntity2);
                    }
                }

                if (entity2 instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity)entity2;
                    this.playerHitTimer = 100;
                    this.attackingPlayer = playerEntity;
                } else if (entity2 instanceof WolfEntity) {
                    WolfEntity wolfEntity = (WolfEntity)entity2;
                    if (wolfEntity.isTamed()) {
                        this.playerHitTimer = 100;
                        LivingEntity var11 = wolfEntity.getOwner();
                        if (var11 instanceof PlayerEntity) {
                            PlayerEntity playerEntity2 = (PlayerEntity)var11;
                            this.attackingPlayer = playerEntity2;
                        } else {
                            this.attackingPlayer = null;
                        }
                    }
                }
            }

            if (bl2) {
                if (bl) {
                    this.getWorld().sendEntityStatus(this, (byte)29);
                } else {
                    this.getWorld().sendEntityDamage(this, source);
                }

                if (!source.isIn(DamageTypeTags.NO_IMPACT) && (!bl || amount > 0.0F)) {
                    this.scheduleVelocityUpdate();
                }

                if (!source.isIn(DamageTypeTags.NO_KNOCKBACK)) {
                    double d = (double)0.0F;
                    double e = (double)0.0F;
                    Entity sourceEntity = source.getSource();
                    if (sourceEntity instanceof ProjectileEntity) {
                        ProjectileEntity projectileEntity = (ProjectileEntity)sourceEntity;
                        DoubleDoubleImmutablePair doubleDoubleImmutablePair = projectileEntity.getKnockback(this, source);
                        d = -doubleDoubleImmutablePair.leftDouble();
                        e = -doubleDoubleImmutablePair.rightDouble();
                    } else if (source.getPosition() != null) {
                        d = source.getPosition().getX() - this.getX();
                        e = source.getPosition().getZ() - this.getZ();
                    }

                    this.takeKnockback((double)0.4F, d, e);
                    if (!bl) {
                        this.tiltScreen(d, e);
                    }
                }
            }

            if (this.isDead()) {
                if (!this.tryUseTotem(source)) {
                    if (bl2) {
                        this.playSound(this.getDeathSound());
                    }

                    this.onDeath(source);
                }
            } else if (bl2) {
                this.playHurtSound(source);
            }

            boolean bl3 = !bl || amount > 0.0F;
            if (bl3) {

                for(StatusEffectInstance statusEffectInstance : this.getStatusEffects()) {
                    statusEffectInstance.onEntityDamage(this, source, amount);
                }
            }

            if (entity2 instanceof ServerPlayerEntity) {
                Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, this, source, f, amount, bl);
            }

            return bl3;
        }
    }

    private boolean tryUseTotem(DamageSource source) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            ItemStack itemStack = null;

            for(Hand hand : Hand.values()) {
                ItemStack itemStack2 = this.getStackInHand(hand);
                if (itemStack2.isOf(Items.TOTEM_OF_UNDYING)) {
                    itemStack = itemStack2.copy();
                    itemStack2.decrement(1);
                    break;
                }
            }

            if (itemStack != null) {
                this.setHealth(1.0F);
                this.clearStatusEffects();
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                this.getWorld().sendEntityStatus(this, (byte)35);
            }

            return itemStack != null;
        }
    }
}
