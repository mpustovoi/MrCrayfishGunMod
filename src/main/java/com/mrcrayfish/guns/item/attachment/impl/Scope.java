package com.mrcrayfish.guns.item.attachment.impl;

import com.mrcrayfish.guns.interfaces.IGunModifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An attachment class related to scopes. Scopes need to at least specify the additional zoom (or fov)
 * they provide and the y-offset to the center of the scope for them to render correctly. Use
 * {@link #create(float, double, IGunModifier...)} to create an get.
 * <p>
 * Author: MrCrayfish
 */
public class Scope extends Attachment
{
    protected float additionalZoom;
    protected double centerOffset;
    protected boolean stable;
    protected double viewFinderOffset;
    protected double aimFov;

    protected Scope() {}

    private Scope(float additionalZoom, double centerOffset, double aimFov, IGunModifier... modifier)
    {
        super(modifier);
        this.additionalZoom = additionalZoom;
        this.centerOffset = centerOffset;
        this.aimFov = aimFov;
    }

    private Scope(float additionalZoom, double centerOffset, boolean stable, double viewFinderOffset, double aimFov, IGunModifier... modifiers)
    {
        super(modifiers);
        this.additionalZoom = additionalZoom;
        this.centerOffset = centerOffset;
        this.stable = stable;
        this.viewFinderOffset = viewFinderOffset;
        this.aimFov = aimFov;
    }

    /**
     * Marks this scope to allow it to be stabilised while using a controller. This is essentially
     * holding your breath while looking down the sight.
     */
    public void stabilise()
    {
        this.stable = true;
    }

    /**
     * Sets the offset distance from the camera to the view finder
     *
     * @param offset the view finder offset
     * @return this scope get
     */
    public Scope viewFinderOffset(double offset)
    {
        this.viewFinderOffset = offset;
        return this;
    }

    /**
     * Gets the amount of additional zoom (or reduced fov) this scope provides
     *
     * @return the scopes additional zoom
     */
    public float getAdditionalZoom()
    {
        return this.additionalZoom;
    }

    /**
     * Gets the offset to the center of the scope. Used to render scope cross hair exactly in the
     * middle of the screen.
     *
     * @return the scope center offset
     */
    public double getCenterOffset()
    {
        return this.centerOffset;
    }

    /**
     * @return If this scope can be stabilised
     */
    public boolean isStable()
    {
        return this.stable;
    }

    /**
     * @return The view finder offset of this scope
     */
    public double getViewFinderOffset()
    {
        return this.viewFinderOffset;
    }

    /**
     * @return The FOV of the first person viewport when aiming
     */
    public double getAimFov()
    {
        return this.aimFov;
    }

    /**
     * Creates a scope. This method is now deprecated. Use the builder instead.
     *
     * @param additionalZoom the additional zoom this scope provides
     * @param centerOffset   the length to the center of the view finder from the base of the scope model in pixels
     * @param modifiers      an array of gun modifiers
     * @return a scope get
     */
    @Deprecated(since = "1.3.0", forRemoval = true)
    public static Scope create(float additionalZoom, double centerOffset, IGunModifier... modifiers)
    {
        return new Scope(additionalZoom, centerOffset, 10.0, modifiers);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private float additionalZoom = 0.0F;
        private double centerOffset = 0.0;
        private boolean stable = false;
        private double viewFinderOffset = 0.0;
        private double aimFov = 10.0;
        private IGunModifier[] modifiers = new IGunModifier[]{};

        private Builder() {}

        public Builder additionalZoom(float additionalZoom)
        {
            this.additionalZoom = additionalZoom;
            return this;
        }

        public Builder centerOffset(double centerOffset)
        {
            this.centerOffset = centerOffset;
            return this;
        }

        public Builder stable(boolean stable)
        {
            this.stable = stable;
            return this;
        }

        public Builder viewFinderOffset(double viewFinderOffset)
        {
            this.viewFinderOffset = viewFinderOffset;
            return this;
        }

        public Builder aimFov(double aimFov)
        {
            this.aimFov = aimFov;
            return this;
        }

        public Builder modifiers(IGunModifier... modifiers)
        {
            this.modifiers = modifiers;
            return this;
        }

        public Scope build()
        {
            return new Scope(this.additionalZoom, this.centerOffset, this.stable, this.viewFinderOffset, this.aimFov, this.modifiers);
        }
    }
}
