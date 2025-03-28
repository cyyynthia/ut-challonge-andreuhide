package m2sdl.challongeandreuhide.entities

object EntityRegistry {
	val DayLightEntities = listOf(
		EvokerEntity::class,
		VindicatorEntity::class,
		RavagerEntity::class,
		PillagerEntity::class,
	)

	val NighttimeEntities = listOf(
		CreeperEntity::class,
		EndermanEntity::class,
		ZombieEntity::class,
		SkeletonEntity::class,
	)
}
