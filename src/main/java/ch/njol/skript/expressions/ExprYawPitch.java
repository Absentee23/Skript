/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011, 2012 Peter Güttinger
 * 
 */

package ch.njol.skript.expressions;

import org.bukkit.Location;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;

/**
 * @author Peter Güttinger
 */
@Name("Yaw / Pitch")
@Description("The yaw or pitch of a location. You likely won't need this expression ever if you don't know what this means.")
@Examples("log \"%player%: %location of player%, %player's yaw%, %player's pitch%\" to \"playerlocs.log\"")
@Since("2.0")
public class ExprYawPitch extends SimplePropertyExpression<Location, Float> {
	static {
		register(ExprYawPitch.class, Float.class, "(0¦yaw|1¦pitch)", "locations");
	}
	
	private boolean yaw;
	
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		yaw = parseResult.mark == 0;
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}
	
	@SuppressWarnings("null")
	@Override
	public Float convert(final Location l) {
		return yaw ? convertToPositive(l.getYaw()) : l.getPitch();
	}
	
	@Override
	public Class<? extends Float> getReturnType() {
		return Float.class;
	}
	
	@Override
	protected String getPropertyName() {
		return yaw ? "yaw" : "pitch";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE)
			return CollectionUtils.array(Float.class);
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		Location l = getExpr().getSingle(e);
		Float f = (Float) delta[0];
		switch (mode) {
		case SET:
			if (yaw)
				l.setYaw(convertToPositive(f));
			else
				l.setPitch(f);
		case ADD:
			if (yaw)
				l.setYaw(convertToPositive(l.getYaw()) + f);
			else
				l.setPitch(l.getPitch() + f);
			break;
		case REMOVE:
			if (yaw)
				l.setYaw(convertToPositive(l.getYaw()) - f);
			else
				l.setPitch(l.getPitch() - f);
			break;
		default:
			break;
		}
	}


	//Some random method decided to use for converting to positive values.
	public float convertToPositive(Number n) {
		if (n.floatValue() * -1 == Math.abs(n.floatValue()))
			return 360 + n.floatValue();
		return n.floatValue();
	}
}
