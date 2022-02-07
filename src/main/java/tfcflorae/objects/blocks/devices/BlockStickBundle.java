package tfcflorae.objects.blocks.devices;

import java.util.Objects;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.blocks.property.ITallPlant;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.Helpers;
import tfcflorae.objects.items.ItemsTFCF;
import tfcflorae.objects.items.itemblock.ItemBlockStickBundle;
import tfcflorae.objects.recipes.StickBundleRecipe;
import tfcflorae.objects.te.TEStickBundle;

public class BlockStickBundle extends Block implements IItemSize, ITallPlant
{

    private static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 1.0D, 0.75D);
    public static final PropertyEnum<BlockStickBundle.EnumBlockPart> PART = PropertyEnum.create("part", BlockStickBundle.EnumBlockPart.class);
	public static final PropertyInteger GROWTH = PropertyInteger.create("growth", 0, 3);

    public BlockStickBundle()
    {
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTH, 0).withProperty(PART, BlockStickBundle.EnumBlockPart.LOWER));
        setHardness(1.0F);
        setResistance(2.0F);
        setHarvestLevel("axe", 0);
    	this.setTickRandomly(true);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE; // Can only store in chests
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stacksize = 1
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

	public TileEntity createNewTileEntity(World world, int meta)
    {
		return new TEStickBundle();
	}

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	return (state.getValue(PART) == BlockStickBundle.EnumBlockPart.UPPER) ? AABB_TOP : AABB_BOTTOM;
    }

	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GROWTH, PART);
    }

	@Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.BROWN;
    }

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
    	if (state.getValue(PART) == BlockStickBundle.EnumBlockPart.UPPER) drops.add(new ItemStack(ItemsTFC.STICK_BUNCH, 1));
    	switch(state.getValue(GROWTH))
        {
			case 4:
				drops.add(new ItemStack(ItemsTFCF.SILK_WORM_COCOON, 1));
				break;
			case 3:
			case 2:
			case 1:
				drops.add(new ItemStack(ItemsTFCF.SILK_WORM, 1));
				break;
			default:
		}
	}

	/**
	 * Called when the blocks is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack held = playerIn.getHeldItem(hand);
            if (held.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) return false;
            TEStickBundle te = Helpers.getTE(worldIn, pos, TEStickBundle.class);
            if (te != null)
            {
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null)
                {
                    ItemStack tryStack = new ItemStack(held.getItem(), 1);
                    if (StickBundleRecipe.get(tryStack) != null && inventory.getStackInSlot(0).isEmpty())
                    {
                        ItemStack leftover = inventory.insertItem(0, held.splitStack(1), false);
                        ItemHandlerHelper.giveItemToPlayer(playerIn, leftover);
                        te.start();
                        te.markForSync();

                        worldIn.setBlockState(pos, state.withProperty(GROWTH, 1));
                        if (state.getValue(PART) == BlockStickBundle.EnumBlockPart.UPPER)
                        {
                            worldIn.setBlockState(pos.down(), this.getDefaultState().withProperty(PART, BlockStickBundle.EnumBlockPart.LOWER).withProperty(GROWTH, 1));
                        }
                        else
                        {
                            worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(PART, BlockStickBundle.EnumBlockPart.UPPER).withProperty(GROWTH, 1));
                        }
                        return true;
                    }
                    if (playerIn.isSneaking())
                    {
                        ItemStack takeStack = inventory.extractItem(0, 1, false);
                        Helpers.spawnItemStack(worldIn, pos, takeStack);
                        te.deleteSlot();
                        te.clear();
                        te.markForSync();

                        worldIn.setBlockState(pos, state.withProperty(GROWTH, 0));
                        if (state.getValue(PART) == BlockStickBundle.EnumBlockPart.UPPER)
                        {
                            worldIn.setBlockState(pos.down(), this.getDefaultState().withProperty(PART, BlockStickBundle.EnumBlockPart.LOWER).withProperty(GROWTH, 0));
                        }
                        else
                        {
                            worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(PART, BlockStickBundle.EnumBlockPart.UPPER).withProperty(GROWTH, 0));
                        }
                        return true;
                    }
                }
            }
        }
        return true;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
		if (state.getValue(PART) == BlockStickBundle.EnumBlockPart.UPPER)
        {
			int growth = state.getValue(GROWTH);
			if (growth > 0 && growth < 3)
            {
				if (rand.nextInt(5) == 0)
                {
					worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(GROWTH,growth + 1));
					worldIn.setBlockState(pos.down(), worldIn.getBlockState(pos.down()).withProperty(GROWTH,growth + 1));
				}
			}
		}
	}

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.down());
    }

    /**
     * Called when a neighboring blocks was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * blocks, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (state.getValue(PART) == BlockStickBundle.EnumBlockPart.UPPER)
        {
            if (worldIn.getBlockState(pos.down()).getBlock() != this)
            {
				this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
            else if (worldIn.getBlockState(pos.down()).getValue(PART) != BlockStickBundle.EnumBlockPart.LOWER)
            {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}
        }
        else
        {
			if (worldIn.getBlockState(pos.up()).getBlock() != this){
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}
            else if (worldIn.getBlockState(pos.up()).getValue(PART) != BlockStickBundle.EnumBlockPart.UPPER){
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}
		}
    }

    @Override
    public EnumPushReaction getPushReaction(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
    }

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing facing)
    {
		return (state.getValue(PART) == BlockStickBundle.EnumBlockPart.UPPER && facing == EnumFacing.UP) ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
        TEStickBundle tile = Helpers.getTE(worldIn, pos, TEStickBundle.class);
        if (tile != null)
        {
            tile.onBreakBlock(worldIn, pos, state);
        }
        super.breakBlock(worldIn, pos, state);
	}

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
    	IBlockState state = this.getDefaultState();
    	if (meta > 3)
        {
    		state = state.withProperty(PART, BlockStickBundle.EnumBlockPart.UPPER);
    		meta -= 4;
		}
        return state.withProperty(GROWTH, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
    	int meta = (state.getValue(PART) == BlockStickBundle.EnumBlockPart.LOWER) ? 0 : 4;
        return meta + state.getValue(GROWTH);
    }
	
	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
    {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

    public Item getCustomItemBlock()
    {
        return new ItemBlockStickBundle(this)
            .setRegistryName(Objects.requireNonNull(this.getRegistryName()))
            .setTranslationKey(this.getTranslationKey());
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
}