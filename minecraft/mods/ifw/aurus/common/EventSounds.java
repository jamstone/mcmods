package mods.ifw.aurus.common;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class EventSounds {
	@ForgeSubscribe
	public void onSound(SoundLoadEvent event) {
		try {
			event.manager.soundPoolSounds.addSound("mob/fledgeling/hurt4.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/hurt4.wav"));

			event.manager.soundPoolSounds.addSound("mob/fledgeling/howl1.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/howl1.wav"));
			event.manager.soundPoolSounds.addSound("mob/fledgeling/howl2.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/howl2.wav"));
			event.manager.soundPoolSounds.addSound("mob/fledgeling/howl3.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/howl3.wav"));
			event.manager.soundPoolSounds.addSound("mob/fledgeling/howl4.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/howl4.wav"));

			event.manager.soundPoolSounds.addSound("mob/fledgeling/death1.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/death1.wav"));
			event.manager.soundPoolSounds.addSound("mob/fledgeling/death2.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/death2.wav"));

			event.manager.soundPoolSounds.addSound(
					"mob/fledgeling/telegraph1.wav", Aurus.class
							.getResource("/mods/ifw_aurus/sounds/telegraph1.wav"));
			event.manager.soundPoolSounds.addSound(
					"mob/fledgeling/telegraph2.wav", Aurus.class
							.getResource("/mods/ifw_aurus/sounds/telegraph2.wav"));
			event.manager.soundPoolSounds.addSound(
					"mob/fledgeling/telegraph3.wav", Aurus.class
							.getResource("/mods/ifw_aurus/sounds/telegraph3.wav"));

			event.manager.soundPoolSounds.addSound("mob/fledgeling/shoot1.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/shoot1.wav"));
			event.manager.soundPoolSounds.addSound("mob/fledgeling/shoot2.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/shoot2.wav"));
			event.manager.soundPoolSounds.addSound("mob/fledgeling/shoot2.wav",
					Aurus.class.getResource("/mods/ifw_aurus/sounds/shoot3.wav"));

		} catch (Exception e) {
			System.err.println("Failed to register one or more sounds.");
		}
	}

}
