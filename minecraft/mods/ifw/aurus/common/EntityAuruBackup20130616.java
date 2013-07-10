//package mods.ifw.aurus.common;
//
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Random;
//
//import mods.ifw.aurus.common.pathfinding.AStarNode;
//import mods.ifw.aurus.common.pathfinding.AStarPath;
//import mods.ifw.aurus.common.pathfinding.JPPathFinder;
//import mods.ifw.aurus.common.pathfinding.AStarNode.Direction;
//import net.minecraft.block.Block;
//import net.minecraft.block.StepSound;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLiving;
//import net.minecraft.entity.ai.EntityAINearestAttackableTargetSorter;
//import net.minecraft.entity.monster.IMob;
//import net.minecraft.entity.passive.EntityChicken;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.potion.Potion;
//import net.minecraft.potion.PotionEffect;
//import net.minecraft.util.AxisAlignedBB;
//import net.minecraft.util.ChunkCoordinates;
//import net.minecraft.util.DamageSource;
//import net.minecraft.util.MathHelper;
//import net.minecraft.world.World;
//import net.minecraftforge.common.ForgeHooks;
//
//public class EntityAuruBackup20130616 extends EntityLiving implements IMob {
//	public int courseChangeCooldown = 0;
//
//	public double waypointX;
//	public double waypointY;
//	public double waypointZ;
//
//	public double targetX;
//	public double targetY;
//	public double targetZ;
//
//	public double closestApproach;
//	public int ticksSinceApproach;
//
//	public float wingRotation;
//	public float lastWingRotation;
//
//	protected EntityLiving targetedEntity = null;
//	protected EntityLiving lostTargetedEntity = null;
//	protected int targetHealth;
//
//	protected int radius;
//
//	double distFleeSq = 49;
//	double distPursueSq = 81;
//	double attackDistanceSq = 8 * distPursueSq;
//
//	double dX, dY, dZ, dSq, d;
//
//	protected int attackCounter;
//	protected int patience = 400;
//	protected int ticksSinceModelChanged = 0;
//
//	public int color;
//
//	int holeEscapeDirection = 1;
//
//	public ArrayList<AStarNode> path = new ArrayList<AStarNode>();
//	public boolean tracking = false;
//
//	static final int MODEL_FLYING = 0;
//	static final int MODEL_WALKING = 1;
//	static final int MODEL_SLEEPING = 2;
//	static final int MODEL_LEERING = 3;
//
//	public int lastStatus;
//
//	public static enum Mood {
//		//
//		VICIOUS(new Phase[] { Phase.ATTACK_FOREVER }),
//		//
//		ANXIOUS(
//				new Phase[] { Phase.HIDE_2_SEC, Phase.SNEAK, Phase.ATTACK_ONCE }),
//		//
//		CAUTIOUS(new Phase[] { Phase.OBSERVE_UNTIL_PROVOKED,
//				Phase.ATTACK_UNTIL_HP_LOW, Phase.HIDE_UNTIL_FOUND }),
//		//
//		COLD(new Phase[] { Phase.ATTACK_ONCE, Phase.ATTACK_UNTIL_DAMAGE_TAKEN,
//				Phase.HIDE_5_SEC }),
//		//
//		GUERILLA(new Phase[] { Phase.ATTACK_ONCE,
//				Phase.ATTACK_UNTIL_TARGET_IS_HURT, Phase.HIDE_2_SEC }),
//		//
//		PLAYFUL(new Phase[] { Phase.ATTACK_ONCE, Phase.HIDE_5_SEC }),
//		//
//		TRICKY(new Phase[] { // Phase.OBSERVE_UNTIL_TARGET_OCCUPIED,
//				Phase.ATTACK_UNTIL_DAMAGE_TAKEN, Phase.PLAY_DEAD,
//						Phase.ATTACK_ONCE_IMMEDIATELY, Phase.HIDE_5_SEC }),
//		//
//		SNEAKY(new Phase[] { Phase.ACT_IDLE, Phase.SNEAK,
//				Phase.ATTACK_UNTIL_DAMAGE_TAKEN,
//				Phase.ATTACK_UNTIL_DAMAGE_TAKEN, Phase.HIDE_UNTIL_HEALTHY });
//
//		public Phase[] phases;
//
//		Mood(Phase[] phaseList) {
//			phases = phaseList;
//		}
//
//		public ArrayList<Phase> getPlan() {
//			ArrayList ar = new ArrayList(Arrays.asList(phases));
//			ar.add(Phase.RESET);
//			return ar;
//		}
//	}
//
//	public Mood currentMood;
//
//	public static enum Phase {
//		RESET(0), GOTO_1(1), GOTO_2(2), GOTO_3(3), GOTO_4(4), GOTO_5(5), GOTO_6(
//				6), GOTO_7(7), GOTO_8(8), GOTO_9(9), GOTO_10(10), GOTO_11(11), GOTO_12(
//				12), GOTO_13(13), GOTO_14(14), GOTO_15(15), GOTO_16(16), GOTO_17(
//				17), GOTO_18(18), GOTO_19(19), GOTO_20(20),
//		//
//		ATTACK_ONCE, ATTACK_UNTIL_DAMAGE_TAKEN, ATTACK_UNTIL_TARGET_IS_HURT,
//		//
//		ATTACK_UNTIL_HP_LOW, ATTACK_FOREVER, HIDE_2_SEC, HIDE_5_SEC,
//		//
//		HIDE_UNTIL_FOUND, HIDE_UNTIL_HEALTHY, HIDE_UNTIL_HIT, SNEAK,
//		//
//		FLEE_UNTIL_SAFE, OBSERVE_UNTIL_PROVOKED, OBSERVE_UNTIL_TARGET_OCCUPIED, ACT_IDLE, PLAY_DEAD, ATTACK_ONCE_IMMEDIATELY;
//
//		public int skipTo;
//
//		Phase() {
//			this.skipTo = 0;
//		}
//
//		Phase(int stepNum) {
//			this.skipTo = stepNum;
//		}
//	}
//
//	protected int phaseCounter;
//
//	// PHASES. (POSSIBLE: a negative phase number indicates the step of the plan
//	// to move forward to. eg. (8, 1, 5, -1) will take the AI back to attacking
//	// rather than sneaking.
//	// static final int PHASE_RESET = 0;
//	// // if the phase is 0, go back to the start of the plan. used for
//	// // choose-your-own-adventure style behavior branching.
//	// static final int PHASE_ATTACK_ONCE = 1;
//	// static final int PHASE_ATTACK_UNTIL_HIT = 2;
//	// static final int PHASE_ATTACK_UNTIL_HP_LOW = 3;
//	// static final int PHASE_ATTACK_FOREVER = 4;
//	// static final int PHASE_HIDE_5_SEC = 5;
//	// static final int PHASE_HIDE_UNTIL_FOUND = 6;
//	// static final int PHASE_HIDE_UNTIL_HIT = 7;
//	// // used for hide-and-seek tag behaviour.
//	// static final int PHASE_SNEAK = 8;
//	// // sneak has special conditions. it will change the phase depending on
//	// the
//	// // outcome. A successful sneak means getting in range, and will advance
//	// to
//	// // the next step. a failed sneak means being seen or hit, and will change
//	// // the phase to hide_5 while going backwards a step so that it can try to
//	// // sneak again after hiding.
//	// static final int PHASE_FLEE_UNTIL_SAFE = 9;
//	// static final int PHASE_OBSERVE = 10;
//
//	static final int STATUS_NORMAL = 0;
//	static final int STATUS_STUNNED = 1;
//	static final int STATUS_SLEEPING = 2;
//	static final int STATUS_PLAYING_DEAD = 3;
//
//	public int model = 0;
//
//	// contains a sequential list of phases to cycle through.
//	public ArrayList<Phase> plan;
//	public int planStep;
//	// typically the current phase will be in sync with the plan step, but
//	// occasionally (as with sneak) it can be different.
//	public Phase currentPhase = Phase.RESET;
//
//	// get/setStatus();
//
//	public EntityAuruBackup20130616(World par1World) {
//		super(par1World);
//		this.texture = "/mods/ifw_aurus/textures/models/Auru.png";
//		this.experienceValue = 15;
//		this.isImmuneToFire = true;
//
//		this.setSize(1.0f, 2.00f); // size determines vanishing point of renders
//
//		attackCounter = rand.nextInt(20) + 50;
//		targetHealth = 0;
//		phaseCounter = 0;
//
//		this.plan = new ArrayList<Phase>();
//		this.plan.add(Phase.ACT_IDLE);
//		this.plan.add(Phase.ATTACK_UNTIL_DAMAGE_TAKEN);
//		this.plan.add(Phase.HIDE_5_SEC);
//		this.plan.add(Phase.RESET);
//
//	}
//
//	public boolean setModel(int i) {
//		if (this.ticksSinceModelChanged > 10) {
//			this.model = i;
//			float h = i > 0 ? 0.8f : 2.0f;
//			this.setSize(1.0f, h);
//			this.ticksSinceModelChanged = 0;
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
//		if (!this.worldObj.isRemote) {
//			if (this.model == this.MODEL_SLEEPING) {
//				this.setModel(MODEL_FLYING);
//			}
//			if (par1DamageSource.getEntity() != this) {
//				if (par1DamageSource.getEntity() instanceof EntityPlayer
//						&& this.getStatus() != STATUS_STUNNED) {
//					this.attackCounter = 100;
//					this.setStatus(STATUS_STUNNED);
//				}
//				return super.attackEntityFrom(par1DamageSource, par2);
//			} else {
//				return false;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	protected void entityInit() {
//		super.entityInit();
//		this.dataWatcher.addObject(31, 0);
//
//	}
//
//	public void setStatus(int i) {
//		if (i == this.STATUS_NORMAL) {
//			if (this.getStatus() == this.STATUS_STUNNED
//					|| this.getStatus() == this.STATUS_PLAYING_DEAD) {
//				this.setModel(MODEL_FLYING);
//				this.motionY += 1.0f;
//			}
//		}
//		this.dataWatcher.updateObject(31, i);
//	}
//
//	public int getStatus() {
//		// 0: normal; 1: stunned; 2: sleeping
//		int n = this.dataWatcher.getWatchableObjectInt(31);
//		return n;
//	}
//
//	@Override
//	protected void fall(float par1) {
//		par1 = ForgeHooks.onLivingFall(this, par1);
//		if (par1 <= 0) {
//			return;
//		}
//
//		int var2 = MathHelper.ceiling_float_int(par1 - 3.0F);
//
//		if (var2 > 0) {
//			if (var2 > 4) {
//				this.worldObj.playSoundAtEntity(this, "damage.fallbig", 1.0F,
//						1.0F);
//			} else {
//				this.worldObj.playSoundAtEntity(this, "damage.fallsmall", 1.0F,
//						1.0F);
//			}
//
//			// this.attackEntityFrom(DamageSource.fall, var2);
//			int var3 = this.worldObj
//					.getBlockId(
//							MathHelper.floor_double(this.posX),
//							MathHelper.floor_double(this.posY
//									- 0.20000000298023224D - this.yOffset),
//							MathHelper.floor_double(this.posZ));
//
//			if (var3 > 0) {
//				StepSound var4 = Block.blocksList[var3].stepSound;
//				this.worldObj.playSoundAtEntity(this, var4.getStepSound(),
//						var4.getVolume() * 0.5F, var4.getPitch() * 0.75F);
//			}
//		}
//	}
//
//	@Override
//	public int getMaxHealth() {
//		return 20;
//	}
//
//	@Override
//	public void onUpdate() {
//		super.onUpdate();
//
//		if (this.getStatus() == this.STATUS_PLAYING_DEAD) {
//			if (this.deathTime < 18) {
//				this.onDeathUpdate();
//			} else if (this.deathTime == 18) {
//				// this.onDeathUpdate();
//				for (int i = 0; i < 2; ++i) {
//					double var8 = this.rand.nextGaussian() * 0.02D;
//					double var4 = this.rand.nextGaussian() * 0.02D;
//					double var6 = this.rand.nextGaussian() * 0.02D;
//					this.worldObj.spawnParticle("explode", this.posX
//							+ (this.rand.nextFloat() * this.width * 2.0F)
//							- this.width, this.posY
//							+ (this.rand.nextFloat() * this.height), this.posZ
//							+ (this.rand.nextFloat() * this.width * 2.0F)
//							- this.width, var8, var4, var6);
//				}
//			}
//		} else if (this.lastStatus == this.STATUS_PLAYING_DEAD) {
//			this.deathTime = 0;
//		}
//
//		this.lastWingRotation = this.wingRotation;
//		this.wingRotation += 0.3;
//
//		this.ticksSinceModelChanged++;
//
//		if (this.rand.nextInt(100) < 2) {
//			this.worldObj.spawnParticle("note", this.posX, this.posY
//					+ this.height + 0.5, this.posZ,
//					this.rand.nextInt(24) / 24.0, 0.0, 0.0);
//		}
//
//		if (this.getStatus() == STATUS_STUNNED) {
//			for (int var6 = 0; var6 < 3; var6++) {
//				double var7 = this.posX - 0.5 + this.rand.nextFloat();
//				double var9 = this.posY + this.rand.nextFloat() * this.height;
//				double var11 = this.posZ - 0.5 + this.rand.nextFloat();
//				double var13 = 0D;
//				double var15 = 0D;
//				double var17 = 0D;
//				int var19 = this.rand.nextInt(2) * 2 - 1;
//				var13 = (this.rand.nextFloat() - 0.5D) * 0.5D;
//				var15 = (this.rand.nextFloat() - 0.5D) * 0.5D;
//				var17 = (this.rand.nextFloat() - 0.5D) * 0.5D;
//
//				this.worldObj.spawnParticle("mobSpellAmbient", var7, var9,
//						var11, 0.8 + var13 / 10, 0.8 + var15, var17);
//			}
//			this.motionY -= 0.5f;
//		}
//
//		// TODO: this is super primitive and leads to sketchy looking
//		// animations. add in a bit more diversity to the assignment, such as
//		// assigning a particular model to a particular phase of the dragon's
//		// AI. while engaging in ranged combat, even if it touches the ground,
//		// it should remain in a flying animation.
//
//		// HANDLE MODE/MODEL SWITCHING
//
//		if (this.model == MODEL_FLYING) {
//			if (this.getStatus() == STATUS_SLEEPING) {
//				this.setStatus(STATUS_NORMAL);
//			}
//			if (!this.hasTarget()
//					&& this.onGround
//					|| (this.motionY <= -0.1f && !this.isCourseTraversable(
//							this.posX, this.posY, this.posZ, this.posX,
//							this.posY - 1, this.posZ)))
//				this.setModel(MODEL_WALKING);
//		} else {
//			if (this.targetedEntity != null// this isn't ideal for multiple
//					// modes of combat
//					// but it works for now
//					|| !this.onGround
//					|| (this.waypointY != this.posY && Math
//							.abs((this.waypointX - this.posX)
//									/ (this.waypointY - this.posY)) < 0.02f)) {
//				this.setModel(MODEL_FLYING);
//			} else {
//				if (this.getStatus() != STATUS_SLEEPING) {
//					if (!this.worldObj.isRemote) {
//						if (!this.hasTarget() && this.rand.nextInt(1000) == 0) {
//							this.setStatus(STATUS_SLEEPING);
//						}
//					}
//					this.setModel(MODEL_WALKING);
//				} else {
//					if (!this.worldObj.isRemote) {
//						if (this.rand.nextInt(10000) == 0) {
//							this.setStatus(STATUS_NORMAL);
//						}
//					}
//					this.setModel(MODEL_SLEEPING);
//				}
//			}
//		}
//		this.lastStatus = this.getStatus();
//	}
//
//	protected boolean hasTarget() {
//		return this.targetedEntity != null || this.lostTargetedEntity != null;
//	}
//
//	@Override
//	protected void updateEntityActionState() {
//
//		if (this.getHomePosition().compareChunkCoordinate(
//				new ChunkCoordinates(0, 0, 0)) == 0) {
//			this.setHomeArea(this.chunkCoordX, this.chunkCoordY,
//					this.chunkCoordZ, 3);
//		}
//
//		if ((!this.worldObj.isRemote) && (this.worldObj.difficultySetting == 0)) {
//			setDead();
//		}
//
//		despawnEntity();
//
//		if (this.model == MODEL_SLEEPING) {
//			return;
//		}
//
//		// Handle targeting
//
//		// if target is dead or if I'm bored, nullify target
//		if ((this.targetedEntity != null && this.targetedEntity.isDead)
//				|| (this.lostTargetedEntity != null && this.lostTargetedEntity.isDead)
//				|| patience < 1) {
//			this.setTargetEntity(null);
//			this.setStep(0);
//		}
//
//		// find a good target, regardless of whether I have one or not... but
//		// the purpose of this is to retarget if there are chickens around, so
//		// maybe add an if statement here to prevent wonky retargeting.
//		this.findTarget();
//
//		// if I found a target and it's different from my old target, reset my
//		// patience
//		if (this.targetedEntity != null
//				&& this.targetedEntity != this.lostTargetedEntity) {
//			this.lostTargetedEntity = this.targetedEntity;
//			patience = rand.nextInt(300) + 200;
//		}
//
//		// if there is a target, but it can't be seen, then I've lost it.
//		if (this.targetedEntity != null
//				&& !canEntityBeSeen(this.targetedEntity)) {
//			this.targetedEntity = null;
//		}
//
//		// if I have either a target or a lost target, do my combat phase stuff.
//		if (this.hasTarget()) {
//
//			// set up distance for phase handling
//
//			dX = this.lostTargetedEntity.posX - this.posX;
//			dY = this.lostTargetedEntity.posY - this.posY;
//			dZ = this.lostTargetedEntity.posZ - this.posZ;
//
//			// handle rotation to face target
//			if (this.targetedEntity != null
//					&& this.getStatus() != this.STATUS_PLAYING_DEAD) {
//				this.prevRotationYaw = this.rotationYaw;
//				this.rotationYaw = -(float) (Math.atan2(dX, dZ)) * 180 / 3.14f;
//
//				this.prevRotationPitch = this.rotationPitch;
//				this.rotationPitch = (float) (Math.atan2(dZ * dZ + dX * dX,
//						Math.copySign(dY * dY, dY)) - 0.5235988F) * 180 / 3.14f;
//				// general formula for pitch in 3D space: atan2(hyp^2, (+/-)y^2)
//				// *180/3.14
//
//				this.rotationPitch = this.rotationPitch > 80 ? 80
//						: this.rotationPitch < -80 ? -80 : this.rotationPitch;
//			}
//
//			dSq = dX * dX + dY * dY + dZ * dZ;
//
//			doPhase();
//
//			this.attackCounter--;
//
//			if (this.getStatus() == STATUS_STUNNED) {
//				if (this.attackCounter <= 70) {
//					this.setStatus(STATUS_NORMAL);
//				}
//			}
//
//			// if I don't have a visible target, I get impatient and look around
//			// my target's location
//			if (this.targetedEntity == null) {
//				patience--;
//				double dX1 = this.targetX - this.posX;
//				double dY1 = this.targetY - this.posY;
//				double dZ1 = this.targetZ - this.posZ;
//
//				// handle rotation
//				this.prevRotationYaw = this.rotationYaw;
//				this.rotationYaw = -(float) (Math.atan2(dX1, dZ1)) * 180 / 3.14f;
//
//				this.prevRotationPitch = this.rotationPitch;
//				this.rotationPitch = (float) (Math.atan2(dZ1 * dZ1 + dX1 * dX1,
//						Math.copySign(dY1 * dY1, dY1)) - 0.5235988F) * 180 / 3.14f;
//			}
//		} else {
//			// if I have no target or lost target, I am out of combat and I can
//			// idle. my stun status is reset and I pick random positions.
//
//			this.attackCounter = rand.nextInt(20) + 50;
//			this.setStep(0);
//
//			if (this.getStatus() == STATUS_STUNNED) {
//				this.setStatus(STATUS_NORMAL);
//			}
//
//			// set my home as my destination
//			if (!this.isWithinHomeDistance((int) (this.targetX / 16),
//					(int) (this.targetY / 16), (int) (this.targetZ / 16))) {
//				setRandomTargetPoint(this.getHomePosition().posX * 16,
//						this.getHomePosition().posY * 16,
//						this.getHomePosition().posZ * 16, 16);
//			}
//
//			double dX1 = this.targetX - this.posX;
//			double dY1 = this.targetY - this.posY;
//			double dZ1 = this.targetZ - this.posZ;
//
//			double d1Sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
//
//			// reduce target change interval by closeness to target.
//			this.courseChangeCooldown -= (int) Math.ceil(16.0 / d1Sq);
//
//			if (this.courseChangeCooldown <= 0 || d1Sq < 2) {
//				this.courseChangeCooldown = this.rand.nextInt(300) + 100;
//				setRandomTargetPoint(this.posX, this.posY, this.posZ, 16.0D);
//			}
//
//			// handle rotation
//			this.prevRotationYaw = this.rotationYaw;
//			this.rotationYaw = -(float) (Math.atan2(dX1, dZ1)) * 180 / 3.14f;
//
//			this.prevRotationPitch = this.rotationPitch;
//			this.rotationPitch = (float) (Math.atan2(dZ1 * dZ1 + dX1 * dX1,
//					Math.copySign(dY1 * dY1, dY1)) - 0.5235988F) * 180 / 3.14f;
//			// general formula for pitch in 3D space: atan2(hyp^2, (+/-)y^2)
//			// *180/3.14
//
//			this.rotationPitch = this.rotationPitch > 80 ? 80
//					: this.rotationPitch < -80 ? -80 : this.rotationPitch;
//
//		}
//
//		// if I have a path, follow it, if I don't, bumble towards my target
//
//		tracking = followPath();
//
//		if (!tracking) {
//			approachTarget();
//		}
//
//		if (this.getStatus() != this.STATUS_PLAYING_DEAD) {
//
//			dX = this.waypointX - this.posX;
//			dY = this.waypointY - this.posY;
//			dZ = this.waypointZ - this.posZ;
//
//			dSq = dX * dX + dY * dY + dZ * dZ;
//
//			d = Math.sqrt(dSq);
//
//			this.motionX += dX / d * 0.05D;
//			this.motionY += dY / d * 0.05D;
//			this.motionZ += dZ / d * 0.05D;
//
//			// wing-based sinusoidal motion
//			if (this.model == this.MODEL_FLYING) {
//				this.motionY -= MathHelper.sin(this.wingRotation) / 20;
//			}
//			// metronome tick
//			if (Math.copySign(1, MathHelper.sin(this.wingRotation)) > Math
//					.copySign(1, MathHelper.sin(this.lastWingRotation))) {
//				this.worldObj.playSoundAtEntity(this, "random.click", 1, 10);
//			}
//		} else {
//			this.motionY -= 0.2f;
//		}
//	}
//
//	/**
//	 * This method will set the next waypoint towards a target. If enough time
//	 * has passed since making any headway towards the target, give up.
//	 */
//	public void approachTarget() {
//		// ticksSinceApproach++;
//		//
//		// double dX1 = this.targetX - this.posX;
//		// double dY1 = this.targetY - this.posY;
//		// double dZ1 = this.targetZ - this.posZ;
//		//
//		// double d1Sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
//		//
//		// if (d1Sq < closestApproach) {
//		// closestApproach = d1Sq;
//		// ticksSinceApproach = 0;
//		// }
//		//
//		// if (ticksSinceApproach > 160 && d1Sq > 16) {
//		// this.setRandomTargetPoint(this.posX, this.posY, this.posZ, 16);
//		// this.closestApproach = 99999;
//		// this.ticksSinceApproach = 0;
//		// }
//
//		// regardless of the existence of a target or not, add to motion
//		// values based on waypoint
//
//		if (this.isCourseTraversable(this.posX, this.posY, this.posZ,
//				this.targetX, this.targetY, this.targetZ)) {
//			// if we can get to the target, then do it.
//			this.setWaypoint(this.targetX, this.targetY, this.targetZ);
//		} else {
//			double dX2 = this.waypointX - this.posX;
//			double dY2 = this.waypointY - this.posY;
//			double dZ2 = this.waypointZ - this.posZ;
//
//			double d2Sq = dX2 * dX2 + dY2 * dY2 + dZ2 * dZ2;
//
//			// if we're close to the last set waypoint, which may have been set
//			// randomly, set a course directly for our target.
//			if (d2Sq < 2) {
//				this.setWaypoint(this.posX + (this.targetX - this.posX) * 0.5d,
//						this.posX + (this.targetX - this.posX) * 0.5d,
//						this.posX + (this.targetX - this.posX) * 0.5d);
//			}
//
//			// hopefully this route works, but if it doesn't, pick new routes
//			// that are sequentially closer to our current position the more we
//			// fail, up to 16 failures.
//			int attempts = 16;
//			while (!this.isCourseTraversable(this.posX, this.posY, this.posZ,
//					this.waypointX, this.waypointY, this.waypointZ)
//					&& attempts < 16) {
//				attempts--;
//				setRandomWaypoint(
//						this.posX + (this.targetX - this.posX) * 0.5d,
//						this.posX + (this.targetX - this.posX) * 0.5d,
//						this.posX + (this.targetX - this.posX) * 0.5d,
//						1 + attempts);
//			}
//			attempts = 0;
//			while (!this.isCourseTraversable(this.posX, this.posY, this.posZ,
//					this.waypointX, this.waypointY, this.waypointZ)
//					&& attempts < 16) {
//				attempts++;
//				setRandomWaypoint(this.posX, this.posY, this.posZ,
//						17 - attempts);
//			}
//
//			// if after 16 tries we can't find a clear path, then we're
//			// probably in a thin shaft, so try going up and down.
//
//			if (attempts >= 16) {
//				if (this.worldObj.isAirBlock((int) Math.floor(this.posX),
//						(int) (Math.floor(this.posY + this.height / 2) + 1
//								* this.height * holeEscapeDirection),
//						(int) Math.floor(this.posZ))) {
//					this.waypointX = this.posX;
//					this.waypointY = this.posY + this.height / 2 + 2
//							* this.height * holeEscapeDirection;
//					this.waypointZ = this.posZ;
//				} else {
//					holeEscapeDirection *= -1;
//					this.waypointX = this.posX;
//					this.waypointY = this.posY + this.height / 2 + 2
//							* this.height * holeEscapeDirection;
//					this.waypointZ = this.posZ;
//				}
//			}
//		}
//	}
//
//	// makes 16 attempts to find a waypoint that is closer to the target than
//	// the current position. if not possible, gives up on reaching the target
//	// and sets the current waypoint as the target point.
//	public boolean closeEnough() {
//		double dX1 = this.posX - this.targetX;
//		double dY1 = this.posY - this.targetY;
//		double dZ1 = this.posZ - this.targetZ;
//		// distance squared to my target
//		double dSq1 = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
//
//		double dX2 = this.waypointX - this.targetX;
//		double dY2 = this.waypointY - this.targetY;
//		double dZ2 = this.waypointZ - this.targetZ;
//		// distance suqared between my waypoint and my target
//		double dSq2 = dX2 * dX2 + dY2 * dY2 + dZ2 * dZ2;
//
//		int attempts = 16;
//
//		while ((dSq1 - dSq2 < 0 || !this.isCourseTraversable(this.posX,
//				this.posY + 1, this.posZ, this.waypointX, this.waypointY,
//				this.waypointZ))
//				&& attempts > 0) {
//			attempts--;
//
//			setRandomWaypointTowardsTarget(16);
//
//			dX2 = this.waypointX - this.targetX;
//			dY2 = this.waypointY - this.targetY;
//			dZ2 = this.waypointZ - this.targetZ;
//			// distance suqared between my waypoint and my target
//			dSq2 = dX2 * dX2 + dY2 * dY2 + dZ2 * dZ2;
//
//			System.out.println("dTSq = " + dSq1 + ", dWSq = " + dSq2
//					+ " Attempt: " + (16 - attempts));
//		}
//
//		if (attempts > 0) {
//			this.setTargetPoint(this.waypointX, this.waypointY, this.waypointZ);
//			return true;
//		}
//
//		return false;
//	}
//
//	// PHASE LOGIC
//
//	public void setStep(int step) {
//		planStep = step;
//		currentPhase = plan.get(step);
//		phaseCounter = 0;
//	}
//
//	public void nextStep() {
//		planStep++;
//		currentPhase = plan.get(planStep);
//		phaseCounter = 0;
//	}
//
//	public void doPhase() {
//		switch (currentPhase) {
//		case ATTACK_FOREVER:
//			doAttackUntil(0);
//			break;
//		case ATTACK_ONCE:
//			doAttackUntil(1);
//			break;
//		case ATTACK_ONCE_IMMEDIATELY:
//			doAttackUntil(5);
//			break;
//		case ATTACK_UNTIL_DAMAGE_TAKEN:
//			doAttackUntil(3);
//			break;
//		case ATTACK_UNTIL_TARGET_IS_HURT:
//			doAttackUntil(2);
//			break;
//		case ATTACK_UNTIL_HP_LOW:
//			doAttackUntil(4);
//			break;
//		case FLEE_UNTIL_SAFE:
//			doFleeFor(3);
//			break;
//		case HIDE_2_SEC:
//			doHide(2);
//			break;
//		case HIDE_5_SEC:
//			doHide(5);
//			break;
//		case HIDE_UNTIL_HEALTHY:
//			doHide(-1);
//			break;
//		case PLAY_DEAD:
//			doPlayDead();
//			break;
//		case ACT_IDLE:
//			doActIdle();
//			break;
//		default:
//			this.setStep(currentPhase.skipTo);
//		}
//
//	}
//
//	public void doActIdle() {
//		this.targetedEntity = null;
//
//		double dX1 = this.targetX - this.posX;
//		double dY1 = this.targetY - this.posY;
//		double dZ1 = this.targetZ - this.posZ;
//
//		double d1Sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
//
//		// reduce target change interval by closeness to target.
//		this.courseChangeCooldown -= (int) Math.ceil(16.0 / d1Sq);
//
//		if (this.courseChangeCooldown <= 0 || d1Sq < 2) {
//			this.courseChangeCooldown = this.rand.nextInt(200) + 100;
//			setRandomTargetPoint(this.lostTargetedEntity.posX,
//					this.lostTargetedEntity.posY, this.lostTargetedEntity.posZ,
//					16.0D);
//		}
//
//		// handle rotation
//		this.prevRotationYaw = this.rotationYaw;
//		this.rotationYaw = -(float) (Math.atan2(dX1, dZ1)) * 180 / 3.14f;
//
//		this.prevRotationPitch = this.rotationPitch;
//		this.rotationPitch = (float) (Math.atan2(dZ1 * dZ1 + dX1 * dX1,
//				Math.copySign(dY1 * dY1, dY1)) - 0.5235988F) * 180 / 3.14f;
//		// general formula for pitch in 3D space: atan2(hyp^2, (+/-)y^2)
//		// *180/3.14
//
//		this.rotationPitch = this.rotationPitch > 80 ? 80
//				: this.rotationPitch < -80 ? -80 : this.rotationPitch;
//
//		if (dSq < 36 || this.hurtTime > 0) {
//			this.targetedEntity = this.lostTargetedEntity;
//			this.nextStep();
//		}
//
//	}
//
//	public void doPlayDead() {
//		if (this.phaseCounter == 0) {
//			this.setStatus(STATUS_PLAYING_DEAD);
//			this.worldObj.playSoundAtEntity(this, this.getDeathSound(),
//					this.getSoundVolume(), this.getSoundPitch());
//			this.hurtResistantTime = 100;
//		}
//		phaseCounter++;
//		if (this.phaseCounter == 20) {
//			for (int i = 0; i < 20; ++i) {
//				double var8 = this.rand.nextGaussian() * 0.02D;
//				double var4 = this.rand.nextGaussian() * 0.02D;
//				double var6 = this.rand.nextGaussian() * 0.02D;
//				this.worldObj.spawnParticle("explode",
//						this.posX + (this.rand.nextFloat() * this.width * 2.0F)
//								- this.width,
//						this.posY + (this.rand.nextFloat() * this.height),
//						this.posZ + (this.rand.nextFloat() * this.width * 2.0F)
//								- this.width, var8, var4, var6);
//			}
//		}
//
//		if (this.getStatus() != this.STATUS_PLAYING_DEAD) {
//			this.nextStep();
//		}
//
//		if (this.phaseCounter > 100
//				|| (this.canEntityBeSeen(this.lostTargetedEntity) && this
//						.getDistanceSqToEntity(this.lostTargetedEntity) < 16)) {
//			this.setStatus(STATUS_NORMAL);
//			this.nextStep();
//		}
//	}
//
//	// 0 = forever, 1 = once, 2 = until target damaged, 3 = until hurt, 4 =
//	// until hp is low
//	public void doAttackUntil(int until) {
//		if (canEntityBeSeen(this.lostTargetedEntity)) {// this.getEntitySenses().canSee(this.targetedEntity)
//			// approach if too far
//			if (dSq > distPursueSq) {
//				if (this.lostTargetedEntity.canEntityBeSeen(this)) {
//					float w1 = 0.25f;
//					float w2 = 1 - w1;
//					this.targetX = (this.posX * w1 + this.lostTargetedEntity.posX
//							* w2);
//					this.targetY = (this.posY * w1 + (this.lostTargetedEntity.posY + 5.0D)
//							* w2);
//					this.targetZ = (this.posZ * w1 + this.lostTargetedEntity.posZ
//							* w2);
//				} else {
//					setRandomTargetPointWithTraversability(
//							this.lostTargetedEntity.posX,
//							this.lostTargetedEntity.posY,
//							this.lostTargetedEntity.posZ, 6);
//				}
//			}
//			// retreat if too close
//			else if (dSq < distFleeSq) {
//				this.targetX = (this.posX - 5.0D * (this.lostTargetedEntity.posX - this.posX));
//				this.targetY = (this.lostTargetedEntity.posY + 5.0D);
//				this.targetZ = (this.posZ - 5.0D * (this.lostTargetedEntity.posZ - this.posZ));
//			}
//			// strafe
//			else {
//
//				double dX2 = this.lostTargetedEntity.posX - this.posX;
//				double dY2 = this.lostTargetedEntity.posY - this.posY;
//				double dZ2 = this.lostTargetedEntity.posZ - this.posZ;
//				float theta = -((float) (Math.atan2(dX2, dZ2)));
//
//				float ticksAsRadians = (this.ticksExisted % 175) / 25.0f;
//
//				this.targetX = this.posX + Math.cos(theta)
//						* Math.sin(ticksAsRadians);
//				this.targetY = this.lostTargetedEntity.posY
//						+ this.lostTargetedEntity.height + rand.nextInt(4);
//				this.targetZ = this.posZ + Math.sin(theta)
//						* Math.sin(ticksAsRadians);
//			}
//
//			// handle shooting.
//
//			if (this.phaseCounter == 0) {
//				if (until == 5) {
//					this.attackCounter = 0;
//				} else {
//					this.attackCounter = this.rand.nextInt(20) + 50;
//				}
//				this.phaseCounter++;
//			}
//
//			if (dSq < attackDistanceSq) {
//				if (until == 2
//						&& this.lostTargetedEntity.getHealth() < this.targetHealth) {
//
//					this.targetHealth = 0;
//					this.nextStep();
//					return;
//				}
//
//				if (this.hurtTime > 0) {
//					if (until == 3
//							|| (until == 4 && ((float) this.getHealth())
//									/ this.getMaxHealth() < 0.33f)) {
//						this.nextStep();
//						return;
//					}
//				}
//
//				if (this.attackCounter == 30) {
//					this.worldObj.playSoundAtEntity(this,
//							"mob.fledgeling.telegraph", this.getSoundVolume(),
//							new Random().nextFloat() * 0.2f + 0.9f);
//				}
//
//				if (this.attackCounter <= 0) {
//					this.launchAttack();
//
//					if (until == 2 || until == 5)
//						this.targetHealth = this.lostTargetedEntity.getHealth();
//
//					this.attackCounter = 50 + rand.nextInt(20);
//
//					if (until == 1)
//						this.nextStep();
//				}
//			}
//		} else {
//			if (this.ticksExisted % 40 == 0)
//				this.setRandomTargetPoint(this.lostTargetedEntity.posX,
//						this.lostTargetedEntity.posY,
//						this.lostTargetedEntity.posZ, 4);
//		}
//	}
//
//	// -1 = until hp normal, -2 = until found, -3 = until hit
//	public void doHide(int secs) {
//
//		this.targetedEntity = null;
//
//		if (phaseCounter == 0 && (path == null || path.size() == 0)) {
//			// flee while finding a hiding spot
//
//			this.targetX = (this.posX - 0.1 * (this.lostTargetedEntity.posX - this.posX));
//			this.targetY = (this.lostTargetedEntity.posY + 5.0D);
//			this.targetZ = (this.posZ - 0.1 * (this.lostTargetedEntity.posZ - this.posZ));
//
//			// find a hiding spot.
//
//			int attempts = 0;
//			int layer = 4;
//
//			AxisAlignedBB AABB;
//
//			double x;
//			double y;
//			double z;
//
//			do {
//				attempts++;
//				layer = 4 + (int) (Math.floor((float) (128 - attempts) / 16)) * 4;
//				x = this.posX + this.rand.nextDouble()
//						+ (this.rand.nextBoolean() ? -1 - layer : layer);
//				y = this.lostTargetedEntity.posY + this.rand.nextDouble() * 12
//						- 4;
//				z = this.posZ + this.rand.nextDouble()
//						+ (this.rand.nextBoolean() ? -1 - layer : layer);
//
//				AABB = this.boundingBox.copy();
//				AABB.offset(x - this.posX, y - this.posY, z - this.posZ);
//				if (this.worldObj.getCollidingBoundingBoxes(this, AABB)
//						.isEmpty()
//						&& !this.isCourseTraversable(x, y, z,
//								this.lostTargetedEntity.posX,
//								this.lostTargetedEntity.posY + 5,
//								this.lostTargetedEntity.posZ)
//						&& !this.isCourseTraversable(x, y, z,
//								this.lostTargetedEntity.posX,
//								this.lostTargetedEntity.posY + 3,
//								this.lostTargetedEntity.posZ)
//						&& !this.isCourseTraversable(x, y, z,
//								this.lostTargetedEntity.posX,
//								this.lostTargetedEntity.posY + 1,
//								this.lostTargetedEntity.posZ)) {
////					JPPathFinder jp = new JPPathFinder(new AStarPath(worldObj));
//					AStarNode start = new AStarNode(
//							(int) this.lostTargetedEntity.posX,
//							(int) this.lostTargetedEntity.posY,
//							(int) this.lostTargetedEntity.posZ, 0);
//					AStarNode goal = new AStarNode((int) x, (int) y, (int) z, 0);
////					jp.setup(worldObj, start, goal, false);
//	//				path = jp.findPath(start, goal);
//
//					if (path != null) {
//
//						for (AStarNode as : path) {
////							worldObj.setBlockWithNotify(as.x, as.y, as.z,
////									mod_aurus.pathMarker.blockID);
//						}
//					} else {
//						System.out.println("Failed to find a path.");
//						continue;
//					}
//
//					phaseCounter++;
//					break;
//				}
//			} while (attempts < 128);
//
//			return;
//		}
//
//		double dX1 = this.targetX - this.posX;
//		double dY1 = this.targetY - this.posY;
//		double dZ1 = this.targetZ - this.posZ;
//
//		double d1Sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
//		if (d1Sq < 100) {
//			this.phaseCounter++;
//		}
//		if (d1Sq < 9
//				&& (this.isCourseTraversable(this.posX, this.posY, this.posZ,
//						this.lostTargetedEntity.posX,
//						this.lostTargetedEntity.posY + 5,
//						this.lostTargetedEntity.posZ)
//						|| this.isCourseTraversable(this.posX, this.posY,
//								this.posZ, this.lostTargetedEntity.posX,
//								this.lostTargetedEntity.posY + 3,
//								this.lostTargetedEntity.posZ) || this
//							.isCourseTraversable(this.posX, this.posY,
//									this.posZ, this.lostTargetedEntity.posX,
//									this.lostTargetedEntity.posY + 1,
//									this.lostTargetedEntity.posZ))) {
//			this.phaseCounter = 0;
//			return;
//		} else if (d1Sq > 9 && phaseCounter < 150 && secs != -1) {
//			this.bleed();
//		}
//
//		if (secs == -1) {
//			if ((float) this.getHealth() / this.getMaxHealth() < 0.67f) {
//				PotionEffect regen = new PotionEffect(Potion.regeneration.id,
//						10, 0);
//				this.addPotionEffect(regen);
//				this.bleed();
//			} else {
//				this.nextStep();
//				this.targetedEntity = this.lostTargetedEntity;
//			}
//		}
//
//		if (secs > 0 && this.phaseCounter > 40 * secs) {
//			this.nextStep();
//			this.targetedEntity = this.lostTargetedEntity;
//		}
//	}
//
//	public void doFleeFor(int secs) {
//		// retreat for some ticks
//		if (phaseCounter < 40 * secs) {
//			phaseCounter++;
//			this.targetX = (this.posX - 5.0D * (this.targetedEntity.posX - this.posX));
//			this.targetY = (this.targetedEntity.posY + 5.0D);
//			this.targetZ = (this.posZ - 5.0D * (this.targetedEntity.posZ - this.posZ));
//			if (phaseCounter > 20) {
//				this.bleed();
//			}
//		} else {
//			this.nextStep();
//		}
//	}
//
//	public void setWaypoint(double x, double y, double z) {
//		this.waypointX = x;
//		this.waypointY = y;
//		this.waypointZ = z;
//	}
//
//	public void setTargetPoint(double x, double y, double z) {
//		this.targetX = x;
//		this.targetY = y;
//		this.targetZ = z;
//	}
//
//	public void setTargetEntity(EntityLiving entity) {
//		this.targetedEntity = entity;
//		this.lostTargetedEntity = entity;
//	}
//
//	@Override
//	protected void dropRareDrop(int par1) {
//		// TODO Auto-generated method stub
//		super.dropRareDrop(par1);
//	}
//
//	// attempts to find a random waypoint with traversability to x, y, z within
//	// range
//	protected void setRandomWaypointWithTraversability(double x, double y,
//			double z, double range) {
//		double xTest, yTest, zTest;
//		int attempts = 0;
//
//		do {
//			xTest = (x + (this.rand.nextFloat() * 2F - 1F) * range);
//			yTest = (y + (this.rand.nextFloat() * 2F - 0.5F) * range / 4);
//			zTest = (z + (this.rand.nextFloat() * 2F - 1F) * range);
//		} while (attempts++ < 4
//				|| !this.isCourseTraversable(xTest, yTest, zTest, x, y, z));
//
//		this.waypointX = xTest;
//		this.waypointY = yTest;
//		this.waypointZ = zTest;
//
//	}
//
//	// attempts to find a random waypoint with traversability to x, y, z within
//	// range
//	protected void setRandomTargetPointWithTraversability(double x, double y,
//			double z, double range) {
//		double xTest, yTest, zTest;
//		int attempts = 0;
//
//		do {
//			xTest = (x + (this.rand.nextFloat() * 2F - 1F) * range);
//			yTest = (y + (this.rand.nextFloat() * 2F - 0.5F) * range / 4);
//			zTest = (z + (this.rand.nextFloat() * 2F - 1F) * range);
//		} while (attempts++ < 16
//				|| !this.isCourseTraversable(xTest, yTest, zTest, x, y, z));
//
//		this.targetX = xTest;
//		this.targetY = yTest;
//		this.targetZ = zTest;
//
//	}
//
//	// attempts to find a random waypoint with NO traversability to x, y, z
//	// within range range. used for hiding.
//	protected void setRandomWaypointWithNoTraversability(double x, double y,
//			double z, double range) {
//		double xTest, yTest, zTest;
//		int attempts = 0;
//
//		do {
//			xTest = (x + (this.rand.nextFloat() * 2F - 1F) * range);
//			yTest = (y + (this.rand.nextFloat() * 2F - 0.5F) * range / 4);
//			zTest = (z + (this.rand.nextFloat() * 2F - 1F) * range);
//		} while (attempts++ < 16
//				|| this.isCourseTraversable(xTest, yTest, zTest, x, y, z));
//
//		this.waypointX = xTest;
//		this.waypointY = yTest;
//		this.waypointZ = zTest;
//
//	}
//
//	// attempts to find a random waypoint around destination(x, y, z) with NO
//	// traversability to x, y, z within range. used for hiding.
//	protected void setRandomWaypointWithNoTraversabilityBetween(double destX,
//			double destY, double destZ, double fearX, double fearY,
//			double fearZ, double range) {
//		double xTest, yTest, zTest;
//		int attempts = 0;
//
//		do {
//			xTest = (destX + (this.rand.nextFloat() * 2F - 1F) * range);
//			yTest = (destY + (this.rand.nextFloat() * 2F - 0.5F) * range / 4);
//			zTest = (destZ + (this.rand.nextFloat() * 2F - 1F) * range);
//		} while (attempts++ < 16
//				|| this.isCourseTraversable(xTest, yTest, zTest, fearX, fearY,
//						fearZ));
//
//		this.waypointX = xTest;
//		this.waypointY = yTest;
//		this.waypointZ = zTest;
//
//	}
//
//	protected void setRandomWaypoint(double x, double y, double z, double range) {
//		this.waypointX = (x + (this.rand.nextFloat() * 2F - 1F) * range);
//		this.waypointY = (y + (this.rand.nextFloat() * 2F - 0.5F) * range / 4);
//		this.waypointZ = (z + (this.rand.nextFloat() * 2F - 1F) * range);
//	}
//
//	protected void setRandomWaypointTowardsTarget(double dub) {
//		int xSca = (int) Math.copySign(dub, this.targetX - this.posX);
//		int zSca = (int) Math.copySign(dub, this.targetZ - this.posZ);
//
//		this.waypointX = this.posX + this.rand.nextDouble() * xSca;
//		this.waypointY = this.posY + this.rand.nextDouble() * 8 - 4;
//		this.waypointZ = this.posZ + this.rand.nextDouble() * zSca;
//	}
//
//	protected void setRandomTargetPoint(double x, double y, double z,
//			double range) {
//		this.targetX = (x + (this.rand.nextFloat() * 2F - 1F) * range);
//		this.targetY = (y + (this.rand.nextFloat() * 2F - 0.5F) * range / 4);
//		this.targetZ = (z + (this.rand.nextFloat() * 2F - 1F) * range);
//	}
//
//	// this method determines if a target entity is suitable based on being
//	// alive and visible.
//	protected boolean isSuitableTarget(EntityLiving entity,
//			boolean visibleRequired) {
//		if (entity == null) {
//			return false;
//		} else if (entity == this) {
//			return false;
//		} else if (!entity.isEntityAlive()) {
//			return false;
//		} else {
//			if (entity instanceof EntityPlayer
//					&& ((EntityPlayer) entity).capabilities.disableDamage) {
//				return false;
//			}
//			if (visibleRequired && !canEntityBeSeen(entity)) // !this.getEntitySenses().canSee(entity))
//			{
//				return false;
//			} else {
//				return true;
//			}
//		}
//	}
//
//	protected void launchAttack() {
//		this.worldObj.playSoundAtEntity(this, "mob.fledgeling.shoot",
//				this.getSoundVolume(), 1.0f);
//
//		Entity entity = new EntityProjectile(this, this.lostTargetedEntity);
//
//		this.worldObj.spawnEntityInWorld(entity);
//
//		// System.out.println("I am launching a...");
//		// System.out.println(entity);
//	}
//
//	protected boolean isCourseTraversable(double xStart, double yStart,
//			double zStart, double distance) {
//
//		double dX = (this.waypointX - xStart) / distance;
//		double dY = (this.waypointY - yStart) / distance;
//		double dZ = (this.waypointZ - zStart) / distance;
//
//		AxisAlignedBB AABB = this.boundingBox.copy();
//		AABB.offset(xStart - this.posX, yStart - this.posY, zStart - this.posZ);
//
//		for (int i = 1; i < distance; i++) {
//			AABB.offset(dX, dY, dZ);
//
//			if (!this.worldObj.getCollidingBoundingBoxes(this, AABB).isEmpty()) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	protected boolean isCourseTraversable(double xStart, double yStart,
//			double zStart, double xEnd, double yEnd, double zEnd) {
//		double dX = xEnd - xStart;
//		double dY = yEnd - yStart;
//		double dZ = zEnd - zStart;
//
//		double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
//
//		dX /= distance;
//		dY /= distance;
//		dZ /= distance;
//
//		AxisAlignedBB AABB = this.boundingBox.copy();
//		AABB.offset(xStart - this.posX, yStart - this.posY, zStart - this.posZ);
//
//		for (int i = 0; i < distance; i++) {
//			AABB.offset(dX, dY, dZ);
//
//			if (!this.worldObj.getCollidingBoundingBoxes(this, AABB).isEmpty()) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	protected void bleed() {
//		if (this.ticksExisted % 30 == 0
//				&& this.getStatus() == this.STATUS_NORMAL) {
//	//		this.dropItem(mod_aurus.dragonBlood.shiftedIndex, 1);
//		}
//	}
//
//	protected void findTarget() {
//		if (this.targetedEntity == null) {
//			// this.targetedEntity = this.worldObj.findNearestEntityWithinAABB(
//			// // this is a debugging targeting method, will target player
//			// // even in creative.
//			// EntityPlayer.class, this.boundingBox.expand(32, 32, 32),
//			// this);
//			this.targetedEntity = this.worldObj // this is the real method.
//					.getClosestVulnerablePlayerToEntity(this, 100.0D);
//			if (!this.isSuitableTarget(this.targetedEntity, true))
//				this.targetedEntity = null;
//		}
//
//		// The following code has the dragons target the nearest visible
//		// chicken, even if they are already chasing a player. Dragons hate
//		// chickens.
//
//		if (this.targetedEntity == null
//				|| this.targetedEntity instanceof EntityPlayer) {
//			List entities = this.worldObj.getEntitiesWithinAABB(EntityChicken.class,
//					this.boundingBox.expand(32, 16, 32));
//			// last argument should be IEntitySelector, not sure what for
//			// reasonable targeting distance seems to be 12, 8, 12.
//			Collections.sort(entities,
//					new EntityAINearestAttackableTargetSorter(null, this));
//			Iterator it = entities.iterator();
//			EntityLiving el = null;
//
//			while (it.hasNext()) {
//				Entity e = (Entity) it.next();
//				el = (EntityLiving) e;
//
//				if (isSuitableTarget(el, true)) {
//					this.targetedEntity = el;
//					break;
//				}
//			}
//		}
//	}
//
//	@Override
//	public void moveEntityWithHeading(float par1, float par2) {
//		// avoid gravity effects while flying
//		if (this.model == this.MODEL_FLYING) {
//			if (this.isInWater()) {
//				this.moveFlying(par1, par2, 0.02F);
//				this.moveEntity(this.motionX, this.motionY, this.motionZ);
//				this.motionX *= 0.800000011920929D;
//				this.motionY *= 0.800000011920929D;
//				this.motionZ *= 0.800000011920929D;
//			} else if (this.handleLavaMovement()) {
//				this.moveFlying(par1, par2, 0.02F);
//				this.moveEntity(this.motionX, this.motionY, this.motionZ);
//				this.motionX *= 0.5D;
//				this.motionY *= 0.5D;
//				this.motionZ *= 0.5D;
//			} else {
//				float var3 = 0.91F;
//
//				this.moveEntity(this.motionX, this.motionY, this.motionZ);
//				this.motionX *= var3;
//				this.motionY *= var3;
//				this.motionZ *= var3;
//			}
//		} else {
//			super.moveEntityWithHeading(par1, par2);
//		}
//	}
//
//	public boolean followPath() {
//		if (this.path == null || this.path.size() < 1) {
//			return false;
//		} else {
//			if (tracking) {
//				if (dSq < 3) {
//					this.path = this.nextWaypoint(path);
//				}
//			} else {
//				this.path = this.nextWaypoint(path);
//			}
//		}
//		return true;
//	}
//
//	public ArrayList<AStarNode> nextWaypoint(ArrayList<AStarNode> nodes) {
//		AStarNode as = nodes.remove(nodes.size() - 1);
//
//		if (nodes.size() > 1) {
//			AStarNode as2 = nodes.remove(nodes.size() - 1);
//
//			Direction dir = as.getDirectionFrom(as.parent);
//
//			while (nodes.size() > 1 && as2.getDirectionFrom(as2.parent) == dir) {
//				as2 = nodes.remove(nodes.size() - 1);
//			}
//
//			as = as2;
//		}
//
//		this.setWaypoint(as.xCoord, as.yCoord, as.zCoord);
//
//		return nodes;
//	}
//
//	@Override
//	public int getTalkInterval() {
//		return 160;
//	}
//
//	@Override
//	protected String getLivingSound() {
//		return "mob.fledgeling.howl";
//	}
//
//	@Override
//	protected String getHurtSound() {
//		return "mob.fledgeling.hurt";
//	}
//
//	@Override
//	protected String getDeathSound() {
//		return "mob.fledgeling.death";
//	}
//
//	@Override
//	protected int getDropItemId() {
//		return 10; //mod_aurus.dragonBlood.shiftedIndex;
//	}
//
//	@Override
//	protected void dropFewItems(boolean par1, int par2) {
//		int var3 = this.getDropItemId();
//
//		if (var3 > 0) {
//			int var4 = this.rand.nextInt(3) + 1;
//
//			if (par2 > 0) {
//				var4 += this.rand.nextInt(par2 + 1);
//			}
//
//			for (int var5 = 0; var5 < var4; ++var5) {
//				this.dropItem(var3, this.rand.nextInt(8) + 4);
//			}
//		}
//	}
//
//	@Override
//	protected float getSoundVolume() {
//		return 1.0F;
//	}
//
//	protected float getSoundPitch() {
//		return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 1.3F
//				: (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 1.0F;
//	}
//
//	@Override
//	public boolean getCanSpawnHere() {
//		return (this.rand.nextInt(20) == 0) && (super.getCanSpawnHere())
//				&& (this.worldObj.difficultySetting > 0);
//	}
//
//	@Override
//	public int getMaxSpawnedInChunk() {
//		return 1;
//	}
//}