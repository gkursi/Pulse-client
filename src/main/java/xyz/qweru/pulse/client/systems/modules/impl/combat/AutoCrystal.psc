
#! Pseudo-code of Auto Crystal rewrite
// settings
ignoreFriends = booleanSetting()

// range settings
range = numberSetting()
crystalScanRange = numberSetting() // how far ground around the target should be scanned
interactRange = numberSetting() // how far from the player can crystals be placed / broken

// damage settings
minEnemyDamage = numberSetting()
maxSelfDamage = numberSetting()
maxFriendDamage = numberSetting()
ignoreFriendDamage = booleanSetting()

// delay settings
placeDelay = numberSetting()
breakDelay = numberSetting()
iterationDelay = numberSetting(0)

// face place settings
doFacePlace = booleanSetting()
facePlaceMaxHealth = numberSetting()
facePlacePlaceDelay = numberSetting()
facePlaceBreakDelay = numberSetting()
forceFacePlaceKey = keybindSetting()

// extrapolation settings
doExtrapolation = booleanSetting()
extrapolateTargetMoveTicks = numberSetting(1)
extrapolateSelfMoveTicks = numberSetting(2)
// extrapolateTargetHealthTicks = numberSetting(0)
// extrapolateSelfHealthTicks = numberSetting(0)
// checkAllFuture = booleanSetting() // check all ticks leading up to set tick (don't know how to implement yet)
checkPresent = booleanSetting()

// mine
doMine = booleanSetting()
packetMine = booleanSetting()
pauseCrystals = booleanSetting()
maxMineTicks = numberSetting()
minMineDurability = numberSetting()

// support
doSupport = booleanSetting()
switch = modeSetting(normal, silent)
minStackAmount = numberSetting()
airPlace = booleanSetting()
supportRange = numberSetting()

// misc settings
rotate = booleanSetting()
maxTargetCount = numberSetting()
pauseOnItemUse = booleanSetting()
pauseOnMove = booleanSetting()
sequential = booleanSetting() // makes the entire module run in one big thread instead of multiple smaller ones
minSelfHealth = numberSetting() // min health for the module to work
crystalSwitch = modeSetting(normal, silent, offHand, offHandSilent)
onlyOwn = booleanSetting() // ignores break sort mode
placeSort = modeSetting(damage, distance)

// performance settings
performanceMode = booleanSetting() // calculates placements less
performanceTicks = numberSetting()

// vars
ptCounter = int
crystalsToPlace = List<UnfinishedEncCrystalData>
crystals = List<EndCrystalData>
usingCrystals = bool

fn onTick() {
    if(player.health < minSelfHealth) return
    if(performanceMode) {
        if(ptCounter = 0) {
            calcPlacements()
            ptCounter++;
        } else if(ptCounter >= performanceTicks) {
            ptCounter = 0;
        }
        run()
    } else {
        calcPlacements()
        run()
    }
}

fn calcPlacements() {
    if(usingCrystals) return
    crystalsToPlace.clear()
    for(entity of world.entities) {
        if(entity is EndCrystalEntity) continue; // ignore other end crystals
        possiblePlacements = List<UnfinishedEndCrystalData>
        targetPos = entity.pos
        if(doExtrapolation) targetPos = PosUtil.predictPos(entity, floor(extrapolateTargetMoveTicks))
        BlockUtil.forBlocksInRange((x, y, z, pos) -> {
             if(canPlaceCrystal(pos)) {
                float damage = DamageUtil.getCrystalDamage(entity, pos.add(0, 1, 0))
                float selfDamage = DamageUtil.getCrystalDamage(player, pos.add(0, 1, 0))
                double distance = PosUtil.getDistanceBetween(pos.add(0, 1, 0).bottomCenterPos(), entity.getPos())
                possiblePlacements.add(new UnfinishedEndCrystalData(pos.centerPos(), distance, damage, selfDamage))
             }
        }, crystalScanRange, targetPos)

        bestPlacement = UnfinishedEndCrystalData
        switch [placeSort] {
            case "damage" -> bestPlacement = getBestDamage(possiblePlacements)
            case "distance" -> bestPlacement = getBestDistance(possiblePlacements)
        }

        if(bestPlacement != null) {
            crystalsToPlace.add(bestPlacement)
        }
    }
}

fn canPlaceCrystal(BlockPos pos) {
    return (BlockUtil.getBlockAt(pos).equals(Blocks.Obsidian) || BlockUtil.getBlockAt(pos).equals(Blocks.Bedrock)) && BlockUtil.getBlockAt(pos.add(0, 1, 0)).equals(Blocks.Air)
}

fn run() {
    if(usingCrystals) return // prevent 2x run from happening at once
    usingCrystals = true
    SlotUtil.runWithItem((slot, inventory) -> {
        for(ecData of crystalsToPlace) {
                hitResult = new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false);
                Util.sleep(placeDelay)
                interactionManager.interactBlock(player, Hand.MAIN_HAND, hitResult);
                Util.sleep(breakDelay)
                crystalEntity = getCrystalPos(pos.add(0, 1, 0).bottomCenterPos())
                if(crystalEntity != null) interactionManager.attackEntity(mc.player, crystalEntity)
        }
    }, Items.End_Crystal)
    usingCrystals = false
}

fn getCrystal(pos) {
    entityList = EntityFinder.getEntitiesAtPos(pos).filter(EndCrystalEntity.class)
    if(entityList.isEmpty()) return null
    else return entityList.get().get(0)
}

fn getBestDamage(List<UnfinishedEndCrystalData> list) -> UnfinishedEndCrystalData {
    bestCrystal = UnfinishedEncCrystalData
    for(ec of list) {
        if(bestCrystal == null) bestCrystal = ec
        else if (ec.damageToTarget > bestCrystal.damageToTarget) bestCrystal = ec
    }
    return bestCrystal
}

fn getBestDistance(List<UnfinishedEndCrystalData> list) -> UnfinishedEndCrystalData {
    bestCrystal = UnfinishedEncCrystalData
    for(ec of list) {
        if(bestCrystal == null) bestCrystal = ec
        else if (ec.distanceToTarget < bestCrystal.distanceToTarget) bestCrystal = ec
    }
    return bestCrystal
}

record EndCrystalData(EndCrystalEntity entity, Vec3D pos, Double distanceToTarget, Float damageToTarget, Float damageToSelf) {}
record UnfinishedEndCrystalData(Vec3D pos, Double distanceToTarget, Float damageToTarget, Float damageToSelf) {}