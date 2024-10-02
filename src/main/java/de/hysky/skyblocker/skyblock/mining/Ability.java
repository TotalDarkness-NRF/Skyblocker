package de.hysky.skyblocker.skyblock.mining;

import java.util.*;

public class Ability {
	private static final Set<Ability> abilities = new HashSet<>();
	private final String name;
	private final int cooldown;
	private int cooldownLeft;
	private boolean isEnabled;

	public Ability(String name, int cooldown) {
		this.name = name;
		this.cooldown = this.cooldownLeft = Math.max(cooldown, 0);
	}

	public String getName() {
		return name;
	}

	public int getCooldown() {
		return cooldown;
	}

	public int getCooldownLeft() {
		return Math.max(cooldownLeft, 0);
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public int getCooldownLocationChange() {
		return cooldown / 2;
	}

	public int getCooldownLate() {
		return  getEnabled()
				.map(a -> a.getCooldownLeft() + Math.abs((this.cooldown - a.cooldown) / 2))
				.orElseGet(() -> this.cooldownLeft);
	}

	protected void decreaseCooldown() {
		setCooldownLeft(this.cooldownLeft - 1);
	}

	protected void setCooldownLeft(int cooldownLeft) {
		this.cooldownLeft = Math.max(cooldownLeft, 0);
	}

	protected void resetCooldown() {
		this.cooldownLeft = this.cooldown;
	}

	public boolean equalName(String abilityName) {
		return this.name.equals(abilityName);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Ability)) return false;
		return equalName(((Ability) object).name);
	}

	@Override
	public int hashCode() {
		return this.name != null ? this.name.hashCode() : 0;
	}

	protected boolean enable() {
		// TODO return boolean needed?
		Optional<Ability> abilityToEnable = getAbility(this);
		if (abilityToEnable.isEmpty()) return false;
		Optional<Ability> enabledAbility = getEnabled();
		if (enabledAbility.isEmpty()) abilityToEnable.get().isEnabled = true;
		else if (abilityToEnable.get().equals(enabledAbility.get())) return false;
		else {
			enabledAbility.get().isEnabled = false;
			abilityToEnable.get().isEnabled = false;
		}
		return true;
	}

	protected static void reset() {
		abilities.clear();
	}

	protected static boolean addAbility(Ability ability) {
		return abilities.add(ability);
	}

	public static Set<Ability> getAbilities() {
		return Set.copyOf(abilities.stream().sorted(Comparator.comparingInt(a -> a.cooldownLeft)).toList());
	}

	public static Optional<Ability> getNextAbility() {
		return getEnabled().flatMap(ability -> {
			int index = getAbilities().stream().toList().indexOf(ability) + 1;
			index = index >= abilities.size() ? 0: index;
			return getAbilities().stream().skip(index).findFirst();
		});
	}

	public static Optional<Ability> getLastAbility() {
		return getEnabled().flatMap(ability -> {
			int index = getAbilities().stream().toList().indexOf(ability);
			index = (index > 0 ? index : abilities.size()) - 1;
			return getAbilities().stream().skip(index).findFirst();
		});
	}

	protected static Optional<Ability> getEnabled() {
		return abilities.stream().filter(a -> a.isEnabled).findFirst();
	}

	protected static Optional<Ability> getAbility(final String name) {
		return abilities.stream().filter(a -> a.equalName(name)).findFirst();
	}

	protected static Optional<Ability> getAbility(final Ability ability) {
		return abilities.stream().filter(a -> a.equals(ability)).findFirst();
	}
}
