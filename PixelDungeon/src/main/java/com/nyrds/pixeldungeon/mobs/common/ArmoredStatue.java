package com.nyrds.pixeldungeon.mobs.common;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.Journal;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.blobs.ToxicGas;
import com.watabou.pixeldungeon.actors.buffs.Poison;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.items.Generator;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.watabou.pixeldungeon.items.weapon.Weapon;
import com.watabou.pixeldungeon.items.weapon.enchantments.Death;
import com.watabou.pixeldungeon.items.weapon.enchantments.Leech;
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.sprites.HeroSpriteDef;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ArmoredStatue extends Mob {

	private Armor armor;

	public ArmoredStatue() {
		EXP = 0;
		state = PASSIVE;

		do {
			armor = (Armor) Generator.random( Generator.Category.ARMOR );
		} while (!(armor instanceof Armor) || armor.level() < 0);

		UpdateSprite();

		hp(ht(15 + Dungeon.depth * 5));
		defenseSkill = 4 + Dungeon.depth;
		
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Poison.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
		IMMUNITIES.add( Leech.class );
	}

	private String[]           lookDesc;

	private static final String ARMOR	= "armor";
	private static final String LOOK    = "look";


	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARMOR, armor );
		bundle.put(LOOK, lookDesc);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		armor = (Armor) bundle.get( ARMOR );
		lookDesc = bundle.getStringArray(LOOK);
	}
	
	@Override
	protected boolean act() {
		if (Dungeon.visible[getPos()]) {
			Journal.add( Journal.Feature.STATUE );
		}
		return super.act();
	}

	@Override
	public int dr() {
		return Dungeon.depth;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 8 );
	}

	@Override
	public int attackSkill( Char target ) {
		return (int)((9 + Dungeon.depth) * 2);
	}

	@Override
	public void damage( int dmg, Object src ) {

		if (state == PASSIVE) {
			state = HUNTING;
		}
		
		super.damage( dmg, src );
	}
	@Override
	public void beckon( int cell ) {
	}
	
	@Override
	public void die( Object cause ) {
		if (armor != null) {
			Dungeon.level.drop( armor, getPos() ).sprite.drop();
		}
		super.die( cause );
	}
	
	@Override
	public void destroy() {
		Journal.remove( Journal.Feature.STATUE );
		super.destroy();
	}
	
	@Override
	public boolean reset() {
		state = PASSIVE;
		return true;
	}

	@Override
	public String description() {
		return Utils.format(Game.getVar(R.string.Statue_Desc), armor.name());
	}

	@Override
	public CharSprite sprite() {
		return new HeroSpriteDef(UpdateSprite());
		}

	public String[] UpdateSprite(){
		if(lookDesc == null) {
		Hero hero = new Hero();
		hero.setPos(Dungeon.hero.getPos());
		hero.setSprite(new HeroSpriteDef(armor));

		lookDesc = hero.getHeroSprite().getLayersDesc();
	}
		return lookDesc;
	}
}
