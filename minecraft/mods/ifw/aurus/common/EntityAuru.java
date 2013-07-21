package mods.ifw.aurus.common;

import mods.ifw.pathfinding.AStarNode;
import mods.ifw.pathfinding.AStarPathPlanner;
import mods.ifw.pathfinding.IAStarPathedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAINearestAttackableTargetSorter;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.*;

public class EntityAuru extends EntityLiving implements IMob,
        IAStarPathedEntity {
    // Fields

    // AI
    public int courseChangeCooldown = 0;
    protected int targetHealth;

    public double waypointX;
    public double waypointY;
    public double waypointZ;

    public double targetX;
    public double targetY;
    public double targetZ;

    protected int attackCounter;
    protected int patience = 400;
    protected int ticksSinceModelChanged = 0;

    static final int MODEL_FLYING = 0;
    static final int MODEL_WALKING = 1;
    static final int MODEL_SLEEPING = 2;
    static final int MODEL_LEERING = 3;

    public int lastStatus;

    public static enum Mood {
        //
        VICIOUS(new Phase[]{Phase.ATTACK_UNTIL_DAMAGE_TAKEN, Phase.ATTACK_ONCE_IMMEDIATELY}),
        //
        ANXIOUS(
                new Phase[]{Phase.HIDE_2_SEC, Phase.SNEAK, Phase.ATTACK_ONCE}),
        //
        CAUTIOUS(new Phase[]{Phase.OBSERVE_UNTIL_PROVOKED,
                Phase.ATTACK_UNTIL_HP_LOW, Phase.HIDE_UNTIL_FOUND}),
        //
        COLD(new Phase[]{Phase.ATTACK_ONCE, Phase.ATTACK_ONCE, Phase.ATTACK_ONCE,
                Phase.HIDE_5_SEC}),
        //
        GUERILLA(new Phase[]{Phase.ATTACK_ONCE,
                Phase.ATTACK_UNTIL_TARGET_IS_HURT, Phase.HIDE_5_SEC}),
        //
        PLAYFUL(new Phase[]{Phase.ATTACK_ONCE, Phase.HIDE_5_SEC}),
        //
        TRICKY(new Phase[]{ // Phase.OBSERVE_UNTIL_TARGET_OCCUPIED,
                Phase.ATTACK_UNTIL_DAMAGE_TAKEN, Phase.PLAY_DEAD,
                Phase.ATTACK_ONCE_IMMEDIATELY, Phase.HIDE_5_SEC}),
        //
        SNEAKY(new Phase[]{Phase.ACT_IDLE, Phase.SNEAK,
                Phase.ATTACK_UNTIL_DAMAGE_TAKEN,
                Phase.ATTACK_UNTIL_DAMAGE_TAKEN, Phase.HIDE_UNTIL_HEALTHY});

        public Phase[] phases;

        Mood(Phase[] phaseList) {
            phases = phaseList;
        }

        public ArrayList<Phase> getPlan() {
            ArrayList ar = new ArrayList(Arrays.asList(phases));
            ar.add(Phase.RESET);
            return ar;
        }
    }

    public Mood currentMood;

    public static enum Phase {
        RESET(0), GOTO_1(1), GOTO_2(2), GOTO_3(3), GOTO_4(4), GOTO_5(5), GOTO_6(
                6), GOTO_7(7), GOTO_8(8), GOTO_9(9), GOTO_10(10), GOTO_11(11), GOTO_12(
                12), GOTO_13(13), GOTO_14(14), GOTO_15(15), GOTO_16(16), GOTO_17(
                17), GOTO_18(18), GOTO_19(19), GOTO_20(20),
        //
        ATTACK_ONCE, ATTACK_UNTIL_DAMAGE_TAKEN, ATTACK_UNTIL_TARGET_IS_HURT,
        //
        ATTACK_UNTIL_HP_LOW, ATTACK_FOREVER, HIDE_2_SEC, HIDE_5_SEC,
        //
        HIDE_UNTIL_FOUND, HIDE_UNTIL_HEALTHY, HIDE_UNTIL_HIT, SNEAK,
        //
        FLEE_UNTIL_SAFE, OBSERVE_UNTIL_PROVOKED, OBSERVE_UNTIL_TARGET_OCCUPIED, ACT_IDLE, PLAY_DEAD, ATTACK_ONCE_IMMEDIATELY;

        public int skipTo;

        Phase() {
            this.skipTo = 0;
        }

        Phase(int stepNum) {
            this.skipTo = stepNum;
        }
    }

    protected int phaseCounter;

    static final int STATUS_NORMAL = 0;
    static final int STATUS_STUNNED = 1;
    static final int STATUS_SLEEPING = 2;
    static final int STATUS_PLAYING_DEAD = 3;

    public int model = 0;

    // contains a sequential list of phases to cycle through.
    public ArrayList<Phase> plan;
    public int planStep;
    // typically the current phase will be in sync with the plan step, but
    // occasionally (as with sneak) it can be different.
    public Phase currentPhase = Phase.RESET;

    // Movement and targeting
    protected EntityLiving targetedEntity = null;
    protected EntityLiving lostTargetedEntity = null;

    public float wingRotation;
    public float lastWingRotation;

    int holeEscapeDirection = 1; // used to deal with buggy AI when the creature
    // gets caught in a vertical column

    public ArrayList<AStarNode> path = null;
    public boolean tracking = false;

    // Working variables
    protected int radius;

    double distFleeSq = 49;
    double distPursueSq = 81;
    double agroDistanceSq = 8 * distPursueSq;

    double dX, dY, dZ, dSq, d;

    public int color;
    private boolean searching = false;
    private boolean pathProcessed = true;
    private ArrayList<AStarNode> pathBack = new ArrayList<AStarNode>();
    private AStarPathPlanner pathPlanner;

    private boolean markPath = true;
    private AxisAlignedBB hidingSpot;
    private AxisAlignedBB potentialHidingSpot;

    // get/setStatus();

    public EntityAuru(World par1World) {
        super(par1World);

        this.texture = "/mods/ifw_aurus/textures/models/Auru.png";
        this.experienceValue = 15;
        this.isImmuneToFire = true;

        this.setSize(0.9f, 1.9f);
        this.renderDistanceWeight = 4; // determines vanishing point of renders

        attackCounter = rand.nextInt(20) + 50;
        targetHealth = 0;
        phaseCounter = 0;

        this.plan = new ArrayList<Phase>();
        this.plan.add(Phase.ACT_IDLE);
        this.plan.add(Phase.ATTACK_UNTIL_DAMAGE_TAKEN);
        this.plan.add(Phase.HIDE_5_SEC);
        this.plan.add(Phase.RESET);

        pathPlanner = new AStarPathPlanner(worldObj, this);

    }

    public boolean setModel(int i) {
        // this delay ensures that the creature doesn't rapidly switch between
        // models in a buggy way

        if (path != null && i == MODEL_WALKING) {
            // I suspect this is happening because the path that gets generated
            // is only generated server-side, so the logic checks don't work on
            // the client side.
            System.out.println("Why is this happening?");
        }
        if (this.ticksSinceModelChanged > 10) {
            this.model = i;
            float h = i > 0 ? 0.8f : 1.9f;
            this.setSize(0.9f, h);
            this.ticksSinceModelChanged = 0;
            return true;
        }
        return false;
    }

    @Override
    // This method handles how the creature responds to damage, including
    // ignoring self-inflicted damage.
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        if (!this.worldObj.isRemote) {
            if (this.model == this.MODEL_SLEEPING) {
                this.setModel(MODEL_FLYING);
            }
            if (par1DamageSource.getEntity() != this) {
                if (par1DamageSource.getEntity() instanceof EntityPlayer
                        && this.getStatus() != STATUS_STUNNED) {
                    this.attackCounter = 100;
                    this.setStatus(STATUS_STUNNED);
                }
                return super.attackEntityFrom(par1DamageSource, par2);
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(31, 0);

    }

    public void setStatus(int i) {
        if (i == this.STATUS_NORMAL) {
            if (this.getStatus() == this.STATUS_STUNNED
                    || this.getStatus() == this.STATUS_PLAYING_DEAD) {
                this.setModel(MODEL_FLYING);
                this.motionY += 1.0f;
            }
        }
        this.dataWatcher.updateObject(31, i);
    }

    public int getStatus() {
        // 0: normal; 1: stunned; 2: sleeping
        int n = this.dataWatcher.getWatchableObjectInt(31);
        return n;
    }

    @Override
    protected void fall(float par1) {
        par1 = ForgeHooks.onLivingFall(this, par1);
        if (par1 <= 0) {
            return;
        }

        int var2 = MathHelper.ceiling_float_int(par1 - 3.0F);

        if (var2 > 0) {
            if (var2 > 4) {
                this.worldObj.playSoundAtEntity(this, "damage.fallbig", 1.0F,
                        1.0F);
            } else {
                this.worldObj.playSoundAtEntity(this, "damage.fallsmall", 1.0F,
                        1.0F);
            }

            // flying creatures suffer no fall damage.
            // this.attackEntityFrom(DamageSource.fall, var2);

            int var3 = this.worldObj
                    .getBlockId(
                            MathHelper.floor_double(this.posX),
                            MathHelper.floor_double(this.posY
                                    - 0.20000000298023224D - this.yOffset),
                            MathHelper.floor_double(this.posZ));

            if (var3 > 0) {
                StepSound var4 = Block.blocksList[var3].stepSound;
                this.worldObj.playSoundAtEntity(this, var4.getStepSound(),
                        var4.getVolume() * 0.5F, var4.getPitch() * 0.75F);
            }
        }
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // before all other things, test if the entity is playing dead.
        // when the entity plays dead, it cannot move and will do the death
        // animation.
        if (this.getStatus() == this.STATUS_PLAYING_DEAD) {
            if (this.deathTime < 18) {
                this.onDeathUpdate();
            } else if (this.deathTime == 18) {
                // this.onDeathUpdate();
                for (int i = 0; i < 2; ++i) {
                    double var8 = this.rand.nextGaussian() * 0.02D;
                    double var4 = this.rand.nextGaussian() * 0.02D;
                    double var6 = this.rand.nextGaussian() * 0.02D;
                    this.worldObj.spawnParticle("explode", this.posX
                            + (this.rand.nextFloat() * this.width * 2.0F)
                            - this.width, this.posY
                            + (this.rand.nextFloat() * this.height), this.posZ
                            + (this.rand.nextFloat() * this.width * 2.0F)
                            - this.width, var8, var4, var6);
                }
            }
        } else if (this.lastStatus == this.STATUS_PLAYING_DEAD) {
            this.deathTime = 0;
        }

        // wing rotation is used to determine bobbing and tail motion
        this.lastWingRotation = this.wingRotation;
        this.wingRotation += 0.3;

        this.ticksSinceModelChanged++;

        // for cuteness, the creature spawns music notes at random.
        if (this.rand.nextInt(100) < 1) {
            this.worldObj.spawnParticle("note", this.posX, this.posY
                    + this.height + 0.5, this.posZ,
                    this.rand.nextInt(24) / 24.0, 0.0, 0.0);
        }

        // when the creature is damaged, it is stunned and cannot fly or attack.
        // stun is apparent from yellow particles coming off the creature.
        if (this.getStatus() == STATUS_STUNNED) {
            for (int var6 = 0; var6 < 3; var6++) {
                double var7 = this.posX - 0.5 + this.rand.nextFloat();
                double var9 = this.posY + this.rand.nextFloat() * this.height;
                double var11 = this.posZ - 0.5 + this.rand.nextFloat();
                double var13 = 0D;
                double var15 = 0D;
                double var17 = 0D;
                int var19 = this.rand.nextInt(2) * 2 - 1;
                var13 = (this.rand.nextFloat() - 0.5D) * 0.5D;
                var15 = (this.rand.nextFloat() - 0.5D) * 0.5D;
                var17 = (this.rand.nextFloat() - 0.5D) * 0.5D;

                this.worldObj.spawnParticle("mobSpellAmbient", var7, var9,
                        var11, 0.8 + var13 / 10, 0.8 + var15, var17);
            }
            this.motionY -= 0.5f;
        }

        // TODO: this is super primitive and leads to sketchy looking
        // animations. add in a bit more diversity to the assignment, such as
        // assigning a particular model to a particular phase of the dragon's
        // AI. while engaging in ranged combat, even if it touches the ground,
        // it should remain in a flying animation.

        // HANDLE MODE/MODEL SWITCHING

        // TODO: At night, land and go to sleep.

        if (this.model == MODEL_FLYING) {
            // flying creatures shouldn't fall asleep for any reason.
            if (this.getStatus() == STATUS_SLEEPING) {
                this.setStatus(STATUS_NORMAL);
            }
            // with no target to attack or chase, if creature is on or near the
            // ground, change to walking model.
            if (path == null
                    && !this.hasTarget()
                    && ((this.onGround || (this.motionY < 0 && !this
                    .isCourseTraversable(this.posX, this.posY,
                            this.posZ, this.posX, this.posY - 0.5,
                            this.posZ)))))
                this.setModel(MODEL_WALKING);
            // creature is on the ground
        } else {
            // if there is a target, or if creature is trying to get off the
            // ground, should probably try to fly.
            if (this.targetedEntity != null
                    || !this.onGround
                    || (this.targetY != this.posY && (Math
                    .abs((this.targetX - this.posX)
                            / (this.targetY - this.posY)) < 0.04f || Math
                    .abs((this.targetZ - this.posZ)
                            / (this.targetY - this.posY)) < 0.04f))
                    || path != null) {
                this.setModel(MODEL_FLYING);
            } else {
                // randomly fall asleep while on the ground.
                if (this.getStatus() != STATUS_SLEEPING) {
                    if (!this.worldObj.isRemote) {
                        if (!this.hasTarget() && this.rand.nextInt(10000) == 0) {
                            this.setStatus(STATUS_SLEEPING);
                        }
                    }
                    // this model change will take effect for this tick
                    // then if the status changed, creature will sleep next
                    // tick.
                    this.setModel(MODEL_WALKING);
                } else {
                    // randomly wake up while sleeping.
                    if (!this.worldObj.isRemote) {
                        if (this.rand.nextInt(10000) == 0) {
                            this.setStatus(STATUS_NORMAL);
                        }
                    }
                    this.setModel(MODEL_SLEEPING);
                }
            }
        }
        this.lastStatus = this.getStatus();
    }

    protected boolean hasTarget() {
        return this.targetedEntity != null || this.lostTargetedEntity != null;
    }

    @Override
    protected void updateEntityActionState() {

        if (this.getHomePosition().compareChunkCoordinate(
                new ChunkCoordinates(0, 0, 0)) == 0) {
            this.setHomeArea(this.chunkCoordX, this.chunkCoordY,
                    this.chunkCoordZ, 3);
        }

        if ((!this.worldObj.isRemote) && (this.worldObj.difficultySetting == 0)) {
            setDead();
        }

        despawnEntity();

        if (this.model == MODEL_SLEEPING) {
            return;
        }

        // Handle targeting

        // if target is dead or if I'm bored, nullify target, reset AI
        if ((this.targetedEntity != null && this.targetedEntity.isDead)
                || (this.lostTargetedEntity != null && this.lostTargetedEntity.isDead)
                || patience < 1) {
            this.setTargetEntity(null);
            this.setStep(0);
        }

        // find a good target, regardless of whether I have one or not... but
        // the purpose of this is to retarget if there are chickens around, so
        // maybe add an if statement here to prevent wonky retargeting.
        if (this.ticksExisted % 10 == 0)
            this.findTarget();

        // if I found a target and it's different from my old target, reset my
        // patience
        if (this.targetedEntity != null
                && this.targetedEntity != this.lostTargetedEntity) {
            this.lostTargetedEntity = this.targetedEntity;
            patience = rand.nextInt(300) + 200;
        }

        // if there is a target, but it can't be seen, then I've lost it.
        if (this.targetedEntity != null
                && !canEntityBeSeen(this.targetedEntity)) {
            this.targetedEntity = null;
        }

        // if I have either a target or a lost target, do my combat phase stuff.
        if (this.hasTarget()) {

            // set up distance for phase handling

            dX = this.lostTargetedEntity.posX - this.posX;
            dY = this.lostTargetedEntity.posY - this.posY;
            dZ = this.lostTargetedEntity.posZ - this.posZ;

            // handle rotation to face target
            if (this.targetedEntity != null
                    && this.getStatus() != this.STATUS_PLAYING_DEAD) {
                this.prevRotationYaw = this.rotationYaw;
                this.rotationYaw = -(float) (Math.atan2(dX, dZ)) * 180 / 3.14f;

                this.prevRotationPitch = this.rotationPitch;
                this.rotationPitch = (float) (Math.atan2(dZ * dZ + dX * dX,
                        Math.copySign(dY * dY, dY)) - 0.5235988F) * 180 / 3.14f;
                // general formula for pitch in 3D space: atan2(hyp^2, (+/-)y^2)
                // *180/3.14

                this.rotationPitch = this.rotationPitch > 80 ? 80
                        : this.rotationPitch < -80 ? -80 : this.rotationPitch;
            }

            dSq = dX * dX + dY * dY + dZ * dZ;

            doPhase();

            this.attackCounter--;

            if (this.getStatus() == STATUS_STUNNED) {
                if (this.attackCounter <= 70) {
                    this.setStatus(STATUS_NORMAL);
                }
            }

            // if I don't have a visible target, I get impatient and look around
            // my target's location
            if (this.targetedEntity == null || path != null) {
                patience--;
                double dX1 = this.targetX - this.posX;
                double dY1 = this.targetY - this.posY;
                double dZ1 = this.targetZ - this.posZ;

                // handle rotation
                this.prevRotationYaw = this.rotationYaw;
                this.rotationYaw = -(float) (Math.atan2(dX1, dZ1)) * 180 / 3.14f;

                this.prevRotationPitch = this.rotationPitch;
                this.rotationPitch = (float) (Math.atan2(dZ1 * dZ1 + dX1 * dX1,
                        Math.copySign(dY1 * dY1, dY1)) - 0.5235988F) * 180 / 3.14f;
            }
        } else {
            // if I have no target or lost target, I am out of combat and I can
            // idle. my stun status is reset and I pick random positions.

            this.attackCounter = rand.nextInt(20) + 50;
            this.setStep(0);

            if (this.getStatus() == STATUS_STUNNED) {
                this.setStatus(STATUS_NORMAL);
            }

            // set my home as my destination
            if (!this.isWithinHomeDistance((int) (this.targetX / 16),
                    (int) (this.targetY / 16), (int) (this.targetZ / 16))) {
                this.courseChangeCooldown = this.rand.nextInt(300) + 100;
                setRandomTargetPoint(this.getHomePosition().posX * 16,
                        this.getHomePosition().posY * 16,
                        this.getHomePosition().posZ * 16, 16);
            }

            double dX1 = this.targetX - this.posX;
            double dY1 = this.targetY - this.posY;
            double dZ1 = this.targetZ - this.posZ;

            double d1Sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;

            // reduce target change interval by closeness to target.
            this.courseChangeCooldown -= (int) Math.ceil(16.0 / d1Sq);

            if (this.courseChangeCooldown <= 0 || d1Sq < 2) {
                this.courseChangeCooldown = this.rand.nextInt(300) + 100;
                setRandomTargetPoint(this.posX, this.posY, this.posZ, 16.0D);
            }

            // handle rotation
            this.prevRotationYaw = this.rotationYaw;
            this.rotationYaw = -(float) (Math.atan2(dX1, dZ1)) * 180 / 3.14f;

            this.prevRotationPitch = this.rotationPitch;
            this.rotationPitch = (float) (Math.atan2(dZ1 * dZ1 + dX1 * dX1,
                    Math.copySign(dY1 * dY1, dY1)) - 0.5235988F) * 180 / 3.14f;
            // general formula for pitch in 3D space: atan2(hyp^2, (+/-)y^2)
            // *180/3.14

            this.rotationPitch = this.rotationPitch > 80 ? 80
                    : this.rotationPitch < -80 ? -80 : this.rotationPitch;

        }

        // if I have a path, follow it, if I don't, bumble towards my target

        if (this.getStatus() != this.STATUS_PLAYING_DEAD) {
            if (path != null) {
                if (!pathProcessed) {
                    pathProcessed = true;
                    processPath();
                }
                followPath();
                // wing-based sinusoidal motion
                if (this.model == this.MODEL_FLYING) {
                    this.motionY -= MathHelper.sin(this.wingRotation) / 20;
                }
                // metronome tick
                if (Math.copySign(1, MathHelper.sin(this.wingRotation)) > Math
                        .copySign(1, MathHelper.sin(this.lastWingRotation))) {
                    this.worldObj.playSoundAtEntity(this, "random.click", 0.1f,
                            10);
                }
            } else {
                dX = this.targetX - this.posX;
                dY = this.targetY - this.posY;
                dZ = this.targetZ - this.posZ;

                dSq = dX * dX + dY * dY + dZ * dZ;

                d = Math.sqrt(dSq);

                this.motionX += dX / d * 0.05D;
                this.motionY += dY / d * 0.05D;
                this.motionZ += dZ / d * 0.05D;

                // wing-based sinusoidal motion
                if (this.model == this.MODEL_FLYING) {
                    this.motionY -= MathHelper.sin(this.wingRotation) / 20;
                }
                // metronome tick
                if (Math.copySign(1, MathHelper.sin(this.wingRotation)) > Math
                        .copySign(1, MathHelper.sin(this.lastWingRotation))) {
                    this.worldObj.playSoundAtEntity(this, "random.click", 0.1f,
                            10);
                }
            }
        } else {
            this.motionY -= 0.2f;
        }
    }

    /**
     * This method will set the next waypoint towards a target. If enough time
     * has passed since making any headway towards the target, give up.
     */

    // PHASE LOGIC
    public void setStep(int step) {
        planStep = step;
        currentPhase = plan.get(step);
        phaseCounter = 0;
    }

    public void nextStep() {
        planStep++;
        if (planStep >= plan.size())
            currentPhase = plan.get(0);
        else
            currentPhase = plan.get(planStep);

        phaseCounter = 0;
    }

    public void doPhase() {
        switch (currentPhase) {
            case ATTACK_FOREVER:
                doAttackUntil(0);
                break;
            case ATTACK_ONCE:
                doAttackUntil(1);
                break;
            case ATTACK_ONCE_IMMEDIATELY:
                doAttackUntil(5);
                break;
            case ATTACK_UNTIL_DAMAGE_TAKEN:
                doAttackUntil(3);
                break;
            case ATTACK_UNTIL_TARGET_IS_HURT:
                doAttackUntil(2);
                break;
            case ATTACK_UNTIL_HP_LOW:
                doAttackUntil(4);
                break;
            case FLEE_UNTIL_SAFE:
                doFleeFor(3);
                break;
            case HIDE_2_SEC:
                doHide(2);
                break;
            case HIDE_5_SEC:
                doHide(5);
                break;
            case HIDE_UNTIL_HEALTHY:
                doHide(-1);
                break;
            case HIDE_UNTIL_FOUND:
                doHide(-2);
                break;
            case PLAY_DEAD:
                doPlayDead();
                break;
            case ACT_IDLE:
                doActIdle();
                break;
            default:
                this.setStep(currentPhase.skipTo);
        }

    }

    public void doActIdle() {
        this.targetedEntity = null;

        double dX1 = this.targetX - this.posX;
        double dY1 = this.targetY - this.posY;
        double dZ1 = this.targetZ - this.posZ;

        double d1Sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;

        // reduce target change interval by closeness to target.
        this.courseChangeCooldown -= (int) Math.ceil(16.0 / d1Sq);

        if (this.courseChangeCooldown <= 0 || d1Sq < 2) {
            this.courseChangeCooldown = this.rand.nextInt(200) + 100;
            setRandomTargetPoint(this.lostTargetedEntity.posX,
                    this.lostTargetedEntity.posY, this.lostTargetedEntity.posZ,
                    16.0D);
        }

        // handle rotation
        this.prevRotationYaw = this.rotationYaw;
        this.rotationYaw = -(float) (Math.atan2(dX1, dZ1)) * 180 / 3.14f;

        this.prevRotationPitch = this.rotationPitch;
        this.rotationPitch = (float) (Math.atan2(dZ1 * dZ1 + dX1 * dX1,
                Math.copySign(dY1 * dY1, dY1)) - 0.5235988F) * 180 / 3.14f;
        // general formula for pitch in 3D space: atan2(hyp^2, (+/-)y^2)
        // *180/3.14

        this.rotationPitch = this.rotationPitch > 80 ? 80
                : this.rotationPitch < -80 ? -80 : this.rotationPitch;

        if (dSq < 36 || this.hurtTime > 0) {
            this.targetedEntity = this.lostTargetedEntity;
            this.nextStep();
        }

    }

    public void doPlayDead() {
        if (this.phaseCounter == 0) {
            this.setStatus(STATUS_PLAYING_DEAD);
            this.worldObj.playSoundAtEntity(this, this.getDeathSound(),
                    this.getSoundVolume(), this.getSoundPitch());
            this.hurtResistantTime = 100;
        }
        phaseCounter++;
        if (this.phaseCounter == 20) {
            for (int i = 0; i < 20; ++i) {
                double var8 = this.rand.nextGaussian() * 0.02D;
                double var4 = this.rand.nextGaussian() * 0.02D;
                double var6 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("explode",
                        this.posX + (this.rand.nextFloat() * this.width * 2.0F)
                                - this.width,
                        this.posY + (this.rand.nextFloat() * this.height),
                        this.posZ + (this.rand.nextFloat() * this.width * 2.0F)
                                - this.width, var8, var4, var6);
            }
        }

        if (this.getStatus() != this.STATUS_PLAYING_DEAD) {
            this.nextStep();
        }

        if (this.phaseCounter > 100
                || (this.canEntityBeSeen(this.lostTargetedEntity) && this
                .getDistanceSqToEntity(this.lostTargetedEntity) < 16)) {
            this.setStatus(STATUS_NORMAL);
            this.nextStep();
        }
    }

    // 0 = forever, 1 = once, 2 = until target damaged, 3 = until hurt, 4 =
    // until hp is low
    public void doAttackUntil(int until) {
        double tX = this.lostTargetedEntity.posX;
        double tY = this.lostTargetedEntity.posY;
        double tZ = this.lostTargetedEntity.posZ;

        // approach if too far
        if (dSq > distPursueSq) {
            float w1 = 0.25f;
            float w2 = 1 - w1;
            this.targetX = (this.posX * w1 + this.lostTargetedEntity.posX * w2);
            this.targetY = (this.posY * w1 + (this.lostTargetedEntity.posY + 5.0D)
                    * w2);
            this.targetZ = (this.posZ * w1 + this.lostTargetedEntity.posZ * w2);
        }

        if (!canEntityBeSeen(this.lostTargetedEntity)) {
            ArrayList<PathPoint> AL = this.getMilestonesBetween(this.posX,
                    this.posY, this.posZ, this.lostTargetedEntity.posX,
                    this.lostTargetedEntity.posY, this.lostTargetedEntity.posZ);
            if (AL.size() > 0) {
                tX = AL.get(0).xCoord;
                tY = AL.get(0).yCoord;
                tZ = AL.get(0).zCoord;
            }

            double dX1 = tX - this.posX;
            double dY1 = tY - this.posY;
            double dZ1 = tZ - this.posZ;

            dSq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
        }

        // retreat if too close
        if (dSq < distFleeSq) {
            this.targetX = (this.posX - 5.0D * (this.lostTargetedEntity.posX - this.posX));
            this.targetY = (this.lostTargetedEntity.posY + 5.0D);
            this.targetZ = (this.posZ - 5.0D * (this.lostTargetedEntity.posZ - this.posZ));
        }
        // strafe
        else {
            double dX2 = tX - this.posX;
            double dY2 = tY - this.posY;
            double dZ2 = tZ - this.posZ;
            float theta = -((float) (Math.atan2(dX2, dZ2)));

            float ticksAsRadians = (this.ticksExisted % 175) / 25.0f;

            this.targetX = tX + 5 * Math.cos(theta) * Math.sin(ticksAsRadians);
            this.targetY = this.lostTargetedEntity.posY
                    + this.lostTargetedEntity.height + 2 + rand.nextInt(3);
            this.targetZ = tZ + 5 * Math.sin(theta) * Math.sin(ticksAsRadians);
        }

        // handle shooting.

        if (this.phaseCounter == 0) {
            if (until == 5) {
                this.attackCounter = 0;
            } else {
                this.attackCounter = this.rand.nextInt(20) + 50;
            }
            this.phaseCounter++;
        }

        if (dSq < agroDistanceSq && canEntityBeSeen(this.lostTargetedEntity)) {
            if (until == 2
                    && this.lostTargetedEntity.getHealth() < this.targetHealth) {

                this.targetHealth = 0;
                this.nextStep();
                return;
            }

            if (this.hurtTime > 0) {
                if (until == 3
                        || (until == 4 && ((float) this.getHealth())
                        / this.getMaxHealth() < 0.33f)) {
                    this.nextStep();
                    return;
                }
            }

            if (this.attackCounter == 30) {
                this.worldObj.playSoundAtEntity(this,
                        "mob.fledgeling.telegraph", this.getSoundVolume(),
                        new Random().nextFloat() * 0.2f + 0.9f);
            }

            if (this.attackCounter <= 0) {
                this.launchAttack();

                if (until == 2 || until == 5)
                    this.targetHealth = this.lostTargetedEntity.getHealth();

                this.attackCounter = 50 + rand.nextInt(20);

                if (until == 1 || until == 5)
                    this.nextStep();
            }
        }
    }

    // -1 = until hp normal, -2 = until found, -3 = until hit
    public void doHide(int secs) {

        this.targetedEntity = null;

        if (this.lostTargetedEntity == null || this.lostTargetedEntity.isDead) {
            this.nextStep();
        }

        if (phaseCounter == 0 && (path == null)) {
            // flee while finding a hiding spot

            this.targetX = (this.posX - 0.1 * (this.lostTargetedEntity.posX - this.posX));
            this.targetY = (this.lostTargetedEntity.posY + 5.0D);
            this.targetZ = (this.posZ - 0.1 * (this.lostTargetedEntity.posZ - this.posZ));

            AxisAlignedBB hidingSpot = hidingSpotAround(
                    lostTargetedEntity.posX, lostTargetedEntity.posY,
                    lostTargetedEntity.posZ, 32, 64);
            if (hidingSpot != null) {
                // TODO: this is out of date now
                findPathBetween((int) posX, (int) hidingSpot.minY, (int) posZ,
                        (int) hidingSpot.minX, (int) hidingSpot.minY,
                        (int) hidingSpot.minZ);

                phaseCounter++;
            }

            // if a hiding spot can't be found in 128 attempts, give up.
            // System.out.println("Couldn't find a hiding spot.");
            // nextStep();
            // return;
        }

        // if there is a path, bleed on the way to the target.

        if (path != null) {

            double dX1 = this.path.get(0).x - this.posX;
            double dY1 = this.path.get(0).y - this.posY;
            double dZ1 = this.path.get(0).z - this.posZ;

            double d1Sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
            if (d1Sq < 100) {
                this.phaseCounter++;
            }
            if (d1Sq < 9
                    && (this.isCourseTraversable(this.posX, this.posY,
                    this.posZ, this.lostTargetedEntity.posX,
                    this.lostTargetedEntity.posY + 5,
                    this.lostTargetedEntity.posZ)
                    || this.isCourseTraversable(this.posX, this.posY,
                    this.posZ, this.lostTargetedEntity.posX,
                    this.lostTargetedEntity.posY + 3,
                    this.lostTargetedEntity.posZ) || this
                    .isCourseTraversable(this.posX, this.posY,
                            this.posZ,
                            this.lostTargetedEntity.posX,
                            this.lostTargetedEntity.posY + 1,
                            this.lostTargetedEntity.posZ))) {
                this.phaseCounter = 0;
                return;
            } else if (d1Sq > 9 && phaseCounter < 150 && secs != -1) {
                this.bleed();
            }

            if (secs == -1) {
                if ((float) this.getHealth() / this.getMaxHealth() < 0.67f) {
                    PotionEffect regen = new PotionEffect(
                            Potion.regeneration.id, 10, 0);
                    this.addPotionEffect(regen);
                    this.bleed();
                } else {
                    this.nextStep();
                    this.targetedEntity = this.lostTargetedEntity;
                    return;
                }
            }
        }

        if (path == null || (secs > 0 && this.phaseCounter > 40 * secs)) {
            this.nextStep();

            // path = pathBack; pathBack = null;

            this.targetedEntity = this.lostTargetedEntity;
        }
    }

    public void doFleeFor(int secs) {
        // retreat for some ticks
        if (phaseCounter < 40 * secs) {
            phaseCounter++;
            this.targetX = (this.posX - 5.0D * (this.targetedEntity.posX - this.posX));
            this.targetY = (this.targetedEntity.posY + 5.0D);
            this.targetZ = (this.posZ - 5.0D * (this.targetedEntity.posZ - this.posZ));
            if (phaseCounter > 20) {
                this.bleed();
            }
        } else {
            this.nextStep();
        }
    }

    public void setTargetPoint(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    public void setTargetEntity(EntityLiving entity) {
        this.targetedEntity = entity;
        this.lostTargetedEntity = entity;
    }

    @Override
    protected void dropRareDrop(int par1) {
        // TODO Auto-generated method stub
        super.dropRareDrop(par1);
    }

    protected void setRandomTargetPoint(double x, double y, double z,
                                        double range) {
        this.targetX = (x + (this.rand.nextFloat() * 2F - 1F) * range);
        this.targetY = (y + (this.rand.nextFloat() * 2F - 0.5F) * range / 4);
        this.targetZ = (z + (this.rand.nextFloat() * 2F - 1F) * range);
    }

    // this method determines if a target entity is suitable based on being
    // alive and visible.
    protected boolean isSuitableTarget(EntityLiving entity,
                                       boolean visibleRequired) {
        if (entity == null) {
            return false;
        } else if (entity == this) {
            return false;
        } else if (!entity.isEntityAlive()) {
            return false;
        } else {
            if (entity instanceof EntityPlayer
                    && ((EntityPlayer) entity).capabilities.disableDamage) {
                return false;
            }
            if (visibleRequired && !canEntityBeSeen(entity)) // !this.getEntitySenses().canSee(entity))
            {
                return false;
            } else {
                return true;
            }
        }
    }

    protected void launchAttack() {
        this.worldObj.playSoundAtEntity(this, "mob.fledgeling.shoot",
                this.getSoundVolume(), 1.0f);

        Entity entity = new EntityProjectile(this, this.lostTargetedEntity);

        this.worldObj.spawnEntityInWorld(entity);

        // System.out.println("I am launching a...");
        // System.out.println(entity);
    }

    // A Milestone is defined as a boundary between traversable space and
    // nontraversable space.
    // ie, air to solid and solid to air in a raycast.
    protected ArrayList<PathPoint> getMilestonesBetween(double xStart,
                                                        double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
        ArrayList<PathPoint> AL = new ArrayList<PathPoint>();

        boolean inObstacle = false;

        double dX = xEnd - xStart;
        double dY = yEnd - yStart;
        double dZ = zEnd - zStart;

        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        dX /= distance;
        dY /= distance;
        dZ /= distance;

        AxisAlignedBB AABB = this.boundingBox.copy();
        AABB.offset(xStart - this.posX, yStart - this.posY, zStart - this.posZ);

        for (int i = 0; i < distance; i++) {
            AABB.offset(dX, dY, dZ);

            if (this.worldObj.getCollidingBoundingBoxes(this, AABB).isEmpty() == inObstacle) {
                inObstacle = !inObstacle;
                // provides a small offset so that targeting isn't inside
                // occupied blocks.
                if (inObstacle) {
                    AL.add(new PathPoint((int) (xStart + (i - 1) * dX),
                            (int) (yStart + (i - 1) * dY),
                            (int) (zStart + (i - 1) * dZ)));
                } else {
                    AL.add(new PathPoint((int) (xStart + (i + 1) * dX),
                            (int) (yStart + (i + 1) * dY),
                            (int) (zStart + (i + 1) * dZ)));

                }
            }
        }

        return AL;
    }

    // A maximum is defined as the block with the highest max y-coordinate
    // between
    // each pair of milestones. O---( ^ )-----(^ )---X
    // Effectively returns peaks between here and there, which can usually serve
    // as path points for flying entities in the overworld.
    protected ArrayList<PathPoint> getMaximumsBetween(double xStart,
                                                      double yStart, double zStart, double xEnd, double yEnd, double zEnd) {

        ArrayList<PathPoint> allPoints = new ArrayList<PathPoint>();

        // a list of indexes of milestones in the array of allPoints.
        ArrayList<Integer> milestones = new ArrayList<Integer>();

        ArrayList<PathPoint> peaks = new ArrayList<PathPoint>();

        boolean inObstacle = false;

        double dX = xEnd - xStart;
        double dY = yEnd - yStart;
        double dZ = zEnd - zStart;

        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        dX /= distance;
        dY /= distance;
        dZ /= distance;

        AxisAlignedBB AABB = this.boundingBox.copy();
        AABB.offset(xStart - this.posX, yStart - this.posY, zStart - this.posZ);

        for (int i = 0; i < distance; i++) {
            AABB.offset(dX, dY, dZ);
            allPoints.add(new PathPoint((int) (xStart + i * dX),
                    (int) (yStart + i * dY), (int) (zStart + i * dZ)));

            if (this.worldObj.getCollidingBoundingBoxes(this, AABB).isEmpty() == inObstacle) {
                inObstacle = !inObstacle;
                milestones.add(allPoints.size() - 1);
            }
        }

        int localMaxIndex;
        int localMaxY;
        for (int i = 0; i < milestones.size(); i += 2) {
            localMaxIndex = 0;
            localMaxY = 0;
            for (int j = milestones.get(i); j < milestones.get(i + 1); j++) {
                int testY = worldObj.getTopSolidOrLiquidBlock(
                        allPoints.get(j).xCoord, allPoints.get(j).zCoord);
                if (testY > localMaxY) {
                    localMaxIndex = j;
                    localMaxY = testY;
                }
            }
            peaks.add(new PathPoint(allPoints.get(localMaxIndex).xCoord,
                    localMaxY + 5, allPoints.get(localMaxIndex).zCoord));
        }

        return peaks;
    }

    // The above is somewhat flawed however, because it will only point to the
    // highest point. We want to use the highest point as a plateau when
    // entering
    // the range of the milestones: O---^ ^ ^-----^^ ^---X
    // This will ensure that the creature can fly over all obstacles.
    protected ArrayList<PathPoint> getMaximumsAsPlateausBetween(double xStart,
                                                                double yStart, double zStart, double xEnd, double yEnd, double zEnd) {

        ArrayList<PathPoint> allPoints = new ArrayList<PathPoint>();

        // a list of indexes of milestones in the array of allPoints.
        ArrayList<Integer> milestones = new ArrayList<Integer>();

        ArrayList<PathPoint> peaks = new ArrayList<PathPoint>();

        boolean inObstacle = false;

        double dX = xEnd - xStart;
        double dY = yEnd - yStart;
        double dZ = zEnd - zStart;

        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        dX /= distance;
        dY /= distance;
        dZ /= distance;

        AxisAlignedBB AABB = this.boundingBox.copy();
        AABB.offset(xStart - this.posX, yStart - this.posY, zStart - this.posZ);

        for (int i = 0; i < distance; i++) {
            AABB.offset(dX, dY, dZ);
            allPoints.add(new PathPoint((int) (xStart + i * dX),
                    (int) (yStart + i * dY), (int) (zStart + i * dZ)));

            if (this.worldObj.getCollidingBoundingBoxes(this, AABB).isEmpty() == inObstacle) {
                inObstacle = !inObstacle;
                milestones.add(allPoints.size() - 1);
            }
        }

        int localMaxIndex;
        int localMaxY;
        for (int i = 0; i < milestones.size(); i += 2) {
            localMaxIndex = 0;
            localMaxY = 0;
            for (int j = milestones.get(i); j < milestones.get(i + 1); j++) {
                int testY = worldObj.getTopSolidOrLiquidBlock(
                        allPoints.get(j).xCoord, allPoints.get(j).zCoord);
                if (testY > localMaxY) {
                    localMaxIndex = j;
                    localMaxY = testY;
                }
            }
            PathPoint pp1 = allPoints.get(i);
            PathPoint pp2 = allPoints.get(i + 1);

            pp1 = new PathPoint(pp1.xCoord, localMaxY + 5, pp1.zCoord);
            pp2 = new PathPoint(pp2.xCoord, localMaxY + 5, pp2.zCoord);

            peaks.add(pp1);
            peaks.add(pp2);
        }

        return peaks;
    }

    protected boolean isCourseTraversable(double xStart, double yStart,
                                          double zStart, double distance) {

        double dX = (this.targetX - xStart) / distance;
        double dY = (this.targetY - yStart) / distance;
        double dZ = (this.targetZ - zStart) / distance;

        AxisAlignedBB AABB = this.boundingBox.copy();
        AABB.offset(xStart - this.posX, yStart - this.posY, zStart - this.posZ);

        for (int i = 1; i < distance; i++) {
            AABB.offset(dX, dY, dZ);

            if (!this.worldObj.getCollidingBoundingBoxes(this, AABB).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    protected boolean isCourseTraversable(double xStart, double yStart,
                                          double zStart, double xEnd, double yEnd, double zEnd) {
        double dX = xEnd - xStart;
        double dY = yEnd - yStart;
        double dZ = zEnd - zStart;

        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        dX /= distance;
        dY /= distance;
        dZ /= distance;

        AxisAlignedBB AABB = this.boundingBox.copy();
        AABB.offset(xStart - this.posX, yStart - this.posY, zStart - this.posZ);

        for (int i = 0; i < distance; i++) {
            AABB.offset(dX, dY, dZ);

            if (!this.worldObj.getCollidingBoundingBoxes(this, AABB).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    protected void bleed() {
        if (this.ticksExisted % 30 == 0
                && this.getStatus() == this.STATUS_NORMAL) {
            this.dropItem(Aurus.dragonBlood.itemID, 1);
        }
    }

    protected void findTarget() {
        if (this.targetedEntity == null) {
            // this.targetedEntity = this.worldObj.findNearestEntityWithinAABB(
            // // this is a debugging targeting method, will target player
            // // even in creative.
            // EntityPlayer.class, this.boundingBox.expand(32, 32, 32),
            // this);
            this.targetedEntity = this.worldObj // this is the real method.
                    .getClosestVulnerablePlayerToEntity(this, 100.0D);
            if (!this.isSuitableTarget(this.targetedEntity, true))
                this.targetedEntity = null;
        }

        // The following code has the dragons target the nearest visible
        // chicken, even if they are already chasing a player. Dragons hate
        // chickens.

        if (this.targetedEntity == null
                || this.targetedEntity instanceof EntityPlayer) {
            List entities = this.worldObj.getEntitiesWithinAABB(
                    EntityChicken.class, this.boundingBox.expand(32, 16, 32));
            // last argument should be IEntitySelector, not sure what for
            // reasonable targeting distance seems to be 12, 8, 12.
            Collections.sort(entities,
                    new EntityAINearestAttackableTargetSorter(null, this));
            Iterator it = entities.iterator();
            EntityLiving el = null;

            while (it.hasNext()) {
                Entity e = (Entity) it.next();
                el = (EntityLiving) e;

                if (isSuitableTarget(el, true)) {
                    this.targetedEntity = el;
                    break;
                }
            }
        }
    }

    @Override
    public void moveEntityWithHeading(float par1, float par2) {
        // avoid gravity effects while flying
        if (this.model == this.MODEL_FLYING) {
            if (this.isInWater()) {
                this.moveFlying(par1, par2, 0.02F);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.800000011920929D;
                this.motionY *= 0.800000011920929D;
                this.motionZ *= 0.800000011920929D;
            } else if (this.handleLavaMovement()) {
                this.moveFlying(par1, par2, 0.02F);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            } else {
                float var3 = 0.91F;

                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= var3;
                this.motionY *= var3;
                this.motionZ *= var3;
            }
        } else {
            super.moveEntityWithHeading(par1, par2);
        }
    }

    public ArrayList<PathPoint> nextPathPoint(ArrayList<PathPoint> nodes) {
        PathPoint as = nodes.remove(nodes.size() - 1);

        if (nodes.size() > 1) {
            PathPoint as2 = nodes.remove(nodes.size() - 1);

            // Direction dir = as.getDirectionFrom(as.parent);
            //
            // while (nodes.size() > 1 && as2.getDirectionFrom(as2.parent) ==
            // dir) {
            // as2 = nodes.remove(nodes.size() - 1);
            // }

            as = as2;
        }

        this.setTargetPoint(as.xCoord, as.yCoord, as.zCoord);

        return nodes;
    }

    @Override
    public int getTalkInterval() {
        return 160;
    }

    @Override
    protected String getLivingSound() {
        return "mob.fledgeling.howl";
    }

    @Override
    protected String getHurtSound() {
        return "mob.fledgeling.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.fledgeling.death";
    }

    @Override
    protected int getDropItemId() {
        return Aurus.dragonBlood.itemID;
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        int var3 = this.getDropItemId();

        if (var3 > 0) {
            int var4 = this.rand.nextInt(3) + 1;

            if (par2 > 0) {
                var4 += this.rand.nextInt(par2 + 1);
            }

            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(var3, this.rand.nextInt(8) + 4);
            }
        }
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    protected float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 1.3F
                : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 1.0F;
    }

    @Override
    public boolean getCanSpawnHere() {
        double newposY = this.worldObj.getHeightValue((int) posX, (int) posZ) + 2;
        this.setPosition(posX, posY, posZ);
        // TODO: this should be 256 for proper release, but for testing it will be 128 for higher occurrence of aurus in the world.
        Entity nearestAuru = worldObj.findNearestEntityWithinAABB(EntityAuru.class, this.boundingBox.expand(128, 128, 128), this);
        //List list = worldObj.getEntitiesWithinAABB(EntityAuru.class, this.boundingBox.expand(128, 128, 128));
        return super.getCanSpawnHere() && /*rand.nextInt(100) <= 2*/ nearestAuru == null && posY >= 50 && posY >= this.worldObj.getHeightValue((int) posX, (int) posZ) - 8;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 2;
    }

    @Override
    protected boolean canDespawn() {
        return false;//super.canDespawn();
    }

    public boolean isRayOpen(double x1, double y1, double z1, double x2,
                             double y2, double z2) {
        MovingObjectPosition mop = this.worldObj.rayTraceBlocks(this.worldObj
                .getWorldVec3Pool().getVecFromPool(x1, y1, z1), this.worldObj
                .getWorldVec3Pool().getVecFromPool(x2, y2, z2));
        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
            int id = worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
            return Block.blocksList[id].getBlocksMovement(worldObj, mop.blockX, mop.blockY, mop.blockZ);
        }

        return mop == null;
    }

    private void findPathToHidingSpotFrom(EntityPlayer p) {
        potentialHidingSpot = hidingSpotAround(p.posX, p.posY + 2, p.posZ, 16,
                32);

        if (potentialHidingSpot != null) {

            double currX = (this.boundingBox.minX + this.boundingBox.maxX) / 2;
            double currY = (this.boundingBox.minY) + 0.01d;// + this.boundingBox.maxY) / 2;
            double currZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2;

            findPathBetween((int) Math.floor(currX),
                    (int) Math.floor(currY), (int) Math.floor(currZ),
                    (int) Math.floor(potentialHidingSpot.minX),
                    (int) Math.floor(potentialHidingSpot.minY),
                    (int) Math.floor(potentialHidingSpot.minZ));
        }
    }

    private AxisAlignedBB hidingSpotAround(double xStart, double yStart,
                                           double zStart, int minRange, int maxRange) {
        AxisAlignedBB aabb = this.boundingBox.copy();
        aabb = aabb.expand(1, 1, 1);

        // ensures hiding spots are not preferentially in any one direction
        int attempts = 52 + worldObj.rand.nextInt(13);

        boolean found = false;

        double x, y, z, xOff, yOff, zOff;

        float angle, radius;

        while (attempts > 0 && !found) {
            attempts--;

            // 2.39996 is the golden angle in radians. using it as the
            // multiplier in this method ensures that randomish directions are
            // chosen without missing any angles.
            angle = 2.39996f * attempts;
            radius = minRange
                    + worldObj.rand.nextInt(2 * (maxRange - minRange))
                    - (maxRange - minRange);

            xOff = radius * Math.cos(angle);
            yOff = (worldObj.rand.nextInt(2 * 6) - 6);
            zOff = radius * Math.sin(angle);

            x = xStart + xOff;
            y = yStart + yOff;
            z = zStart + zOff;

            aabb.offset(x - aabb.minX, y - aabb.minY, z - aabb.minZ);

            if (worldObj.getCollidingBlockBounds(aabb).isEmpty()) {
                if (!isRayOpen(xStart, yStart, zStart, aabb.minX,
                        (aabb.minY + aabb.maxY) / 2, aabb.minZ)
                        && !isRayOpen(xStart, yStart, zStart, aabb.maxX,
                        (aabb.minY + aabb.maxY) / 2, aabb.minZ)
                        && !isRayOpen(xStart, yStart, zStart, aabb.maxX,
                        (aabb.minY + aabb.maxY) / 2, aabb.maxZ)
                        && !isRayOpen(xStart, yStart, zStart, aabb.minX,
                        (aabb.minY + aabb.maxY) / 2, aabb.maxZ)) {
                    return aabb;
                }
            }
        }
        return null;
    }

    private int lastTraversablePathIndex() {
        int indexMinusOne = path.size() - 1;

        AStarNode traversableStepMinusOne;

        double offX = 0.5;
        double offY = 0.5;
        double offZ = 0.5;

        do {
            traversableStepMinusOne = path.get(indexMinusOne);
            indexMinusOne--;
        } while (this.isCourseTraversable(this.boundingBox.minX,
                this.boundingBox.minY, this.boundingBox.minZ,
                traversableStepMinusOne.x + offX, traversableStepMinusOne.y
                + offY, traversableStepMinusOne.z + offZ)
                && indexMinusOne >= 0);

        return indexMinusOne + 1;
    }

    private void breadCrumbs() {
        markPath = false;
        if (markPath && worldObj.rand.nextInt(50) < path.size()) {
            AStarNode step = path.get(path.size() - 1);
            if (worldObj.isAirBlock(step.x, step.y, step.z))
                worldObj.setBlock(step.x, step.y, step.z,
                        Aurus.pathMarker.blockID);
            else if (worldObj.isAirBlock(step.x, step.y + 1, step.z))
                worldObj.setBlock(step.x, step.y + 1, step.z,
                        Aurus.pathMarker.blockID);
        }
    }

    private void processPath() {

        // for (AStarNode step : path) {
        // if (worldObj.isAirBlock(step.x, step.y, step.z))
        // worldObj.setBlock(step.x, step.y, step.z,
        // Aurus.pathMarker.blockID);
        // else if (worldObj.isAirBlock(step.x, step.y + 1, step.z))
        // worldObj.setBlock(step.x, step.y + 1, step.z,
        // Aurus.pathMarker.blockID);
        // }

        if (path.size() > 1) {
            int toBeRemoved = (path.size() - 1) - lastTraversablePathIndex()
                    - 1;
            for (int i = 0; i < toBeRemoved - 1; i++) {
                breadCrumbs();
                path.remove(path.size() - 1);
            }
        }
    }

    public void followPath() {
        if (!path.isEmpty()) {
            AStarNode step = path.get(path.size() - 1);

            double currX = (this.boundingBox.minX + this.boundingBox.maxX) / 2;
            double currY = this.boundingBox.minY + 0.5;
            double currZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2;

            this.targetX = step.x + 0.5;
            this.targetY = step.y + 0.5;
            this.targetZ = step.z + 0.5;

            double dX1 = this.targetX - currX;
            double dY1 = this.targetY - currY;
            double dZ1 = this.targetZ - currZ;

            double d1sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;

            if (d1sq > 0.50D) {
                double d1 = MathHelper.sqrt_double(d1sq);
                this.motionX += dX1 / d1 * 0.095D;
                this.motionY += dY1 / d1 * 0.095D;
                this.motionZ += dZ1 / d1 * 0.095D;
            } else {

                // pathMarking.
                breadCrumbs();

                // brake at key points to avoid drifting off path too far.
                this.motionX *= 0.2D;
                this.motionY *= 0.2D;
                this.motionZ *= 0.2D;

                pathBack.add(path.remove(path.size() - 1));
                if (path.size() <= 0) {
                    AStarNode as = pathBack.get(pathBack.size() - 1);

                    // at the end.
                    onEndOfPathReached();

                    path = null;
                    return;
                }

                if (path.size() > 1) {
                    int toBeRemoved = (path.size() - 1)
                            - lastTraversablePathIndex() - 1;
                    for (int i = 0; i < toBeRemoved; i++) {
                        breadCrumbs();
                        path.remove(path.size() - 1);
                    }
                }
            }
        }
    }

    public void findPathTo(int x, int y, int z) {
        findPathBetween((int) posX, (int) posY, (int) this.posZ, x, y, z);
    }

    public void findPathBetween(int x1, int y1, int z1, int x2, int y2, int z2) {
        if (!searching) {
            pathBack = new ArrayList<AStarNode>();

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 2, 1);

            pathPlanner.getPath(x1, y1, z1, x2, y2, z2, aabb);// boundingBox);

            searching = true;
            System.out.println("Finding path.");
        }
    }

    @Override
    public void onFoundPath(ArrayList<AStarNode> result) {
        searching = false;

        path = result;
        hidingSpot = potentialHidingSpot;

        potentialHidingSpot = null;
        pathProcessed = false;

        System.out.println("Found path.");
    }

    @Override
    public void onNoPathAvailable() {
        searching = false;
        path = null;

        System.out.println("Couldn't find path.");

    }

    public void onEndOfPathReached() {
        path = null;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!worldObj.isRemote) {
            System.out.println("Au ru. =F");

            findPathToHidingSpotFrom(player);
        } else {
            double var13 = -10.0D;
            double var15 = 2.0D;
            double var17 = 1.0D;

            worldObj.spawnParticle("reddust", this.boundingBox.minX
                    + this.width / 2, this.boundingBox.minY + this.height / 2,
                    this.boundingBox.minZ + this.width / 2, var13, var15, var17);
        }

        return super.interact(player);
    }

}