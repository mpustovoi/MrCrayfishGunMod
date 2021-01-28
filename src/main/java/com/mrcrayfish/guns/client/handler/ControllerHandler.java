package com.mrcrayfish.guns.client.handler;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.Action;
import com.mrcrayfish.controllable.client.ButtonBindings;
import com.mrcrayfish.controllable.client.Buttons;
import com.mrcrayfish.controllable.client.Controller;
import com.mrcrayfish.controllable.event.AvailableActionsEvent;
import com.mrcrayfish.controllable.event.ControllerEvent;
import com.mrcrayfish.controllable.event.GatherActionsEvent;
import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.init.ModSyncedDataKeys;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.attachment.IScope;
import com.mrcrayfish.guns.item.attachment.impl.Scope;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.MessageAttachments;
import com.mrcrayfish.guns.network.message.MessageUnload;
import com.mrcrayfish.guns.util.GunEnchantmentHelper;
import com.mrcrayfish.obfuscate.common.data.SyncedPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ControllerHandler
{
    private int reloadCounter = -1;

    @SubscribeEvent
    public void onButtonInput(ControllerEvent.ButtonInput event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        World world = Minecraft.getInstance().world;
        if(player != null && world != null)
        {
            ItemStack heldItem = player.getHeldItemMainhand();
            int button = event.getButton();
            if(button == ButtonBindings.ATTACK.getButton())
            {
                if(heldItem.getItem() instanceof GunItem)
                {
                    event.setCanceled(true);
                    if(event.getState())
                    {
                        ShootingHandler.get().fire(player, heldItem);
                    }
                }
            }
            else if(button == ButtonBindings.USE_ITEM.getButton())
            {
                if(heldItem.getItem() instanceof GunItem)
                {
                    event.setCanceled(true);
                }
            }
            else if(button == ButtonBindings.SNEAK.getButton())
            {
                if(heldItem.getItem() instanceof GunItem)
                {
                    event.setCanceled(true);
                }
            }
            else if(button == ButtonBindings.SWAP_HANDS.getButton())
            {
                if(heldItem.getItem() instanceof GunItem)
                {
                    event.setCanceled(true);
                    if(event.getState())
                    {
                        this.reloadCounter = 0;
                    }
                }
            }
            else if(button == ButtonBindings.PICK_BLOCK.getButton())
            {
                if(heldItem.getItem() instanceof GunItem && Minecraft.getInstance().currentScreen == null)
                {
                    event.setCanceled(true);
                    if(event.getState())
                    {
                        PacketHandler.getPlayChannel().sendToServer(new MessageAttachments());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onControllerTurn(ControllerEvent.Turn event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player != null)
        {
            ItemStack heldItem = player.getHeldItemMainhand();
            if(heldItem.getItem() instanceof GunItem && AimingHandler.get().isAiming())
            {
                double adsSensitivity = Config.CLIENT.controls.aimDownSightSensitivity.get();
                event.setYawSpeed(10.0F * (float) adsSensitivity);
                event.setPitchSpeed(7.5F * (float) adsSensitivity);

                Scope scope = Gun.getScope(heldItem);
                if(scope != null && scope.isStable() && Controllable.isButtonPressed(ButtonBindings.SNEAK.getButton()))
                {
                    event.setYawSpeed(event.getYawSpeed() / 2.0F);
                    event.setPitchSpeed(event.getPitchSpeed() / 2.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public void updateAvailableActions(GatherActionsEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.currentScreen != null) return;

        PlayerEntity player = Minecraft.getInstance().player;
        if(player != null)
        {
            ItemStack heldItem = player.getHeldItemMainhand();
            if(heldItem.getItem() instanceof GunItem)
            {
                event.getActions().put(ButtonBindings.USE_ITEM, new Action("Aim", Action.Side.RIGHT));
                event.getActions().put(ButtonBindings.ATTACK, new Action("Shoot", Action.Side.RIGHT));

                GunItem gunItem = (GunItem) heldItem.getItem();
                Gun modifiedGun = gunItem.getModifiedGun(heldItem);
                CompoundNBT tag = heldItem.getTag();
                if(tag != null && tag.getInt("AmmoCount") < GunEnchantmentHelper.getAmmoCapacity(heldItem, modifiedGun))
                {
                    event.getActions().put(ButtonBindings.SWAP_HANDS, new Action("Reload", Action.Side.LEFT));
                }

                ItemStack scopeStack = Gun.getScopeStack(heldItem);
                if(scopeStack.getItem() instanceof IScope && AimingHandler.get().isAiming())
                {
                    IScope iscope = (IScope) scopeStack.getItem();
                    Scope scope = iscope.getProperties();
                    if(scope.isStable())
                    {
                        event.getActions().put(ButtonBindings.SNEAK, new Action("Hold Breath", Action.Side.RIGHT));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event)
    {
        Controller controller = Controllable.getController();
        if(controller == null)
            return;

        if(event.phase == TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if(player == null)
            return;

        if(Controllable.isButtonPressed(ButtonBindings.ATTACK.getButton()))
        {
            ItemStack heldItem = player.getHeldItemMainhand();
            if(heldItem.getItem() instanceof GunItem)
            {
                Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem);
                if(gun.getGeneral().isAuto())
                {
                    ShootingHandler.get().fire(player, heldItem);
                }
            }
        }

        if(mc.currentScreen == null && this.reloadCounter != -1)
        {
            if(Controllable.isButtonPressed(ButtonBindings.SWAP_HANDS.getButton()))
            {
                this.reloadCounter++;
            }
        }

        if(this.reloadCounter > 40)
        {
            ReloadHandler.get().setReloading(false);
            PacketHandler.getPlayChannel().sendToServer(new MessageUnload());
            this.reloadCounter = -1;
        }
        else if(this.reloadCounter > 0 && !ButtonBindings.SWAP_HANDS.isButtonDown())
        {
            ReloadHandler.get().setReloading(!SyncedPlayerData.instance().get(player, ModSyncedDataKeys.RELOADING));
            this.reloadCounter = -1;
        }
    }

    public static boolean isAiming()
    {
        Controller controller = Controllable.getController();
        return controller != null && Controllable.isButtonPressed(ButtonBindings.USE_ITEM.getButton());
    }

    public static boolean isShooting()
    {
        Controller controller = Controllable.getController();
        return controller != null && Controllable.isButtonPressed(ButtonBindings.ATTACK.getButton());
    }
}
