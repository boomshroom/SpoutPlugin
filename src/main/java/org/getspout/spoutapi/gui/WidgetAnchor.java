/*
 * This file is part of SpoutPlugin.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutPlugin is licensed under the GNU Lesser General Public License.
 *
 * SpoutPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spoutapi.gui;

import java.util.HashMap;

/**
 * Widget anchors allow you to place widgets that
 * stick or "anchor" to a point on the screen.
 * <p/>
 * A widget's coordinates refer to it's <b>top left</b>
 * corner and anchors change the point they are
 * relative to on the screen.
 * <p/>
 * You can choose any of nine points to anchor to,
 * noting that if anchoring to the bottom or right
 * the widget will be offscreen until you set a
 * negative y or x value to "correct" it.
 * <p/>
 * When a widget is anchored to any of those points
 * the display will be scaled to the same GUI Scale
 * setting as the client options.
 * <p/>
 * The only exception is SCALE (default) which assumes the
 * screen to always be 427x240 and stretches everything
 * widgets to conform.
 * <p/>
 * In order to move a set of widgets to a specific
 * anchor it is advised you use a Container, then
 * anchor and move that instead.
 * <p/>
 * Widgets are anchored by their top-left corner
 * because the positioning of one widget may rely
 * on another widget, and there is no way to know
 * which widgets are related to each other without
 * using a Container.
 */
public enum WidgetAnchor {
	/**
	 * Anchor the top-left of the widget to the top-left of the display.
	 */
	TOP_LEFT(0),
	/**
	 * Anchor the top-left of the widget to the top-center of the display.
	 * <p/>
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth() / 2);
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	TOP_CENTER(1),
	/**
	 * Anchor the top-left of the widget to the top-right of the display.
	 * <p/>
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth());
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	TOP_RIGHT(2),
	/**
	 * Anchor the top-left of the widget to the center-left of the display.
	 * <p/>
	 * Vertical correction: widget.shiftYpos(- widget.getHeight() / 2);
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	CENTER_LEFT(3),
	/**
	 * Anchor the top-left of the widget to the center of the display.
	 * <p/>
	 * This is the anchor used by in-game popups and menus.
	 * <p/>
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth() / 2);
	 * Vertical correction: widget.shiftYpos(- widget.getHeight() / 2);
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	CENTER_CENTER(4),
	/**
	 * Anchor the top-left of the widget to the center-right of the display.
	 * <p/>
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth());
	 * Vertical correction: widget.shiftYpos(- widget.getHeight() / 2);
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	CENTER_RIGHT(5),
	/**
	 * Anchor the top-left of the widget to the bottom-left of the display.
	 * <p/>
	 * Vertical correction: widget.shiftYpos(- widget.getHeight());
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	BOTTOM_LEFT(6),
	/**
	 * Anchor the top-left of the widget to the bottom-center of the display.
	 * <p/>
	 * This is the anchor used by the in-game HUD.
	 * <p/>
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth() / 2);
	 * Vertical correction: widget.shiftYpos(- widget.getHeight());
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	BOTTOM_CENTER(7),
	/**
	 * Anchor the top-left of the widget to the bottom-right of the display.
	 * <p/>
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth());
	 * Vertical correction: widget.shiftYpos(- widget.getHeight());
	 * <p/>
	 * For multiple widgets being anchored it is advised to use a Container
	 * and to anchor that instead.
	 */
	BOTTOM_RIGHT(8),
	/**
	 * Scale the widget to a percentage of the display (default).
	 * <p/>
	 * This will stretch the widget as if the client screen has a 427x240
	 * pixel display. This can result in significant distortion if the player
	 * is full-screen or has changed their window shape from the default.
	 * <p/>
	 * NOTE: Do not assume that because it looks ok on your display when
	 * testing that it will look ok for anyone else!!!
	 */
	SCALE(9);
	private final int id;

	WidgetAnchor(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	private static final HashMap<Integer, WidgetAnchor> lookupId = new HashMap<Integer, WidgetAnchor>();

	static {
		for (WidgetAnchor t : values()) {
			lookupId.put(t.getId(), t);
		}
	}

	public static WidgetAnchor getAnchorFromId(int id) {
		return lookupId.get(id);
	}
}
