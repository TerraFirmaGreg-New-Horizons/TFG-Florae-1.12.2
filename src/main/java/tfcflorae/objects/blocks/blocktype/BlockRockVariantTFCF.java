package tfcflorae.objects.blocks.blocktype;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcp.MethodsReturnNonnullByDefault;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.stone.*;
import net.dries007.tfc.objects.items.rock.ItemRock;

import tfcflorae.objects.items.ItemsTFCF;
import tfcflorae.objects.items.rock.ItemMud;
import tfcflorae.types.BlockTypesTFCF;
import tfcflorae.types.BlockTypesTFCF.RockTFCF;
import tfcflorae.util.OreDictionaryHelper;

import static net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC.WILD;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRockVariantTFCF extends Block implements IItemSize
{
    private static final Map<Rock, EnumMap<RockTFCF, BlockRockVariantTFCF>> TABLE = new HashMap<>();

    public static BlockRockVariantTFCF get(Rock rock, RockTFCF rockTFCF)
    {
        //noinspection ConstantConditions
        if (rock == null)
        {
            return TABLE.get(Rock.GRANITE).get(rockTFCF);
        }
        return TABLE.get(rock).get(rockTFCF);
    }

    public static BlockRockVariantTFCF create(Rock rock, RockTFCF rockTFCF)
    {
        switch (rockTFCF)
        {
            case MUD_BRICKS:
            case MOSSY_RAW:
                return new BlockRockRawTFCF(rockTFCF, rock);
            case COARSE_DIRT:
            case MUD:
            case LOAMY_SAND:
            case COARSE_LOAMY_SAND:
            case SANDY_LOAM:
            case COARSE_SANDY_LOAM:
            case SANDY_CLAY_LOAM:
            case COARSE_SANDY_CLAY_LOAM:
            case SANDY_CLAY:
            case COARSE_SANDY_CLAY:
            case LOAM:
            case COARSE_LOAM:
            case CLAY_LOAM:
            case COARSE_CLAY_LOAM:
            case COARSE_CLAY:
            case SILTY_CLAY:
            case COARSE_SILTY_CLAY:
            case SILTY_CLAY_LOAM:
            case COARSE_SILTY_CLAY_LOAM:
            case SILT_LOAM:
            case COARSE_SILT_LOAM:
            case SILT:
            case COARSE_SILT:
                return new BlockRockVariantFallableTFCF(rockTFCF, rock);
            case PODZOL:
            case LOAMY_SAND_GRASS:
            case LOAMY_SAND_PODZOL:
            case SANDY_LOAM_GRASS:
            case SANDY_LOAM_PODZOL:
            case SANDY_CLAY_LOAM_GRASS:
            case SANDY_CLAY_LOAM_PODZOL:
            case SANDY_CLAY_GRASS:
            case SANDY_CLAY_PODZOL:
            case LOAM_GRASS:
            case LOAM_PODZOL:
            case CLAY_LOAM_GRASS:
            case CLAY_LOAM_PODZOL:
            case CLAY_PODZOL:
            case SILTY_CLAY_GRASS:
            case SILTY_CLAY_PODZOL:
            case SILTY_CLAY_LOAM_GRASS:
            case SILTY_CLAY_LOAM_PODZOL:
            case SILT_LOAM_GRASS:
            case SILT_LOAM_PODZOL:
            case SILT_GRASS:
            case SILT_PODZOL:
            case DRY_LOAMY_SAND_GRASS:
            case DRY_SANDY_LOAM_GRASS:
            case DRY_SANDY_CLAY_LOAM_GRASS:
            case DRY_SANDY_CLAY_GRASS:
            case DRY_LOAM_GRASS:
            case DRY_CLAY_LOAM_GRASS:
            case DRY_CLAY_GRASS:
            case DRY_SILTY_CLAY_GRASS:
            case DRY_SILTY_CLAY_LOAM_GRASS:
            case DRY_SILT_LOAM_GRASS:
            case DRY_SILT_GRASS:
                return new BlockRockVariantConnectedTFCF(rockTFCF, rock);
            default:
                return new BlockRockVariantTFCF(rockTFCF, rock);
        }
    }

    protected final RockTFCF rockTFCF;
    protected final Rock rock;

    public BlockRockVariantTFCF(RockTFCF rockTFCF, Rock rock)
    {
        super(rockTFCF.material);

        if (!TABLE.containsKey(rock))
        {
            TABLE.put(rock, new EnumMap<>(RockTFCF.class));
        }
        TABLE.get(rock).put(rockTFCF, this);

        this.rockTFCF = rockTFCF;
        this.rock = rock;
        if (rockTFCF.isGrass) setTickRandomly(true);
        switch (rockTFCF)
        {
            case MOSSY_RAW:
                setSoundType(SoundType.STONE);
                setHardness(rock.getRockCategory().getHardness()).setResistance(rock.getRockCategory().getResistance());
                setHarvestLevel("pickaxe", 0);
                break;
            case COARSE_DIRT:
            case MUD:
            case LOAMY_SAND:
            case SANDY_LOAM:
            case LOAM:
            case SILT_LOAM:
            case SILT:
                setSoundType(SoundType.GROUND);
                setHardness(rock.getRockCategory().getHardness() * 0.15F);
                setHarvestLevel("shovel", 0);
                break;
            case SANDY_CLAY_LOAM:
            case SANDY_CLAY:
            case CLAY_LOAM:
            case SILTY_CLAY_LOAM:
            case SILTY_CLAY:
            case COARSE_LOAMY_SAND:
            case COARSE_SANDY_LOAM:
            case COARSE_SANDY_CLAY_LOAM:
            case COARSE_SANDY_CLAY:
            case COARSE_LOAM:
            case COARSE_CLAY_LOAM:
            case COARSE_CLAY:
            case COARSE_SILTY_CLAY:
            case COARSE_SILTY_CLAY_LOAM:
            case COARSE_SILT_LOAM:
            case COARSE_SILT:
                setSoundType(SoundType.GROUND);
                setHardness(rock.getRockCategory().getHardness() * 0.2F);
                setHarvestLevel("shovel", 0);
                break;
            case PODZOL:
            case LOAMY_SAND_GRASS:
            case LOAMY_SAND_PODZOL:
            case SANDY_LOAM_GRASS:
            case SANDY_LOAM_PODZOL:
            case SANDY_CLAY_LOAM_GRASS:
            case SANDY_CLAY_LOAM_PODZOL:
            case SANDY_CLAY_GRASS:
            case SANDY_CLAY_PODZOL:
            case LOAM_GRASS:
            case LOAM_PODZOL:
            case CLAY_LOAM_GRASS:
            case CLAY_LOAM_PODZOL:
            case CLAY_PODZOL:
            case SILTY_CLAY_GRASS:
            case SILTY_CLAY_PODZOL:
            case SILTY_CLAY_LOAM_GRASS:
            case SILTY_CLAY_LOAM_PODZOL:
            case SILT_LOAM_GRASS:
            case SILT_LOAM_PODZOL:
            case SILT_GRASS:
            case SILT_PODZOL:
            case DRY_LOAMY_SAND_GRASS:
            case DRY_SANDY_LOAM_GRASS:
            case DRY_SANDY_CLAY_LOAM_GRASS:
            case DRY_SANDY_CLAY_GRASS:
            case DRY_LOAM_GRASS:
            case DRY_CLAY_LOAM_GRASS:
            case DRY_CLAY_GRASS:
            case DRY_SILTY_CLAY_GRASS:
            case DRY_SILTY_CLAY_LOAM_GRASS:
            case DRY_SILT_LOAM_GRASS:
            case DRY_SILT_GRASS:
                setSoundType(SoundType.PLANT);
                setHardness(rock.getRockCategory().getHardness() * 0.2F);
                setHarvestLevel("shovel", 0);
                break;
        }
        //if (rockTFCF != Rock.Type.SPIKE && rockTFCF != Rock.Type.ANVIL) //since spikes and anvils don't generate ItemBlocks
        {
            OreDictionaryHelper.registerRockType(this, rockTFCF);
        }
        
        /* Soundtypes:
        static SoundType	ANVIL
        static SoundType	CLOTH
        static SoundType	GLASS
        static SoundType	GROUND
        static SoundType	LADDER
        static SoundType	METAL
        static SoundType	PLANT
        static SoundType	SAND
        static SoundType	SLIME
        static SoundType	SNOW
        static SoundType	STONE
        static SoundType	WOOD
        */
    }

    public BlockRockVariantTFCF getVariant(RockTFCF t)
    {
        return TABLE.get(rock).get(t);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        switch (this.rockTFCF)
        {
            default:
                return super.shouldSideBeRendered(blockState, world, pos, side);
        }
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (world.isRemote) return;
        if (rockTFCF.isGrass) BlockRockVariantConnectedTFCF.spreadGrass(world, pos, state, rand);
        super.randomTick(world, pos, state, rand);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        switch (rockTFCF)
        {
            case MOSSY_RAW:
                return ItemRock.get(rock);
            case MUD:
                return ItemMud.get(rock);
            case DRY_LOAMY_SAND_GRASS:
            case LOAMY_SAND_GRASS:
            case LOAMY_SAND_PODZOL:
                return Item.getItemFromBlock(get(rock, RockTFCF.LOAMY_SAND));
            case DRY_SANDY_LOAM_GRASS:
            case SANDY_LOAM_GRASS:
            case SANDY_LOAM_PODZOL:
                return Item.getItemFromBlock(get(rock, RockTFCF.SANDY_LOAM));
            case DRY_LOAM_GRASS:
            case LOAM_GRASS:
            case LOAM_PODZOL:
                return Item.getItemFromBlock(get(rock, RockTFCF.LOAM));
            case DRY_SILT_LOAM_GRASS:
            case SILT_LOAM_GRASS:
            case SILT_LOAM_PODZOL:
                return Item.getItemFromBlock(get(rock, RockTFCF.SILT_LOAM));
            case DRY_SILT_GRASS:
            case SILT_GRASS:
            case SILT_PODZOL:
                return Item.getItemFromBlock(get(rock, RockTFCF.SILT));
            case SANDY_CLAY_LOAM:
            case SANDY_CLAY:
            case CLAY_LOAM:
            case SILTY_CLAY_LOAM:
            case SILTY_CLAY:
            case COARSE_SANDY_CLAY_LOAM:
            case COARSE_SANDY_CLAY:
            case COARSE_CLAY_LOAM:
            case COARSE_CLAY:
            case COARSE_SILTY_CLAY:
            case COARSE_SILTY_CLAY_LOAM:
            case SANDY_CLAY_LOAM_GRASS:
            case SANDY_CLAY_LOAM_PODZOL:
            case SANDY_CLAY_GRASS:
            case SANDY_CLAY_PODZOL:
            case CLAY_LOAM_GRASS:
            case CLAY_LOAM_PODZOL:
            case CLAY_PODZOL:
            case SILTY_CLAY_GRASS:
            case SILTY_CLAY_PODZOL:
            case SILTY_CLAY_LOAM_GRASS:
            case SILTY_CLAY_LOAM_PODZOL:
            case DRY_SANDY_CLAY_LOAM_GRASS:
            case DRY_SANDY_CLAY_GRASS:
            case DRY_CLAY_LOAM_GRASS:
            case DRY_CLAY_GRASS:
            case DRY_SILTY_CLAY_GRASS:
            case DRY_SILTY_CLAY_LOAM_GRASS:
                return Items.CLAY_BALL;
            case COARSE_DIRT:
                return Item.getItemFromBlock(get(rock, RockTFCF.COARSE_DIRT));
            case PODZOL:
                return Item.getItemFromBlock(get(rock, RockTFCF.PODZOL));
            /*
            case COARSE_DIRT:
            case PODZOL:
                return Item.getItemFromBlock(get(rock, Rock.Type.DIRT));
            */
            default:
                return super.getItemDropped(state, rand, fortune);
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return rockTFCF.isGrass ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        switch (rockTFCF)
        {
            case SANDY_CLAY_LOAM:
            case SANDY_CLAY:
            case CLAY_LOAM:
            case SILTY_CLAY_LOAM:
            case SILTY_CLAY:
            case COARSE_SANDY_CLAY_LOAM:
            case COARSE_SANDY_CLAY:
            case COARSE_CLAY_LOAM:
            case COARSE_CLAY:
            case COARSE_SILTY_CLAY:
            case COARSE_SILTY_CLAY_LOAM:
            case SANDY_CLAY_LOAM_GRASS:
            case SANDY_CLAY_LOAM_PODZOL:
            case SANDY_CLAY_GRASS:
            case SANDY_CLAY_PODZOL:
            case CLAY_LOAM_GRASS:
            case CLAY_LOAM_PODZOL:
            case CLAY_PODZOL:
            case SILTY_CLAY_GRASS:
            case SILTY_CLAY_PODZOL:
            case SILTY_CLAY_LOAM_GRASS:
            case SILTY_CLAY_LOAM_PODZOL:
            case DRY_SANDY_CLAY_LOAM_GRASS:
            case DRY_SANDY_CLAY_GRASS:
            case DRY_CLAY_LOAM_GRASS:
            case DRY_CLAY_GRASS:
            case DRY_SILTY_CLAY_GRASS:
            case DRY_SILTY_CLAY_LOAM_GRASS:
                return 2 + random.nextInt(2);
            case MOSSY_RAW:
                return 1 + random.nextInt(3);
            case MUD:
                return 1 + random.nextInt(3);
            default:
                return super.quantityDropped(state, fortune, random);
        }
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
    {
        int beachDistance = 2;

        if (plantable instanceof BlockPlantTFC)
        {
            switch (((BlockPlantTFC) plantable).getPlantTypeTFC())
            {
                case CLAY:
                    return
                    rockTFCF == RockTFCF.COARSE_DIRT || 
                    rockTFCF == RockTFCF.MUD || 
                    rockTFCF == RockTFCF.LOAMY_SAND || 
                    rockTFCF == RockTFCF.SANDY_LOAM || 
                    rockTFCF == RockTFCF.LOAM || 
                    rockTFCF == RockTFCF.SILT_LOAM || 
                    rockTFCF == RockTFCF.SILT || 
                    rockTFCF == RockTFCF.SANDY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.SANDY_CLAY || 
                    rockTFCF == RockTFCF.CLAY_LOAM || 
                    rockTFCF == RockTFCF.SILTY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.SILTY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                    rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SANDY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SANDY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_LOAM || 
                    rockTFCF == RockTFCF.COARSE_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_CLAY || 
                    rockTFCF == RockTFCF.COARSE_SILTY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_SILTY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT || 
                    rockTFCF == RockTFCF.PODZOL || 
                    rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SANDY_CLAY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.SANDY_CLAY_PODZOL || 
                    rockTFCF == RockTFCF.LOAM_GRASS || 
                    rockTFCF == RockTFCF.LOAM_PODZOL || 
                    rockTFCF == RockTFCF.CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.CLAY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.CLAY_PODZOL || 
                    rockTFCF == RockTFCF.SILTY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.SILTY_CLAY_PODZOL || 
                    rockTFCF == RockTFCF.SILTY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SILTY_CLAY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_GRASS || 
                    rockTFCF == RockTFCF.SILT_PODZOL || 
                    rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILTY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILTY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_GRASS;
                case DESERT_CLAY:
                    return
                    rockTFCF == RockTFCF.MUD || 
                    rockTFCF == RockTFCF.SANDY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.SANDY_CLAY || 
                    rockTFCF == RockTFCF.CLAY_LOAM || 
                    rockTFCF == RockTFCF.SILTY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.SILTY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_SANDY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SANDY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_CLAY || 
                    rockTFCF == RockTFCF.COARSE_SILTY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_SILTY_CLAY_LOAM;
                case DRY_CLAY:
                    return
                    rockTFCF == RockTFCF.COARSE_DIRT || 
                    rockTFCF == RockTFCF.MUD || 
                    rockTFCF == RockTFCF.LOAMY_SAND || 
                    rockTFCF == RockTFCF.SANDY_LOAM || 
                    rockTFCF == RockTFCF.LOAM || 
                    rockTFCF == RockTFCF.SILT_LOAM || 
                    rockTFCF == RockTFCF.SILT || 
                    rockTFCF == RockTFCF.SANDY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.SANDY_CLAY || 
                    rockTFCF == RockTFCF.CLAY_LOAM || 
                    rockTFCF == RockTFCF.SILTY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.SILTY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                    rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SANDY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SANDY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_LOAM || 
                    rockTFCF == RockTFCF.COARSE_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_CLAY || 
                    rockTFCF == RockTFCF.COARSE_SILTY_CLAY || 
                    rockTFCF == RockTFCF.COARSE_SILTY_CLAY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT || 
                    rockTFCF == RockTFCF.PODZOL || 
                    rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SANDY_CLAY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.SANDY_CLAY_PODZOL || 
                    rockTFCF == RockTFCF.LOAM_GRASS || 
                    rockTFCF == RockTFCF.LOAM_PODZOL || 
                    rockTFCF == RockTFCF.CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.CLAY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.CLAY_PODZOL || 
                    rockTFCF == RockTFCF.SILTY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.SILTY_CLAY_PODZOL || 
                    rockTFCF == RockTFCF.SILTY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SILTY_CLAY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_GRASS || 
                    rockTFCF == RockTFCF.SILT_PODZOL || 
                    rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILTY_CLAY_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILTY_CLAY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_GRASS;
                case DRY:
                    return
                    rockTFCF == RockTFCF.COARSE_DIRT || 
                    rockTFCF == RockTFCF.MUD || 
                    rockTFCF == RockTFCF.LOAMY_SAND || 
                    rockTFCF == RockTFCF.SANDY_LOAM || 
                    rockTFCF == RockTFCF.LOAM || 
                    rockTFCF == RockTFCF.SILT_LOAM || 
                    rockTFCF == RockTFCF.SILT || 
                    rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                    rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT || 
                    rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_GRASS;
                case FRESH_WATER:
                    return
                    rockTFCF == RockTFCF.COARSE_DIRT || 
                    rockTFCF == RockTFCF.MUD || 
                    rockTFCF == RockTFCF.LOAMY_SAND || 
                    rockTFCF == RockTFCF.SANDY_LOAM || 
                    rockTFCF == RockTFCF.LOAM || 
                    rockTFCF == RockTFCF.SILT_LOAM || 
                    rockTFCF == RockTFCF.SILT || 
                    rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                    rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT || 
                    rockTFCF == RockTFCF.PODZOL || 
                    rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.LOAM_GRASS || 
                    rockTFCF == RockTFCF.LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_GRASS || 
                    rockTFCF == RockTFCF.SILT_PODZOL || 
                    rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_GRASS;
                case SALT_WATER:
                    return
                    rockTFCF == RockTFCF.COARSE_DIRT || 
                    rockTFCF == RockTFCF.MUD || 
                    rockTFCF == RockTFCF.LOAMY_SAND || 
                    rockTFCF == RockTFCF.SANDY_LOAM || 
                    rockTFCF == RockTFCF.LOAM || 
                    rockTFCF == RockTFCF.SILT_LOAM || 
                    rockTFCF == RockTFCF.SILT || 
                    rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                    rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT || 
                    rockTFCF == RockTFCF.PODZOL || 
                    rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.LOAM_GRASS || 
                    rockTFCF == RockTFCF.LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_GRASS || 
                    rockTFCF == RockTFCF.SILT_PODZOL || 
                    rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_GRASS;
                case FRESH_BEACH:
                {
                    boolean flag = false;
                    for (EnumFacing facing : EnumFacing.HORIZONTALS)
                    {
                        for (int i = 1; i <= beachDistance; i++)
                        {
                            if (BlocksTFC.isFreshWaterOrIce(world.getBlockState(pos.offset(facing, i))))
                            {
                                flag = true;
                                break;
                            }
                        }
                    }
                    return 
                    (
                        rockTFCF == RockTFCF.COARSE_DIRT || 
                        rockTFCF == RockTFCF.MUD || 
                        rockTFCF == RockTFCF.LOAMY_SAND || 
                        rockTFCF == RockTFCF.SANDY_LOAM || 
                        rockTFCF == RockTFCF.LOAM || 
                        rockTFCF == RockTFCF.SILT_LOAM || 
                        rockTFCF == RockTFCF.SILT || 
                        rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                        rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                        rockTFCF == RockTFCF.COARSE_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SILT || 
                        rockTFCF == RockTFCF.PODZOL || 
                        rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                        rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                        rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.LOAM_GRASS || 
                        rockTFCF == RockTFCF.LOAM_PODZOL || 
                        rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.SILT_GRASS || 
                        rockTFCF == RockTFCF.SILT_PODZOL || 
                        rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                        rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_SILT_GRASS
                    ) && flag;
                }
                case SALT_BEACH:
                {
                    boolean flag = false;
                    for (EnumFacing facing : EnumFacing.HORIZONTALS)
                    {
                        for (int i = 1; i <= beachDistance; i++)
                            if (BlocksTFC.isSaltWater(world.getBlockState(pos.offset(facing, i))))
                            {
                                flag = true;
                            }
                    }
                    return 
                    (
                        rockTFCF == RockTFCF.COARSE_DIRT || 
                        rockTFCF == RockTFCF.MUD || 
                        rockTFCF == RockTFCF.LOAMY_SAND || 
                        rockTFCF == RockTFCF.SANDY_LOAM || 
                        rockTFCF == RockTFCF.LOAM || 
                        rockTFCF == RockTFCF.SILT_LOAM || 
                        rockTFCF == RockTFCF.SILT || 
                        rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                        rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                        rockTFCF == RockTFCF.COARSE_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SILT || 
                        rockTFCF == RockTFCF.PODZOL || 
                        rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                        rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                        rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.LOAM_GRASS || 
                        rockTFCF == RockTFCF.LOAM_PODZOL || 
                        rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.SILT_GRASS || 
                        rockTFCF == RockTFCF.SILT_PODZOL || 
                        rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                        rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_SILT_GRASS
                    ) && flag;
                }
            }
        }

        /*
        else if (plantable instanceof BlockCropTFC)
        {
            IBlockState cropState = world.getBlockState(pos.up());
            if (cropState.getBlock() instanceof BlockCropTFC)
            {
                boolean isWild = cropState.getValue(WILD);
                if (isWild)
                {
                    if 
                    (
                        rockTFCF == RockTFCF.COARSE_DIRT || 
                        rockTFCF == RockTFCF.SANDY_LOAM || 
                        rockTFCF == RockTFCF.LOAM || 
                        rockTFCF == RockTFCF.SILT_LOAM || 
                        rockTFCF == RockTFCF.SANDY_CLAY_LOAM || 
                        rockTFCF == RockTFCF.SANDY_CLAY || 
                        rockTFCF == RockTFCF.CLAY_LOAM || 
                        rockTFCF == RockTFCF.SILTY_CLAY_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SANDY_CLAY_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SANDY_CLAY || 
                        rockTFCF == RockTFCF.COARSE_LOAM || 
                        rockTFCF == RockTFCF.COARSE_CLAY_LOAM || 
                        rockTFCF == RockTFCF.COARSE_CLAY || 
                        rockTFCF == RockTFCF.COARSE_SILTY_CLAY_LOAM || 
                        rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                        rockTFCF == RockTFCF.PODZOL || 
                        rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.SANDY_CLAY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SANDY_CLAY_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.SANDY_CLAY_GRASS || 
                        rockTFCF == RockTFCF.SANDY_CLAY_PODZOL || 
                        rockTFCF == RockTFCF.LOAM_GRASS || 
                        rockTFCF == RockTFCF.LOAM_PODZOL || 
                        rockTFCF == RockTFCF.CLAY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.CLAY_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.CLAY_PODZOL || 
                        rockTFCF == RockTFCF.SILTY_CLAY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SILTY_CLAY_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                        rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                        rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                        rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                        rockTFCF == RockTFCF.DRY_SILT_GRASS
                    )
                    {
                        return true;
                    }
                }
                return type == Rock.Type.FARMLAND;
            }
        }
        */

        switch (plantable.getPlantType(world, pos.offset(direction)))
        {
            case Plains:
                return 
                rockTFCF == RockTFCF.COARSE_DIRT || 
                rockTFCF == RockTFCF.LOAMY_SAND || 
                rockTFCF == RockTFCF.SANDY_LOAM || 
                rockTFCF == RockTFCF.LOAM || 
                rockTFCF == RockTFCF.SILT_LOAM || 
                rockTFCF == RockTFCF.SILT || 
                rockTFCF == RockTFCF.SANDY_CLAY_LOAM || 
                rockTFCF == RockTFCF.SANDY_CLAY || 
                rockTFCF == RockTFCF.CLAY_LOAM || 
                rockTFCF == RockTFCF.SILTY_CLAY_LOAM || 
                rockTFCF == RockTFCF.SILTY_CLAY || 
                rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                rockTFCF == RockTFCF.COARSE_SANDY_CLAY_LOAM || 
                rockTFCF == RockTFCF.COARSE_SANDY_CLAY || 
                rockTFCF == RockTFCF.COARSE_LOAM || 
                rockTFCF == RockTFCF.COARSE_CLAY_LOAM || 
                rockTFCF == RockTFCF.COARSE_CLAY || 
                rockTFCF == RockTFCF.COARSE_SILTY_CLAY || 
                rockTFCF == RockTFCF.COARSE_SILTY_CLAY_LOAM || 
                rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                rockTFCF == RockTFCF.COARSE_SILT || 
                rockTFCF == RockTFCF.PODZOL || 
                rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                rockTFCF == RockTFCF.SANDY_CLAY_LOAM_GRASS || 
                rockTFCF == RockTFCF.SANDY_CLAY_LOAM_PODZOL || 
                rockTFCF == RockTFCF.SANDY_CLAY_GRASS || 
                rockTFCF == RockTFCF.SANDY_CLAY_PODZOL || 
                rockTFCF == RockTFCF.LOAM_GRASS || 
                rockTFCF == RockTFCF.LOAM_PODZOL || 
                rockTFCF == RockTFCF.CLAY_LOAM_GRASS || 
                rockTFCF == RockTFCF.CLAY_LOAM_PODZOL || 
                rockTFCF == RockTFCF.CLAY_PODZOL || 
                rockTFCF == RockTFCF.SILTY_CLAY_GRASS || 
                rockTFCF == RockTFCF.SILTY_CLAY_PODZOL || 
                rockTFCF == RockTFCF.SILTY_CLAY_LOAM_GRASS || 
                rockTFCF == RockTFCF.SILTY_CLAY_LOAM_PODZOL || 
                rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                rockTFCF == RockTFCF.SILT_GRASS || 
                rockTFCF == RockTFCF.SILT_PODZOL || 
                rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                rockTFCF == RockTFCF.DRY_SANDY_CLAY_LOAM_GRASS || 
                rockTFCF == RockTFCF.DRY_SANDY_CLAY_GRASS || 
                rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                rockTFCF == RockTFCF.DRY_CLAY_LOAM_GRASS || 
                rockTFCF == RockTFCF.DRY_CLAY_GRASS || 
                rockTFCF == RockTFCF.DRY_SILTY_CLAY_GRASS || 
                rockTFCF == RockTFCF.DRY_SILTY_CLAY_LOAM_GRASS || 
                rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                rockTFCF == RockTFCF.DRY_SILT_GRASS;
            case Cave:
                return true;
            case Water:
                return false;
            case Beach:
            {
                boolean flag = false;
                for (EnumFacing facing : EnumFacing.HORIZONTALS)
                {
                    for (int i = 1; i <= beachDistance; i++)
                        if (BlocksTFC.isWater(world.getBlockState(pos.offset(facing, i))))
                        {
                            flag = true;
                        }
                }
                return 
                (
                    rockTFCF == RockTFCF.COARSE_DIRT || 
                    rockTFCF == RockTFCF.MUD || 
                    rockTFCF == RockTFCF.LOAMY_SAND || 
                    rockTFCF == RockTFCF.SANDY_LOAM || 
                    rockTFCF == RockTFCF.LOAM || 
                    rockTFCF == RockTFCF.SILT_LOAM || 
                    rockTFCF == RockTFCF.SILT || 
                    rockTFCF == RockTFCF.COARSE_LOAMY_SAND || 
                    rockTFCF == RockTFCF.COARSE_SANDY_LOAM || 
                    rockTFCF == RockTFCF.COARSE_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT_LOAM || 
                    rockTFCF == RockTFCF.COARSE_SILT || 
                    rockTFCF == RockTFCF.PODZOL || 
                    rockTFCF == RockTFCF.LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.LOAMY_SAND_PODZOL || 
                    rockTFCF == RockTFCF.SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SANDY_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.LOAM_GRASS || 
                    rockTFCF == RockTFCF.LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.SILT_LOAM_PODZOL || 
                    rockTFCF == RockTFCF.SILT_GRASS || 
                    rockTFCF == RockTFCF.SILT_PODZOL || 
                    rockTFCF == RockTFCF.DRY_LOAMY_SAND_GRASS || 
                    rockTFCF == RockTFCF.DRY_SANDY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_LOAM_GRASS || 
                    rockTFCF == RockTFCF.DRY_SILT_GRASS
                ) && flag;
            }
            case Nether:
                return false;
        }

        return false;
    }

    public RockTFCF getType()
    {
        return rockTFCF;
    }

    public Rock getRock()
    {
        return rock;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL; // Store anywhere
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT; // Stacksize = 32
    }

    protected void onRockSlide(World world, BlockPos pos)
    {
        switch (rockTFCF)
        {
            case COARSE_DIRT:
            case MUD:
            case LOAMY_SAND:
            case COARSE_LOAMY_SAND:
            case SANDY_LOAM:
            case COARSE_SANDY_LOAM:
            case SANDY_CLAY_LOAM:
            case COARSE_SANDY_CLAY_LOAM:
            case SANDY_CLAY:
            case COARSE_SANDY_CLAY:
            case LOAM:
            case COARSE_LOAM:
            case CLAY_LOAM:
            case COARSE_CLAY_LOAM:
            case COARSE_CLAY:
            case SILTY_CLAY:
            case COARSE_SILTY_CLAY:
            case SILTY_CLAY_LOAM:
            case COARSE_SILTY_CLAY_LOAM:
            case SILT_LOAM:
            case COARSE_SILT_LOAM:
            case SILT:
            case COARSE_SILT:
            case PODZOL:
            case LOAMY_SAND_GRASS:
            case LOAMY_SAND_PODZOL:
            case SANDY_LOAM_GRASS:
            case SANDY_LOAM_PODZOL:
            case SANDY_CLAY_LOAM_GRASS:
            case SANDY_CLAY_LOAM_PODZOL:
            case SANDY_CLAY_GRASS:
            case SANDY_CLAY_PODZOL:
            case LOAM_GRASS:
            case LOAM_PODZOL:
            case CLAY_LOAM_GRASS:
            case CLAY_LOAM_PODZOL:
            case CLAY_PODZOL:
            case SILTY_CLAY_GRASS:
            case SILTY_CLAY_PODZOL:
            case SILTY_CLAY_LOAM_GRASS:
            case SILTY_CLAY_LOAM_PODZOL:
            case SILT_LOAM_GRASS:
            case SILT_LOAM_PODZOL:
            case SILT_GRASS:
            case SILT_PODZOL:
            case DRY_LOAMY_SAND_GRASS:
            case DRY_SANDY_LOAM_GRASS:
            case DRY_SANDY_CLAY_LOAM_GRASS:
            case DRY_SANDY_CLAY_GRASS:
            case DRY_LOAM_GRASS:
            case DRY_CLAY_LOAM_GRASS:
            case DRY_CLAY_GRASS:
            case DRY_SILTY_CLAY_GRASS:
            case DRY_SILTY_CLAY_LOAM_GRASS:
            case DRY_SILT_LOAM_GRASS:
            case DRY_SILT_GRASS:
                world.playSound(null, pos, TFCSounds.DIRT_SLIDE_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                break;
        }
    }
}