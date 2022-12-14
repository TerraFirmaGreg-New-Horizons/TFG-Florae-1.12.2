package tfcflorae.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IProxy
{
    @Nonnull
    IThreadListener getThreadListener(MessageContext context);

    @Nullable
    EntityPlayer getPlayer(MessageContext context);

    @Nullable
    World getWorld(MessageContext context);

    class WrongSideException extends RuntimeException
    {
        WrongSideException(String message)
        {
            super(message);
        }
    }
}
