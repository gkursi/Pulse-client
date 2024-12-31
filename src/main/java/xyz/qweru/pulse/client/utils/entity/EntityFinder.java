package xyz.qweru.pulse.client.utils.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.utils.world.PosUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class EntityFinder {

    public static EntityList findEntitiesInRange(double range, Vec3d pos) {
        EntityList entityList = new EntityList();

        for (Entity entity : mc.world.getEntities()) {
            if(PosUtil.distanceBetween(pos, entity.getPos()) <= range) entityList.add(entity);
        }

        return entityList;
    }

    public static EntityList findEntitiesIn2DRange(double range, Vec3d pos) {
        EntityList entityList = new EntityList();

        for (Entity entity : mc.world.getEntities()) {
            if(Pulse2D.distance(pos.x, pos.z, entity.getX(), entity.getZ()) <= range) entityList.add(entity);
        }

        return entityList;
    }

    public static EntityList findEntitiesAtPos(Vec3d pos) {
        EntityList entityList = new EntityList();
        for (Entity entity : mc.world.getEntities()) {
            if(entity.getPos().equals(pos)) entityList.add(entity);
        }

        return entityList;
    }

    public static class EntityList {
        List<Entity> entities;
        public EntityList(ArrayList<Entity> entities) {
            this.entities = entities;
        }

        public EntityList(Entity... entities) {
            this.entities = new ArrayList<>(List.of(entities));
        }

        public EntityList filterForEntities(Entity... entities) {
            List<Entity> filters = List.of(entities);
            List<Entity> newList = new ArrayList<>();
            for (Entity entity : this.entities) {
                for (Entity filter : filters) {
                    if(entity.getClass().equals(filter.getClass())) {
                        newList.add(entity);
                        break;
                    }
                }
            }
            this.entities = newList;
            return this;
        }

        public EntityList filterClass(Class... entities) {
            List<Class> filters = List.of(entities);
            List<Entity> newList = new ArrayList<>();
            for (Entity entity : this.entities) {
                for (Class filter : filters) {
                    if(entity.getClass().equals(filter)) {
                        newList.add(entity);
                        break;
                    }
                }
            }
            this.entities = newList;
            return this;
        }

        public EntityList filter(EntityFilter filter) {
            List<Entity> newList = new ArrayList<>();
            for (Entity entity : this.entities) {
                if(filter.apply(entity)) {
                    newList.add(entity);
                    break;
                }
            }
            this.entities = newList;
            PulseClient.LOGGER.info("Filtered to {} entities", entities.size());
            return this;
        }

        public void add(Entity entity) {
            entities.add(entity);
        }

        public List<Entity> get() {
            return entities;
        }

        public boolean isEmpty() {
            return entities.isEmpty();
        }

        public static interface EntityFilter {
            boolean apply(Entity entity);
        }

        public static EntityFilter LIVING_ENTITY = entity -> entity instanceof LivingEntity;

        public static EntityFilter PLAYER_ENTITY = entity -> entity instanceof PlayerEntity;
        public static EntityFilter PASSIVE_ENTITY = entity -> entity instanceof PassiveEntity;
        public static EntityFilter AGGRESSIVE_ENTITY = entity -> entity instanceof MobEntity;

        public COWEntityList getThreadSafe() {
            COWEntityList list = new COWEntityList();
            for (Entity entity : this.entities) {
                list.add(entity);
            }

            return list;
        }
    }

    /**
     * thread-safe entity list
     */
    public static class COWEntityList {
        CopyOnWriteArrayList<Entity> entities;
        public COWEntityList(CopyOnWriteArrayList<Entity> entities) {
            this.entities = entities;
        }

        public COWEntityList(Entity... entities) {
            this.entities = new CopyOnWriteArrayList<>(entities);
        }

        public COWEntityList sort(Comparator<? super Entity> comparator) {
            this.entities.sort(comparator);
            return this;
        }

        public COWEntityList filterForEntities(Entity... entities) {
            List<Entity> filters = List.of(entities);
            CopyOnWriteArrayList<Entity> newList = new CopyOnWriteArrayList<>();
            for (Entity entity : this.entities) {
                for (Entity filter : filters) {
                    if(entity.getClass().equals(filter.getClass())) {
                        newList.add(entity);
                        break;
                    }
                }
            }
            this.entities = newList;
            return this;
        }

        public COWEntityList filterClass(Class... entities) {
            List<Class> filters = List.of(entities);
            CopyOnWriteArrayList<Entity> newList = new CopyOnWriteArrayList<>();
            for (Entity entity : this.entities) {
                for (Class filter : filters) {
                    if(entity.getClass().equals(filter)) {
                        newList.add(entity);
                        break;
                    }
                }
            }
            this.entities = newList;
            return this;
        }

        public COWEntityList filter(EntityFilter filter) {
            CopyOnWriteArrayList<Entity> newList = new CopyOnWriteArrayList<>();
            for (Entity entity : this.entities) {
                if(filter.apply(entity)) {
                    newList.add(entity);
                    break;
                }
            }
            this.entities = newList;
            return this;
        }

        public void add(Entity entity) {
            entities.add(entity);
        }

        public List<Entity> get() {
            return entities;
        }

        public boolean isEmpty() {
            return entities.isEmpty();
        }

        @FunctionalInterface
        public static interface EntityFilter {
            boolean apply(Entity entity);
        }

        public static EntityFilter LIVING_ENTITY = entity -> entity instanceof LivingEntity;

        public static EntityFilter PLAYER_ENTITY = entity -> entity instanceof PlayerEntity;
        public static EntityFilter PASSIVE_ENTITY = entity -> entity instanceof PassiveEntity;
        public static EntityFilter AGGRESSIVE_ENTITY = entity -> entity instanceof MobEntity;

    }
}
