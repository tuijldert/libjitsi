/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.impl.neomedia.device;

import java.awt.*;

import org.jitsi.service.neomedia.device.*;

/**
 * Implementation of <tt>ScreenDevice</tt>.
 *
 * @author Sebastien Vincent
 * @author Lyubomir Marinov
 */
public class ScreenDeviceImpl implements ScreenDevice
{
    private static final ScreenDevice[] EMPTY_SCREEN_DEVICE_ARRAY
        = new ScreenDevice[0];

    /**
     * AWT <tt>GraphicsDevice</tt>.
     */
    final GraphicsDevice screen;

    /**
     * Screen index.
     */
    private final int index;

    /**
     * Returns all available <tt>ScreenDevice</tt> device.
     *
     * @return array of <tt>ScreenDevice</tt> device
     */
    public static ScreenDevice[] getAvailableScreenDevices()
    {
        GraphicsEnvironment ge;

        try
        {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        }
        catch(NoClassDefFoundError ncdfe)
        {
            ge = null;
        }

        ScreenDevice[] screens = null;

        /*
         * Make sure the GraphicsEnvironment is not headless in order to avoid a
         * HeadlessException.
         */
        if ((ge != null) && !ge.isHeadlessInstance())
        {
            GraphicsDevice[] devices = ge.getScreenDevices();

            if ((devices != null) && (devices.length != 0))
            {
                screens = new ScreenDevice[devices.length];

                int i = 0;

                for (GraphicsDevice dev : devices)
                {
                    // We know that GraphicsDevice type is TYPE_RASTER_SCREEN.
                    screens[i] = new ScreenDeviceImpl(i, dev);
                    i++;
                }
            }
        }

        return (screens == null) ? EMPTY_SCREEN_DEVICE_ARRAY : screens;
    }

    public static ScreenDevice getDefaultScreenDevice()
    {
        ScreenDevice[] screens = getAvailableScreenDevices();
        int width = 0;
        int height = 0;
        ScreenDevice best = null;

        for (ScreenDevice screen : screens)
        {
            java.awt.Dimension res = screen.getSize();

            if ((res != null) && ((width < res.width) || (height < res.height)))
            {
                width = res.width;
                height = res.height;
                best = screen;
            }
        }
        return best;
    }

    /**
     * Constructor.
     *
     * @param index screen index
     * @param screen screen device
     */
    protected ScreenDeviceImpl(int index, GraphicsDevice screen)
    {
        this.index = index;
        this.screen = screen;
    }

    /**
     * Get the screen index.
     *
     * @return screen index
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Get current resolution of <tt>ScreenDevice</tt> device.
     *
     * @return current resolution of the screen
     */
    public Dimension getSize()
    {
        /* get current display resolution */
        DisplayMode mode = screen.getDisplayMode();

        return
            (mode == null)
                ? null
                : new Dimension(mode.getWidth(), mode.getHeight());
    }

    /**
     * Get the identifier of the screen.
     *
     * @return ID of the screen
     */
    public String getName()
    {
        return screen.getIDstring();
    }

    /**
     * If the screen contains specified point.
     *
     * @param p point coordinate
     * @return true if point belongs to screen, false otherwise
     */
    public boolean containsPoint(Point p)
    {
        return screen.getDefaultConfiguration().getBounds().contains(p);
    }

    /**
     * Get bounds of the screen.
     *
     * @return bounds of the screen
     */
    public Rectangle getBounds()
    {
        return screen.getDefaultConfiguration().getBounds();
    }
}
