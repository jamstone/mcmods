package mods.jamstone.ghosttrain.common;

import mods.jamstone.pathfinding.AStarNode;
import mods.jamstone.pathfinding.AStarStatic;
import mods.jamstone.pathfinding.minecart.MinecartPathMediator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;

public class EntityWilloWisp extends EntityFlying implements IMob {
    private boolean firstUpdate = true;

    public ArrayList<AStarNode> path = null;
    public EntityPlayer rider = null;
    public EntityMinecart ghostTrain = null;
    private ChunkCoordinates lastRailPosition;
    public LinkedList<ChunkCoordinates> added = new LinkedList<ChunkCoordinates>();

    AStarNode lastAs = null;
    AStarNode lastlastAs = null;

    private boolean searching;
    private MinecartPathMediator pathPlanner;

    public EntityWilloWisp(World par1World) {
        super(par1World);
        this.texture = "/mods/jamstone_ghosttrain/textures/models/willoWisp.png";
        this.setSize(0.5f, 0.5f); // size determines vanishing point of

        this.isImmuneToFire = true;
        this.experienceValue = 1;
    }

    public void setPath(ArrayList<AStarNode> newPath) {
        path = newPath;
        if (!added.isEmpty()) {
            for (ChunkCoordinates cc : added) {
                worldObj.setBlockToAir(cc.posX, cc.posY + 1, cc.posZ);
                worldObj.setBlockToAir(cc.posX, cc.posY, cc.posZ);
            }
            added.clear();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {

        return super.attackEntityFrom(par1DamageSource, par2);
    }

    public void setDead() {
        super.setDead();
        path = null;
        if (ghostTrain != null) {
            ghostTrain.setDead();
            ghostTrain = null;
        }
        clearAddedBlocks();

//        this.worldObj.spawnParticle("largeexplode", this.posX, this.posY, this.posZ, 1.5f, 0, 0);
        this.worldObj.spawnParticle("hugeexplosion", this.posX, this.posY, this.posZ, 1.5f, 0, 0);

    }


    @Override
    protected void entityInit() {
        super.entityInit();
//        this.setPosition(path.get(path.size() - 5).x, path.get(path.size() - 5).y, path.get(path.size() - 5).z);
//        this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
    }

    @Override
    public int getMaxHealth() {
        return 1;
    }

    public int getAttackStrength(Entity par1Entity) {
        return 1;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote && this.rand.nextInt(100) < 50) {
            float hueShift = rand.nextFloat() * 0.3f;
            this.worldObj.spawnParticle("reddust", this.posX, this.posY + this.height / 2, this.posZ, 0.5f, 1 + hueShift, 1 - hueShift);
        }
        if (!this.worldObj.isRemote) {
            if (worldObj.isAirBlock((int) Math.floor(this.posX), (int) Math.floor(this.posY + this.height / 2), (int) Math.floor(this.posZ)) || worldObj.getBlockId((int) Math.floor(this.posX), (int) Math.floor(this.posY + this.height / 2), (int) Math.floor(this.posZ)) == GhostTrain.blockBrightSpace.blockID) {
                worldObj.setBlock((int) Math.floor(this.posX), (int) Math.floor(this.posY + this.height / 2), (int) Math.floor(this.posZ), GhostTrain.blockBrightSpace.blockID, 0, 3);
            }
        }

//        byte var1 = this.dataWatcher.getWatchableObjectByte(16);
    }

    @Override
    protected void updateEntityActionState() {
        if (path != null && this.getDistanceSq(path.get(path.size() - 1).x + 0.5, path.get(path.size() - 1).y + 2, path.get(path.size() - 1).z + 0.5) > 4) {
            this.setPosition(path.get(path.size() - 1).x + 0.5, path.get(path.size() - 1).y + 2, path.get(path.size() - 1).z + 0.5);
        }
        if (ghostTrain != null) {
            if (path == null) {
                if (ghostTrain.getDistanceSq(lastAs.x + 0.5, lastAs.y + 1, lastAs.z + 0.5) < 1) {
                    ghostTrain.setDead();
                }
            } else {
                if (worldObj.getBlockMaterial((int) Math.floor(ghostTrain.posX), (int) Math.floor(ghostTrain.posY), (int) Math.floor(ghostTrain.posZ)) != Material.circuits
                        && worldObj.getBlockMaterial((int) Math.floor(ghostTrain.posX), (int) Math.floor(ghostTrain.posY) + 1, (int) Math.floor(ghostTrain.posZ)) != Material.circuits
                        && worldObj.getBlockMaterial((int) Math.floor(ghostTrain.posX), (int) Math.floor(ghostTrain.posY) - 1, (int) Math.floor(ghostTrain.posZ)) != Material.circuits) {
                    System.out.println("I am off a rail.");
                    ghostTrain.setVelocity((this.posX - ghostTrain.posX) / 10, 0.05, (this.posZ - ghostTrain.posZ) / 10);
                    ghostTrain.setPosition(lastRailPosition.posX + 0.5, lastRailPosition.posY + 1, lastRailPosition.posZ + 0.5);
                } else {
                    lastRailPosition = new ChunkCoordinates((int) Math.floor(ghostTrain.posX), (int) Math.floor(ghostTrain.posY), (int) Math.floor(ghostTrain.posZ));
                }
            }

            if (ghostTrain.riddenByEntity == null) {
                ghostTrain.setVelocity(0, 0, 0);
                ghostTrain.setPosition(ghostTrain.prevPosX, ghostTrain.prevPosY, ghostTrain.prevPosZ);
            } else if (ghostTrain.motionX * ghostTrain.motionX + ghostTrain.motionZ * ghostTrain.motionZ < 0.1) {
                System.out.println("I am slow.");
                ghostTrain.addVelocity((this.posX - ghostTrain.posX) / 10, ghostTrain.motionY, (this.posZ - ghostTrain.posZ) / 10);
            }

            if (ghostTrain.isDead) {
                ghostTrain = null;
            }

        }

        if (path != null) {

            if (ghostTrain == null) {
                ghostTrain = EntityMinecart.createMinecart(worldObj, path.get(path.size() - 1).x + 0.5, path.get(path.size() - 1).y + 1, path.get(path.size() - 1).z + 0.5, 0);
                if (lastRailPosition != null) {
                    ghostTrain.setPosition(lastRailPosition.posX + 0.5, lastRailPosition.posY + 1, lastRailPosition.posZ + 0.5);
                }
                worldObj.spawnEntityInWorld(ghostTrain);

            }
            AStarNode as = path.get(path.size() - 1);
            while (as.getDistanceTo((int) ghostTrain.posX, (int) ghostTrain.posY, (int) ghostTrain.posZ) < AStarStatic.getDistanceBetween(0, 0, 0, 8, 8, 8)) {
                path.remove(path.size() - 1);

                addCartPathBlock(as.x, as.y, as.z, /*Block.blockRedstone.blockID*/ GhostTrain.blockPhantomPower.blockID);
                worldObj.setBlock(as.x, as.y + 1, as.z, Block.railPowered.blockID);

                if (lastlastAs != null && lastlastAs.getDirectionTo(lastAs) != lastAs.getDirectionTo(as) && lastAs.getDirectionTo(as).y != 1) {
                    worldObj.setBlock(lastAs.x, lastAs.y + 1, lastAs.z, Block.rail.blockID);
                }

                lastlastAs = lastAs;
                lastAs = as;
                if (path.size() < 1) {
                    path = null;
                    break;
                }
                as = path.get(path.size() - 1);
            }
            if (lastAs != null) {
                this.setPosition(lastAs.x + 0.5, lastAs.y + 2, lastAs.z + 0.5);
            }
        }

        clearAddedBlocks();

        this.rotationPitch = 90;
        this.rotationYaw += 30f;
        this.motionY += 0.005d * Math.sin(this.ticksExisted / 3d);
        this.motionX += 0.01d * Math.sin(this.ticksExisted / 6d);
        this.motionZ += 0.01d * Math.cos(this.ticksExisted / 6d);

        if (path == null && ghostTrain == null) {
            this.setDead();
        }

    }

    private void clearAddedBlocks() {
        if (!added.isEmpty() && lastAs != null) {
            ChunkCoordinates cc = added.peekFirst();
            while (lastAs.getDistanceTo(cc.posX, cc.posY, cc.posZ) > AStarStatic.getDistanceBetween(0, 0, 0, 12, 12, 12) || (path == null && ghostTrain == null)) {
                added.removeFirst();
                if (worldObj.getBlockMaterial(cc.posX, cc.posY + 1, cc.posZ) == Material.circuits) {
                    worldObj.setBlockToAir(cc.posX, cc.posY + 1, cc.posZ);
                }
                if (worldObj.getBlockMaterial(cc.posX - 1, cc.posY, cc.posZ) == Material.circuits) {
                    worldObj.setBlockToAir(cc.posX - 1, cc.posY, cc.posZ);
                }
                if (worldObj.getBlockMaterial(cc.posX + 1, cc.posY, cc.posZ) == Material.circuits) {
                    worldObj.setBlockToAir(cc.posX + 1, cc.posY, cc.posZ);
                }
                if (worldObj.getBlockMaterial(cc.posX, cc.posY, cc.posZ - 1) == Material.circuits) {
                    worldObj.setBlockToAir(cc.posX, cc.posY, cc.posZ - 1);
                }
                if (worldObj.getBlockMaterial(cc.posX, cc.posY, cc.posZ + 1) == Material.circuits) {
                    worldObj.setBlockToAir(cc.posX, cc.posY, cc.posZ + 1);
                }

                worldObj.setBlockToAir(cc.posX, cc.posY, cc.posZ);

                if (added.size() < 1) {
                    break;
                }
                cc = added.peekFirst();
            }
        }
    }

    private void addCartPathBlock(int x, int y, int z, int blockID) {
        this.worldObj.setBlock(x, y, z, blockID);
        added.addLast(new ChunkCoordinates(x, y, z));
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 2;
    }

    @Override
    public int getBrightnessForRender(float par1) {
        return 240;
    }

    @Override
    public boolean shouldRenderInPass(int i) {
        return i == 1;
    }

}
