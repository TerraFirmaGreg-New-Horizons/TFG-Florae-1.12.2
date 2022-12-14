package tfcflorae.world.worldgen.groundcover;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenOreVeins;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;

import tfcflorae.ConfigTFCF;
import tfcflorae.TFCFlorae;
import tfcflorae.objects.blocks.groundcover.BlockSurfaceOreDeposit;
import tfcflorae.objects.blocks.BlocksTFCF;

public class WorldGenSurfaceOreDeposits implements IWorldGenerator
{
    private final boolean generateOres;
    private double factor;

    public WorldGenSurfaceOreDeposits(boolean generateOres)
    {
        this.generateOres = generateOres;
        factor = 1;
    }

    public void setFactor(double factor)
    {
        if (factor < 0) factor = 0;
        if (factor > 1) factor = 1;
        this.factor = factor;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkGenTFC && world.provider.getDimension() == 0)
        {
            final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
            final ChunkDataTFC baseChunkData = ChunkDataTFC.get(world, chunkBlockPos);

            // Get the proper list of veins
            List<Vein> veins = Collections.emptyList();
            int xoff = chunkX * 16 + 8;
            int zoff = chunkZ * 16 + 8;

            if (generateOres)
            {
                // Grab 2x2 area
                ChunkDataTFC[] chunkData = {
                    baseChunkData, // This chunk
                    ChunkDataTFC.get(world, chunkBlockPos.add(16, 0, 0)),
                    ChunkDataTFC.get(world, chunkBlockPos.add(0, 0, 16)),
                    ChunkDataTFC.get(world, chunkBlockPos.add(16, 0, 16))
                };
                if (!chunkData[0].isInitialized())
                {
                    return;
                }

                // Default to 35 below the surface, like classic
                int lowestYScan = Math.max(10, world.getTopSolidOrLiquidBlock(chunkBlockPos).getY() - 80);


                veins = WorldGenOreVeins.getNearbyVeins(chunkX, chunkZ, world.getSeed(), 1);
                if (!veins.isEmpty())
                {
                    veins.removeIf(v -> {
                        if (v.getType() == null || !v.getType().hasLooseRocks() || v.getHighestY() < lowestYScan)
                        {
                            return true;
                        }
                        for (ChunkDataTFC data : chunkData)
                        {
                            // No need to check for initialized chunk data, ores will be empty.
                            if (data.getGeneratedVeins().contains(v))
                            {
                                return false;
                            }
                        }
                        return true;
                    });
                }
            }

            for (int i = 0; i < ConfigTFCF.General.WORLD.groundcoverOreDepositFrequency * factor; i++)
            {
                BlockPos pos = new BlockPos(
                    xoff + random.nextInt(16),
                    0,
                    zoff + random.nextInt(16)
                );
                Rock rock = baseChunkData.getRock1(pos);
                generateRock(world, pos.up(world.getTopSolidOrLiquidBlock(pos).getY()), getRandomVein(veins, pos, random), rock);
            }
        }
    }

    private void generateRock(World world, BlockPos pos, @Nullable Vein vein, Rock rock)
    {
        if (world.isAirBlock(pos) && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) && (BlocksTFC.isSoil(world.getBlockState(pos.down())) || BlocksTFCF.isSoil(world.getBlockState(pos.down())) || BlocksTFC.isRawStone(world.getBlockState(pos.down()))))
        {
            if(vein != null && rock != null)
            {
                world.setBlockState(pos, BlockSurfaceOreDeposit.get(vein.getType().getOre(), rock).getDefaultState(), 1);
            }
        }
    }

    @Nullable
    private Vein getRandomVein(List<Vein> veins, BlockPos pos, Random rand)
    {
        if (!veins.isEmpty() && rand.nextDouble() < 0.4)
        {
            Vein vein = veins.get(rand.nextInt(veins.size()));
            if (vein.inRange(pos.getX(), pos.getZ(), 8))
            {
                return vein;
            }
        }
        return null;
    }
}